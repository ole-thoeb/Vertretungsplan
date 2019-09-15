package com.example.eloem.vertretungsplan.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.database.PlanRepository
import com.example.eloem.vertretungsplan.ui.HostActivity
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerPlanWidgetProvider: AppWidgetProvider() {
    
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        context?.let {  ctx ->
            Log.d(TAG, "updating app widget")
            CoroutineScope(Dispatchers.IO).launch {
                when (PlanRepository.create(ctx).updateVerPlan(ctx.generalPreferences { grade }, ctx)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        Log.d(TAG, "successfuly updated verPlan")
                        updateRest(ctx, appWidgetManager, appWidgetIds)
                    }
                    is Result.Failure -> withContext(Dispatchers.Main) {
                        Log.e(TAG, "failed to update VerPlan")
                        Toast.makeText(ctx, R.string.appwidget_refreshFailed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
     private fun updateRest(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray){
        for (id in appWidgetIds) {
            Log.d("Appwidget", "Updating Widget $id")
            val views = RemoteViews(context?.packageName, R.layout.appwidget)
            
            val svcIntent = Intent(context, ListViewWidgetService::class.java)
            // Add the app widget ID to the intent extras.
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))
            
            views.setRemoteAdapter(R.id.list, svcIntent)
            views.setEmptyView(R.id.list, R.id.emptyView)
    
            //refresh button
            val refreshIntent = Intent(context, VerPlanWidgetProvider::class.java)
            refreshIntent.action = REFRESH_ACTION
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pRefreshIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.buttonRefresh, pRefreshIntent)
            
            //switcher Text view
            val switchIntent = Intent(context, VerPlanWidgetProvider::class.java)
            switchIntent.action = SWITCH_ACTION
            switchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pSwitchIntent = PendingIntent.getBroadcast(context, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.informationTV, pSwitchIntent)
            
            views.setTextViewText(R.id.informationTV, context?.resources?.getString(
                    if (context.widgetPreferences { isMyPlan }) R.string.title_myPlan
                    else R.string.app_name)
            )
            
            //Intent, wenn man item in list klickt -> mainactivity
            val intent = Intent(context, HostActivity::class.java)
            val pIntent = PendingIntent.getActivity(context, Intent.FLAG_ACTIVITY_NEW_TASK, intent, 0)
            views.setPendingIntentTemplate(R.id.list, pIntent)
    
            appWidgetManager?.notifyAppWidgetViewDataChanged(id, R.id.list)
            appWidgetManager?.updateAppWidget(id, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
    
    private fun switchPlans(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray){
        val views = RemoteViews(context?.packageName, R.layout.appwidget)
    
        val resources = context?.resources
    
        context?.widgetPreferences {
            val isMyPlanLocal = !isMyPlan
    
            views.setTextViewText(R.id.informationTV, resources?.getString(
                    if (isMyPlanLocal) R.string.title_myPlan
                    else R.string.app_name)
            )
    
            isMyPlan = isMyPlanLocal
        }
        
        appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)
        appWidgetManager?.updateAppWidget(appWidgetIds, views)
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AppWidget", "received Intent: $intent")
        when(intent?.action){
            REFRESH_ACTION -> {
            
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
    
                Toast.makeText(context, R.string.appwidget_refreshing, Toast.LENGTH_SHORT).show()
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
            }
            SWITCH_ACTION -> {
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
    
                switchPlans(context, AppWidgetManager.getInstance(context), appWidgetIds)
            }
            else -> super.onReceive(context, intent)
        }
    }
    
    companion object {
        const val REFRESH_ACTION = "MY.REFRESH_ACTION"
        const val SWITCH_ACTION = "MY.SWITCH_ACTION"
        const val IS_MY_PLAN_BOOLEAN = "IS.MY.PLAN"
        
        private const val TAG = "VerPlanAppWidgetPro"
    }
}