package com.example.eloem.vertretungsplan.network

import android.content.Context
import android.util.Log
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import java.lang.Error
import java.lang.Exception
import java.util.logging.Logger

object ResponseModel {
    data class VerPlan(
            val fetchedTime: Long,
            val table: List<Vertretungsplan.Row>,
            val weekDay: WeekDay,
            val updateTime: Long,
            val targetDay: Long,
            val grade: Vertretungsplan.Grade
    ) {
        fun toVertretungsplan(context: Context, timetable: Timetable?): Vertretungsplan {
            val gPlan = Vertretungsplan.Plan(newPlanId(context), table)
    
            val cPlanId = newPlanId(context)
            val cPlan = if (timetable != null) {
                try {
                    Vertretungsplan.calculateCustPlan(weekDay, timetable, gPlan, cPlanId)
                } catch (e: Exception) {
                    Vertretungsplan.Plan(cPlanId, emptyList(), Vertretungsplan.PlanStatus.CALCULATION_ERROR)
                }
            } else {
                Vertretungsplan.Plan(cPlanId, emptyList(), Vertretungsplan.PlanStatus.NO_TIMETABLE)
            }
            
            return Vertretungsplan(
                    newVertretungsplanId(context),
                    fetchedTime,
                    gPlan,
                    cPlan,
                    weekDay,
                    updateTime,
                    targetDay,
                    grade,
                    timetable?.id
            )
        }
        
        companion object {
            fun fromString(str: String): Result<VerPlan, Error> {
                return tryResult {
                    val table = extractVerPlan(str)
                    val weekDay = extractWeekday(str)
                    val targetDay = extractTargetDay(str)
                    val updateTime = extractUpdateTime(str)
                    val grade = extractGrade(str)
                    VerPlan(
                            System.currentTimeMillis(),
                            table,
                            weekDay,
                            updateTime,
                            targetDay,
                            grade
                    )
                }.catchResult {
                    Error.PARSE_ERROR
                }
            }
        }
    }
    
    enum class Error { NO_INTERNET, PARSE_ERROR }
    
    private const val TAG = "ResponseModel"
}