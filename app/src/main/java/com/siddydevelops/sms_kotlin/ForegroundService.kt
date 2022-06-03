package com.siddydevelops.sms_kotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }
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
        val normalMode = intent.getBooleanExtra("NormalBool",false)
        val audioManager: AudioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        if(normalMode) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            SendSMS(phoneNumber!!,"Sound Profile changed to Normal-Mode.")
        } else {
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> SendSMS(phoneNumber!!,"Your device is in Silent-Mode.")
                AudioManager.RINGER_MODE_VIBRATE -> SendSMS(phoneNumber!!,"Your device is in Vibrate-Mode.")
                AudioManager.RINGER_MODE_NORMAL -> SendSMS(phoneNumber!!,"Your device is in Normal-Mode.")
            }
        }

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Resec Sound Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}