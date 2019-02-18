package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import com.example.eloem.vertretungsplan.util.newTimetableId
import org.jetbrains.anko.collections.forEachReversedWithIndex
import kotlin.collections.ArrayList

class Timetable(val id: Int, val days: Int, val lessons: Int,
                val table: MutableList<MutableList<Lesson>> =
                        List(days){ List(lessons) { Lesson() }.toMutableList()}.toMutableList()) {
    
    data class Lesson(val subject: String = "", val teacher: String = "", val room: String = "",
                      val color: Int = DEFAULT_COLOR){
        
        companion object {
            const val DEFAULT_COLOR = 0xFFFAFAFA.toInt()
        }
    }
    
    
    /*init {
        clear()
        /*this.endOfLessons.add(Date(0, 0, 0, 8, 40))
        this.endOfLessons.add(Date(0, 0, 0, 9, 30))
        this.endOfLessons.add(Date(0, 0, 0, 10, 35))
        this.endOfLessons.add(Date(0, 0, 0, 11, 25))
        this.endOfLessons.add(Date(0, 0, 0, 12, 25))
        this.endOfLessons.add(Date(0, 0, 0, 13, 15))
        this.endOfLessons.add(Date(0, 0, 0, 14, 10))
        this.endOfLessons.add(Date(0, 0, 0, 15, 0))
        this.endOfLessons.add(Date(0, 0, 0, 15, 50))
        this.endOfLessons.add(Date(0, 0, 0, 16, 40))
        this.endOfLessons.add(c)*/
    }*/
    
    /*val distinctLessons: List<Lesson> get() {
        val list = ArrayList<Lesson>()
        val alreadyInList = ArrayList<String>()
        alreadyInList.add("")
        
        for (day in table) {
            for (lesson in list) {
                if (lesson.subject !in alreadyInList) list.add(lesson)
            }
        }
        return list
    }*/
    
    val distinctLessons: List<Lesson> get() = table.flatten().distinctBy { it.subject }
    
    fun endOfDay(day: Int): JustTime{
        val endOfLessons = arrayOf(JustTime(8, 40),
                JustTime(9, 30),
                JustTime(10, 35),
                JustTime(11, 25),
                JustTime(12, 25),
                JustTime(13, 15),
                JustTime(14, 10),
                JustTime(15, 0),
                JustTime(15, 50),
                JustTime(16, 40),
                JustTime(17, 30))
        
        table[day].forEachReversedWithIndex { i, lesson ->
            if(lesson.subject != ""){
                return endOfLessons[i]
            }
        }
        return endOfLessons[0]
    }
    
    fun clear(){
        table.clear()
        for (i in 0.until(days)) {
            val day = ArrayList<Lesson>()
            for (j in 0.until(lessons)) {
                day.add(j, Lesson())
            }
            table.add(i, day)
        }
    }
    
    operator fun get(day: Int) = table[day]
    operator fun set(pos: Int, value: MutableList<Lesson>){
        table[pos] = value
    }
    
    companion object {
        fun newDefaultInstance(context: Context) = Timetable(newTimetableId(context), 5, 11)
    }
}