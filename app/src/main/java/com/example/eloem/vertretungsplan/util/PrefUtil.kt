package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.eloem.vertretungsplan.helperClasses.Timetable
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.google.gson.Gson
import java.util.*

const val VERTRETUNGSPLAN_ID = "com.example.eloem.vertretungsplan.vertretungsplan"

fun writeVertretungsplan(verPlan: Vertretungsplan, context: Context?){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    val gson = Gson()
    val json = gson.toJson(verPlan)
    preference.putString(VERTRETUNGSPLAN_ID, json)
    preference.apply()
}

fun readVertretungsplan(context: Context?): Vertretungsplan {
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    val json = preference.getString(VERTRETUNGSPLAN_ID, "")
    return gson.fromJson(json, Vertretungsplan::class.java)
}

const val VERTRETUNGSPLAN_TIME_ID = "com.example.eloem.vertretungsplan.vertretungsplan_time"

fun writeVerPlanTime(time: Date, context: Context?){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    val gson = Gson()
    val json = gson.toJson(time)
    preference.putString(VERTRETUNGSPLAN_TIME_ID, json)
    preference.apply()
}

fun readVerPlanTime(context: Context?): Date{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    try {
        val json = preference.getString(VERTRETUNGSPLAN_TIME_ID, "")
        return gson.fromJson(json, Date::class.java)
    }catch (e:java.lang.IllegalStateException){
        val cal = Calendar.getInstance()
        val currentTime = cal.time
        return subMinutesToDate(10, currentTime)
    }
}

const val TIMETABLE_ID =  "com.example.eloem.vertretungsplan.timetable"

fun writeTimetable(timetable: Timetable, context: Context?){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    val gson = Gson()
    val json = gson.toJson(timetable)
    preference.putString(TIMETABLE_ID, json)
    preference.apply()
}

fun readTimetable(context: Context?): Timetable {
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    try {
        val json = preference.getString(TIMETABLE_ID, "")
        return gson.fromJson(json, Timetable::class.java)
    }catch (e: java.lang.IllegalStateException){
        val timetable = Timetable(4, 10)
        writeTimetable(timetable, context)
        return readTimetable(context)
    }
}

const val NOTIFICATION_ID_ID = "com.example.eloem.vertretungsplan.notification"

fun newNotificationId(context: Context?): Int{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    var id: Int
    try {
        id = preference.getInt(NOTIFICATION_ID_ID, 0)
        id++
    }catch (e: java.lang.IllegalStateException){
        id = 0
    }
    preference.edit()
            .putInt(NOTIFICATION_ID_ID, id)
            .apply()
    return id
}

/** Zum lesen von Einstellugnen**/
fun readGrade(context: Context?): String{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    return preference.getString("grade", "")
}

fun readSortPlan(context: Context):Boolean{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val value = preference.getString("sort_plan", "")
    return value == "1"
}

/**nur für das Widget**/

const val CURRENLY_MY_PLAN_ID = "com.example.eloem.vertretungsplan.currentlyMyPlan"

fun writeCurrentlyMyPLan(context: Context?, isMyPlan: Boolean){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    preference.putBoolean(CURRENLY_MY_PLAN_ID, isMyPlan).apply()
}

fun readCurrentlyMyPlan(context: Context?): Boolean{
    try {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        val bool = preference.getBoolean(CURRENLY_MY_PLAN_ID, true)
        return bool
    }catch (e: java.lang.IllegalStateException){
        writeCurrentlyMyPLan(context, true)
    }
    return true
}