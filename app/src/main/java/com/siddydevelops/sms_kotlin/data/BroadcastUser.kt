package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.util.Log
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.GetContacts
import com.siddydevelops.sms_kotlin.utils.actions.GetDeviceLocation
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS
import com.siddydevelops.sms_kotlin.utils.actions.SoundProfile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import org.apache.commons.lang3.StringUtils

class BroadcastUser(contextIn: Context, messageIn: String, phoneNumberIn: String) {

    private var myUser: User = User("","")
    private var context: Context
    private var message: String
    private var phoneNumber: String

    private var contactName = Regex("^[a-zA-Z]*\$")

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
                if (message == "Resec.Contacts<${MainActivity.getMyUser()?.userId}><${MainActivity.getMyUser()?.userPin}>") {
                    SendSMS(phoneNumber, Constants.CONTACTS_COMMANDS)
                    //GetContacts(context,'0',"0",phoneNumber)
                }
                if(message.contains("Resec.ContactName")) {
                    GetContacts(context,'0',StringUtils.substringBetween(message,"<",">"),phoneNumber)
                }
                if(message.contains("Resec.Contacts")) {
                    GetContacts(context,message[15],"0",phoneNumber)
                }
                else {
                    SendSMS(phoneNumber, Constants.INVALID_CREDS)
                }
            } else {
                when (message) {
                    Constants.ACTIVE -> SendSMS(phoneNumber, Constants.SEND_ACK)
                    Constants.HELP -> SendSMS(phoneNumber, Constants.MESSAGE_ABOUT)
                    Constants.SOUND_PROFILE_STATUS -> SoundProfile(context,phoneNumber,false)
                    Constants.SOUND_PROFILE_NORMAL -> SoundProfile(context,phoneNumber,true)
                    Constants.LOCATION_COMMAND -> GetDeviceLocation(context,phoneNumber)
                    else -> {
                        SendSMS(phoneNumber, Constants.TRY_AGAIN)
                    }
                }
            }
        }
    }
}