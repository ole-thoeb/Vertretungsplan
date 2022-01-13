package com.example.eloem.vertretungsplan.helperClasses

import com.example.eloem.vertretungsplan.util.WeekDay
import com.example.eloem.vertretungsplan.util.currentWeekday

data class Vertretungsplan(
        val id: Long,
        val fetchedTime: Long = System.currentTimeMillis(),
        val generalPlan: Plan = Plan(-1),
        val customPlan: Plan = Plan(-1),
        val weekDay: WeekDay = WeekDay.MONDAY,
        val updateTime: Long = 0,
        val targetDay: Long = 0,
        val grade: Grade = Grade.Q1,
        val computedWith: Long
) {
    
    data class Row(val lesson: Int, val teacher: String, val verTeacher: String, val room: String,
                   val type: String, val verText: String)
    
    data class Plan(val id: Long, val plan: List<Row> = mutableListOf(), var status: PlanStatus = PlanStatus.OK) {
        fun contentEquals(other: Plan) = other.plan == plan && status == other.status
        
        companion object {
            val EMPTY = Plan(-1, emptyList(), PlanStatus.OK)
        }
    }
    
    fun getRelevantPlan(isCustomPlan: Boolean): Vertretungsplan.Plan =
            if (isCustomPlan) customPlan
            else generalPlan
    
    //compares two plans except their ids and fetchTime
    fun contentEquals(other: Vertretungsplan): Boolean {
    
        val g = generalPlan.contentEquals(other.generalPlan)
        val c = customPlan.contentEquals(other.customPlan)
        return weekDay == other.weekDay &&
                updateTime == other.updateTime && c && g
    }
    
    enum class Grade(val url: String) {
        EF("https://www.europaschule-bornheim.de/fileadmin/vertretung/Ver_Kla_A_EF.htm"),
        Q1("https://www.europaschule-bornheim.de/fileadmin/vertretung/Ver_Kla_A_Q1.htm"),
        Q2("https://www.europaschule-bornheim.de/fileadmin/vertretung/Ver_Kla_A_Q2.htm")
    }
    
    enum class PlanStatus { OK, WRONG_DAY, NO_PLAN, CALCULATION_ERROR, NO_TIMETABLE }
    
    companion object {
        fun calculateCustPlan(weekDay: WeekDay, timetable: Timetable, generalPlan: Plan, id: Long): Plan{
            val plan = mutableListOf<Row>()
    
            var currentDay = currentWeekday
        
            if (currentDay.isWeekend) currentDay = WeekDay.MONDAY
            else {
                val time = JustTime.now()
                val endOfDayTime = timetable.endOfDay(currentDay)
                if (time > endOfDayTime) {
                    currentDay = currentDay.nextDayNotWeekend()
                }
            }
        
            if (weekDay != currentDay) {
                return Plan(id, plan, PlanStatus.WRONG_DAY)
            }
    
            var status = PlanStatus.NO_PLAN
            generalPlan.plan.forEach { row ->
                if (row.teacher == timetable[weekDay.ordinal][row.lesson -1].teacher && row.teacher != ""){
                    status = PlanStatus.OK
                    plan.add(row)
                }
            }
            return Plan(id, plan, status)
        }
    }
}