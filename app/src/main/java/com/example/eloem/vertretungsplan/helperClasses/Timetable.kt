package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import androidx.annotation.ColorInt
import com.example.eloem.vertretungsplan.util.WeekDay
import com.example.eloem.vertretungsplan.util.newTimetableId

interface Timetable : Iterable<Timetable.Day> {
    val id: Long
    val name: String
    val lastChange: Long
    val isArchived: Boolean
    
    val days: Int
    
    /**
     * max lessons
     */
    val lessons: Int
    
    fun endOfDay(weekDay: WeekDay): JustTime
    
    val distinctLessons: List<Lesson>
    
    operator fun get(weekDay: WeekDay): Day
    operator fun get(day: Int): Day
    override operator fun iterator(): Iterator<Day>
    
    fun editable(newId: Long): EditTimetable
    
    interface Day : Iterable<Lesson> {
        val weekDay: WeekDay
        operator fun get(lesson: Int): Lesson
        override operator fun iterator(): Iterator<Lesson>
    }
    
    interface Lesson {
        val subject: String
        val teacher: String
        val room: String
        val color: Int
        
        companion object {
            @ColorInt
            const val DEFAULT_COLOR = 0
        }
    }
    
    companion object {
        fun newDefaultInstance(context: Context) = newDefaultInstance(newTimetableId(context))
        
        fun newDefaultInstance(id: Long) = Timetable19_20.newDefaultInstance(id)
    }
}

interface EditTimetable : Timetable {
    
    override var name: String
    override var lastChange: Long
    override var isArchived: Boolean
    
    override operator fun get(day: Int): EditDay
    override operator fun get(weekDay: WeekDay): EditDay
    override operator fun iterator(): Iterator<EditDay>
    
    interface EditDay : Timetable.Day {
        override operator fun get(lesson: Int): EditLesson
        operator fun set(lessonNr: Int, lesson: EditLesson)
        override operator fun iterator(): Iterator<EditLesson>
    }
    
    interface EditLesson : Timetable.Lesson {
        override var subject: String
        override var teacher: String
        override var room: String
        override var color: Int
    }
}