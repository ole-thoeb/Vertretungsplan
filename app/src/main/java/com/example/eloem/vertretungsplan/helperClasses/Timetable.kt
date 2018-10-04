package com.example.eloem.vertretungsplan.helperClasses

import org.jetbrains.anko.collections.forEachReversedByIndex
import org.jetbrains.anko.collections.forEachReversedWithIndex
import kotlin.collections.ArrayList

class Timetable(private val days: Int, private val lessons: Int) {
    
    data class Lesson(val subject: String = "", val teacher: String = "", val room: String = "",
                      val color: String = "#FFFAFAFA")
    
    val table: ArrayList<ArrayList<Lesson>> = ArrayList<ArrayList<Lesson>>()
    
    init {
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
    }
    
    fun distinctLessons(): ArrayList<Lesson>{
        val list = ArrayList<Lesson>()
        val alreadyInList = ArrayList<String>()
        alreadyInList.add("")
        
        for (i in this.table) {
            for (j in i) {
                if (j.subject !in alreadyInList){
                    list.add(j)
                    alreadyInList.add(j.subject)
                }
            }
        }
        return list
    }
    
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
        for (i in 0..days) {
            val day = ArrayList<Lesson>()
            for (j in 0..lessons) {
                day.add(j, Lesson())
            }
            table.add(i, day)
        }
    }
    operator fun get(day: Int) = table[day]
}