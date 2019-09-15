package com.example.eloem.vertretungsplan.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.navigation.NavDeepLinkBuilder
import com.example.eloem.vertretungsplan.R
import com.example.eloem.vertretungsplan.database.PlanRepository
import com.example.eloem.vertretungsplan.ui.HostActivity
import com.example.eloem.vertretungsplan.ui.currentplan.CurrentPlanFragmentArgs
import com.example.eloem.vertretungsplan.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerPlanWidgetProvider: AppWidgetProvider() {
    
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        Log.d(TAG, "current appWidgetIds are ${appWidgetIds.joinToString()}")
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
         if (context == null) {
             Log.e(TAG, "cant update appwidgets [${appWidgetIds.joinToString()}], without context")
             return
         }
        for (id in appWidgetIds) {
            Log.d(TAG, "Updating Widget $id")
            val views = RemoteViews(context.packageName, R.layout.appwidget)
            
            val svcIntent = Intent(context, ListViewWidgetService::class.java)
            // Add the app widget ID to the intent extras.
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))
            
            views.setRemoteAdapter(R.id.list, svcIntent)
            views.setEmptyView(R.id.list, R.id.emptyView)
    
            //refresh button
            val refreshIntent = Intent(context, VerPlanWidgetProvider::class.java)
            refreshIntent.action = ACTION_REFRESH
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pRefreshIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.buttonRefresh, pRefreshIntent)
            
            //switcher Text view
            val switchIntent = Intent(context, VerPlanWidgetProvider::class.java)
            switchIntent.action = ACTION_SWITCH
            switchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            switchIntent.data = Uri.parse(switchIntent.toUri(Intent.URI_INTENT_SCHEME))
            val pSwitchIntent = PendingIntent.getBroadcast(context, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.informationTV, pSwitchIntent)
            
            views.setTextViewText(R.id.informationTV, context.resources?.getString(
                    if (context.widgetPreferences(id) { isMyPlan }) R.string.title_myPlan
                    else R.string.app_name)
            )
            
            val pIntent = NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.currentPlanFragment)
                    .setArguments(CurrentPlanFragmentArgs(calledFromAppwidget = id).toBundle())
                    .createPendingIntent()
            
            views.setPendingIntentTemplate(R.id.list, pIntent)
    
            appWidgetManager?.notifyAppWidgetViewDataChanged(id, R.id.list)
            appWidgetManager?.updateAppWidget(id, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
    
    private fun switchPlans(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int){
        val views = RemoteViews(context?.packageName, R.layout.appwidget)
    
        val resources = context?.resources
    
        context?.widgetPreferences(appWidgetId) {
            val isMyPlanLocal = !isMyPlan
    
            views.setTextViewText(R.id.informationTV, resources?.getString(
                    if (isMyPlanLocal) R.string.title_myPlan
                    else R.string.app_name)
            )
    
            isMyPlan = isMyPlanLocal
        }
        
        appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list)
        appWidgetManager?.updateAppWidget(appWidgetId, views)
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "received Intent: $intent")
        when(intent?.action){
            ACTION_REFRESH -> {
            
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS).let {
                    if (it.isEmpty() && context != null) {
                        appWidgetManager.getAppWidgetIds(ComponentName(context, VerPlanWidgetProvider::class.java))
                    } else {
                        it
                    }
                }
                
                Toast.makeText(context, R.string.appwidget_refreshing, Toast.LENGTH_SHORT).show()
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            ACTION_SWITCH -> {
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
                if (appWidgetId == INVALID_APPWIDGET_ID) {
                    Log.e(TAG, "received no appwidgetId with switch intent $intent")
                    return
                }
                Log.d(TAG, "received intent to switch widget with id $appWidgetId")
    
                switchPlans(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
            else -> super.onReceive(context, intent)
        }
    }
    
    companion object {
        const val ACTION_REFRESH = "MY.ACTION_REFRESH"
        const val ACTION_SWITCH = "MY.ACTION_SWITCH"
        const val IS_MY_PLAN_BOOLEAN = "IS.MY.PLAN"
        
        const val INVALID_APPWIDGET_ID = -1
        
        private const val TAG = "VerPlanAppWidgetPro"
    }
}