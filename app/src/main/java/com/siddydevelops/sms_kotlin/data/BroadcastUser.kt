package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.notifications.SetNotification
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.*
import org.apache.commons.lang3.StringUtils

class BroadcastUser(contextIn: Context, messageIn: String, phoneNumberIn: String) {

    private var context: Context
    private var message: String
    private var phoneNumber: String
    private var activeBool = false
    private var contactBool = false

    private var  sharedPreferences: SharedPreferences

    init {
        context = contextIn
        message = messageIn
        phoneNumber = phoneNumberIn

        sharedPreferences = context.getSharedPreferences("USER_STORE", Context.MODE_PRIVATE)

        if(sharedPreferences.contains("UserID")) {
            MainActivity.setMyUser(
                sharedPreferences.getString("UserID", "default")!!,
                sharedPreferences.getString("UserPin", "default")!!
            )
        }

        if(message == Constants.ACTIVE) {
            SendSMS(phoneNumber, Constants.SEND_ACK)
            toggleActive(true)
            SetNotification(context,"Active")
        }

        if(activeBool) {
            smsCommands()
        } else {
            SendSMS(phoneNumber,Constants.SEND_NACK)
        }

    }

    private fun smsCommands() {
        if (message.contains("Resec.Contact")) {
            if (message == "Resec.Contacts<${MainActivity.getMyUser()?.userId}><${MainActivity.getMyUser()?.userPin}>") {
                SendSMS(phoneNumber, Constants.CONTACTS_COMMANDS)
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
                Constants.COMMANDS -> {
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS0)
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS1)
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS2)
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS3)
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS4)
                    SendSMS(phoneNumber, Constants.MESSAGE_COMMANDS5)
                }
                Constants.HELP -> SendSMS(phoneNumber, Constants.MESSAGE_ABOUT)
                Constants.INACTIVE -> SendSMS(phoneNumber, Constants.SEND_NACK)
                Constants.SOUND_PROFILE_STATUS -> SoundProfile(context,phoneNumber,false)
                Constants.SOUND_PROFILE_NORMAL -> SoundProfile(context,phoneNumber,true)
                Constants.LOCATION_COMMAND -> GetDeviceLocation(context,phoneNumber)
                Constants.LOCK_COMMAND -> LockScreen(context,phoneNumber)
                else -> SendSMS(phoneNumber, Constants.TRY_AGAIN)
            }
        }
    }

    fun toggleActive(bool: Boolean){
        activeBool = bool
        contactBool = bool
    }

}