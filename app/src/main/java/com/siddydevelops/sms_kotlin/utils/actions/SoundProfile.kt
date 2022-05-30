package com.siddydevelops.sms_kotlin.utils.actions

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.util.Log


class SoundProfile(context: Context, phoneNumber: String, soundNormal: Boolean) {

    init {
        val audioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        if(soundNormal) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        } else {
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> SendSMS(phoneNumber,"Your device is in Silent-Mode.")
                AudioManager.RINGER_MODE_VIBRATE -> SendSMS(phoneNumber,"Your device is in Vibrate-Mode.")
                AudioManager.RINGER_MODE_NORMAL -> SendSMS(phoneNumber,"Your device is in Normal-Mode.")
            }
        }
    }

}