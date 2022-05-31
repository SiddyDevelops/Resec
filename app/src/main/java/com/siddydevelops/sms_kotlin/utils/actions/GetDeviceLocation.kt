package com.siddydevelops.sms_kotlin.utils.actions

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

@SuppressLint("MissingPermission")
class GetDeviceLocation(context: Context,phoneNumber: String) {
    private var fusedLocationClient: FusedLocationProviderClient
    private var geocoder: Geocoder
    private lateinit var address: List<Address>

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        geocoder = Geocoder(context, Locale.getDefault())
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if(location != null) {
                      Log.d("Location:","${location.latitude}+${location.longitude}")
                        address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        SendSMS(phoneNumber,"Your device location:\n${address[0].locality}, ${address[0].adminArea}, ${address[0].postalCode}")
                    } else {
                        SendSMS(phoneNumber, "The location cannot be found. The location might be turned off on your device.")
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
            SendSMS(phoneNumber, "The location cannot be found. The location might be turned off on your device.")
        }
    }

}