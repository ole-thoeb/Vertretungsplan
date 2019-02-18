package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

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

fun Long.toDate(): Date{
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.time
}


fun Date.toDateTimeString(): String{
    val sdf= SimpleDateFormat("dd.MM.yyyy HH:mm")
    return sdf.format(this)
}

fun Date.toTimeString(): String{
    val sdf= SimpleDateFormat("HH:mm")
    return sdf.format(this)
}

fun shortWeekdayString(weekday: Int) =  when(weekday){
    1 -> "So."
    2 -> "Mo."
    3 -> "Di."
    4 -> "Mi."
    5 -> "Do."
    6 -> "Fr."
    else -> "Sa."
}

fun Date.toWeekdayDateTimeString(): String{
    val sdf= SimpleDateFormat("dd.MM.yyyy HH:mm")
    val cal = Calendar.getInstance()
    cal.time = this
    val d = cal.get(Calendar.DAY_OF_WEEK)
    return "${shortWeekdayString(d)} ${sdf.format(this)}"
}

fun Date.toWeekdayDateString(): String{
    val sdf= SimpleDateFormat("dd.MM.yyyy")
    val cal = Calendar.getInstance()
    cal.time = this
    val d = cal.get(Calendar.DAY_OF_WEEK)
    return "${shortWeekdayString(d)} ${sdf.format(this)}"
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
    return String.format("#%06x", colorInt)
}

//http://stackoverflow.com/a/24810681/2444312
fun isDarkColor(colorStr: String): Boolean = isDarkColor(Color.parseColor(colorStr))

fun isDarkColor(color: Int): Boolean =
        Color.red(color) * 0.299 +
        Color.green(color) * 0.587 +
        Color.blue(color) * 0.114 < 160

fun differentShade (color: Int, difference: Float): Int{
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
fun Date.addMinutesToDate(minutes: Int): Date{
    val ONE_MINUTE_IN_MILLIS = 60000//millisecs
    
    val curTimeInMs = time
    val afterAddingMins = Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS))
    return afterAddingMins
}

fun Date.subMinutesToDate(minutes: Int): Date{
    val ONE_MINUTE_IN_MILLIS = 60000//millisecs
    
    val curTimeInMs = time
    val afterSubtractingMins = Date(curTimeInMs - (minutes * ONE_MINUTE_IN_MILLIS));
    return afterSubtractingMins
}


//returns Monday as 0, Thursday as 1 ...
val currentWeekday: Int get() {
    val cal = Calendar.getInstance()
    var weekDay = cal.get(Calendar.DAY_OF_WEEK)
    weekDay -= 2
    if (weekDay < 0) weekDay = 6
    return weekDay
}

fun Context.getAttribute(resourceId: Int, resolveRef: Boolean): TypedValue{
    val tv = TypedValue()
    theme.resolveAttribute(resourceId, tv, resolveRef)
    return tv
}

/**
 * @param[copyPart] lambda that specifies on each element what part to copy
 * @returns a new [List] with copied elements
 */
fun <T, K> Collection<T>.subList(copyPart: (T) -> K) = toList().copyOf(copyPart)

fun <T, K> List<T>.copyOf(copyPart: (T) -> K) = List(size){ copyPart(this[it]) }

/**
 * @returns shallow copy of the list
 */
fun <T> List<T>.copyOf() = copyOf { it }

fun <T> List<T>.subList(indices: List<Int>): List<T> = List(indices.size) { this[indices[it]] }

fun <T> List<T>.onEach(action: (T) -> T): List<T> = List(size){ action(this[it]) }

fun String.retainMatches(regex: Regex, vararg matches: Int): String{
    val matchAll = -1 in matches
    var currentMatchNum = 0
    
    var resultString = ""
    var matchResult = regex.find(this)
    
    while (matchResult != null) {
        if (matchAll || currentMatchNum in matches) {
            resultString += matchResult.groups.joinToString(separator = "") { it?.value ?: "" }
        }
        matchResult = matchResult.next()
        currentMatchNum++
    }
    return resultString
}

fun View.setNoDoubleClickListener(timeInterval: Long = 500, action: (View) -> Unit){
    setOnClickListener(makeNoDoubleActivation(timeInterval, action))
}

fun <T> makeNoDoubleActivation(coolDown: Long, action: (T) -> Unit): (T) -> Unit {
    var lastClick = 0L
    return {
        val currTime = System.currentTimeMillis()
        if (lastClick + coolDown < currTime) {
            lastClick = currTime
            action(it)
        }
    }
}