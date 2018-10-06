package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*
import java.time.DayOfWeek

fun normaliseDateString(date: String):String{
    var day = date.removeRange(date.indexOf('.'), date.length)
    if(day.length != 2) day = "0$day"
    var month = date.removeRange(0, date.indexOf('.') + 1)
    month = month.removeRange(month.indexOf('.'), month.length)
    if(month.length != 2) month = "0$month"
    val year = date.removeRange(0, date.indexOf('.', 3) + 1)
    return "$day.$month.$year"
}

fun normaliseTimeString(time : String): String{
    var hour = time.removeRange(time.indexOf(':'), time.length)
    if (hour.length != 2) hour = "0$hour"
    var minutes = time.removeRange(0, time.indexOf(':') + 1)
    if (minutes.length != 2) minutes = "0$minutes"
    return "$hour:$minutes"
}

fun dateFromMillis(millis: Long): Date{
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    return cal.time
}


fun Date.toStringWithTime(): String{
    val sdf= SimpleDateFormat("dd.MM.yyyy HH:mm")
    return sdf.format(this)
}

fun Date.toStringWithJustTime(): String{
    val sdf= SimpleDateFormat("HH:mm")
    return sdf.format(this)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun random(from: Int, to: Int): Int{
    val rand = Random()
    return rand.nextInt(to - from) + from
}

fun randomColorString(): String{
    val int = random(0, 16777215)
    return String.format("#%06x", int)
}

fun colorIntToString(colorInt: Int):String{
    return String.format("#%06x",colorInt)
}

//http://stackoverflow.com/a/24810681/2444312
fun isDarkColor(colorStr: String): Boolean{
    val colorInt = Color.parseColor(colorStr)
    val brightness = Color.red(colorInt) * 0.299 +
            Color.green(colorInt) * 0.587 +
            Color.blue(colorInt) * 0.114
    return brightness < 160
}

fun differentshade (color: Int, difference: Float): Int{
    val factor = 1 + difference
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] = hsv[2] * factor
    return Color.HSVToColor(hsv)
}

/**
*  Convenience method to add a specified number of minutes to a Date object
*  From: http://stackoverflow.com/questions/9043981/how-to-add-minutes-to-my-date
*  @param  minutes  The number of minutes to add
*  @param  beforeTime  The time that will have minutes added to it
*  @return  A date object with the specified number of minutes added to it
*/
fun addMinutesToDate(minutes: Int, beforeTime: Date): Date{
    val ONE_MINUTE_IN_MILLIS = 60000//millisecs
    
    val curTimeInMs = beforeTime.time
    val afterAddingMins = Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
    return afterAddingMins
}

fun subMinutesToDate(minutes: Int, beforeTime: Date): Date{
    val ONE_MINUTE_IN_MILLIS = 60000//millisecs
    
    val curTimeInMs = beforeTime.time
    val afterSubtractingMins = Date(curTimeInMs - (minutes * ONE_MINUTE_IN_MILLIS));
    return afterSubtractingMins
}


//returns Monday as 0, Thursday as 1 ...
fun currentWeekday(): Int{
    val cal = Calendar.getInstance()
    var weekDay = cal.get(Calendar.DAY_OF_WEEK)
    weekDay -= 2
    if (weekDay < 0) weekDay = 6
    //return weekDay
    return 3
}