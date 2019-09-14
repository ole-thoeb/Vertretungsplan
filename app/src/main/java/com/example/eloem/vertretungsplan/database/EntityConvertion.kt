package com.example.eloem.vertretungsplan.database

import com.example.eloem.vertretungsplan.helperClasses.EditTimetable
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Timetable19_20
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.WeekDay
import kotlinx.coroutines.internal.LockFreeLinkedListNode

fun PlanWithRows.toPlan() : Vertretungsplan.Plan {
    val planRows = rows!!
            .map { Vertretungsplan.Row(it.lesson, it.teacher, it.verTeacher, it.room, it.verRoom, it.verText) }
    
    return Vertretungsplan.Plan(plan!!.id, planRows, Vertretungsplan.PlanStatus.values()[plan!!.status])
}

fun SqlVerPlan.toVerPlan(gPlan: Vertretungsplan.Plan, cPlan: Vertretungsplan.Plan): Vertretungsplan {
    return Vertretungsplan(
            id,
            fetchedTime,
            gPlan,
            cPlan,
            WeekDay.values()[weekDay],
            updateTime,
            targetDay,
            Vertretungsplan.Grade.values()[grade],
            computedWith
    )
}

fun Vertretungsplan.toSqlType(): SqlVerPlan {
    return SqlVerPlan(
            id,
            fetchedTime,
            generalPlan.id,
            customPlan.id,
            weekDay.ordinal,
            updateTime,
            targetDay,
            grade.ordinal,
            computedWith
    )
}

fun Vertretungsplan.Plan.toSqlType(): Pair<SqlPlan, List<SqlPlanRow>> {
    return SqlPlan(id, status.ordinal) to plan.map {
        SqlPlanRow(it.lesson, it.teacher, it.verTeacher, it.room, it.verRoom, it.verText, id)
    }
}

fun Timetable.toSqlType(): SqlTimetable {
    return SqlTimetable(id, name, lastChange, days, lessons, isArchived)
}

fun Timetable.lessonsToSqlType(): List<SqlTimetableLesson> {
    return flatMap { day ->
        day.mapIndexed { index, lesson ->
            lesson.toSqlType(day.weekDay, index, id)
        }
    }
}

fun SqlTimetable.toTimetable(table: List<EditTimetable.EditDay>): Timetable {
    return Timetable19_20(id, name, lastChange, isArchived, table as List<Timetable19_20.Day>)
}

fun List<SqlTimetableLesson>.toTimetableLesson(days: Int, lessons: Int): List<EditTimetable.EditDay> {
    val table = List(days) { day ->
        Timetable19_20.Day(WeekDay.values()[day], MutableList(lessons) {
            Timetable19_20.Lesson()
        })
    }
    forEach {
        table[it.day][it.lesson].apply {
            subject = it.subject
            teacher = it.teacher
            room = it.room
            color = it.color
        }
    }
    return table
}

fun Timetable.Lesson.toSqlType(day: WeekDay, lessonNr: Int, tTableId: Long): SqlTimetableLesson {
    return SqlTimetableLesson(subject, teacher, room, color, tTableId, day.ordinal, lessonNr)
}