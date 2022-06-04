package com.siddydevelops.sms_kotlin.utils.actions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

@SuppressLint("MissingPermission")
class GetDeviceLocation(context: Context,phoneNumber: String) {

    init {

        val serviceIntent = Intent(context, com.siddydevelops.sms_kotlin.services.LocationServices::class.java)
        serviceIntent.putExtra("PhoneNumber", phoneNumber)
        context.startService(serviceIntent)
    }

}