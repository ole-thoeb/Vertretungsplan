package com.example.eloem.vertretungsplan.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_6_TO_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE SqlPlanRow RENAME COLUMN verRoom to type;")
        }
    }
}