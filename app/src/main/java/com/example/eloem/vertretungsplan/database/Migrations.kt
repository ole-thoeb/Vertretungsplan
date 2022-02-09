package com.example.eloem.vertretungsplan.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_6_TO_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE SqlPlanRow RENAME COLUMN verRoom to type;")
        }
    }
    
    val MIGRATION_7_TO_9 = object : Migration(7, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE SqlVerPlan;")
            database.execSQL("CREATE TABLE IF NOT EXISTS `SqlVerPlan` (`id` INTEGER NOT NULL, `fetchedTime` INTEGER NOT NULL, `generalPlan` INTEGER NOT NULL, `customPlan` INTEGER NOT NULL, `weekDay` INTEGER NOT NULL, `updateTime` INTEGER NOT NULL, `targetDay` INTEGER NOT NULL, `grade` INTEGER NOT NULL, `computedWith` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`customPlan`) REFERENCES `SqlPlan`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`generalPlan`) REFERENCES `SqlPlan`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`computedWith`) REFERENCES `SqlTimetable`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
            
            database.execSQL("DROP TABLE SqlPlanRow;")
            database.execSQL("CREATE TABLE IF NOT EXISTS `SqlPlanRow` (`lesson` INTEGER NOT NULL, `teacher` TEXT NOT NULL, `verTeacher` TEXT NOT NULL, `room` TEXT NOT NULL, `type` TEXT NOT NULL, `verText` TEXT NOT NULL, `planId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT, FOREIGN KEY(`planId`) REFERENCES `SqlPlan`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
            
            database.execSQL("ALTER TABLE SqlTimetableLesson RENAME TO SqlTimetableLessonOld;")
            database.execSQL("CREATE TABLE IF NOT EXISTS `SqlTimetableLesson` (`subject` TEXT NOT NULL, `teacher` TEXT NOT NULL, `room` TEXT NOT NULL, `color` INTEGER NOT NULL, `timetableId` INTEGER NOT NULL, `day` INTEGER NOT NULL, `lesson` INTEGER NOT NULL, PRIMARY KEY(`day`, `lesson`, `timetableId`), FOREIGN KEY(`timetableId`) REFERENCES `SqlTimetable`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
            database.execSQL("INSERT INTO SqlTimetableLesson (subject, teacher, room, color, timetableId, day, lesson) SELECT * FROM SqlTimetableLessonOld;")
            database.execSQL("DROP TABLE SqlTimetableLessonOld;")
        }
    }
}