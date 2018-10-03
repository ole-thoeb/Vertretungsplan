package com.example.eloem.vertretungsplan.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ProgressBar
import android.widget.RemoteViews
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eloem.vertretungsplan.MainActivity
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.helperClasses.Vertretungsplan
import com.example.eloem.vertretungsplan.util.*

class VerPlanWidgetProvider: AppWidgetProvider() {
    
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        //plan neu abrufen

        val queue = Volley.newRequestQueue(context)

        val url = getUrl(readGrade(context))

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val verPlan = parseHtml(context, response)
                    writeVertretungsplan(verPlan, context)
                    
                    updateRest(context, appWidgetManager, appWidgetIds)
                },
                Response.ErrorListener { _ ->
                    val verPlan = Vertretungsplan()
                    verPlan.setError(Vertretungsplan.ERROR_CONNECTION)
                    writeVertretungsplan(verPlan, context)

                    updateRest(context, appWidgetManager, appWidgetIds)
                })

        queue.add(stringRequest)
    }
    
    private fun updateRest(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray){
        for (id in appWidgetIds) {
            val views = RemoteViews(context?.packageName, R.layout.appwidget)
            
            views.setViewVisibility(R.id.progressBar, ProgressBar.GONE)
            
            val svcIntent = Intent(context, ListViewWidgetService::class.java)
            // Add the app widget ID to the intent extras.
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            svcIntent.putExtra(IS_MY_PLAN_BOOLEAN, false)
            svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))
    
            views.setRemoteAdapter(R.id.list, svcIntent)
            views.setEmptyView(R.id.list, R.id.emptyView)
    
            //refresh button
            val refreshIntent = Intent(context, VerPlanWidgetProvider::class.java)
            refreshIntent.action = VerPlanWidgetProvider.REFRESH_ACTION
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pRefreshIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.buttonRefresh, pRefreshIntent)
            
            //switcher Text view
            val switchIntent = Intent(context, VerPlanWidgetProvider::class.java)
            switchIntent.action = VerPlanWidgetProvider.SWITCH_ACTION
            switchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pSwitchIntent = PendingIntent.getBroadcast(context, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.informationTV, pSwitchIntent)
            
            //Intent, wenn man item in list klickt -> mainactivity
            val intent = Intent(context, MainActivity::class.java)
            val pIntent = PendingIntent.getActivity(context, Intent.FLAG_ACTIVITY_NEW_TASK, intent, 0)
            views.setPendingIntentTemplate(R.id.list, pIntent)
    
            appWidgetManager?.updateAppWidget(id, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        
        if (intent?.action == VerPlanWidgetProvider.REFRESH_ACTION) {
            
            val extras = intent.extras
            
            if (extras != null) {
                
                val appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                    updateList(context, appWidgetIds)
                }
            }
        }else if (intent?.action == VerPlanWidgetProvider.SWITCH_ACTION){
            val views = RemoteViews(context?.packageName, R.layout.appwidget)
            
            val resources = context?.resources
            
            val isMyPlan = readCurrentlyMyPlan(context)
            
            if (!isMyPlan) views.setTextViewText(R.id.informationTV, resources?.getString(R.string.title_myPlan))
            else views.setTextViewText(R.id.informationTV, resources?.getString(R.string.app_name))
            
            writeCurrentlyMyPLan(context, !isMyPlan)
            
            val extras = intent.extras
            println(extras)
            if (extras != null) {
                val appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)
                appWidgetManager.updateAppWidget(appWidgetIds, views)
            }
        }
    }
    
    private fun updateList(context: Context?, appWidgetIds: IntArray){
        val appWidgetManager = AppWidgetManager.getInstance(context)
        
        val views = RemoteViews(context?.packageName, R.layout.appwidget)
        
        views.setViewVisibility(R.id.progressBar, ProgressBar.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetIds, views)
        
        //plan neu abrufen
        val queue = Volley.newRequestQueue(context)
    
        val url = getUrl(readGrade(context))
    
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val verPlan = parseHtml(context, response)
                    writeVertretungsplan(verPlan, context)
    
                    views.setViewVisibility(R.id.progressBar, ProgressBar.GONE)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)
                    
                    appWidgetManager.updateAppWidget(appWidgetIds, views)
                },
                Response.ErrorListener { _ ->
                    val verPlan = Vertretungsplan()
                    verPlan.setError(Vertretungsplan.ERROR_CONNECTION)
                    writeVertretungsplan(verPlan, context)
    
                    views.setViewVisibility(R.id.progressBar, ProgressBar.GONE)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)
                    
                    appWidgetManager.updateAppWidget(appWidgetIds, views)
                })
    
        queue.add(stringRequest)
    }
    
    companion object {
        const val REFRESH_ACTION = "MY.REFRESH_ACTION"
        const val SWITCH_ACTION = "MY.SWITCH_ACTION"
        const val IS_MY_PLAN_BOOLEAN = "IS.MY.PLAN"
    }
}