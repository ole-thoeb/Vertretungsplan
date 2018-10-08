package com.example.eloem.vertretungsplan.helperClasses

import android.content.Context
import com.example.eloem.vertretungsplan.util.*

data class Vertretungsplan(val fetchedTime: Long = System.currentTimeMillis(),
                           val generalPlan: Plan = Plan(-1), val customPlan: Plan = Plan(-1),
                           val day: Int = 0, val updateTime: Long = 0) {
    
    data class Row(val lesson: Int, val teacher: String, val verTeacher: String, val room: String,
                   val verRoom: String, val verText: String)
    
    data class Plan(val id: Int, val plan: MutableList<Row> = mutableListOf(), var error: String = ERROR_NO)
    
    fun calculateCustPlan(timetable: Timetable){
        val verDay = day
        var currentDay = currentWeekday()
    
        if (currentDay > 4) currentDay = 0
        else{
            val time = JustTime()
            val endOfDayTime = timetable.endOfDay(currentDay)
            if (time.isLaterThen(endOfDayTime)){
                currentDay++
                if(currentDay > 4) currentDay = 0
            }
        }
    
        if (verDay != currentDay){
            customPlan.error = ERROR_WRONG_DAY
            customPlan.plan.clear()
            return
        }
    
        customPlan.error = ERROR_NO_PLAN
        generalPlan.plan.forEach { row ->
            if (row.teacher == timetable[verDay][row.lesson -1].teacher){
                customPlan.error = ERROR_NO
                customPlan.plan.add(row)
            }
        }
    }
    
    companion object {
        const val ERROR_NO_PLAN = "noPlanError"
        const val ERROR_WRONG_DAY = "wrongDayError"
        const val ERROR_CONNECTION = "connectionError"
        const val ERROR_NO = "noError"
        
        fun newInstance(html: String, context: Context): Vertretungsplan{
            val gPlan = extractVerPlan(html, context)
            val weekDay = extractDay(html)
            val updateTime = extractUpdateTime(html)
            
            val vPlan = Vertretungsplan(generalPlan = gPlan, day = weekDay, updateTime = updateTime)
            vPlan.calculateCustPlan(readTimetable(context))
            
            return vPlan
        }
        
        fun noConnectionPlan(): Vertretungsplan{
            val vPlan = Vertretungsplan()
            vPlan.generalPlan.error = ERROR_CONNECTION
            vPlan.customPlan.error = ERROR_CONNECTION
            return vPlan
        }
    }
}