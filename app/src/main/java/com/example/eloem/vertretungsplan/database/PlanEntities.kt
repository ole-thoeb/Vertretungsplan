package com.example.eloem.vertretungsplan.database

import androidx.room.*
import javax.security.auth.Subject

@Entity(primaryKeys = ["id"])
@ForeignKey(entity = SqlPlan::class, parentColumns = ["id, id"], childColumns = ["generalPlan", "customPlan"])
class SqlVerPlan(
        val id: Long,
        val fetchedTime: Long ,
        val generalPlan: Long,
        val customPlan: Long,
        val weekDay: Int,
        val updateTime: Long,
        val targetDay: Long,
        val grade: Int,
        @ForeignKey(entity = SqlTimetable::class, parentColumns = ["id"], childColumns = ["computedWith"])
        val computedWith: Long
)

@Entity(primaryKeys = ["id"])
class SqlPlan(
        val id: Long,
        val status: Int
)

@Entity
@ForeignKey(entity = SqlPlan::class, parentColumns = ["id"], childColumns = ["planId"])
class SqlPlanRow(
        val lesson: Int,
        val teacher: String,
        val verTeacher: String,
        val room: String,
        val verRoom: String,
        val verText: String,
        val planId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}

@Entity(primaryKeys = ["id"])
class SqlTimetable(
        val id: Long,
        val name: String,
        val lastChange: Long,
        val days: Int,
        val lessons: Int,
        val isArchived: Boolean
)

@Entity(primaryKeys = ["day", "lesson", "timetableId"])
@ForeignKey(entity = SqlTimetable::class, parentColumns = ["id"], childColumns = ["timetableId"])
class SqlTimetableLesson(
        val subject: String,
        val teacher: String,
        val room: String,
        val color: Int,
        val timetableId: Long,
        val day: Int,
        val lesson: Int
)

class PlanWithRows {
    @Embedded
    var plan: SqlPlan? = null
    @Relation(parentColumn = "id", entityColumn = "planId")
    var rows: List<SqlPlanRow>? = null
}

class TimetableWithLessons {
    @Embedded
    lateinit var timetable: SqlTimetable
    @Relation(parentColumn = "id", entityColumn = "timetableId")
    lateinit var lessons: List<SqlTimetableLesson>
}