package com.siddydevelops.sms_kotlin.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "preference_settings", indices = [Index(value = ["item_start_time"], unique = true)])
data class SettingsItem(
    @ColumnInfo(name = "item_activity")
    val active: Boolean,
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
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "item_start_time")
    val startTime: String
) {
}
