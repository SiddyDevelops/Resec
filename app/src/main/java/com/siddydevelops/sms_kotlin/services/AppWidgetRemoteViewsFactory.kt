package com.siddydevelops.sms_kotlin.services

import android.content.Context
import android.util.Log
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import androidx.lifecycle.LiveData
import com.siddydevelops.sms_kotlin.R
import com.siddydevelops.sms_kotlin.data.db.SettingsDAO
import com.siddydevelops.sms_kotlin.data.db.SettingsDatabase.Companion.getDatabase
import com.siddydevelops.sms_kotlin.data.db.SettingsRepository
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

class AppWidgetRemoteViewsFactory(private val context: Context) : RemoteViewsFactory {

    private var settingsRepository: SettingsRepository
    private lateinit var allSettings: LiveData<List<SettingsItem>>
    private var settingsItemList = ArrayList<SettingsItem>()
    private var dao: SettingsDAO = getDatabase(context).getSettingDao()

    init {
        settingsRepository = SettingsRepository(dao)
    }

    override fun onCreate() {
        allSettings = settingsRepository.allSettings
        allSettings.observeForever { list->
            updateList(list)
        }
        Log.d("Widget", "Init: New")
    }

    override fun onDataSetChanged() {
        allSettings = settingsRepository.allSettings
//        allSettings.observeForever { list->
//            updateList(list)
//        }
    }

    private fun updateList(newList: List<SettingsItem>) {
        settingsItemList.clear()
        settingsItemList.addAll(newList)
        Log.d("SomeListItem",newList.toString())
        onDataSetChanged()
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return settingsItemList.size
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || settingsItemList.isEmpty()) {
            return null
        }

        val rv = RemoteViews(context.packageName, R.layout.widget_item_layout)
        rv.setTextViewText(R.id.soundProfileTV, settingsItemList[position].soundProfile)
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 0
    }

    override fun getItemId(p0: Int): Long {
        return settingsItemList.size.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}