package com.siddydevelops.sms_kotlin.utils.actions

import android.content.Context
import android.content.Intent
import com.siddydevelops.sms_kotlin.services.ForegroundService


class SoundProfile(context: Context, phoneNumber: String, soundNormal: Boolean) {

    init {
        val serviceIntent = Intent(context, ForegroundService::class.java)
        serviceIntent.putExtra("PhoneNumber", phoneNumber)
        serviceIntent.putExtra("NormalBool", soundNormal)
        context.startService(serviceIntent)
    }

}