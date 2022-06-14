package com.siddydevelops.sms_kotlin.utils.actions

import android.app.Service
import android.content.Context
import android.media.AudioManager


class SoundProfile(context: Context, phoneNumber: String, soundNormal: Boolean) {

    init {
        val audioManager: AudioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
        if(soundNormal) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            SendSMS(phoneNumber,"Sound Profile changed to Normal-Mode.")
        } else {
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> SendSMS(phoneNumber,"Your device is in Silent-Mode. Use <Resec.SoundNormal> to set profile to normal.")
                AudioManager.RINGER_MODE_VIBRATE -> SendSMS(phoneNumber,"Your device is in Vibrate-Mode. Use <Resec.SoundNormal> to set profile to normal.")
                AudioManager.RINGER_MODE_NORMAL -> SendSMS(phoneNumber,"Your device is in Normal-Mode.")
            }
        }

    }

}