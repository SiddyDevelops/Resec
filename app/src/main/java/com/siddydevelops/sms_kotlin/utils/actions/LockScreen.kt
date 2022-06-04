package com.siddydevelops.sms_kotlin.utils.actions

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.siddydevelops.sms_kotlin.utils.admin.DeviceAdmin


class LockScreen(context: Context,phoneNumber: String) {

    init {
        val componentName = ComponentName(context,DeviceAdmin::class.java)
        val devicePolicyManager: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        if(devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()
            SendSMS(phoneNumber,"Device has been locked successfully.")
        } else {
            SendSMS(phoneNumber,"Device is already locked.")
        }
    }

}