package com.example.eloem.vertretungsplan.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.recyclerView.ContextAdapter
import org.jetbrains.anko.defaultSharedPreferences
import java.io.Serializable
import kotlin.reflect.KProperty

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

fun newPlanId(context: Context): Long = context.defaultSharedPreferences.newId(PLAN_ID_ID)

const val TIMETABLE_ID_ID = "timetableId"

fun newTimetableId(context: Context): Long = context.defaultSharedPreferences.newId(TIMETABLE_ID_ID)

const val VERTRETUNGSPLAN_ID_ID = "vertretungsplanId"

fun newVertretungsplanId(context: Context): Long = context.defaultSharedPreferences.newId(VERTRETUNGSPLAN_ID_ID)

fun SharedPreferences.newId(key: String): Long{
    return synchronized(this) {
        val id = getLong(key, 0) + 1
        edit {
            putLong(key, id)
        }
        id
    }
}

/** Zum lesen von Einstellugnen**/
fun readGrade(context: Context?): Vertretungsplan.Grade {
    val default = Vertretungsplan.Grade.Q1
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    return when (preference.getString("grade", default.toString())) {
        "EF" -> Vertretungsplan.Grade.EF
        "Q1" -> Vertretungsplan.Grade.Q1
        "Q2" -> Vertretungsplan.Grade.Q2
        else -> {
            preference.edit { putString("grade", default.toString()) }
            default
        }
    }
}

private const val FAVORITE_TIMETABLE_ID_KEY = "favoriteTimetableId"

class GeneralPreferences(private val ctx: Context) {
    val grade: Vertretungsplan.Grade
        get() = readGrade(ctx)
    
    val downloadAllPlansEnabled: Boolean
        get() = readDownloadAllPlans(ctx)
    
    
    var favoriteTimetableId: Long
        set(value) {
            ctx.defaultSharedPreferences.edit { putLong(FAVORITE_TIMETABLE_ID_KEY, value) }
        }
        get() =  ctx.defaultSharedPreferences.getLong(FAVORITE_TIMETABLE_ID_KEY, -1)
}

inline fun <T> ContextOwner.generalPreferences(block: GeneralPreferences.() -> T) {
    block(GeneralPreferences(ctx))
}

inline fun <T> Context.generalPreferences(block: GeneralPreferences.() -> T): T {
    return block(GeneralPreferences(this))
}

/*fun readSortPlan(ctx: Context):Boolean{
    val preference = PreferenceManager.getDefaultSharedPreferences(ctx)
    val value = preference.getString("sort_plan", "")
    return value == "1"
}*/

fun readDownloadAllPlans(context: Context): Boolean = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getBoolean("downloadAllPlans", false)

/**nur für das Widget**/

const val CURRENTLY_MY_PLAN_ID = "com.example.eloem.vertretungsplan.currentlyMyPlan"

fun writeCurrentlyMyPLan(context: Context?, isMyPlan: Boolean) {
    PreferenceManager.getDefaultSharedPreferences(context).edit {
        putBoolean(CURRENTLY_MY_PLAN_ID, isMyPlan)
    }
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