package com.example.eloem.vertretungsplan.util

object VertretungsPlan{
    const val tableName = "tableVertretungsplan"
    const val columnFetchedTime = "fetchedTime"
    const val columnGeneralPlanId = "gPlanId"
    const val columnCustomPlanId = "cPlanId"
    const val columnDay = "day"
    const val columnUpdateTime = "updateTime"
}

object Plan{
    const val tableName = "tablePlan"
    const val columnId = "id"
    const val columnError = "error"
}

object Row{
    const val tableName = "tableRow"
    const val columnLesson = "lesson"
    const val columnTeacher = "teacher"
    const val columnVerTeacher = "verTeacher"
    const val columnRoom = "room"
    const val columnVerRoom = "verRoom"
    const val columnVerText = "verText"
    const val columnPlanId = "planId"
}