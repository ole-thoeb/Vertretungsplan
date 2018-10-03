package com.example.eloem.vertretungsplan.util

import android.content.Context
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import java.util.*

fun getUrl(grade: String):String{
    var url = "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
    when(grade){
        "EF" -> url = "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_EF.htm"
        "Q1" -> url = "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
        "Q2" -> url = "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q2.htm"
    }
    return url
}

fun planIsUpToDate(context: Context?): Boolean{
    val lastTime = readVerPlanTime(context)
    val cal = Calendar.getInstance()
    val currentTime = cal.time
    
    return addMinutesToDate(5, lastTime) > currentTime
}

fun parseHtml(context: Context?, html: String): Vertretungsplan{
    val verPlan = Vertretungsplan()
    extractVerPlan(html, verPlan)
    extractDay(html, verPlan)
    extractUpdateTime(html, verPlan)
    
    verPlan.calculateCustPlan(readTimetable(context))
    
    val cal = Calendar.getInstance()
    val currentTime = cal.time
    
    writeVerPlanTime(currentTime, context)
    writeVertretungsplan(verPlan, context)
    
    return verPlan
}

fun extractVerPlan(htmlString: String, plan: Vertretungsplan) {
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
                plan.addRow(i, splitList[1], splitList[2], splitList[4], splitList[6], splitList[7])
            }
        } else {
            //Stund, Lehrer, Vertreter, Raum, Verraum, ver-Text in eine Reihe
            plan.addRow(splitList[0].toInt(), splitList[1], splitList[2], splitList[4], splitList[6], splitList[7])
        }
        
        //eingefügte elemente entfernen
        for (i in 0..7) splitList = splitList.drop(1)
    }
}
    
fun extractUpdateTime(htmlString: String, plan: Vertretungsplan){
    var mHtml = htmlString
    //Anfang entfernen
    mHtml = mHtml.removeRange(0, mHtml.indexOf("D-53332, GOETHESTR. 1</TD><TD></TD><TD align=\"right\">")+53)
    //Ende entfernen
    mHtml = mHtml.removeRange(mHtml.indexOf("</TD></TR></TABLE><BR>"),mHtml.length)
    //alles außer Uhrzeit entfernen
    val time = mHtml.removeRange(0, mHtml.indexOf(" ")+1)
    plan.setUpdateTime(time)
}

fun extractDay(htmlString: String, plan: Vertretungsplan){
    var mHtml = htmlString
    //Anfang entfernen
    mHtml = mHtml.removeRange(0, mHtml.indexOf("<B>")+23)
    //Ende enfernen
    mHtml = mHtml.removeRange(mHtml.indexOf("</B>"),mHtml.length)
    //alles außer Wochentag entfernen
    val weekday = mHtml.removeRange(0, mHtml.indexOf("/")+2)
    when(weekday){
        "Montag" -> plan.setDay(0)
        "Dienstag" -> plan.setDay(1)
        "Mittwoch" -> plan.setDay(2)
        "Donnerstag" -> plan.setDay(3)
        "Freitag" -> plan.setDay(4)
        else -> plan.setDay(0)
    }
}



