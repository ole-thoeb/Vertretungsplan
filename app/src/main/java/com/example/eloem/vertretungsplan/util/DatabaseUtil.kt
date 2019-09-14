package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.helperClasses.database
import org.jetbrains.anko.db.*

fun createTables(db: SQLiteDatabase?){
    db?.createTable(VerPlan.tableName, true,
            VerPlan.columnFetchedTime to INTEGER + PRIMARY_KEY,
            VerPlan.columnGeneralPlanId to INTEGER,
            VerPlan.columnCustomPlanId to INTEGER,
            VerPlan.columnDay to INTEGER,
            VerPlan.columnUpdateTime to INTEGER,
            VerPlan.columnTargetDay to INTEGER,
            VerPlan.columnGrade to TEXT)
    
    db?.createTable(Plan.tableName, true,
            Plan.columnId to INTEGER + PRIMARY_KEY,
            Plan.columnError to TEXT)
    
    db?.createTable(VerRow.tableName, true,
            VerRow.columnLesson to INTEGER,
            VerRow.columnTeacher to TEXT,
            VerRow.columnVerTeacher to TEXT,
            VerRow.columnRoom to TEXT,
            VerRow.columnVerRoom to TEXT,
            VerRow.columnVerText to TEXT,
            VerRow.columnPlanId to INTEGER,
            FOREIGN_KEY(VerRow.columnPlanId, Plan.tableName, Plan.columnId))
    
    db?.createTable(Timetables.tableName, true,
            Timetables.columnId to INTEGER + PRIMARY_KEY,
            Timetables.columnDays to INTEGER,
            Timetables.columnLessons to INTEGER)
    
    db?.createTable(Lessons.tableName, true,
            Lessons.columnSubject to TEXT,
            Lessons.columnTeacher to TEXT,
            Lessons.columnRoom to TEXT,
            Lessons.columnColor to INTEGER,
            Lessons.columnTimetableId to INTEGER,
            Lessons.columnDay to INTEGER,
            Lessons.columnLesson to INTEGER,
            FOREIGN_KEY(Lessons.columnTimetableId, Timetables.tableName, Timetables.columnId))
}

fun dropTables(db: SQLiteDatabase?){
    if (db != null) with(db) {
        dropTable(VerPlan.tableName)
        dropTable(Plan.tableName)
        dropTable(VerRow.tableName)
        dropTable(Timetables.tableName)
        dropTable(Lessons.tableName)
    }
}
/*
fun verPlanParser(ctx: Context) = rowParser{ fetchedTime: Long, gPlanID: Int, cPlanId: Int, day: Int,
                                                 updateTime: Long, tagrgetDay: Long, grade: String ->
    
    Vertretungsplan(fetchedTime, getPlan(ctx, gPlanID),
            getPlan(ctx, cPlanId), day, updateTime, tagrgetDay ,grade)
}

fun getAllVerPlans(ctx: Context): List<Vertretungsplan> = ctx.database.use {
    
    select(VerPlan.tableName)
            .parseList(verPlanParser(ctx))
}

fun getVerPlan(ctx: Context, fetchedTime: Long): Vertretungsplan = ctx.database.use {
    
    select(VerPlan.tableName)
            .whereArgs("${VerPlan.columnFetchedTime} = {fTime}", "fTime" to fetchedTime)
            .parseSingle(verPlanParser(ctx))
}

fun getLatestVerPlanByGrade(ctx: Context, grade: String = readGrade(ctx)):
        Vertretungsplan = ctx.database.use {
    
    select(VerPlan.tableName)
            .orderBy(VerPlan.columnFetchedTime, SqlOrderDirection.DESC)
            .whereArgs("${VerPlan.columnGrade} = {g}", "g" to grade)
            .limit(1)
            .parseSingle(verPlanParser(ctx))
}

fun insertVertretungdplanIfNew(ctx: Context, verPlan: Vertretungsplan){
    try {
        val lastPlan = getLatestVerPlanByGrade(ctx, verPlan.grade)
        if (!lastPlan.contentEquals(verPlan))
            insertVertretungsplan(ctx, verPlan)
        else{
            ctx.database.use {
                update(VerPlan.tableName,
                        VerPlan.columnFetchedTime to System.currentTimeMillis())
                        .whereArgs("${VerPlan.columnFetchedTime} = {fTime}", "fTime" to lastPlan.fetchedTime)
                        .exec()
            }
        }
    }catch (e: Throwable){
        Log.e("Vertretungsplan", "No Vertretungsplan in Database", e)
        insertVertretungsplan(ctx, verPlan)
    }
}

fun lastUpdate(ctx: Context, grade: String = readGrade(ctx)): Long = ctx.database.use {
    
    val longParser = rowParser { l: Long -> l }
    
    try {
        select(VerPlan.tableName,
                VerPlan.columnFetchedTime)
                .whereArgs("${VerPlan.columnGrade} = {g}", "g" to grade)
                .orderBy(VerPlan.columnFetchedTime, SqlOrderDirection.DESC)
                .limit(1)
                .parseSingle(longParser)
    }catch (e: Throwable){
        0L
    }
}

fun insertVertretungsplan(ctx: Context, verPlan: Vertretungsplan) = ctx.database.use {
    Log.d("Vertretungsplan", "inserting verPlan in DB")
    insert(VerPlan.tableName,
            VerPlan.columnFetchedTime to verPlan.fetchedTime,
            VerPlan.columnGeneralPlanId to verPlan.generalPlan.id,
            VerPlan.columnCustomPlanId to verPlan.customPlan.id,
            VerPlan.columnDay to verPlan.weekDay,
            VerPlan.columnUpdateTime to verPlan.updateTime,
            VerPlan.columnTargetDay to verPlan.targetDay,
            VerPlan.columnGrade to verPlan.grade)
    
    insertPlan(ctx, verPlan.generalPlan)
    insertPlan(ctx, verPlan.customPlan)
}

fun getPlan(ctx: Context, planId: Int): Vertretungsplan.Plan = ctx.database.use {
    
    val planParser = rowParser {id: Int, status: String ->
        Vertretungsplan.Plan(id, getAllRowsFormOnePlan(ctx, id), status)
    }
    
    select(Plan.tableName)
            .whereArgs("${Plan.columnId} = {pId}", "pId" to planId)
            .parseSingle(planParser)
}

fun getAllRowsFormOnePlan(ctx: Context, planId: Int): MutableList<Vertretungsplan.Row>
        = ctx.database.use {
    
    val mRowParser = rowParser { lesson: Int, teacher: String, verTeacher: String, room: String,
                                 verRoom: String, verText: String ->
        
        Vertretungsplan.Row(lesson, teacher, verTeacher, room, verRoom, verText)
    }
    
    select(VerRow.tableName,
            VerRow.columnLesson, VerRow.columnTeacher, VerRow.columnVerTeacher, VerRow.columnRoom,
            VerRow.columnVerRoom, VerRow.columnVerText)
            .whereArgs("${VerRow.columnPlanId} = {planId}", "planId" to planId)
            .parseList(mRowParser)
            .toMutableList()
}

fun insertPlan(ctx: Context, plan: Vertretungsplan.Plan) = ctx.database.use {
    insert(Plan.tableName,
            Plan.columnId to plan.id,
            Plan.columnError to plan.status)
    
    plan.plan.forEach { insertRow(ctx, it, plan.id) }
}

fun insertRow(ctx: Context, row: Vertretungsplan.Row, planId: Int) = ctx.database.use {
    insert(VerRow.tableName,
            VerRow.columnLesson to row.lesson,
            VerRow.columnTeacher to row.teacher,
            VerRow.columnVerTeacher to row.verTeacher,
            VerRow.columnRoom to row.room,
            VerRow.columnVerRoom to row.verRoom,
            VerRow.columnVerText to row.verText,
            VerRow.columnPlanId to planId)
}

fun deleteVertretungsPlan(ctx: Context, verPlan: Vertretungsplan) = ctx.database.use {
    delete(VerPlan.tableName,
            "${VerPlan.columnFetchedTime} = {fetchedTime}", "fetchedTime" to verPlan.fetchedTime)
    
    deletePlan(ctx, verPlan.customPlan)
    deletePlan(ctx, verPlan.generalPlan)
}

fun deletePlan(ctx: Context, plan: Vertretungsplan.Plan) = ctx.database.use {
    delete(Plan.tableName,
            "${Plan.columnId} = {id}", "id" to plan.id)
    
    deleteRows(ctx, plan.id)
}

fun deleteRows(ctx: Context, planId: Int) = ctx.database.use {
    delete(VerRow.tableName,
            "${VerRow.columnPlanId} = {id}", "id" to planId)
}

fun insertTimetable(ctx: Context, timetable: Timetable): Unit = ctx.database.use {
    insert(Timetables.tableName,
            Timetables.columnId to timetable.id,
            Timetables.columnDays to timetable.days,
            Timetables.columnLessons to timetable.lessons)
    
    timetable.table.forEachIndexed { d, day ->
        day.forEachIndexed { l, lesson ->
            insertLesson(ctx, lesson, timetable.id, d, l)
        }
    }
}

fun insertLesson(ctx: Context, lesson: Timetable.Lesson, tId: Int, d: Int, l: Int): Unit = ctx.database.use {
    insert(Lessons.tableName,
            Lessons.columnSubject to lesson.subject,
            Lessons.columnTeacher to lesson.teacher,
            Lessons.columnRoom to lesson.room,
            Lessons.columnColor to lesson.color,
            Lessons.columnTimetableId to tId,
            Lessons.columnDay to d,
            Lessons.columnLesson to l)
}

fun updateLesson(ctx: Context, timetableId: Int, day: Int, lesson: Int, newLesson: Timetable.Lesson) = ctx.database.use {
    update(Lessons.tableName,
            Lessons.columnTeacher to newLesson.teacher,
            Lessons.columnSubject to newLesson.subject,
            Lessons.columnRoom to newLesson.room,
            Lessons.columnColor to newLesson.color)
            .whereArgs("${Lessons.columnTimetableId} = {id} and " +
                    "${Lessons.columnDay} = {day} and ${Lessons.columnLesson} = {lesson}",
                    "id" to timetableId, "day" to day, "lesson" to lesson)
            .exec()
}

fun getLatestTimetable(ctx: Context): Timetable = try {
    ctx.database.use {
    
    val mParser = rowParser { id: Int, days: Int, lessons: Int ->
        Timetable(id, days, lessons, getLessonFromOnePlan(ctx, id, days, lessons))
    }
    
    select(Timetables.tableName,
            Timetables.columnId,
            Timetables.columnDays,
            Timetables.columnLessons)
            .orderBy(Timetables.columnId, SqlOrderDirection.DESC)
            .limit(1)
            .parseSingle(mParser)
    }
    
}catch (e: Throwable){
    Log.e("Vertretungsplan", "No Timetable in Database", e)
    val t = Timetable.newDefaultInstance(ctx)
    insertTimetable(ctx, t)
    
    t
}

fun getLessonFromOnePlan(ctx: Context, planId: Int, days: Int, lessons: Int):
        MutableList<MutableList<Timetable.Lesson>> = ctx.database.use {
    
    val lessonList = MutableList(days) { MutableList(lessons) { Timetable.Lesson() }}
    
    val mLessonParser = rowParser { subject: String, teacher: String, room: String, color: Int,
                                    day: Int, lesson: Int ->
        lessonList[day][lesson] = Timetable.Lesson(subject, teacher, room, color)
    }
    
    select(Lessons.tableName,
            Lessons.columnSubject,
            Lessons.columnTeacher,
            Lessons.columnRoom,
            Lessons.columnColor,
            Lessons.columnDay,
            Lessons.columnLesson)
            .whereArgs("${Lessons.columnTimetableId} = {id}", "id" to planId)
            .parseList(mLessonParser)
    
    lessonList
}*/