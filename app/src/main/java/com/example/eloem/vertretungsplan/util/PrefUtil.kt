package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.jetbrains.anko.defaultSharedPreferences
import java.io.Serializable
import kotlin.reflect.KProperty

/*
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
}*/

/*const val VERTRETUNGSPLAN_TIME_ID = "com.example.eloem.vertretungsplan.vertretungsplan_time"

fun writeVerPlanTime(time: Long, context: Context?){
    PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(VERTRETUNGSPLAN_TIME_ID, time)
            .apply()
}

fun readVerPlanTime(context: Context?): Long = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getLong(VERTRETUNGSPLAN_TIME_ID, 0)
        */

/*
const val TIMETABLE_ID =  "com.example.eloem.vertretungsplan.timetable"

fun writeTimetable(timetable: Timetable, context: Context?){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    val gson = Gson()
    val json = gson.toJson(timetable)
    preference.putString(TIMETABLE_ID, json)
    preference.apply()
}

fun readTimetable(context: Context): Timetable {
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val gson = Gson()
    try {
        val json = preference.getString(TIMETABLE_ID, "")
        return gson.fromJson(json, Timetable::class.java)
    }catch (e: Throwable){
        val timetable = Timetable.newDefaultInstance(context)
        writeTimetable(timetable, context)
        return readTimetable(context)
    }
}*/

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

const val PLAN_ID_ID = "planId"

fun newPlanId(context: Context): Int = context.defaultSharedPreferences.newId(PLAN_ID_ID)

const val TIMETABLE_ID_ID = "timetableId"

fun newTimetableId(context: Context): Int = context.defaultSharedPreferences.newId(TIMETABLE_ID_ID)

fun SharedPreferences.newId(key: String): Int{
    val id = getInt(key, 0) + 1
    edit().putInt(key, id).apply()
    return id
}

/** Zum lesen von Einstellugnen**/
fun readGrade(context: Context?): String{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    return preference.getString("grade", "Q1") ?: "Q1"
}

/*fun readSortPlan(context: Context):Boolean{
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val value = preference.getString("sort_plan", "")
    return value == "1"
}*/

fun readDownloadAllPlans(context: Context): Boolean = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getBoolean("downloadAllPlans", false)

/**nur für das Widget**/

const val CURRENTLY_MY_PLAN_ID = "com.example.eloem.vertretungsplan.currentlyMyPlan"

fun writeCurrentlyMyPLan(context: Context?, isMyPlan: Boolean){
    val preference = PreferenceManager.getDefaultSharedPreferences(context).edit()
    preference.putBoolean(CURRENTLY_MY_PLAN_ID, isMyPlan).apply()
}

fun readCurrentlyMyPlan(context: Context?): Boolean{
    try {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        val bool = preference.getBoolean(CURRENTLY_MY_PLAN_ID, true)
        return bool
    }catch (e: java.lang.IllegalStateException){
        writeCurrentlyMyPLan(context, true)
    }
    return true
}

const val FILTER_EF_ENABLED_KEY = "filterEfEnabledKey"
const val FILTER_Q1_ENABLED_KEY = "filterQ1EnabledKey"
const val FILTER_Q2_ENABLED_KEY = "filterQ2EnabledKey"
const val FILTER_FOR_ME_KEY = "filterForMeKey"
const val FILTER_SEEKER_PROGRESS_KEY = "filterSeekerProgressKey"
const val FILTER_LAST_OF_DAY_KEY = "filterLastOfDayKey"

const val SETTINGS_THEME_KEY = "preference_theme"
const val RECREATE_PARENT_KEY = "recreateParentKey"

fun <T: Context> T.booleanPref(key: String, default: Boolean = false) = BooleanDelegateImpl(this, key, default)

class BooleanDelegateImpl(private val context: Context, mKey: String, private val default: Boolean):
        PreferencesDelegateImpl<Boolean>(mKey){
    
    override fun readFromPreferences(key: String): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, default)
    
    override fun writeToPreferences(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply()
    }
}

fun <T: Context> T.intPref(key: String, default: Int = 0) = IntDelegateImpl(this, key, default)

class IntDelegateImpl(private val context: Context, mKey: String, private val default: Int):
        PreferencesDelegateImpl<Int>(mKey){
    
    override fun readFromPreferences(key: String): Int =
            PreferenceManager.getDefaultSharedPreferences(context).getInt(key, default)
    
    override fun writeToPreferences(key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply()
    }
}

private object UNINITIALIZED_VALUE
abstract class PreferencesDelegateImpl<T>(private val mKey: String, lock: Any? = null): Lazy<T>, Serializable{

    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    private val lock = lock ?: this
    
    fun invalidate(){
        _value = UNINITIALIZED_VALUE
    }
    
    abstract fun readFromPreferences(key: String): T
    abstract fun writeToPreferences(key: String, value: T)
    
    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }
            
            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST")
                    _v2 as T
                }
                else {
                    val typedValue = readFromPreferences(mKey)
                    _value = typedValue
                    typedValue
                }
            }
        }
    
    
    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
    
    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
    
    operator fun setValue(any: Any?, property: KProperty<*>, t: T) {
        writeToPreferences(mKey, t)
        _value = t
    }
}