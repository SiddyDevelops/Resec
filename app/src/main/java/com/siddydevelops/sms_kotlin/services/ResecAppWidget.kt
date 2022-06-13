package com.siddydevelops.sms_kotlin.services

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.siddydevelops.sms_kotlin.R


/**
 * Implementation of App Widget functionality.
 */
class ResecAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(com.siddydevelops.sms_kotlin.R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, com.siddydevelops.sms_kotlin.R.layout.resec_app_widget)
    views.setTextViewText(com.siddydevelops.sms_kotlin.R.id.appwidget_text, widgetText)

    val intent = Intent(context, WidgetRemoteViewsService::class.java)
    views.setRemoteAdapter(com.siddydevelops.sms_kotlin.R.id.widgetListView, intent)
    appWidgetManager.updateAppWidget(appWidgetId, views)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}