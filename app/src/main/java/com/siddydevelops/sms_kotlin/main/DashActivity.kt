package com.siddydevelops.sms_kotlin.main

import android.Manifest
import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.markushi.ui.CircleButton
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.siddydevelops.sms_kotlin.R
import com.siddydevelops.sms_kotlin.data.User
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem
import com.siddydevelops.sms_kotlin.notifications.SetNotification
import com.siddydevelops.sms_kotlin.services.AutomateReceiver
import com.siddydevelops.sms_kotlin.ui.RVAdapter
import com.siddydevelops.sms_kotlin.ui.SettingsViewModel
import com.siddydevelops.sms_kotlin.utils.Constants
import com.siddydevelops.sms_kotlin.utils.admin.DeviceAdmin
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class DashActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    RVAdapter.LongClickDeleteInterface,
    RVAdapter.InitiateSettingsInterface,
    RVAdapter.UpdateSettingsInterface {

    private lateinit var userId: EditText
    private lateinit var userPin: EditText
    private lateinit var saveBtn: Button
    private lateinit var stateBtn: Button
    private lateinit var addPrefSetting: FloatingActionButton
    private lateinit var pinVisibility: ImageView
    private lateinit var stateTV: TextView

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var contextView: ConstraintLayout
    private lateinit var dialog: Dialog

    private lateinit var recyclerView: RecyclerView

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private lateinit var dialogView: View
    private lateinit var startTimeBtn: View

    private var brightness: Int = 0
    private lateinit var cResolver: ContentResolver
    private lateinit var w: Window

    private var brightPermission = false
    private var uidBtnAction = false

    private var startTime: String? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: SettingsViewModel
    private val prefSettingsList = ArrayList<SettingsItem>()
    private val intentArray = ArrayList<PendingIntent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        checkPermissions()

        userId = findViewById(R.id.userId)
        userPin = findViewById(R.id.userPin)
        saveBtn = findViewById(R.id.saveBtn)
        stateBtn = findViewById(R.id.stateBtn)
        pinVisibility = findViewById(R.id.pin_visibility)
        stateTV = findViewById(R.id.stateTV)
        addPrefSetting = findViewById(R.id.addPrefSetting)
        recyclerView = findViewById(R.id.recyclerView)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        contextView = findViewById(R.id.context_view)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SettingsViewModel::class.java]

        sharedPreferences = getSharedPreferences("USER_STORE", Context.MODE_PRIVATE)
        w = window
        cResolver = contentResolver
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        recyclerView.layoutManager = LinearLayoutManager(this)
        val rvAdapter = RVAdapter(this, this, this, this)
        recyclerView.adapter = rvAdapter

        viewModel.allSettings.observe(this) { list ->
            rvAdapter.updateList(list)
            generatePrefList(list)
        }

        addPrefSetting.setOnClickListener {
            addNewPreferenceSettings()
        }

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_about -> {
                    aboutDialog()
                    dialog.show()
                    Log.d("Dialog","Clicked")
                    true
                }
                else -> false
            }
        }

        if (sharedPreferences.getBoolean("STATE", false)) {
            stateTV.text = getString(R.string.resec_state_active)
            stateTV.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
            stateBtn.visibility = View.GONE
        }

        stateBtn.setOnClickListener {
            setResecState(true)
            val editor = sharedPreferences.edit()
            editor.putBoolean("STATE", true)
            editor.apply()
            SetNotification(this, getString(R.string.resec_state_active))
            stateTV.text = getString(R.string.resec_state_active)
            stateTV.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
            stateBtn.visibility = View.GONE
        }

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
                    Snackbar.make(contextView,"Credentials Saved Successfully.",Snackbar.LENGTH_SHORT).show()
                    uidBtnAction = true
                    saveBtn.text = getString(R.string.edit)
                    userId.isClickable = false
                    userPin.isClickable = false
                    userId.isFocusable = false
                    userPin.isFocusable = false
                } else {
                    Snackbar.make(contextView,"Please fill all the fields.",Snackbar.LENGTH_SHORT).show()
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
                pinVisibility.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_visibility_off
                    )
                )
                pinVisibility.tag = "invisi"
                userPin.transformationMethod = null
            } else {
                pinVisibility.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_visibility
                    )
                )
                pinVisibility.tag = "visi"
                userPin.transformationMethod = PasswordTransformationMethod()
            }
        }

        if (Settings.System.canWrite(this)) {
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
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, REQUEST_PERMISSION_CODE)
        }

        changePreferenceSettings()
    }

    private fun aboutDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.about_dialog)
        dialog.window!!.setBackgroundDrawable(getDrawable(R.drawable.inset_bg))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.animation_for_dia
        val closeButton = dialog.findViewById<Button>(R.id.dialogButton)
        val mePhoto = dialog.findViewById<ImageView>(R.id.mePhoto)
        val github = dialog.findViewById<ImageView>(R.id.githubIV)
        val linkedin = dialog.findViewById<ImageView>(R.id.linkIV)
        val instagram = dialog.findViewById<ImageView>(R.id.instagramIV)
        mePhoto.clipToOutline = true
        closeButton.setOnClickListener {
            Snackbar.make(contextView,"Thank you for using my App.",Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        github.setOnClickListener {
            Toast.makeText(this, "Hold On!", Toast.LENGTH_SHORT).show()
            val url = "https://github.com/SiddyDevelops"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        instagram.setOnClickListener {
            Toast.makeText(this, "Hold On!", Toast.LENGTH_SHORT).show()
            val url = "https://www.instagram.com/_siddy_08_/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        linkedin.setOnClickListener {
            Toast.makeText(this, "Hold On!", Toast.LENGTH_SHORT).show()
            val url = "https://www.linkedin.com/in/siddharth-singh-08/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }
    }

    private fun generatePrefList(list: List<SettingsItem>) {
        prefSettingsList.clear()
        prefSettingsList.addAll(list)
        generateActivePrefList()
    }

    private fun generateActivePrefList() {
        val activeSettings = ArrayList<SettingsItem>()
        for (settings in prefSettingsList) {
            if (settings.active) {
                activeSettings.add(settings)
            }
        }
        automateSettings(activeSettings)
    }

    private fun cancelAutomateSettings() {
        if (intentArray.size > 0) {
            for (i in 0 until intentArray.size) {
                alarmMgr!!.cancel(intentArray[i])
                Log.d("Cancel Ayto",intentArray[i].toString())
            }
            intentArray.clear()
        }
    }

    private fun automateSettings(activeSettings: List<SettingsItem>) {
        for (i in activeSettings.indices) {
            Log.d("Added Item", activeSettings[i].toString())
            val formattedTimeHour =
                SimpleDateFormat("hh:mm a", Locale.US).parse(activeSettings[i].startTime)
            val cal = Calendar.getInstance()
            cal.time = formattedTimeHour!!

            alarmIntent = Intent(this, AutomateReceiver::class.java).let { intent ->
                intent.putExtra(Constants.EXTRA_ACTIVE, activeSettings[i].active)
                intent.putExtra(Constants.EXTRA_SOUND_PROFILE, activeSettings[i].soundProfile)
                intent.putExtra(Constants.EXTRA_VOL_RING, activeSettings[i].volRing)
                intent.putExtra(Constants.EXTRA_VOL_MEDIA, activeSettings[i].volMedia)
                intent.putExtra(
                    Constants.EXTRA_SOUND_NOTIFICATION,
                    activeSettings[i].volNotification
                )
                intent.putExtra(Constants.EXTRA_BRIGHTNESS, activeSettings[i].brightness)
                intent.putExtra(Constants.EXTRA_START_TIME, activeSettings[i].startTime)
                PendingIntent.getBroadcast(this, i, intent, 0)
            }
            intentArray.add(alarmIntent)

            // Set the alarm to start at START-TIME
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            }

            // setRepeating() lets you specify a precise custom interval--in this case,1 day.
            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * 60 * 24,
                alarmIntent
            )
        }
        Log.d("ActiveList", activeSettings.toString())
    }

    private fun addNewPreferenceSettings() {
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
        val saveSettingsBtn = dialogView.findViewById<CircleButton>(R.id.saveSettingsBtn)

        var checkRadioButton: RadioButton? = null

        // Initializing view properties
        startTime = null
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

        radioGroup?.setOnCheckedChangeListener { _, id ->
            checkRadioButton = dialogView.findViewById(id)
        }

        startTimeBtn.setOnClickListener {
            setPickerTime()
        }

        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        saveSettingsBtn?.setOnClickListener {
            when {
                startTime == null -> {
                    Toast.makeText(this, "Please select start time.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (checkRadioButton?.text.toString() == "NORMAL") {
                        viewModel.addSetting(
                            SettingsItem(
                                false,
                                checkRadioButton?.text.toString(),
                                ringSlider.value.toString(),
                                mediaSlider.value.toString(),
                                notificationSlider.value.toString(),
                                brightnessSlider.value.toString(),
                                startTime.toString()
                            )
                        )
                    } else {
                        viewModel.addSetting(
                            SettingsItem(
                                false,
                                checkRadioButton?.text.toString(),
                                "0.00",
                                mediaSlider.value.toString(),
                                notificationSlider.value.toString(),
                                brightnessSlider.value.toString(),
                                startTime.toString()
                            )
                        )
                    }
                    Snackbar.make(contextView,"Settings saved successfully!",Snackbar.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    viewModel.allSettings.observe(this) { list ->
                        generatePrefList(list)
                    }
                }
            }
        }
    }

    private fun setPickerTime() {
        val startTimeTV = dialogView.findViewById<TextView>(R.id.startTimeTV)

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Start Time:")
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
            startTime = formattedTime
            startTimeTV.visibility = View.VISIBLE
            startTimeTV.text = startTime.toString()
            startTimeBtn.visibility = View.INVISIBLE
        }
    }

    override fun onPressDelete(settingsItem: SettingsItem) {
        viewModel.deleteSetting(settingsItem)
        changePreferenceSettings()
        Snackbar.make(contextView,"Settings has been deleted.",Snackbar.LENGTH_SHORT).show()
    }

    override fun changePreferenceSettings() {
        cancelAutomateSettings()
        viewModel.allSettings.observe(this) { list ->
            generatePrefList(list)
        }
    }

    override fun updatePreferenceSettings(state: Boolean, startTime: String) {
        viewModel.updateSettingState(state, startTime)
        cancelAutomateSettings()
        viewModel.allSettings.observe(this) { list ->
            generatePrefList(list)
        }
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
            val incomingSms = intent.getStringExtra("incomingSms") //

            val phoneNumber = intent.getStringExtra("incomingPhoneNumber")
            Log.d("DATA->", "${incomingSms},$phoneNumber")
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
            Toast.makeText(this, "All permissions are already granted!", Toast.LENGTH_SHORT).show()
        } else {
            brightPermission = Settings.System.canWrite(this)
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
        if (!n.isNotificationPolicyAccessGranted) {
            // Ask the user to grant access
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent, REQUEST_PERMISSION_CODE)
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
        Toast.makeText(this, "All permissions are granted!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1

        private lateinit var weakSelf: WeakReference<DashActivity>

        @JvmStatic
        fun get(): DashActivity {
            return weakSelf.get()!!
        }

        private var user: User? = null
        private var resecState: Boolean = false
        private var contactState: Boolean = false

        fun setMyUser(userId: String, userPin: String) {
            user = User(userId, userPin)
        }

        fun setResecState(state: Boolean) {
            resecState = state
        }

        fun setContactState(state: Boolean) {
            contactState = state
        }

        fun getMyUser() = user

        fun getResecState() = resecState
        fun getContactState() = contactState

    }

    init {
        weakSelf = WeakReference(this)
    }

}