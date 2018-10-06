package com.example.eloem.vertretungsplan.helperClasses

import com.example.eloem.vertretungsplan.util.currentWeekday
import com.example.eloem.vertretungsplan.util.extractDay
import com.example.eloem.vertretungsplan.util.extractUpdateTime
import com.example.eloem.vertretungsplan.util.extractVerPlan

data class Vertretungsplan(val generalPlan: Plan = Plan(), val customPlan: Plan = Plan(),
                           val day: Int = 0, val updateTime: Long = 0) {
    
    data class Row(val lesson: Int, val teacher: String, val verTeacher: String, val room: String,
                   val verRoom: String, val verText: String)
    
    data class Plan(val plan: MutableList<Row> = mutableListOf(), var error: String = ERROR_NO)
    
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
        
        fun newInstance(html: String, timetable: Timetable): Vertretungsplan{
            val gPlan = extractVerPlan(html)
            val weekDay = extractDay(html)
            val updateTime = extractUpdateTime(html)
            
            val vPlan = Vertretungsplan(generalPlan = gPlan, day = weekDay, updateTime = updateTime)
            vPlan.calculateCustPlan(timetable)
            
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