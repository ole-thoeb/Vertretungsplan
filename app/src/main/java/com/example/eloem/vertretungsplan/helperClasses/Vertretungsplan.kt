package com.example.eloem.vertretungsplan.helperClasses

import com.example.eloem.vertretungsplan.util.currentWeekday

class Vertretungsplan {
    
    data class Row(val lesson: Int, val teacher: String, val verTeacher: String, val room: String, val verRoom: String, val verText: String)
    
    private val plan = ArrayList<Row>()
    private val custPlan = ArrayList<Row>()
    private var day = 0
    private var updateTime = "00:00"
    private var error = ERROR_NO
    
    fun addRow(pLesson: Int, pTeacher: String, pVerTeacher: String, pRoom: String, pVerRoom: String, pVerText: String){
        this.plan.add(Row(pLesson, pTeacher, pVerTeacher, pRoom, pVerRoom, pVerText))
    }
    
    fun getRow(index: Int): Row {
        return this.plan[index]
    }
    
    fun getPlan(): ArrayList<Row>{
        return this.plan
    }
    
    fun length(): Int{
        return this.plan.size
    }
    
    fun setDay(pDay: Int){
        this.day = pDay
    }
    
    fun getDay(): Int{
        return this.day
    }
    
    fun setUpdateTime(pTime: String){
        this.updateTime = pTime
    }
    
    fun getUpdateTime(): String{
        if (this.getError(false) == ERROR_NO){
            return this.updateTime
        }
        return "ERROR"
    }
    
    fun setError(error: String){
        if(this.error != ERROR_CONNECTION){
            this.error = error
        }
    }
    
    fun getError(forCustPlan:Boolean):String{
        if (forCustPlan || this.error == ERROR_CONNECTION) {
            return this.error
        }
        return ERROR_NO
    }
    
    fun calculateCustPlan(timetable: Timetable){
        val verDay = this.getDay()
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
            this.setError(ERROR_WRONG_DAY)
            this.custPlan.clear()
        }
    
        this.setError(ERROR_NO_PLAN)
        for (row in this.plan){
            if (row.teacher == timetable.getTeacher(verDay, row.lesson -1)){
                this.setError(ERROR_NO)
                custPlan.add(row)
            }
        }
    }
    
    fun getCustPlan():ArrayList<Row>{
        return this.custPlan
    }
    
    fun getCustRow(pos: Int): Row{
        return custPlan[pos]
    }
    
    fun custLenght(): Int{
        return  custPlan.size
    }
    
    
    companion object {
        const val ERROR_NO_PLAN = "noPlanError"
        const val ERROR_WRONG_DAY = "wrongDayError"
        const val ERROR_CONNECTION = "connectionError"
        const val ERROR_NO = "noError"
    }
}