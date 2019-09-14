package com.example.eloem.vertretungsplan.widget

import android.content.Intent
import android.widget.RemoteViewsService

class ListViewWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        println("triggerd")
        return ListRowFactoryWidget(applicationContext)
    }
}