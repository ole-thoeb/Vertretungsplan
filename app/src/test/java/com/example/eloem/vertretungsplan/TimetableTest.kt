package com.example.eloem.vertretungsplan

import com.example.eloem.vertretungsplan.helperClasses.JustTime
import com.example.eloem.vertretungsplan.helperClasses.Timetable19_20
import com.example.eloem.vertretungsplan.util.WeekDay
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.asin

class TimetableTest {
    @Test
    fun endOfDayTest() {
        val timetable = Timetable19_20.newDefaultInstance(1)
        timetable[WeekDay.MONDAY][6].apply {
            subject = "Deutsch"
            teacher = "Klee"
            color = 0xFFAF00FF.toInt()
        }
        assertEquals(JustTime(16, 15), timetable.endOfDay(WeekDay.MONDAY))
    }
}