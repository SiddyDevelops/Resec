package com.siddydevelops.sms_kotlin.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.siddydevelops.sms_kotlin.data.PreferenceSetting

@Entity(tableName = "preference_settings")
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
    val VolNotification: String,
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
