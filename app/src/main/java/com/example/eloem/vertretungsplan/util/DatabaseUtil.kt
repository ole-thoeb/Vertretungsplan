package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.helperClasses.database
import org.jetbrains.anko.db.*

fun createTables(db: SQLiteDatabase?){
    db?.createTable(VertretungsPlan.tableName, true,
            VertretungsPlan.columnFetchedTime to INTEGER + UNIQUE,
            VertretungsPlan.columnGeneralPlanId to INTEGER,
            VertretungsPlan.columnCustomPlanId to INTEGER,
            VertretungsPlan.columnDay to INTEGER,
            VertretungsPlan.columnUpdateTime to INTEGER)
    
    db?.createTable(Plan.tableName, true,
            Plan.columnId to INTEGER + UNIQUE + PRIMARY_KEY,
            Plan.columnError to TEXT)
    
    db?.createTable(Row.tableName, true,
            Row.columnLesson to INTEGER,
            Row.columnTeacher to TEXT,
            Row.columnVerTeacher to TEXT,
            Row.columnRoom to TEXT,
            Row.columnVerRoom to TEXT,
            Row.columnVerText to TEXT,
            Row.columnPlanId to INTEGER)
}

fun dropTables(db: SQLiteDatabase?){
    
    db?.dropTable(VertretungsPlan.tableName)
    db?.dropTable(Plan.tableName)
    db?.dropTable(Row.tableName)
}

fun getAllVerPlans(context: Context): List<Vertretungsplan> = context.database.use {
    
    val vPlanParser = rowParser{fetchedTime: Long, gPlanID: Int, cPlanId: Int, day: Int,
                                updateTime: Long ->
        
        Vertretungsplan(fetchedTime, getPlan(context, gPlanID),
                getPlan(context, cPlanId), day, updateTime)
    }
    
    select(VertretungsPlan.tableName)
            .parseList(vPlanParser)
}

fun getLatestPlan(context: Context): Vertretungsplan = context.database.use {
    
    val vPlanParser = rowParser{fetchedTime: Long, gPlanID: Int, cPlanId: Int, day: Int,
                                updateTime: Long ->
        
        Vertretungsplan(fetchedTime, getPlan(context, gPlanID),
                getPlan(context, cPlanId), day, updateTime)
    }
    
    select(VertretungsPlan.tableName)
            .orderBy(VertretungsPlan.columnFetchedTime, SqlOrderDirection.DESC)
            .limit(1)
            .parseSingle(vPlanParser)
}

fun getPlan(context: Context, planId: Int): Vertretungsplan.Plan = context.database.use {
    
     val planParser = rowParser {id: Int, error: String ->
         Vertretungsplan.Plan(id, getAllRowsFormOnePlan(context, id), error)
     }
    
    select(Plan.tableName)
            .whereArgs("${Plan.columnId} = {pId}", "pId" to planId)
            .parseSingle(planParser)
}

fun getAllRowsFormOnePlan(context: Context, planId: Int): MutableList<Vertretungsplan.Row>
        = context.database.use {
    
    val mRowParser = rowParser { lesson: Int, teacher: String, verTeacher: String, room: String,
                                 verRoom: String, verText: String ->
        
       Vertretungsplan.Row(lesson, teacher, verTeacher, room, verRoom, verText)
    }
    
    select(Row.tableName)
            .whereArgs("${Row.columnPlanId} = {planId}", "planId" to planId)
            .parseList(mRowParser)
            .toMutableList()
}

fun insertVertretungsplan(context: Context, verPlan: Vertretungsplan) = context.database.use {
    insert(VertretungsPlan.tableName,
            VertretungsPlan.columnFetchedTime to verPlan.fetchedTime,
            VertretungsPlan.columnGeneralPlanId to verPlan.generalPlan.id,
            VertretungsPlan.columnCustomPlanId to verPlan.customPlan.id,
            VertretungsPlan.columnDay to verPlan.day,
            VertretungsPlan.columnUpdateTime to verPlan.updateTime)
    
    insertPlan(context, verPlan.generalPlan)
    insertPlan(context, verPlan.customPlan)
}

fun insertPlan(context: Context, plan: Vertretungsplan.Plan) = context.database.use {
    insert(Plan.tableName,
            Plan.columnId to plan.id,
            Plan.columnError to plan.error)
    
    plan.plan.forEach { insertRow(context, it, plan.id) }
}

fun insertRow(context: Context, row: Vertretungsplan.Row, planId: Int) = context.database.use {
    insert(Row.tableName,
            Row.columnLesson to row.lesson,
            Row.columnTeacher to row.teacher,
            Row.columnVerTeacher to row.verTeacher,
            Row.columnRoom to row.room,
            Row.columnVerRoom to row.verRoom,
            Row.columnVerText to Row.columnVerText,
            Row.columnPlanId to planId)
}