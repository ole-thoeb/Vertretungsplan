package com.example.eloem.vertretungsplan.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException

fun normaliseDateString(date: String): String{
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
    val sdf= SimpleDateFormat("dd.MM HH:mm")
    val cal = Calendar.getInstance()
    cal.time = this
    val d = cal.get(Calendar.DAY_OF_WEEK)
    return "${shortWeekdayString(d)} ${sdf.format(this)}"
}

fun Date.toWeekdayDateString(): String{
    val sdf= SimpleDateFormat("dd.MM")
    val cal = Calendar.getInstance()
    cal.time = this
    val d = cal.get(Calendar.DAY_OF_WEEK)
    return "${shortWeekdayString(d)} ${sdf.format(this)}"
}


enum class WeekDay(val fullName: String, val shortName: String) {
    MONDAY("Montag", "Mo."),
    TUESDAY("Dienstag", "Di."),
    WEDNESDAY("Mittwoch", "Mi."),
    THURSDAY("Donnerstag", "Do."),
    FRIDAY("Freitag", "Fr."),
    SATURDAY("Samstag", "Sa."),
    SUNDAY("Sonntag", "So.");
    
    val isWeekend: Boolean get() = this == SATURDAY || this == SUNDAY
    val isNotWeekend: Boolean get() = !isWeekend
    fun nextDay(): WeekDay = values()[(ordinal + 1) % 7]
    fun nextDayNotWeekend(): WeekDay = if (this == FRIDAY || this == SUNDAY) MONDAY else nextDay()
}

//returns Monday as 0, Thursday as 1 ...
val currentWeekday: WeekDay get() {
    val cal = Calendar.getInstance()
    var weekDay = cal.get(Calendar.DAY_OF_WEEK)
    weekDay -= 2
    if (weekDay < 0) weekDay = 6
    return WeekDay.values()[weekDay]
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

fun <T> Iterable<Iterable<T>>.flatIterator(): Iterator<T> {
    return object : Iterator<T> {
        private val outerIter = this@flatIterator.iterator()
        private var curIter: Iterator<T>? = null
    
        override fun next(): T {
            val cIter = curIter
            return if (cIter?.hasNext() == true) {
                cIter.next()
            } else {
                curIter = outerIter.next().iterator()
                next()
            }
        }
        
        override fun hasNext(): Boolean {
            if (curIter?.hasNext() == true) return true
            while (outerIter.hasNext()) {
                val cIter = outerIter.next().iterator()
                if (cIter.hasNext()) {
                    curIter = cIter
                    return true
                }
            }
            return false
        }
    }
}

fun <T> Iterable<Iterable<T>>.flatIterable(): Iterable<T> {
    return flatIterator().asIterable()
}

fun <T> Iterator<T>.asIterable(): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> = this@asIterable
    }
}

inline fun <T> MutableList<T>.findAndRemove(predicate: (T) -> Boolean): T {
    val iter = iterator()
    while (iter.hasNext()) {
        val value = iter.next()
        if (predicate(value)) {
            iter.remove()
            return value
        }
    }
    throw NoSuchElementException()
}

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


fun String.toIntCharByChar(): Int = fold(0) {_, c -> c.toInt()}

infix fun Int.divides(that: Int): Boolean = that % this == 0

fun CharSequence?.orEmpty(): String = this?.toString() ?: ""