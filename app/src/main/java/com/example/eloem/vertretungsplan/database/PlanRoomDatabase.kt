package com.example.eloem.vertretungsplan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
        SqlVerPlan::class,
        SqlPlan::class,
        SqlPlanRow::class,
        SqlTimetable::class,
        SqlTimetableLesson::class
], version = 6, exportSchema = false)
abstract class PlanRoomDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    
    companion object {
        @Volatile
        private var INSTANCE: PlanRoomDatabase? = null
        
        fun getDatabase(context: Context): PlanRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlanRoomDatabase::class.java,
                        "plan_database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}