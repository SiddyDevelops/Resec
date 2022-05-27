package com.siddydevelops.sms_kotlin.utils.actions

import android.telephony.SmsManager

class SendSMS(phoneNumber: String,message: String) {
    init {
        val smsManager: SmsManager = SmsManager.getDefault()

        smsManager.sendTextMessage(phoneNumber,
            null,
            message,
            null,
            null)
    }
}