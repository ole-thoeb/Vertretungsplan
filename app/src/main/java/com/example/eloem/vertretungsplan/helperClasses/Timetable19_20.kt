package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.example.eloem.vertretungsplan.util.WeekDay
import com.example.eloem.vertretungsplan.util.flatIterable
import com.example.eloem.vertretungsplan.util.newTimetableId
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex

class Timetable19_20(
        override val id: Long,
        override var name: String,
        override var lastChange: Long,
        override var isArchived: Boolean,
        val table: List<Day> = List(DAYS){ Day(WeekDay.values()[it], MutableList(LESSONS) { Lesson() }) }
) : EditTimetable {
    
    override val days: Int = DAYS
    override val lessons = LESSONS
    
    data class Lesson(
            override var subject: String = "",
            override var teacher: String = "",
            override var room: String = "",
            @ColorInt override var color: Int = DEFAULT_COLOR
    ) : EditTimetable.EditLesson{
    
        companion object {
            const val DEFAULT_COLOR = Color.TRANSPARENT
        }
    }
    
    data class Day(override val weekDay: WeekDay, val lessons: MutableList<EditTimetable.EditLesson>) : EditTimetable.EditDay {
        override operator fun get(lesson: Int): EditTimetable.EditLesson = lessons[lesson]
        override fun set(lessonNr: Int, lesson: EditTimetable.EditLesson) {
            lessons[lessonNr] = lesson
        }
        override operator fun iterator(): Iterator<EditTimetable.EditLesson> = lessons.iterator()
    }
    
    override val distinctLessons: List<Timetable.Lesson>
        get() = table.flatIterable().distinctBy { it.subject }
    
    override fun endOfDay(weekDay: WeekDay): JustTime{
        table[weekDay.ordinal].toList().forEachReversedWithIndex { i, lesson ->
            if(lesson.subject.isNotBlank()){
                return LESSON_TIMES[weekDay.ordinal][i].endInclusive
            }
        }
        return LESSON_TIMES[weekDay.ordinal][0].endInclusive
    }
    
    override fun get(day: Int): EditTimetable.EditDay = table[day]
    override fun get(weekDay: WeekDay): EditTimetable.EditDay = get(weekDay.ordinal)
    override fun iterator(): Iterator<EditTimetable.EditDay> = table.iterator()
    
    override fun editable(newId: Long): EditTimetable {
        return Timetable19_20(newId, name, lastChange, isArchived, table.map { day ->
            day.copy(lessons = day.lessons.map { lesson ->
                lesson.editable()
            }.toMutableList())
        })
    }
    
    override val lessonTimes: List<List<ClosedRange<JustTime>>> get() = LESSON_TIMES
    
    companion object {
        fun newDefaultInstance(id: Long): EditTimetable = Timetable19_20(
                id,
                "",
                System.currentTimeMillis(),
                false
        )
        
        const val DAYS = 5
        const val LESSONS = 7
    
        private val FILL_TIME = JustTime(0, 0)..JustTime(0, 0)
        //after http://www.europaschule-bornheim.de/contao/index.php/stundenplan.html
        val LESSON_TIMES = listOf(
                listOf( //monday
                        JustTime(7, 55)..JustTime(9, 0),
                        JustTime(9, 5)..JustTime(10, 10),
                        JustTime(10, 35)..JustTime(11, 40),
                        JustTime(11, 45)..JustTime(12, 50),
                        JustTime(12, 50)..JustTime(14, 0),
                        JustTime(14, 0)..JustTime(15, 5),
                        JustTime(15, 10)..JustTime(16, 15)
                ),
                listOf( //tuesday
                        JustTime(7, 55)..JustTime(9, 0),
                        JustTime(9, 5)..JustTime(10, 10),
                        JustTime(10, 30)..JustTime(11, 35),
                        JustTime(11, 40)..JustTime(12, 45),
                        JustTime(12, 50)..JustTime(13, 25),
                        FILL_TIME,
                        FILL_TIME
                ),
                listOf( //wednesday
                        JustTime(7, 55)..JustTime(9, 0),
                        JustTime(9, 5)..JustTime(10, 10),
                        JustTime(10, 35)..JustTime(11, 40),
                        JustTime(11, 45)..JustTime(12, 50),
                        JustTime(12, 50)..JustTime(14, 0),
                        JustTime(14, 0)..JustTime(15, 5),
                        JustTime(15, 10)..JustTime(15, 40)
                ),
                listOf( //thursday
                        JustTime(7, 55)..JustTime(9, 0),
                        JustTime(9, 5)..JustTime(10, 10),
                        JustTime(10, 35)..JustTime(11, 40),
                        JustTime(11, 45)..JustTime(12, 50),
                        JustTime(12, 50)..JustTime(14, 0),
                        JustTime(14, 0)..JustTime(15, 5),
                        JustTime(15, 10)..JustTime(15, 40)
                ),
                listOf( //friday
                        JustTime(7, 55)..JustTime(9, 0),
                        JustTime(9, 5)..JustTime(10, 10),
                        JustTime(10, 30)..JustTime(11, 35),
                        JustTime(11, 40)..JustTime(12, 45),
                        JustTime(12, 45)..JustTime(13, 15),
                        JustTime(13, 15)..JustTime(14, 20),
                        JustTime(14, 25)..JustTime(15, 30)
                )
        )
    }
}

fun Timetable.Lesson.editable(): EditTimetable.EditLesson = Timetable19_20.Lesson(subject, teacher, room, color)