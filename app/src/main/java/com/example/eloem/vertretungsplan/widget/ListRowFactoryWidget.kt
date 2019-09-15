package com.example.eloem.vertretungsplan.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.database.PlanRepository
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class ListRowFactoryWidget(override val ctx: Context): RemoteViewsService.RemoteViewsFactory, ContextOwner {
    
    private var isMyPlan = widgetPreferences { isMyPlan }
    private var plan: Vertretungsplan.Plan = getLatestPlan()
    
    override fun onCreate() {
        //nothing
    }
    
    override fun onDestroy() {
        //nothing
    }
    
    override fun getCount(): Int =
        if (plan.status != Vertretungsplan.PlanStatus.OK) 1
        else plan.plan.size
    
    override fun getLoadingView(): RemoteViews? {
        return null
    }
    
    override fun getItemId(p0: Int): Long = p0.toLong()
    
    override fun getViewAt(pos: Int): RemoteViews {
        if (pos == 0 && plan.status != Vertretungsplan.PlanStatus.OK){
            val resources = ctx.resources
            val messageRow = RemoteViews(ctx.packageName, R.layout.widget_row_message)
            messageRow.setOnClickFillInIntent(R.id.messageTV, Intent())
            messageRow.setTextViewText(R.id.messageTV,
                when(plan.status){
                    Vertretungsplan.PlanStatus.NO_PLAN -> resources.getString(R.string.error_message_no_plan)
                    Vertretungsplan.PlanStatus.WRONG_DAY -> resources.getString(R.string.error_message_wrong_day)
                    Vertretungsplan.PlanStatus.OK -> ""
                    Vertretungsplan.PlanStatus.CALCULATION_ERROR -> resources.getString(R.string.error_message_calc)
                    Vertretungsplan.PlanStatus.NO_TIMETABLE -> resources.getString(R.string.error_message_no_timetable)
                }
            )
            return messageRow
        }
        
        val row = RemoteViews(ctx.packageName, R.layout.widget_row)
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
        if (text.isBlank()){
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
    
    private fun getLatestPlan(): Vertretungsplan.Plan = runBlocking(Dispatchers.IO) {
        PlanRepository.create(ctx)
                .getLatestLocalPlan(generalPreferences { grade })!!
                .getRelevantPlan(isMyPlan)
    }
    
    override fun onDataSetChanged() {
        isMyPlan = widgetPreferences { isMyPlan }
        plan = getLatestPlan()
        Log.d("AppWidget", "fetched isMyPlan = $isMyPlan, plan = $plan")
    }
}