package com.siddydevelops.sms_kotlin.utils.actions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.siddydevelops.sms_kotlin.services.ForegroundService
import java.util.*

@SuppressLint("MissingPermission")
class GetDeviceLocation(context: Context,phoneNumber: String) {

    init {

        val serviceIntent = Intent(context, com.siddydevelops.sms_kotlin.services.LocationServices::class.java)
        serviceIntent.putExtra("PhoneNumber", phoneNumber)
        context.startService(serviceIntent)
    }

}