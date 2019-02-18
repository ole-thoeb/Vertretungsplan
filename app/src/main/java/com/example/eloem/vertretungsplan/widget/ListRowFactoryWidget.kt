package com.example.eloem.vertretungsplan.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*

class ListRowFactoryWidget(val context: Context): RemoteViewsService.RemoteViewsFactory{
    
    private var isMyPlan = readCurrentlyMyPlan(context)
    private var plan: Vertretungsplan.Plan = getLatestVerPlanByGrade(context).getRelevantPlan(isMyPlan)
    
    override fun onCreate() {
        //nothing
    }
    
    override fun onDestroy() {
        //nothing
    }
    
    override fun getCount(): Int =
        if (plan.error != Vertretungsplan.ERROR_NO) 1
        else plan.plan.size
    
    override fun getLoadingView(): RemoteViews? {
        return null
    }
    
    override fun getItemId(p0: Int): Long = p0.toLong()
    
    override fun getViewAt(pos: Int): RemoteViews {
        if (pos == 0 && plan.error != Vertretungsplan.ERROR_NO){
            val resources = context.resources
            val messageRow = RemoteViews(context.packageName, R.layout.widget_row_message)
            messageRow.setOnClickFillInIntent(R.id.messageTV, Intent())
            when(plan.error){
                Vertretungsplan.ERROR_NO_PLAN -> messageRow.setTextViewText(R.id.messageTV, resources.getString(R.string.error_message_no_plan))
                Vertretungsplan.ERROR_WRONG_DAY -> messageRow.setTextViewText(R.id.messageTV, resources.getString(R.string.error_message_wrong_day))
                Vertretungsplan.ERROR_CONNECTION -> messageRow.setTextViewText(R.id.messageTV, resources.getString(R.string.error_message_no_connection))
                else -> messageRow.setTextViewText(R.id.messageTV, resources.getString(R.string.error_message_universal))
            }
            return messageRow
        }
        
        val row = RemoteViews(context.packageName, R.layout.widget_row)
        val verPlanRow = if(isMyPlan) plan.plan[pos]
                         else plan.plan[pos]
        
        setText(row, R.id.lessonTV, verPlanRow.lesson.toString())
        setText(row, R.id.teacherTV, verPlanRow.teacher)
        setText(row, R.id.verTeacherTV, verPlanRow.verTeacher)
        setText(row, R.id.roomTV, verPlanRow.room)
        setText(row, R.id.verRoomTV, verPlanRow.verRoom)
        setText(row, R.id.verTextTV, verPlanRow.verText)
        
        row.setOnClickFillInIntent(R.id.root, Intent())
        return row
    }
    
    private fun setText(row: RemoteViews, layout: Int, pText: String){
        var text = pText
        if (text == "" || text == " "){
            text = "---"
        }
        row.setTextViewText(layout, text)
    }
    
    override fun getViewTypeCount(): Int {
        return 2
    }
    
    override fun hasStableIds(): Boolean {
        return true
    }
    
    override fun onDataSetChanged() {
        isMyPlan = readCurrentlyMyPlan(context)
        plan = getLatestVerPlanByGrade(context).getRelevantPlan(isMyPlan)
        Log.d("AppWidget", "fetched isMyPlan = $isMyPlan, plan = $plan")
    }
}