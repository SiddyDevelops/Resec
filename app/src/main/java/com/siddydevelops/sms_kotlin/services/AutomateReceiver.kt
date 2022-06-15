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
import com.siddydevelops.sms_kotlin.utils.Constants

class AutomateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // This will be called at Start-Time
        val settingsItem = SettingsItem(
            intent!!.getBooleanExtra(Constants.EXTRA_ACTIVE, true),
            intent.getStringExtra(Constants.EXTRA_SOUND_PROFILE)!!,
            intent.getStringExtra(Constants.EXTRA_VOL_RING)!!,
            intent.getStringExtra(Constants.EXTRA_VOL_MEDIA)!!,
            intent.getStringExtra(Constants.EXTRA_SOUND_NOTIFICATION)!!,
            intent.getStringExtra(Constants.EXTRA_BRIGHTNESS)!!,
            intent.getStringExtra(Constants.EXTRA_START_TIME)!!,
            intent.getStringExtra(Constants.EXTRA_END_TIME)!!
        )
        initiateSettings(settingsItem)
    }

    private fun initiateSettings(settingsItem: SettingsItem) {
        Log.d("ItemToFunction: ", settingsItem.toString())
    }
}