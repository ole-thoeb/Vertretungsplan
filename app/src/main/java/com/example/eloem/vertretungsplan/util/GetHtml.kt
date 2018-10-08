package com.example.eloem.vertretungsplan.util

import android.content.Context
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import java.text.SimpleDateFormat
import java.util.*

fun getUrl(grade: String) = when(grade){
    "EF" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_EF.htm"
    "Q1" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
    "Q2" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q2.htm"
    else -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
}

fun planIsUpToDate(context: Context?): Boolean{
    val lastTime = readVerPlanTime(context)
    val currentTime = System.currentTimeMillis()
    
    return lastTime + 5 * 60 * 1000 > currentTime
}

fun extractVerPlan(htmlString: String, context: Context?): Vertretungsplan.Plan{
    val plan = Vertretungsplan.Plan(newPlanId(context))
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
    return plan
}

fun extractUpdateTime(htmlString: String): Long{
    var mHtml = htmlString
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
    return dateObj.time
    /*
    val sdf2 = SimpleDateFormat("HH:mm")
    time = normaliseTimeString(time)
    val t = sdf2.parse(time)
    
    val cal1 = Calendar.getInstance()
    cal1.time = d
    
    val cal2 = Calendar.getInstance()
    cal1.time = t
    
    cal1.add(Calendar.HOUR, cal2.get(Calendar.HOUR))
    cal1.add(Calendar.MINUTE, cal2.get(Calendar.MINUTE))
    */
}

fun extractDay(htmlString: String): Int{
    var mHtml = htmlString
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
    }
}



