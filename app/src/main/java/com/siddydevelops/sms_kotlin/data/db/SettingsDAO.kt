package com.siddydevelops.sms_kotlin.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

@Dao
interface SettingsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settingsItem: SettingsItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(settingsItem: SettingsItem)

    @Delete
    suspend fun delete(settingsItem: SettingsItem)

    @Query("UPDATE preference_settings SET item_activity=:state WHERE item_start_time = :startTime")
    fun updateState(state: Boolean, startTime: String)

    @Query("SELECT * FROM preference_settings")
    fun getAllSettings(): LiveData<List<SettingsItem>>
}