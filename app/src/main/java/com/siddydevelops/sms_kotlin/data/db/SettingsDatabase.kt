package com.siddydevelops.sms_kotlin.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

@Database(
    entities = [SettingsItem::class],
    version = 1,
    exportSchema = false
)
abstract class SettingsDatabase : RoomDatabase() {

    abstract fun getSettingDao(): SettingsDAO

    companion object {
        @Volatile
        private var INSTANCE: SettingsDatabase? = null

        fun getDatabase(context: Context): SettingsDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SettingsDatabase::class.java,
                    "settings_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}