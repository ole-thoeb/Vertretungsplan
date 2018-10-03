package com.example.eloem.vertretungsplan.helperClasses

import kotlin.collections.ArrayList

class Timetable(days: Int, lessons: Int) {
    
    data class Lesson(var pSubject: String, var pTeacher: String, var pRoom: String, var pColor: String)
    
    val table: ArrayList<ArrayList<Lesson>> = ArrayList<ArrayList<Lesson>>()
    
    init {
        for (i in 0..days) {
            val day = ArrayList<Lesson>()
            for (j in 0..lessons) {
                day.add(j, Lesson("", "", "", "#FFFAFAFA"))
            }
            this.table.add(i, day)
        }
        
       
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
                if (j.pSubject !in alreadyInList){
                    list.add(j)
                    alreadyInList.add(j.pSubject)
                }
            }
        }
        return list
    }
    
    fun changeContent(day: Int, lesson: Int, subject: String = "", teacher: String = "", room: String = "", color: String = "#FFFAFAFA"){
        this.table[day][lesson] = Lesson(subject, teacher, room, color)
    }
    
    fun getSubject(day: Int, lesson: Int): String{
        return this.table[day][lesson].pSubject
    }
    
    fun getTeacher(day: Int, lesson: Int): String{
        return this.table[day][lesson].pTeacher
    }
    
    fun getRoom(day: Int, lesson: Int): String{
        return this.table[day][lesson].pRoom
    }
    
    fun getColor(day: Int, lesson: Int): String{
        return this.table[day][lesson].pColor
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
        
        for((i, s) in this.table[day].withIndex().reversed()){
            if(s.pSubject != ""){
                return endOfLessons[i]
            }
        }
        return endOfLessons[0]
    }
}