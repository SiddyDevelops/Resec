package com.siddydevelops.sms_kotlin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.siddydevelops.sms_kotlin.data.DataStoreManager
import com.siddydevelops.sms_kotlin.data.User
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var messageTV: TextView
    private lateinit var userId: EditText
    private lateinit var userPin: EditText
    private lateinit var saveBtn: Button

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataStoreManager = DataStoreManager(this)

        checkPermissions()

        messageTV = findViewById(R.id.message)
        userId = findViewById(R.id.userId)
        userPin = findViewById(R.id.userPin)
        saveBtn = findViewById(R.id.saveBtn)

        GlobalScope.launch(Dispatchers.IO) {
            dataStoreManager.getFromDataStore().catch { e->
                e.printStackTrace()
            }.collect { user->
                setMyUser(user.userId,user.userPin)
                if(user.userId.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        userId.setText(user.userId)
                        userPin.setText(user.userPin)
                        saveBtn.visibility = View.GONE
                    }
                }
                Log.d("UserCreds", getMyUser().toString())
            }
        }

        saveBtn.setOnClickListener {
            if(userId.text.toString().isNotEmpty() && userPin.text.toString().isNotEmpty()) {
                setMyUser(userId.text.toString(),userPin.text.toString())
                GlobalScope.launch(Dispatchers.IO) {
                    dataStoreManager.savetoDataStore(User(userId.text.toString(),userPin.text.toString()))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity,"Credentials Saved Successfully.",Toast.LENGTH_SHORT).show()
                        saveBtn.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(this,"Please fill all the fields.",Toast.LENGTH_SHORT).show()
            }
        }

        registerReceiver(IncomingSMS(), IntentFilter("broadCastName"))

        val message = intent.getStringExtra("SMS_IC")
        messageTV.text = message

    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(mServiceReceiver)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    private fun checkPermissions() {
        if(EasyPermissions.hasPermissions(
                this,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG
            )) {
            Toast.makeText(this,"All permissions are already granted!",Toast.LENGTH_LONG).show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Permissions are required for this application to function.",
                REQUEST_PERMISSION_CODE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Permissions are required for this application to function.",
                REQUEST_PERMISSION_CODE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this,"All permissions are granted!",Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1

        private lateinit var weakSelf: WeakReference<MainActivity>

        @JvmStatic
        fun get(): MainActivity {
            return weakSelf.get()!!
        }

        private var user: User? = null

        fun setMyUser(userId: String,userPin: String) {
            user = User(userId,userPin)
        }

        fun getMyUser() = get().userId.text

    }

    init {
        weakSelf = WeakReference(this)
    }

}