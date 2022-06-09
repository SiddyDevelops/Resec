package com.siddydevelops.sms_kotlin

import android.Manifest
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.*
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.siddydevelops.sms_kotlin.data.User
import com.siddydevelops.sms_kotlin.notifications.SetNotification
import com.siddydevelops.sms_kotlin.utils.admin.DeviceAdmin
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var userId: EditText
    private lateinit var userPin: EditText
    private lateinit var saveBtn: Button
    private lateinit var stateBtn: Button
    private lateinit var brightBtn: Button
    private lateinit var addPrefSetting: Button
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var pin_visibility: ImageView
    private lateinit var stateTV: TextView

    private var brightness: Int = 0
    private lateinit var cResolver: ContentResolver
    private lateinit var w: Window

    private var brightPermission = false
    private var uidBtnAction = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        userId = findViewById(R.id.userId)
        userPin = findViewById(R.id.userPin)
        saveBtn = findViewById(R.id.saveBtn)
        stateBtn = findViewById(R.id.stateBtn)
        brightBtn = findViewById(R.id.brightBtn)
        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        pin_visibility = findViewById(R.id.pin_visibility)
        stateTV = findViewById(R.id.stateTV)
        addPrefSetting = findViewById(R.id.addPrefSetting)

        addPrefSetting.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView: View = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, viewGroup, false)
            builder.setView(dialogView)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        stateBtn.setOnClickListener {
            SetNotification(this, getString(R.string.resec_state_active))
            stateTV.text = getString(R.string.resec_state_active)
            stateTV.setTextColor(resources.getColor(R.color.green))
            stateBtn.visibility = View.GONE
        }

        sharedPreferences = getSharedPreferences("USER_STORE", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("UserID")) {
            setMyUser(
                sharedPreferences.getString("UserID", "default")!!,
                sharedPreferences.getString("UserPin", "default")!!
            )
        }

        if (user?.userId?.isNotEmpty() == true) {
            userId.setText(user?.userId)
            userPin.setText(user?.userPin)
            saveBtn.text = getString(R.string.edit)
            uidBtnAction = true
            userId.isClickable = false
            userPin.isClickable = false
            userId.isFocusable = false
            userPin.isFocusable = false
        }

        saveBtn.setOnClickListener {
            if(!uidBtnAction) {
                if (userId.text.toString().isNotEmpty() && userPin.text.toString().isNotEmpty()) {
                    setMyUser(userId.text.toString(), userPin.text.toString())
                    val editor = sharedPreferences.edit()
                    editor.putString("UserID", userId.text.toString())
                    editor.putString("UserPin", userPin.text.toString())
                    editor.apply()
                    Toast.makeText(
                        this@MainActivity,
                        "Credentials Saved Successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    uidBtnAction = true
                    saveBtn.text = getString(R.string.edit)
                    userId.isClickable = false
                    userPin.isClickable = false
                    userId.isFocusable = false
                    userPin.isFocusable = false
                } else {
                    Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
                }
            } else {
                uidBtnAction = false
                saveBtn.text = getString(R.string.save)
                userId.text.clear()
                userPin.text.clear()
                userId.isFocusableInTouchMode = true
                userPin.isFocusableInTouchMode = true
            }
        }

        registerReceiver(IncomingSMS(), IntentFilter("broadCastName"))

        pin_visibility.setOnClickListener {
            if(pin_visibility.tag == "visi") {
                pin_visibility.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                pin_visibility.tag = "invisi"
                userPin.transformationMethod = null
            } else {
                pin_visibility.setImageDrawable(getDrawable(R.drawable.ic_visibility))
                pin_visibility.tag = "visi"
                userPin.transformationMethod = PasswordTransformationMethod()
            }
        }

        w = window
        cResolver = contentResolver

        if (brightPermission) {
            try {
                // To handle the auto
                Settings.System.putInt(
                    cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
                // Get the current system brightness
                brightness = Settings.System.getInt(
                    cResolver, Settings.System.SCREEN_BRIGHTNESS
                )
            } catch (e: Settings.SettingNotFoundException) {
                // Throw an error case it couldn't be retrieved
                Log.e("Error", "Cannot access system brightness")
                e.printStackTrace()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                brightPermission = Settings.System.canWrite(this)
            }
        }

        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, newVolume: Int, b: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        brightBtn.setOnClickListener {
            setBrightness(50)
        }

    }

    private fun setBrightness(brightness: Int) {
        Settings.System.putInt(
            cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness
        )
        val layoutPars = w.attributes
        layoutPars.screenBrightness = brightness / 255f
        w.attributes = layoutPars
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
            Log.d("DATA->", "${IncomingSms},$phoneNumber")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            )
        ) {
            Toast.makeText(this, "All permissions are already granted!", Toast.LENGTH_LONG).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                brightPermission = Settings.System.canWrite(this)
            }
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(
                    this,
                    DeviceAdmin::class.java
                )
            )
            startActivity(intent)
            EasyPermissions.requestPermissions(
                this,
                "Permissions are required for this application to function.",
                REQUEST_PERMISSION_CODE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            )
        }
        val n = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!n.isNotificationPolicyAccessGranted) {
                // Ask the user to grant access
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent, REQUEST_PERMISSION_CODE)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(
                    this,
                    DeviceAdmin::class.java
                )
            )
            startActivity(intent)
            EasyPermissions.requestPermissions(
                this,
                "Permissions are required for this application to function.",
                REQUEST_PERMISSION_CODE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "All permissions are granted!", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1

        private lateinit var weakSelf: WeakReference<MainActivity>

        @JvmStatic
        fun get(): MainActivity {
            return weakSelf.get()!!
        }

        private var user: User? = null

        fun setMyUser(userId: String, userPin: String) {
            user = User(userId, userPin)
        }

        fun getMyUser() = user

    }

    init {
        weakSelf = WeakReference(this)
    }

}