package com.siddydevelops.sms_kotlin.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ToggleButtonListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Btn Clicked:", "Toggle")
    }

}