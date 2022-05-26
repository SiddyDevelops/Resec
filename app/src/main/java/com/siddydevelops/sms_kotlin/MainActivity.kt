package com.siddydevelops.sms_kotlin

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var messageTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageTV = findViewById(R.id.message)

        registerReceiver(IncomingSMS(), IntentFilter("broadCastName"))

        val message = intent.getStringExtra("SMS_IC")
        messageTV.text = message

    }

    override fun onPause() {
        super.onPause()
        try {
            if (mServiceReceiver != null) {
                unregisterReceiver(mServiceReceiver)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.intent.action.SmsReceiver")
        registerReceiver(mServiceReceiver, filter)
    }

    private val mServiceReceiver: IncomingSMS = object : IncomingSMS() {
        override fun onReceive(context: Context?, intent: Intent) {
            val IncomingSms = intent.getStringExtra("incomingSms") //

            val phoneNumber = intent.getStringExtra("incomingPhoneNumber")
            Log.d("DATA->","${IncomingSms},$phoneNumber")
        }
    }

}