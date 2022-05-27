package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.util.Log
import com.siddydevelops.sms_kotlin.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BroadcastUser(context: Context) {
    init {
        GlobalScope.launch(Dispatchers.IO) {
            context.let {
                DataStoreManager(it).getFromDataStore().catch { e->
                    e.printStackTrace()
                }.collect { user->
                    MainActivity.setMyUser(user.userId, user.userPin)
                    if(user.userId.isNotEmpty()) {
                        Log.d("UserCredsB", MainActivity.getMyUser().toString())
                    }
                }
            }
        }
    }
}