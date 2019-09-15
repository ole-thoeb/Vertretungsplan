package com.example.eloem.vertretungsplan.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService
import com.example.eloem.vertretungsplan.util.ifNull

class ListViewWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        Log.d(TAG, "triggert")
        val appwidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, VerPlanWidgetProvider.INVALID_APPWIDGET_ID).ifNull {
            Log.e(TAG, "intent was null")
            VerPlanWidgetProvider.INVALID_APPWIDGET_ID
        }
        return ListRowFactoryWidget(applicationContext, appwidgetId)
    }
    
    companion object {
        private const val TAG = "ListViewWidgetService"
    }
}