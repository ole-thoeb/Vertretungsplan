package com.example.eloem.vertretungsplan.network

import android.content.Context
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.onEach

fun extractVerPlan(htmlString: String): List<Vertretungsplan.Row> {
    /*val plan = Vertretungsplan.Plan(newPlanId(ctx))
    var mHtml = htmlString
    //umbrüche entfernen
    mHtml = mHtml.replace("\r\n", "", true)
    //farben/tags/tabs entfernen (einheitlich machen)
    mHtml = mHtml.replace(" color=\"#010101\"", "", true)
    mHtml = mHtml.replace(" color=\"#000000\"", "", true)
    mHtml = mHtml.replace("<font size=\"3\" face=\"Arial\">", "", true)
    mHtml = mHtml.replace("</TR>", "", true)
    mHtml = mHtml.replace("<TR>", "", true)
    mHtml = mHtml.replace("</font> ", "", true)
    mHtml = mHtml.replace("&nbsp;", " ", true)
    //anfang entfernen
    mHtml = mHtml.removeRange(0, mHtml.indexOf("Vertretungs-Text</TD><TD align=center>"))
    //ende entfernen
    mHtml = mHtml.removeRange(mHtml.indexOf("</TD></TABLE><TABLE"), mHtml.length)
    //in einzelne Elemente splitten
    var splitList = mHtml.split("</TD><TD align=center>")
    //erstes Element Vertretungs_Text entfernen
    splitList = splitList.drop(1)
    
    while (splitList.isNotEmpty()) {
        if ("-" in splitList[0]) { //wenn mehrere Stunden (z.B 1 - 2)
            val multLessons = splitList[0].split(" - ")
            for (i in multLessons[0].toInt()..multLessons[1].toInt()) {
                //Stund, Lehrer, Vertreter, Raum, Verraum, ver-Text in eine Reihe
                plan.plan.add(Vertretungsplan.Row(i, splitList[1], splitList[2], splitList[4], splitList[6], splitList[7]))
            }
        } else {
            //Stund, Lehrer, Vertreter, Raum, Verraum, ver-Text in eine Reihe
            plan.plan.add(Vertretungsplan.Row(splitList[0].toInt(), splitList[1], splitList[2], splitList[4], splitList[6], splitList[7]))
        }
        
        //eingefügte elemente entfernen
        for (i in 0..7) splitList = splitList.drop(1)
    }
    return plan*/
    val table = parsTable(htmlString, 1).drop(1)//first row are just column names
    val plan = mutableListOf<Vertretungsplan.Row>()
    table.forEach {
        val lessonField = it[1]
        val lessons = if ("-" in lessonField) { //wenn mehrere Stunden (z.B 1 - 2)
            val (start, end) = it[0].split(""" *- *""".toRegex())
            start.toInt()..end.toInt()
        } else {
            val lesson = lessonField.toInt()
            lesson..lesson
        }
        for (lesson in lessons) {
            plan.add(Vertretungsplan.Row(
                lesson = lesson,
                teacher = it[6],
                verTeacher = it[2],
                room = it[4],
                type = it[5],
                verText = it[7]
            ))
        }
    }
    return plan
}

fun parsTable(htmlString: String, index: Int = 0): List<List<String>> {
    val doc = Jsoup.parse(htmlString)
    val rows = doc.select("table")[index].select("tr")
    
    return rows.map { row ->
        row.select("td").map { column ->
           column.text()
        }
    }
}

fun extractUpdateTime(htmlString: String): Long{
    /*var mHtml = htmlString
    //Anfang entfernen
    mHtml = mHtml.removeRange(0, mHtml.indexOf("D-53332, GOETHESTR. 1</TD><TD></TD><TD align=\"right\">")+53)
    //Ende entfernen
    mHtml = mHtml.removeRange(mHtml.indexOf("</TD></TR></TABLE><BR>"),mHtml.length)
    //alles außer Uhrzeit entfernen
    var time = mHtml.removeRange(0, mHtml.indexOf(" ")+1)
    //alles außer datum entfernen
    var date = mHtml.removeRange(mHtml.indexOf(" "), mHtml.length)
    
    date = normaliseDateString(date)
    time = normaliseTimeString(time)
    val normalisedString = "$date $time"
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
    val dateObj = sdf.parse(normalisedString)
    return dateObj.time*/
    val dateTime = htmlString.retainMatches("""\d+\.\d+\.\d+ \d+:\d+""".toRegex(), 0)
    
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
    val dateObj = sdf.parse(dateTime)
    return dateObj.time
}

fun extractWeekday(htmlString: String): WeekDay {
    /*var mHtml = htmlString
    //Anfang entfernen
    mHtml = mHtml.removeRange(0, mHtml.indexOf("<B>")+23)
    //Ende enfernen
    mHtml = mHtml.removeRange(mHtml.indexOf("</B>"),mHtml.length)
    //alles außer Wochentag entfernen
    val weekday = mHtml.removeRange(0, mHtml.indexOf("/")+2)
    return when(weekday){
        "Montag" -> 0
        "Dienstag" -> 1
        "Mittwoch" -> 2
        "Donnerstag" -> 3
        "Freitag" -> 4
        else -> 0
    }*/
    val weekday = htmlString.retainMatches("""(?<=/\s)\w+""".toRegex(), 1)
    return when(weekday){
        "Montag" -> WeekDay.MONDAY
        "Dienstag" -> WeekDay.TUESDAY
        "Mittwoch" -> WeekDay.WEDNESDAY
        "Donnerstag" -> WeekDay.THURSDAY
        "Freitag" -> WeekDay.FRIDAY
        else -> WeekDay.MONDAY
    }
}

fun extractTargetDay(html: String): Long{
    val dateString = html.retainMatches("""\d+\.\d+\.""".toRegex(), 1)
    val year = Calendar.getInstance()[Calendar.YEAR]
    val sdf = SimpleDateFormat("dd.MM.yyyy")
    val dateObj = sdf.parse(dateString + year)
    require(dateObj == dateObj.time.toDate())
    return dateObj.time
}

fun extractGrade(html: String): Vertretungsplan.Grade {
    val gradeString = html.retainMatches("""(?<=<font size="6" face="Arial">\r\n)\w+(?=\r\n</font>)""".toRegex(), 0)
    return Vertretungsplan.Grade.valueOf(gradeString)
}

fun extractTable(html: String): List<List<String>>{
    //leere Zellen könnten verschwinden
    val beginOfTableRex = Regex("""(?i)<table[^<>]+>((<[^<>]+>)|\s)*""")
    var s = html.removeRange(0, beginOfTableRex.find(html)?.next()!!.range.last + 1)
    val endOfTable = Regex("""(<[^<>]+>|<[^<>]+> |[\n\r])*(?i)</table""")
    s = s.removeRange(endOfTable.find(s)!!.range.first, s.length)
    
    s = s.replace("&nbsp(;)*".toRegex(), "")
    
    val dirtyList = s.split("""(?i)(<[^<>]+> |<[^<>]+>|[\n\r])+<TR[^<>]*>(<[^<>]+>|\s)*""".toRegex()).toMutableList()
    var table = MutableList(dirtyList.size){dirtyList[it].split("""(?i)(<[^<>]*>)*[^<>]*<\/td>[^<>]*(<[^<>]*>)*<td[^<>]*>""".toRegex()).toMutableList()}
    
    table = table.onEach { x ->
        x.onEach { str ->
            str.replace("""[\r\n]|(\s*(<[^<>]+>)\s*)*""".toRegex(), "")
        }.toMutableList()
    }.toMutableList()
    return table
}

private const val TAG = "GetHtml"