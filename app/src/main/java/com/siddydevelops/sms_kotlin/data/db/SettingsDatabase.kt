package com.siddydevelops.sms_kotlin.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem

@Database(
    entities = [SettingsItem::class],
    version = 1
)
abstract class SettingsDatabase : RoomDatabase() {

    abstract fun getSettingDao(): SettingsDAO

    companion object {
        @Volatile
        private var instance: SettingsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                SettingsDatabase::class.java,"SettingsDB.db").build()
    }

}