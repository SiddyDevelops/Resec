package com.siddydevelops.sms_kotlin.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ToggleButtonListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            SetNotification(context,"InActive")
        }
    }

}