package com.siddydevelops.sms_kotlin.utils.actions

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager


class SoundProfile(context: Context) {

    init {

        val audioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

    }

}