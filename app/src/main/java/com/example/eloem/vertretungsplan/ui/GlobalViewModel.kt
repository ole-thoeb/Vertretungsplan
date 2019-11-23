package com.example.eloem.vertretungsplan.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.eloem.vertretungsplan.database.PlanRepository
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.network.ResponseModel
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(application: Application): AndroidViewModel(application), ContextOwner {
    
    private val TAG = "GlobalViewModel"
    private val repository: PlanRepository = PlanRepository.create(getApplication())
    
    override val ctx: Context get() = getApplication()
    
    private var favTimetableChanged = false
    
    private val contextIO = viewModelScope.coroutineContext + Dispatchers.IO
    
    suspend fun currentPlan(forceRefresh: Boolean): Result<Vertretungsplan, ResponseModel.Error> = withContext(contextIO) {
        generalPreferences {
            if (forceRefresh || favTimetableChanged) {
                Log.d(TAG, "updating because it was forced")
                favTimetableChanged = false
                repository.updateVerPlan(grade, ctx)
            } else {
                repository.currentVerPlan(grade, ctx)
            }
        }
    }
    
    private val livePlans = Vertretungsplan.Grade.values().map {
        liveData(Dispatchers.IO) {
            emitSource(repository.getLatestLocalPlanLive(it))
        }
    }
    
    fun currentLocalPlan(grade: Vertretungsplan.Grade): LiveData<Vertretungsplan?> = livePlans[grade.ordinal]
    
    suspend fun getVerPlan(verPlanId: Long) = withContext(contextIO) {
        repository.getVerPlan(verPlanId)
    }
    
    val timetables: LiveData<List<Timetable>> = liveData(Dispatchers.IO) {
        emitSource(repository.getAllActiveTimeTables())
    }
    
    private val timetableIdMap: MutableMap<Long, Long> = mutableMapOf()
    
    fun getTimetableLive(id: Long): LiveData<Timetable?> = timetables.map {
        val updatedId = timetableIdMap.getOrElse(id) { id }
        it.find { timetable ->
            timetable.id == updatedId
        }.onNull { Log.e(TAG, "timetable with id $id not found") }
    }
    
    suspend fun getTimetable(id: Long): Timetable? = withContext(contextIO) {
        repository.getTimetable(id)
    }
    
    fun insertTimetable(timetable: Timetable) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "inserting timetable with id ${timetable.id}")
        if (repository.noActiveTimetableIsPresent()) {
            generalPreferences { favoriteTimetableId = timetable.id }
        }
        repository.insertTimetable(timetable)
    }
    
    fun deleteTimetable(timetable: Timetable) = viewModelScope.launch(Dispatchers.IO) {
        generalPreferences {
            if (favoriteTimetableId == timetable.id) {
                favTimetableChanged = true
            }
        }
        repository.deleteTimetable(timetable)
    }
    
    fun updateTimetableLesson(lesson: Timetable.Lesson, day: WeekDay, lessonNr: Int, timetable: Timetable) = viewModelScope.launch(Dispatchers.IO) {
        generalPreferences {
            if (favoriteTimetableId == timetable.id) {
                favTimetableChanged = true
            }
            val newId = repository.updateLesson(ctx, lesson, timetable, day, lessonNr) ?: return@launch
            timetableIdMap[timetable.id] = newId
            if (favoriteTimetableId == timetable.id) {
                favoriteTimetableId = newId
            }
        }
    }
    
    fun updateTimetableName(timetable: Timetable, newName: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTimetableName(timetable, newName)
    }
    
//    @WorkerThread
//    fun updateCurrentPlan() = viewModelScope.launch(Dispatchers.IO) {
//        Log.d(TAG, "updating plan")
//        val verPlan = repository.updateVerPlan(readGrade(getApplication()), getApplication())
//        currentPlanLiveData.postValue(verPlan)
//    }
//
//    @WorkerThread
//    fun updateCurrentPlanLazy() = viewModelScope.launch(Dispatchers.IO) {
//        val verPlan = repository.currentVerPlan(readGrade(getApplication()), getApplication())
//        currentPlanLiveData.postValue(verPlan)
//    }
}

