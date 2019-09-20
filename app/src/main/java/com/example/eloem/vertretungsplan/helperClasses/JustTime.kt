package com.example.eloem.vertretungsplan.helperClasses

import java.util.*

data class JustTime(val hour: Int, val minute: Int): Comparable<JustTime> {
    
    val minutes = hour * 60 + minute
    
    override operator fun compareTo(other: JustTime): Int {
        return minutes - other.minutes
    }
    
    override fun toString(): String{
        return "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }
    
    companion object {
        fun fromCalender(calendar: Calendar): JustTime {
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            return JustTime(hour, minute)
        }
        
        fun now(): JustTime {
            return System.currentTimeMillis().justTime()
        }
    }
}

fun Long.justTime(): JustTime {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return JustTime.fromCalender(cal)
}

fun Date.justTime(): JustTime {
    val cal = Calendar.getInstance()
    cal.time = this
    IntRange
    return JustTime.fromCalender(cal)
}

private class SimpleRange<T: Comparable<T>>(override val start: T, override val endInclusive: T): ClosedRange<T>

operator fun JustTime.rangeTo(that: JustTime): ClosedRange<JustTime> = SimpleRange(this, that)