package com.siddydevelops.sms_kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.siddydevelops.sms_kotlin.data.BroadcastUser
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.actions.SendSMS


open class IncomingSMS : BroadcastReceiver() {

    private var c: Context? = null
    private var phoneNumber: String? = null

    override fun onReceive(context: Context?, intent: Intent) {
        if (context != null) {
            c = context
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
        val bundle: Bundle? = intent.extras

        try {
            val pdusObj = bundle!!["pdus"] as Array<*>?
            for (i in pdusObj!!.indices) {
                val currentMessage = SmsMessage.createFromPdu(
                    pdusObj[i] as ByteArray?
                )
                phoneNumber = currentMessage.displayOriginatingAddress
                val message = currentMessage.displayMessageBody
                Log.i("SmsReceiver", "senderNum: $phoneNumber; message: $message")
                val duration = Toast.LENGTH_LONG
                Toast.makeText(
                    context,
                    "SenderNum: $phoneNumber, Message: $message", duration
                ).show()
//                val ic = Intent(context, MainActivity::class.java)
//                ic.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                ic.putExtra("SMS_IC", message)
//                context!!.startActivity(ic)

                val i = Intent("android.intent.action.SmsReceiver").putExtra("incomingSms", message)
                i.putExtra("incomingPhoneNumber", phoneNumber)
                context!!.sendBroadcast(i)

                if(message.contains("Resec")) {
                    BroadcastUser(context,message,phoneNumber!!)
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
        }
    }

}