package com.siddydevelops.sms_kotlin.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.siddydevelops.sms_kotlin.data.BroadcastUser
import com.siddydevelops.sms_kotlin.utils.Constants


class ToggleButtonListener : BroadcastReceiver() {

    @SuppressLint("MissingPermission", "HardwareIds")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val tMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            BroadcastUser(context,Constants.INACTIVE,
                tMgr.line1Number
            ).toggleActive(false)
            SetNotification(context,"InActive")
        }
    }

}