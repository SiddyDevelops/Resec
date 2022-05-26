package com.siddydevelops.sms_kotlin

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import com.siddydevelops.sms_kotlin.data.DataStoreManager
import com.siddydevelops.sms_kotlin.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Application : Application() {

    private var user: User? = null
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        //Initialize here
        //context = applicationContext
//        GlobalScope.launch(Dispatchers.IO) {
//            DataStoreManager(applicationContext).getFromDataStore().catch { e->
//                e.printStackTrace()
//            }.collect {
//                if(it.userId.isNotEmpty()) {
//                    user = it
//                }
//            }
//        }
    }

    fun setMyUser(userId: String,userPin: String) {
        user = User(userId,userPin)
    }

    fun getMyUser(): User? {
        return user
    }

    init {
        //user = User("","")
//        GlobalScope.launch(Dispatchers.IO) {
//            DataStoreManager(context).getFromDataStore().catch { e->
//                e.printStackTrace()
//            }.collect {
//                if(it.userId.isNotEmpty()) {
//                    user = it
//                }
//            }
//        }
    }

}