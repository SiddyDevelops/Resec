package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.util.Log
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.GetContacts
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BroadcastUser(context: Context,message: String, phoneNumber: String) {
    init {
        GlobalScope.launch(Dispatchers.IO) {
            context.let {
                DataStoreManager(it).getFromDataStore().catch { e->
                    e.printStackTrace()
                }.collect { user->
                    MainActivity.setMyUser(user.userId, user.userPin)
                    if(user.userId.isNotEmpty()) {

                    }
                }
            }
        }

        if(message.contains("{}")) {
            if(message == "Resec.Contact{${MainActivity.getMyUser()?.userId}}{${MainActivity.getMyUser()?.userPin}}") {
                GetContacts(context)
            } else {
                SendSMS(phoneNumber, Constants.INVALID_CREDS)
            }
        }

        when(message) {
            Constants.ACTIVE -> SendSMS(phoneNumber, Constants.SEND_ACK)
            Constants.HELP -> SendSMS(phoneNumber, Constants.MESSAGE_ABOUT)
            else -> {
                SendSMS(phoneNumber, Constants.TRY_AGAIN)
            }
        }

    }
}