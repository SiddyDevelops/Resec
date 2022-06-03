package com.siddydevelops.sms_kotlin.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.siddydevelops.sms_kotlin.services.ForegroundService

class ToggleButtonListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            ForegroundService.stopService(context)
            SetNotification(context,"InActive")
        }
    }

}