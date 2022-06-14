package com.siddydevelops.sms_kotlin.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import com.siddydevelops.sms_kotlin.data.db.SettingsDAO
import com.siddydevelops.sms_kotlin.data.db.SettingsDatabase.Companion.getDatabase
import com.siddydevelops.sms_kotlin.data.db.SettingsRepository
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

class AutomateReceiver : BroadcastReceiver() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var allSettings: LiveData<List<SettingsItem>>
    private var settingsItemList = ArrayList<SettingsItem>()
    private lateinit var dao: SettingsDAO

    override fun onReceive(context: Context, intent: Intent?) {
        // This will be called at Start-Time
        dao = getDatabase(context).getSettingDao()
        settingsRepository = SettingsRepository(dao)
        allSettings = settingsRepository.allSettings
        allSettings.observeForever { list ->
            updateList(list)
        }
        Log.d("Intent",intent!!.getStringExtra("PrefSettingExtra").toString())
    }

    private fun updateList(newList: List<SettingsItem>) {
        settingsItemList.clear()
        settingsItemList.addAll(newList)
        Log.d("SomeListItem",newList.toString())
    }

    private fun initiateSettings(settingsItem: SettingsItem) {
        Log.d("ItemToFunction: ",settingsItem.toString())
    }
}