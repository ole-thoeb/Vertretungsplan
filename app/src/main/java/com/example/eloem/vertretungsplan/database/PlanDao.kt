package com.example.eloem.vertretungsplan.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan

@Dao
interface PlanDao {

    @Transaction
    suspend fun insertVertretungsplan(verPlan: Vertretungsplan) {
        Log.d(TAG, "inserting Vertretungsplan. id = ${verPlan.id} grade = ${verPlan.grade}")
        insertVerPlan(verPlan.toSqlType())
        val (gPlan, gRows) = verPlan.generalPlan.toSqlType()
        val (cPlan, cRows) = verPlan.customPlan.toSqlType()
        insertPlan(gPlan)
        insertPlan(cPlan)
    
        insertPlanRows(gRows)
        insertPlanRows(cRows)
    }
    
    @Insert
    suspend fun insertVerPlan(verPlan: SqlVerPlan)
    
    @Insert
    suspend fun insertPlan(plan: SqlPlan)
    
    @Insert
    suspend fun insertPlanRows(rows: List<SqlPlanRow>)
    
    @Query("UPDATE SqlVerPlan SET fetchedTime = :newFetchedTime WHERE id = :id")
    suspend fun updateFetchedTime(id: Long, newFetchedTime: Long)
    
    @Query("SELECT * FROM SqlVerPlan")
    fun getAllVerPlans(): LiveData<List<SqlVerPlan>>
    
    @Transaction
    @Query("SELECT * FROM SqlPlan")
    fun getAllPlans(): LiveData<List<PlanWithRows>>
    
    @Query("SELECT * FROM SqlVerPlan WHERE grade IN (:grades) AND fetchedTime > :notBefore")
    fun getPlansWith(grades: List<Int>, notBefore: Long): LiveData<List<SqlVerPlan>>
    
    @Query("SELECT * FROM SqlVerPlan WHERE grade = :grade ORDER BY fetchedTime DESC LIMIT 1")
    fun getLatestVerPlanLive(grade: Int) : LiveData<SqlVerPlan?>
    
    @Query("SELECT * FROM SqlVerPlan WHERE grade = :grade ORDER BY fetchedTime DESC LIMIT 1")
    suspend fun getLatestVerPlan(grade: Int) : SqlVerPlan?
    
    @Query("SELECT * FROM SqlVerPlan WHERE id = :verPlanId")
    suspend fun getVerPlan(verPlanId: Long): SqlVerPlan?
    
    //TODO make suspend -> split into single queries and map in a transaction
    @Transaction
    @Query("SELECT * FROM SqlPlan WHERE id = :id")
    fun getPlan(id: Long): PlanWithRows
    
    @Query("SELECT fetchedTime FROM SqlVerPlan WHERE grade = :grade ORDER BY fetchedTime DESC LIMIT 1")
    suspend fun lastUpdateTime(grade: Int): Long
    
    @Transaction
    suspend fun deleteVerPlan(verPlan: Vertretungsplan) {
        deletePlan(verPlan.customPlan.id)
        deletePlanRows(verPlan.customPlan.id)
        
        deletePlan(verPlan.generalPlan.id)
        deletePlanRows(verPlan.generalPlan.id)
    
        deleteVerPlan(verPlan.id)
    }
    
    @Query("DELETE FROM SqlVerPlan WHERE id = :verPlanId")
    suspend fun deleteVerPlan(verPlanId: Long)
    
    @Query("DELETE FROM SqlPlan WHERE id = :planId")
    suspend fun deletePlan(planId: Long)
    
    @Query("DELETE FROM SqlPlanRow WHERE planId = :planId")
    suspend fun deletePlanRows(planId: Long)
    
    
    
    /**
     * ################ TIMETABLE ############################
     * */
    
    @Transaction
    suspend fun insertTimetable(timetable: Timetable) {
        insertTimetable(timetable.toSqlType())
        insertTimetableLessons(timetable.lessonsToSqlType())
    }
    
    @Insert
    suspend fun insertTimetable(timetable: SqlTimetable)
    
    @Insert
    suspend fun insertTimetableLessons(lessons: List<SqlTimetableLesson>)
    
    @Update
    suspend fun updateTimetableLesson(lesson: SqlTimetableLesson)
    
    @Query("UPDATE SqlTimetable SET name = :newName WHERE id =:id")
    suspend fun updateTimetableName(id: Long, newName: String)
    
    @Query("DELETE FROM SqlTimetable WHERE id = :id")
    suspend fun deleteTimetable(id: Long)
    
    @Query("DELETE FROM SqlTimetableLesson WHERE timetableId = :timetableId")
    suspend fun deleteLessonsFromTimetable(timetableId: Long)
    
    @Query("SELECT * FROM SqlTimetable WHERE id = :id")
    suspend fun getTimetable(id: Long): SqlTimetable?
    
    @Query("SELECT * FROM SqlTimetableLesson WHERE timetableId = :timetableId")
    suspend fun getLessonsToTimetable(timetableId: Long): List<SqlTimetableLesson>
    
    @Transaction
    @Query("SELECT * FROM SqlTimetable WHERE NOT isArchived")
    fun getAllActiveTimetablesLive(): LiveData<List<TimetableWithLessons>>
    
    @Query("SELECT * FROM SqlTimetable WHERE NOT isArchived")
    suspend fun getAllActiveTimetables(): List<SqlTimetable>
    
    @Query("SELECT * FROM SqlTimetableLesson WHERE timetableId = :timetableId")
    fun getLessonsToTimetableLive(timetableId: Long): LiveData<List<SqlTimetableLesson>>
    
    @Query("SELECT * FROM SqlVerPlan WHERE computedWith = :timetableId")
    suspend fun getPlansComputedWithTimetable(timetableId: Long): List<SqlVerPlan>
    
    @Query("UPDATE SqlTimetable SET isArchived = 1 WHERE id = :timetableId")
    suspend fun archiveTimetable(timetableId: Long)
    
    companion object {
        private const val TAG = "PlanDao"
    }
}