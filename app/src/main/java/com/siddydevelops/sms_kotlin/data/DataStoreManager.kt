package com.siddydevelops.sms_kotlin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = userDatastore)

    companion object {
        const val userDatastore = "USER_DATASTORE"
        val userID = stringPreferencesKey("USER_ID")
        val userPin = stringPreferencesKey("USER_PIN")
    }

    suspend fun savetoDataStore(user: User) {
        context.dataStore.edit {
            it[userID] = user.userId
            it[userPin] = user.userPin
        }
    }

    suspend fun getFromDataStore() = context.dataStore.data.map {
        User(
            userId = it[userID]?:"",
            userPin = it[userPin]?:""
        )
    }

}

