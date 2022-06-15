package com.siddydevelops.sms_kotlin.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.siddydevelops.sms_kotlin.DashActivity
import com.siddydevelops.sms_kotlin.notifications.SetNotification
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.*
import org.apache.commons.lang3.StringUtils
import kotlin.properties.Delegates

class BroadcastUser(contextIn: Context, messageIn: String, phoneNumberIn: String) {

    private var context: Context
    private var message: String
    private var phoneNumber: String
    private var activeBool by Delegates.notNull<Boolean>()
    private var contactBool = false

    private var  sharedPreferences: SharedPreferences

    init {
        context = contextIn
        message = messageIn
        phoneNumber = phoneNumberIn

        sharedPreferences = context.getSharedPreferences("USER_STORE", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        activeBool = DashActivity.getResecState()

        if(sharedPreferences.contains("UserID")) {
            DashActivity.setMyUser(
                sharedPreferences.getString("UserID", "default")!!,
                sharedPreferences.getString("UserPin", "default")!!
            )
            DashActivity.setResecState(sharedPreferences.getBoolean("STATE",true))
            Log.d("STATE",activeBool.toString())
        }

        if(message == Constants.ACTIVE) {
            SendSMS(phoneNumber, Constants.SEND_ACK)
            editor.putBoolean("STATE",true)
            editor.apply()
            toggleActive(true)
            SetNotification(context,"Active")
        }

        if(activeBool) {
            smsCommands()
        } else if(message == Constants.SEND_NACK) {
            Unit
        } else{
            SendSMS(phoneNumber,Constants.SEND_NACK)
        }

    }

    private fun smsCommands() {
        if (message.contains("Resec.Contact")) {
            contactBool = DashActivity.getContactState()
            if (message == "Resec.Contacts<${DashActivity.getMyUser()?.userId}><${DashActivity.getMyUser()?.userPin}>") {
                SendSMS(phoneNumber, Constants.CONTACTS_COMMANDS)
                toggleContact(true)
                DashActivity.setContactState(true)
            }
            else if(message.contains("Resec.ContactName") && contactBool) {
                GetContacts(context,'0',StringUtils.substringBetween(message,"<",">"),phoneNumber)
            }
            else if(message.contains("Resec.Contacts") && contactBool) {
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
                Constants.INACTIVE -> {
                    SendSMS(phoneNumber,Constants.SEND_NACK)
                    //Background mein Kam nhi karega
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("STATE",false)
                    editor.apply()
                    SetNotification(context,"InActive")
                    DashActivity.setResecState(false)
                    DashActivity.setContactState(false)
                    toggleActive(false)
                    toggleContact(false)
                }
                Constants.SOUND_PROFILE_STATUS -> SoundProfile(context,phoneNumber,false)
                Constants.SOUND_PROFILE_NORMAL -> SoundProfile(context,phoneNumber,true)
                Constants.LOCATION_COMMAND -> GetDeviceLocation(context,phoneNumber)
                Constants.LOCK_COMMAND -> LockScreen(context,phoneNumber)
                Constants.ACTIVE -> Unit
                else -> SendSMS(phoneNumber, Constants.TRY_AGAIN)
            }
        }
    }

    fun toggleActive(bool: Boolean){
        activeBool = bool
    }

    fun toggleContact(bool: Boolean){
        contactBool = bool
    }

}