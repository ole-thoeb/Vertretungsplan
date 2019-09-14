package com.example.eloem.vertretungsplan.util

object VerPlan{
    const val tableName = "tableVertretungsplan"
    const val columnFetchedTime = "fetchedTime"
    const val columnGeneralPlanId = "gPlanId"
    const val columnCustomPlanId = "cPlanId"
    const val columnDay = "day"
    const val columnUpdateTime = "updateTime"
    const val columnTargetDay = "targetDay"
    const val columnGrade = "grade"
}

object Plan{
    const val tableName = "tablePlan"
    const val columnId = "id"
    const val columnError = "status"
}

object VerRow{
    const val tableName = "tableRow"
    const val columnLesson = "lesson"
    const val columnTeacher = "teacher"
    const val columnVerTeacher = "verTeacher"
    const val columnRoom = "room"
    const val columnVerRoom = "verRoom"
    const val columnVerText = "verText"
    const val columnPlanId = "planId"
}

object Timetables{
    const val tableName = "tableTimetable"
    const val columnId = "id"
    const val columnDays = "days"
    const val columnLessons = "lessons"
}

object Lessons{
    const val tableName = "tableLesson"
    const val columnSubject = "subject"
    const val columnTeacher = "teacher"
    const val columnRoom = "room"
    const val columnColor = "color"
    
    const val columnTimetableId = "tid"
    const val columnDay = "day"
    const val columnLesson = "lesson"
}