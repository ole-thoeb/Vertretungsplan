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
        }
    }
}