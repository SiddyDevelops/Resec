package com.siddydevelops.sms_kotlin.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.siddydevelops.sms_kotlin.data.db.SettingsDatabase
import com.siddydevelops.sms_kotlin.data.db.SettingsRepository
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    val allSettings: LiveData<List<SettingsItem>>
    private val repository: SettingsRepository

    init {
        val dao = SettingsDatabase.getDatabase(application).getSettingDao()
        repository = SettingsRepository(dao)
        allSettings = repository.allSettings
    }

    fun addSetting(settingsItem: SettingsItem) = viewModelScope.launch(Dispatchers.IO) { repository.insert(settingsItem) }

    fun updateSetting(settingsItem: SettingsItem) = viewModelScope.launch(Dispatchers.IO) { repository.update(settingsItem) }

    fun updateSettingState(state: Boolean,startTime: String) = viewModelScope.launch(Dispatchers.IO) { repository.updateState(state,startTime) }

    fun deleteSetting(settingsItem: SettingsItem) = viewModelScope.launch(Dispatchers.IO) { repository.delete(settingsItem) }
}