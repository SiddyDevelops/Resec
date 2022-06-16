package com.siddydevelops.sms_kotlin.services

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import android.util.Log
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem
import com.siddydevelops.sms_kotlin.utils.Constants


class AutomateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // This will be called at Start-Time
        val settingsItem = SettingsItem(
            intent!!.getBooleanExtra(Constants.EXTRA_ACTIVE, true),
            intent.getStringExtra(Constants.EXTRA_SOUND_PROFILE)!!,
            intent.getStringExtra(Constants.EXTRA_VOL_RING)!!,
            intent.getStringExtra(Constants.EXTRA_VOL_MEDIA)!!,
            intent.getStringExtra(Constants.EXTRA_SOUND_NOTIFICATION)!!,
            intent.getStringExtra(Constants.EXTRA_BRIGHTNESS)!!,
            intent.getStringExtra(Constants.EXTRA_START_TIME)!!,
            intent.getStringExtra(Constants.EXTRA_END_TIME)!!
        )
        initiateSettings(context,settingsItem)
    }

    private fun initiateSettings(context: Context,settingsItem: SettingsItem) {
        Log.d("ItemToFunction: ", settingsItem.toString())
        // Change Sound Profile
        val audioManager: AudioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
        when (settingsItem.soundProfile) {
            "NORMAL" -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                // Change Volume
                audioManager.setStreamVolume(
                    AudioManager.STREAM_RING,
                    settingsItem.volRing.toFloat().toInt(),
                    0
                )
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    settingsItem.volMedia.toFloat().toInt(),
                    0
                )
                audioManager.setStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    settingsItem.volNotification.toFloat().toInt(),
                    0
                )
            }
            "VIBRATE" -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                // Change Volume
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    settingsItem.volMedia.toFloat().toInt(),
                    0
                )
                audioManager.setStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    settingsItem.volNotification.toFloat().toInt(),
                    0
                )
            }
            "SILENT" -> {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                // Change Volume
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    settingsItem.volMedia.toFloat().toInt(),
                    0
                )
                audioManager.setStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    settingsItem.volNotification.toFloat().toInt(),
                    0
                )
            }
        }

        // Change Brightness
        val brightness = settingsItem.brightness.toFloat().toInt()
        Settings.System.putInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness
        )
    }
}