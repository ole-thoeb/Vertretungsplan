package com.example.eloem.vertretungsplan.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.eloem.vertretungsplan.helperClasses.CombinedLiveData
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.helperClasses.editable
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.network.VerPlanNetworkService
import com.example.eloem.vertretungsplan.network.VolleyVerPlanService
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlanRepository(private val planDao: PlanDao, private val planService: VerPlanNetworkService) {

    val allVerPlans: LiveData<List<Vertretungsplan>> = liveData(Dispatchers.IO) {
        emitSource(CombinedLiveData(planDao.getAllVerPlans(), planDao.getAllPlans()) { verPlans, plansWithRows ->
            if (verPlans == null || plansWithRows == null) return@CombinedLiveData emptyList<Vertretungsplan>()
            
            val plans = plansWithRows.map { it.toPlan() }.toMutableList()
            
            return@CombinedLiveData verPlans.map { verPlan ->
                verPlan.toVerPlan(
                        plans.findAndRemove { it.id == verPlan.generalPlan },
                        plans.findAndRemove { it.id == verPlan.customPlan }
                )
            }
        })
    }
    
    suspend fun getVerPlans(enabledGrades: List<Vertretungsplan.Grade> = Vertretungsplan.Grade.values().asList(), notBefore: Long = 0, forMe: Boolean = false, lastOfDay: Boolean = false): LiveData<List<Vertretungsplan>> {
        return planDao.getPlansWith(enabledGrades.map { it.ordinal }, notBefore).switchMap { list ->
            liveData(Dispatchers.IO) {
                val plans = list.mapNotNull {
                    val verPlan = it.toVerPlan()
                    if (forMe && verPlan.customPlan.plan.isEmpty()) null
                    else verPlan
                }
                val filterPlans = if (lastOfDay) {
                    plans.groupBy { it.grade.ordinal.toLong() + it.targetDay }
                            .values
                            .map { it.maxByOrNull { plan -> plan.fetchedTime }!! }
                } else {
                    plans
                }
                emit(filterPlans)
            }
        }
    }
    
    suspend fun getLatestLocalPlan(grade: Vertretungsplan.Grade): Vertretungsplan? {
        return planDao.getLatestVerPlan(grade.ordinal)
                .ifNull { return null }
                .toVerPlan()
    }
    
    suspend fun getLatestLocalPlanLive(grade: Vertretungsplan.Grade): LiveData<Vertretungsplan?> {
        return planDao.getLatestVerPlanLive(grade.ordinal).switchMap<SqlVerPlan?, Vertretungsplan?> {
            if (it == null) return@switchMap MutableLiveData(null)
            liveData<Vertretungsplan?>(Dispatchers.IO) {
                emit(it.toVerPlan())
            }
        }
    }
    
    private suspend fun SqlVerPlan.toVerPlan(): Vertretungsplan {
        val gPlan = planDao.getPlan(generalPlan)
        val cPlan = planDao.getPlan(customPlan)
        return toVerPlan(gPlan.toPlan(), cPlan.toPlan())
    }
    
    suspend fun currentVerPlan(grade: Vertretungsplan.Grade, context: Context, fromAppwidget: Boolean = false): Result<Vertretungsplan, ResponseModel.Error> {
        val lastPlan = getLatestLocalPlan(grade) ?: run {
            Log.d(TAG, "Updating because last was null")
            return updateVerPlan(grade, context, fromAppwidget)
        }
        val lastTime = lastPlan.fetchedTime
        val currentTime = System.currentTimeMillis()
        val upToDate =  lastTime + 5 * 60 * 1000 > currentTime
        
        return if (!upToDate) {
            Log.d(TAG, "Fetching because of time last: ${lastTime.toDate()}, currentTime: ${currentTime.toDate()}")
            updateVerPlan(grade, context, fromAppwidget)
//            planService.planForGrade(grade).withSuccess {  plan ->
//                plan.toVertretungsplan(ctx).also { updateDatabaseWithPlan(it, lastPlan) }
//            }
        } else {
            Log.d(TAG, "Last plan was up to date. resume with last plan")
            Result.Success(lastPlan)
        }
    }
    
    suspend fun getVerPlan(verPlanId: Long): Vertretungsplan? {
        return planDao.getVerPlan(verPlanId)
                .ifNull { return null }
                .toVerPlan()
    }
    
    suspend fun updateVerPlan(grade: Vertretungsplan.Grade, context: Context, fromAppwidget: Boolean = false): Result<Vertretungsplan, ResponseModel.Error> {
        val otherGrades = if (context.generalPreferences { downloadAllPlansEnabled }) {
            Vertretungsplan.Grade.values().toMutableList().apply { remove(grade) }
        } else {
            emptyList<Vertretungsplan.Grade>()
        }
        
        return withContext(Dispatchers.IO) {
            otherGrades.forEach {
                launch {
                    updateVerPlanSlave(it, context)
                }
            }
            updateVerPlanSlave(grade, context).also {
                if (!fromAppwidget && it is Result.Success) context.updateVerPlanWidget()
            }
        }
    }
    
    private suspend fun updateVerPlanSlave(grade: Vertretungsplan.Grade, context: Context): Result<Vertretungsplan, ResponseModel.Error> {
        Log.d(TAG, "updateVerPlanSlave for grade $grade")
        return planService.planForGrade(grade).withSuccess { plan ->
            plan.toVertretungsplan(context, context.favoriteTimetable()).also { updateDatabaseWithPlan(it) }
        }
    }
    
    private suspend fun updateDatabaseWithPlan(new: Vertretungsplan) {
        updateDatabaseWithPlan(new, getLatestLocalPlan(new.grade))
    }
    
    private suspend fun updateDatabaseWithPlan(new: Vertretungsplan, old: Vertretungsplan?) {
        Log.d(TAG, "comparing new $new to old $old")
        if (old != null && new.contentEquals(old)) {
            Log.d(TAG, "Updating fetchedTime in database")
            planDao.updateFetchedTime(old.id, new.fetchedTime)
        } else {
            Log.d(TAG, "Inserting new Plan into Database")
            insertVerPlan(new)
        }
    }
    
    private suspend fun insertVerPlan(verPlan: Vertretungsplan) {
        planDao.insertVertretungsplan(verPlan)
    }
    
    suspend fun deleteVerPlan(verPlan: Vertretungsplan) {
        planDao.deleteVerPlan(verPlan)
        if (planDao.getPlansComputedWithTimetable(verPlan.computedWith).isEmpty()) {
            deleteTimetable(verPlan.computedWith)
        }
    }
    
    /**
     * ##################### TIMETABLE STUFF ################################
     */
    
    suspend fun insertTimetable(timetable: Timetable) {
        planDao.insertTimetable(timetable)
    }
    
    suspend fun deleteTimetable(timetable: Timetable) {
        deleteTimetable(timetable.id)
    }
    
    private suspend fun deleteTimetable(timetableId: Long) {
        if (planDao.getPlansComputedWithTimetable(timetableId).isNotEmpty()) {
            archive(timetableId)
        } else {
            planDao.deleteTimetable(timetableId)
            planDao.deleteLessonsFromTimetable(timetableId)
        }
    }
    
    suspend fun getTimetable(id: Long): Timetable? {
        val sqlTimetable = planDao.getTimetable(id) ?: return null
        val lessons = planDao.getLessonsToTimetable(id).toTimetableLesson(5, 7)
        return sqlTimetable.toTimetable(lessons)
    }
    
    private suspend fun Context.favoriteTimetable(): Timetable? {
        val favoriteId = generalPreferences { favoriteTimetableId }
        if (favoriteId == -1L) return null
        return getTimetable(favoriteId)
    }
    
    suspend fun getAllActiveTimeTables(): LiveData<List<Timetable>> {
        return planDao.getAllActiveTimetablesLive().map { list ->
            list.map {
                val lessons = it.lessons.toTimetableLesson(5, 7)
                it.timetable.toTimetable(lessons)
            }
        }
    }
    
    suspend fun updateLesson(context: Context, lesson: Timetable.Lesson, timetable: Timetable, day: WeekDay, lessonNr: Int): Long? {
        if (planDao.getPlansComputedWithTimetable(timetable.id).isNotEmpty()) {
            archive(timetable.id)
            val newId = newTimetableId(context)
            val copy = timetable.editable(newId)
            copy[day][lessonNr] = lesson.editable()
            insertTimetable(copy)
            return newId
        }
        planDao.updateTimetableLesson(lesson.toSqlType(day, lessonNr, timetable.id))
        return null
    }
    
    suspend fun updateTimetableName(timetable: Timetable, newName: String) {
        planDao.updateTimetableName(timetable.id, newName)
    }
    
    private suspend fun archive(timetableId: Long) {
        Log.d(TAG, "archiving timetable $timetableId")
        planDao.archiveTimetable(timetableId)
    }
    
    suspend fun noActiveTimetableIsPresent(): Boolean {
        return planDao.getAllActiveTimetables().let {
            it.isEmpty()
        }
    }
    
    companion object {
        fun create(context: Context): PlanRepository {
            val planDao = PlanRoomDatabase.getDatabase(context).planDao()
            val verPlanService = VolleyVerPlanService(context)
            return PlanRepository(planDao, verPlanService)
        }
    
    
        private const val TAG = "PlanRepository"
    }
}