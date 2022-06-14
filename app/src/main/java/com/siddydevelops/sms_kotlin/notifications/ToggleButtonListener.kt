package com.siddydevelops.sms_kotlin.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.siddydevelops.sms_kotlin.DashActivity
import com.siddydevelops.sms_kotlin.data.BroadcastUser
import com.siddydevelops.sms_kotlin.utils.Constants


class ToggleButtonListener : BroadcastReceiver() {

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val sharedPreferences = context.getSharedPreferences("USER_STORE", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val tMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val bu = BroadcastUser(context,Constants.INACTIVE,
                tMgr.line1Number
            )
            DashActivity.setResecState(false)
            DashActivity.setContactState(false)
            editor.putBoolean("STATE",false)
            editor.apply()
            bu.toggleActive(false)
            bu.toggleContact(false)
            SetNotification(context,"InActive")
        }
    }

}