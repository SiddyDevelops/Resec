package com.siddydevelops.sms_kotlin.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.R
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS
import java.util.*

@SuppressLint("MissingPermission")
class LocationServices : Service() {

    private val CHANNEL_ID = "LocationService Kotlin"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var address: List<Address>

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();
        val phoneNumber = intent!!.getStringExtra("PhoneNumber")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if(location != null) {
                        Log.d("Location:","${location.latitude}+${location.longitude}")
                        address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        SendSMS(phoneNumber!!,"Your device location:\n${address[0].subThoroughfare}, ${address[0].subLocality}, ${address[0].locality}, ${address[0].adminArea}, ${address[0].postalCode}")
                        SendSMS(phoneNumber,"\n" +
                                "Click this link to follow:\n" +
                                "https://www.google.com/maps/search/?api=1&query=${location.latitude}%2C${location.longitude}")
                    } else {
                        SendSMS(phoneNumber!!, "The location cannot be found. The location-services might be turned off on your device.")
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
            SendSMS(phoneNumber!!, "The location cannot be found. The location might be turned off on your device.")
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Resec Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

}