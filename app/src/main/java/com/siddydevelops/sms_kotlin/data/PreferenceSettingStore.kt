package com.siddydevelops.sms_kotlin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class PreferenceSettingStore(private val context: Context, storeName: String) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = storeName)

    suspend fun storeDate(prefSetting: PreferenceSetting) {
        context.dataStore.edit { pref ->
            pref[SOUND_PROFILE] = prefSetting.SOUND_PROFILE
            pref[VOL_RING] = prefSetting.VOL_RING
            pref[VOL_MEDIA] = prefSetting.VOL_MEDIA
            pref[VOL_NOTIFICATION] = prefSetting.VOL_NOTIFICATION
            pref[BRIGHTNESS] = prefSetting.BRIGHTNESS
            pref[START_TIME] = prefSetting.START_TIME
            pref[END_TIME] = prefSetting.END_TIME
        }
    }

    fun getData() = context.dataStore.data.map { pref ->
        PreferenceSetting(
            SOUND_PROFILE = pref[SOUND_PROFILE] ?: "",
            VOL_RING = pref[VOL_RING] ?: "",
            VOL_MEDIA = pref[VOL_MEDIA] ?: "",
            VOL_NOTIFICATION = pref[VOL_NOTIFICATION] ?: "",
            BRIGHTNESS = pref[BRIGHTNESS] ?: "",
            START_TIME = pref[START_TIME] ?: "",
            END_TIME = pref[END_TIME] ?: "",
        )
    }

    companion object {
        val SOUND_PROFILE = stringPreferencesKey("SOUND_PROFILE")
        val VOL_RING = stringPreferencesKey("VOL_RING")
        val VOL_MEDIA = stringPreferencesKey("VOL_MEDIA")
        val VOL_NOTIFICATION = stringPreferencesKey("VOL_NOTIFICATION")
        val BRIGHTNESS = stringPreferencesKey("BRIGHTNESS")
        val START_TIME = stringPreferencesKey("START_TIME")
        val END_TIME = stringPreferencesKey("END_TIME")
    }
}