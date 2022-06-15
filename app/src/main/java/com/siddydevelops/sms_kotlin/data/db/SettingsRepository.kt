package com.siddydevelops.sms_kotlin.data.db

import androidx.lifecycle.LiveData
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

class SettingsRepository(private val settingsDAO: SettingsDAO) {
    val allSettings: LiveData<List<SettingsItem>> = settingsDAO.getAllSettings()

    suspend fun insert(settingsItem: SettingsItem) {
        settingsDAO.insert(settingsItem)
    }

    suspend fun update(settingsItem: SettingsItem) {
        settingsDAO.update(settingsItem)
    }

    suspend fun updateState(state: Boolean,startTime: String) {
        settingsDAO.updateState(state,startTime)
    }

    suspend fun delete(settingsItem: SettingsItem) {
        settingsDAO.delete(settingsItem)
    }
}