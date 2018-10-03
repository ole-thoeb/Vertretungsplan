package com.example.eloem.vertretungsplan.helperClasses

import java.text.SimpleDateFormat
import java.util.*

class JustTime {
    var hour = 0
    var minute = 0
    
    constructor(){
        val cal = Calendar.getInstance()
        this.hour = cal.get(Calendar.HOUR_OF_DAY)
        this.minute = cal.get(Calendar.MINUTE)
    }
    
    constructor(pHour: Int, pMinute: Int){
        this.hour = pHour
        this.minute = pMinute
    }
    
    constructor(date: Date){
        val mdformat = SimpleDateFormat("HH:mm")
        val stringTime = mdformat.format(date)
        val splitTime = stringTime.split(":")
        this.hour = splitTime[0].toInt()
        this.minute = splitTime[1].toInt()
    }
    
    fun isLaterThen(time2: JustTime): Boolean{
        val thisTime = (this.hour * 60) + this.minute
        val otherTime = (time2.hour * 60) + time2.minute
        return thisTime > otherTime
    }
    
    override fun toString(): String{
        return "${this.hour}:${this.minute}"
    }
}