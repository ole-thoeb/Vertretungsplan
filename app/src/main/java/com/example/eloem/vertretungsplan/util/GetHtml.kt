package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

fun getUrl(grade: String) = when(grade){
    "EF" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_EF.htm"
    "Q1" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
    "Q2" -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q2.htm"
    else -> "http://www.europaschule-bornheim.eu/fileadmin/vertretung/Ver_Kla_A_Q1.htm"
}

fun planIsUpToDate(context: Context): Boolean{
    val lastTime = lastUpdate(context)
    val currentTime = System.currentTimeMillis()
    
    return lastTime + 5 * 60 * 1000 > currentTime
}

fun extractVerPlan(htmlString: String, context: Context): Vertretungsplan.Plan{
    /*val plan = Vertretungsplan.Plan(newPlanId(context))
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
    val table = extractTable(htmlString).drop(1)//first row are just column names
    val plan = Vertretungsplan.Plan(newPlanId(context))
    table.forEach {
        if ("-" in it[0]) { //wenn mehrere Stunden (z.B 1 - 2)
            val multLessons = it[0].split(""" *- *""".toRegex())
            for (i in multLessons[0].toInt()..multLessons[1].toInt())
            plan.plan.add(Vertretungsplan.Row(i, it[1], it[2], it[4], it[6], it[7]))
        }else plan.plan.add(Vertretungsplan.Row(try {
            it[0].toInt()
        }catch (e: NumberFormatException){ -1 }, it[1], it[2], it[4], it[6], it[7]))
    }
    return plan
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

fun extractWeekday(htmlString: String): Int{
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
        "Montag" -> 0
        "Dienstag" -> 1
        "Mittwoch" -> 2
        "Donnerstag" -> 3
        "Freitag" -> 4
        else -> 0
    }
}

fun extractTargetDay(html: String): Long{
    val dateString = html.retainMatches("""(?<=Erweitert\s\s)\d+.\d+.""".toRegex(), 0) +
            Calendar.getInstance().get(Calendar.YEAR).toString()
    
    val sdf = SimpleDateFormat("dd.MM.yyyy")
    val dateObj = sdf.parse(dateString)
    //Log.d(TAG, "Target date: $dateObj")
    return dateObj.time
}

fun extractTable(html: String): List<List<String>>{
    //leere Zellen könnten verschwinden
    val beginOfTableRex = Regex("""(?i)<table[^<>]+>((<[^<>]+>)|\s)*""")
    var s = html.removeRange(0, beginOfTableRex.find(html)?.next()!!.range.last + 1)
    val endOfTable = Regex("""(<[^<>]+>|<[^<>]+> |[\n\r])*(?i)</table""")
    s = s.removeRange(endOfTable.find(s)!!.range.first, s.length)
    
    val dirtyList = s.split("""(?i)(<[^<>]+> |<[^<>]+>|[\n\r])+<TR[^<>]*>(<[^<>]+>|\s)*""".toRegex()).toMutableList()
    var table = MutableList(dirtyList.size){dirtyList[it].split("""(<[^<>]*>)+(?!<)(?=.)""".toRegex()).toMutableList()}
    
    table = table.onEach { x ->
        x.onEach { str ->
            str.replace("""[\r\n]|(\s*(<[^<>]+>)\s*)*""".toRegex(), "")
        }.toMutableList()
    }.toMutableList()
    return table
}

fun <T: Context> T.simpleRequest(url: String, actionSuccess: (String) -> Unit, actionError: (VolleyError) -> Unit){
    val queue = Volley.newRequestQueue(this)
    
    val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> (actionSuccess),
            Response.ErrorListener (actionError))
    
    queue.add(stringRequest)
}

fun <T: Context> T.fetchPlan(onFinish: (Vertretungsplan) -> Unit){
    doAsync {
        val grade = readGrade(this@fetchPlan)
        
        if (readDownloadAllPlans(this@fetchPlan)) {
            val otherGrades = mutableListOf("EF", "Q1", "Q2")
            otherGrades.remove(grade)
        
            otherGrades.forEach {
                try {
                    simpleRequest(getUrl(it), { response ->
                        val verPlan = Vertretungsplan.newInstance(response, this@fetchPlan, it)
                        insertVertretungdplanIfNew(this@fetchPlan, verPlan)
                    }, { /**error: do nothing*/ })
                }catch (e:Throwable){
                    Log.e(TAG, e.localizedMessage)
                }
            }
        }
    
        simpleRequest(getUrl(grade), { response ->
            try {
                val verPlan = Vertretungsplan.newInstance(response, this@fetchPlan, grade)
                insertVertretungdplanIfNew(this@fetchPlan, verPlan)
                onFinish(verPlan)
            }catch (e: Throwable){
                onFinish(Vertretungsplan.noConnectionPlan())
            }
        }, { error ->
            Log.e(TAG, error.localizedMessage)
            uiThread { onFinish(Vertretungsplan.noConnectionPlan()) }
        })
    }
}

const val TAG = "GetHtml"