package com.siddydevelops.sms_kotlin

import android.Manifest
import android.app.NotificationManager
import android.app.Service
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.siddydevelops.sms_kotlin.data.User
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem
import com.siddydevelops.sms_kotlin.notifications.SetNotification
import com.siddydevelops.sms_kotlin.ui.RVAdapter
import com.siddydevelops.sms_kotlin.ui.SettingsViewModel
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
    private lateinit var pinVisibility: ImageView
    private lateinit var stateTV: TextView

    private lateinit var recyclerView: RecyclerView

    private lateinit var dialogView: View
    private lateinit var startTimeBtn: View
    private lateinit var endTimeBtn: View

    private var brightness: Int = 0
    private lateinit var cResolver: ContentResolver
    private lateinit var w: Window

    private var brightPermission = false
    private var uidBtnAction = false

    private var startTime: String? = null
    private var endTime: String? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: SettingsViewModel

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
        pinVisibility = findViewById(R.id.pin_visibility)
        stateTV = findViewById(R.id.stateTV)
        addPrefSetting = findViewById(R.id.addPrefSetting)
        recyclerView = findViewById(R.id.recyclerView)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SettingsViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(this)
        val rvAdapter = RVAdapter(this)
        recyclerView.adapter = rvAdapter

        viewModel.allSettings.observe(this, Observer { list ->
            rvAdapter.updateList(list)
        })

        addPrefSetting.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            dialogView =
                LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, viewGroup, false)
            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
            val soundNormal = dialogView.findViewById<RadioButton>(R.id.sound_normal)
            val soundVibrate = dialogView.findViewById<RadioButton>(R.id.sound_vibrate)
            val soundSilent = dialogView.findViewById<RadioButton>(R.id.sound_silent)
            val ringSlider = dialogView.findViewById<Slider>(R.id.ringSlider)
            val mediaSlider = dialogView.findViewById<Slider>(R.id.mediaSlider)
            val notificationSlider = dialogView.findViewById<Slider>(R.id.notificationSlider)
            val brightnessSlider = dialogView.findViewById<Slider>(R.id.brightnessSlider)
            startTimeBtn = dialogView.findViewById<Button>(R.id.startTimeBtn)
            endTimeBtn = dialogView.findViewById<Button>(R.id.endTimeBtn)
            val saveSettingsBtn = dialogView.findViewById<Button>(R.id.saveSettingsBtn)

            var checkRadioButton: RadioButton? = null

            // Initializing view properties
            startTime = null
            endTime = null
            val audioManager: AudioManager = getSystemService(Service.AUDIO_SERVICE) as AudioManager
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> {
                    soundSilent.isChecked = true
                    checkRadioButton = soundSilent
                }
                AudioManager.RINGER_MODE_VIBRATE -> {
                    soundVibrate.isChecked = true
                    checkRadioButton = soundVibrate
                }
                AudioManager.RINGER_MODE_NORMAL -> {
                    soundNormal.isChecked = true
                    checkRadioButton = soundNormal
                }
            }
            ringSlider.valueTo = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING).toFloat()
            ringSlider.value = audioManager.getStreamVolume(AudioManager.STREAM_RING).toFloat()

            mediaSlider.valueTo =
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
            mediaSlider.value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()

            notificationSlider?.valueTo =
                audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION).toFloat()
            notificationSlider?.value =
                audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION).toFloat()

            brightnessSlider?.value = Settings.System.getInt(
                cResolver, Settings.System.SCREEN_BRIGHTNESS, 0
            ).toFloat()

            radioGroup?.setOnCheckedChangeListener { p0, id ->
                checkRadioButton = dialogView.findViewById(id)
            }

            startTimeBtn.setOnClickListener {
                setPickerTime("Select Start Time:")
            }

            endTimeBtn.setOnClickListener {
                setPickerTime("Select End Time:")
            }

            saveSettingsBtn?.setOnClickListener {
                when {
                    startTime == null -> {
                        Toast.makeText(this, "Please select start time.", Toast.LENGTH_SHORT).show()
                    }
                    endTime == null -> {
                        Toast.makeText(this, "Please select end time.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.addSetting(
                            SettingsItem(
                                "STORE01",
                                checkRadioButton?.text.toString(),
                                ringSlider.value.toString(),
                                mediaSlider.value.toString(),
                                notificationSlider.value.toString(),
                                brightnessSlider.value.toString(),
                                startTime.toString(),
                                endTime.toString()
                            )
                        )
                        Toast.makeText(
                                    applicationContext,
                                    "Settings saved successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                    }
                }
            }

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
            if (!uidBtnAction) {
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

        pinVisibility.setOnClickListener {
            if (pinVisibility.tag == "visi") {
                pinVisibility.setImageDrawable(getDrawable(R.drawable.ic_visibility_off))
                pinVisibility.tag = "invisi"
                userPin.transformationMethod = null
            } else {
                pinVisibility.setImageDrawable(getDrawable(R.drawable.ic_visibility))
                pinVisibility.tag = "visi"
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

    private fun setPickerTime(titleText: String) {
        val startTimeTV = dialogView.findViewById<TextView>(R.id.startTimeTV)
        val endTimeTV = dialogView.findViewById<TextView>(R.id.endTimeTV)

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(titleText)
                .build()
        picker.show(supportFragmentManager, "TIME_PICKER")
        picker.addOnPositiveButtonClickListener {
            val pickedHour = picker.hour
            val pickedMinute = picker.minute
            val formattedTime = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "${picker.hour - 12}:0${picker.minute} PM"
                    } else {
                        "${picker.hour - 12}:${picker.minute} PM"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "${picker.hour}:0${picker.minute} PM"
                    } else {
                        "${picker.hour}:${picker.minute} PM"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "${picker.hour + 12}:0${picker.minute} AM"
                    } else {
                        "${picker.hour + 12}:${picker.minute} AM"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "${picker.hour}:0${picker.minute} AM"
                    } else {
                        "${picker.hour}:${picker.minute} AM"
                    }
                }
            }
            if (titleText == "Select Start Time:") {
                startTime = formattedTime
                startTimeTV.visibility = View.VISIBLE
                startTimeTV.text = startTime.toString()
                startTimeBtn.visibility = View.INVISIBLE
            } else if (titleText == "Select End Time:") {
                endTime = formattedTime
                endTimeTV.visibility = View.VISIBLE
                endTimeTV.text = endTime.toString()
                endTimeBtn.visibility = View.INVISIBLE
            }
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