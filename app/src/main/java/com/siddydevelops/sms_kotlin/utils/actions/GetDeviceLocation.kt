package com.siddydevelops.sms_kotlin.utils.actions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class GetDeviceLocation(context: Context) {
    private var fusedLocationClient: FusedLocationProviderClient

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if(location != null) {
                      Log.d("Location:","${location.latitude}+${location.longitude}")
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}