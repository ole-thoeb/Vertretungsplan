package com.example.eloem.vertretungsplan
//
//import androidx.room.AutoMigration
//import androidx.room.testing.MigrationTestHelper
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
////class MigrationTest {
////    private val TEST_DB = "migration-test"
////
////    @get:Rule
////    val helper: MigrationTestHelper = MigrationTestHelper(
////        InstrumentationRegistry.getInstrumentation(),
////        AppDatabase::class.java.canonicalName,
////        FrameworkSQLiteOpenHelperFactory()
////    )
////
////    @Test
////    fun migrateAll() {
////        // Create earliest version of the database.
////        helper.createDatabase(TEST_DB, 7).apply {
////            close()
////        }
////        helper.runMigrationsAndValidate(TEST_DB, 9, false, AutoMigration(7, 9))
////    }
////}