package com.siddydevelops.sms_kotlin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.siddydevelops.sms_kotlin.MainActivity.Companion.get
import com.siddydevelops.sms_kotlin.MainActivity.Companion.getMyUser
import com.siddydevelops.sms_kotlin.utils.Constants


open class IncomingSMS : BroadcastReceiver() {

    private var c: Context? = null
    private var phoneNumber: String? = null

    private var application = Application()

    override fun onReceive(context: Context?, intent: Intent) {
        if (context != null) {
            c = context
        }
        val smsManager: SmsManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                val toast = Toast.makeText(
                    context,
                    "SenderNum: $phoneNumber, Message: $message", duration
                )
                toast.show()
//                val ic = Intent(context, MainActivity::class.java)
//                ic.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                ic.putExtra("SMS_IC", message)
//                context!!.startActivity(ic)

                val i = Intent("android.intent.action.SmsReceiver").putExtra("incomingSms", message)
                i.putExtra("incomingPhoneNumber", phoneNumber)
                context!!.sendBroadcast(i)
                when(message) {
                    Constants.ACTIVE -> smsSendMessage(Constants.SEND_ACK)
                }
                //application.setMyUser("USER","PIN")
                //Log.d("UserCreds", application.getMyUser()?.userId!!)
                Log.d("UserCreds", getMyUser().toString())

                //getContactList()
//                if(message == "Update") {
//                    smsSendMessage("By Siddy-Develops")
//                } else {
//                        smsSendMessage("Please try again.")
//                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
        }
    }

    @SuppressLint("Range")
    private fun getContactList() {
        //val cr: ContentResolver = context.getContentResolver()
        val cr: ContentResolver = c!!.contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur?.moveToNext()!!) {
                        val phoneNo: String = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        Log.i("ContactName", "Name: $name")
                        Log.i("ContactPhone", "Phone Number: $phoneNo")
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
    }

    private fun smsSendMessage(message: String) {
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            phoneNumber, null, message,
            null, null
        )
    }

}