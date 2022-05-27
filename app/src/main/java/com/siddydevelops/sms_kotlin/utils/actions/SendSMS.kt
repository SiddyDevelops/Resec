package com.siddydevelops.sms_kotlin.utils.actions

import android.telephony.SmsManager
import android.util.Log

class SendSMS(phoneNumber: String,message: String) {
    init {
        Log.d("Det",phoneNumber)
        val smsManager: SmsManager = SmsManager.getDefault()

        smsManager.sendTextMessage(phoneNumber,
            null,
            message,
            null,
            null)
    }
}