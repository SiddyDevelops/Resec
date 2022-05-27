package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.util.Log
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.GetContacts
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch

class BroadcastUser(contextIn: Context, messageIn: String, phoneNumberIn: String) {

    private var myUser: User = User("","")
    private var context: Context
    private var message: String
    private var phoneNumber: String

    init {
//        GlobalScope.launch(Dispatchers.IO) {
//            context.let {
//                DataStoreManager(it).getFromDataStore().catch { e ->
//                    e.printStackTrace()
//                }.collect { user ->
//                    MainActivity.setMyUser(user.userId, user.userPin)
//                }
//            }
//        }

        context = contextIn
        message = messageIn
        phoneNumber = phoneNumberIn

        GlobalScope.launch(Dispatchers.Main){
            getInitUser()
        }

    }

    private suspend fun getInitUser() {
        GlobalScope.async {
            context.let {
                DataStoreManager(it).getFromDataStore().catch { e ->
                    e.printStackTrace()
                }.collect { user ->
                    MainActivity.setMyUser(user.userId, user.userPin)
                    user
                }
            }
            if (message.contains("Resec.Contact")) {
                Log.d("UserDetails", MainActivity.getMyUser().toString())
                if (message == "Resec.Contact<${MainActivity.getMyUser()?.userId}><${MainActivity.getMyUser()?.userPin}>") {
                    GetContacts(context,'0',"0")
                } else {
                    SendSMS(phoneNumber, Constants.INVALID_CREDS)
                }
            } else {
                when (message) {
                    Constants.ACTIVE -> SendSMS(phoneNumber, Constants.SEND_ACK)
                    Constants.HELP -> SendSMS(phoneNumber, Constants.MESSAGE_ABOUT)
                    else -> {
                        SendSMS(phoneNumber, Constants.TRY_AGAIN)
                    }
                }
            }
        }
    }
}