package com.siddydevelops.sms_kotlin.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "preference_settings", indices = [Index(value = ["item_start_time"], unique = true)])
data class SettingsItem(
    @ColumnInfo(name = "item_name")
    val name: String,
    @ColumnInfo(name = "item_sound_profile")
    val soundProfile: String,
    @ColumnInfo(name = "item_vol_ring")
    val volRing: String,
    @ColumnInfo(name = "item_vol_media")
    val volMedia: String,
    @ColumnInfo(name = "item_vol_notification")
    val volNotification: String,
    @ColumnInfo(name = "item_brightness")
    val brightness: String,
    @ColumnInfo(name = "item_start_time")
    val startTime: String,
    @ColumnInfo(name = "item_end_time")
    val endTime: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
