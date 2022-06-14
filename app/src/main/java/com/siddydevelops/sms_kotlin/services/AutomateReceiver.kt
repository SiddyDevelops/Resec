package com.siddydevelops.sms_kotlin.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutomateReceiver(context: Context) : BroadcastReceiver() {

    init {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, YourAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }

}