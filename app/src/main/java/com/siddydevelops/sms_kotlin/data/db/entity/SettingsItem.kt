package com.siddydevelops.sms_kotlin.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.siddydevelops.sms_kotlin.data.PreferenceSetting

@Entity(tableName = "preference_settings")
data class SettingsItem(
    @ColumnInfo(name = "item_name")
    val name: String,
    @ColumnInfo(name = "item_preferences")
    val preferenceSetting: PreferenceSetting
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
