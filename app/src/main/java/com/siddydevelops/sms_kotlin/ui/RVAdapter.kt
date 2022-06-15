package com.siddydevelops.sms_kotlin.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.siddydevelops.sms_kotlin.R
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem
import java.text.DecimalFormat

class RVAdapter(
    private val context: Context,
    private val longClickDeleteInterface: LongClickDeleteInterface,
    private val initiateSettingsInterface: InitiateSettingsInterface,
    private val updateSettingsInterface: UpdateSettingsInterface
) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    private val allSettings = ArrayList<SettingsItem>()
    private val df = DecimalFormat("#.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.setting_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(allSettings[position].active) {
            holder.settingSwitch.isChecked = true
            initiateSettingsInterface.changePreferenceSettings(allSettings[position])
        }
        holder.soundProfileTV.text = allSettings[position].soundProfile
        holder.ringTV.text = df.format(allSettings[position].volRing.toFloat())
        holder.mediaTV.text = df.format(allSettings[position].volMedia.toFloat())
        holder.notificationTV.text = df.format(allSettings[position].volNotification.toFloat())
        holder.brightnessTV.text = df.format(allSettings[position].brightness.toFloat())
        holder.timePeriodTV.text = context.getString(
            R.string.time_period_text,
            allSettings[position].startTime,
            allSettings[position].endTime
        )

        holder.settingSwitch.setOnClickListener {
            Log.d("Item Clicked",allSettings[position].startTime)
            if(holder.settingSwitch.isChecked) {
                initiateSettingsInterface.changePreferenceSettings(allSettings[position])
                updateSettingsInterface.updatePreferenceSettings(true,allSettings[position].startTime)
            } else {
                //Change to default here
                initiateSettingsInterface.changePreferenceSettings(SettingsItem(
                    false,
                    "NORMAL",
                    "15.0",
                    "7.0",
                    "3.",
                    "40.0",
                    "00:00 AM",
                    "11:59 PM" ))
                updateSettingsInterface.updatePreferenceSettings(false,allSettings[position].startTime)
            }
        }

        holder.deleteIV.setOnClickListener {
            longClickDeleteInterface.onPressDelete(allSettings[position])
        }
    }

    override fun getItemCount(): Int {
        return allSettings.size
    }

    fun updateList(newList: List<SettingsItem>) {
        allSettings.clear()
        allSettings.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ringTV: TextView = itemView.findViewById(R.id.ringTV)
        val mediaTV: TextView = itemView.findViewById(R.id.mediaTV)
        val notificationTV: TextView = itemView.findViewById(R.id.notificationTV)
        val brightnessTV: TextView = itemView.findViewById(R.id.brightnessTV)
        val timePeriodTV: TextView = itemView.findViewById(R.id.timePeriodTV)
        val soundProfileTV: TextView = itemView.findViewById(R.id.soundProfileTV)
        val deleteIV: ImageView = itemView.findViewById(R.id.deleteIV)
        val settingSwitch: SwitchCompat = itemView.findViewById(R.id.settingSwitch)
    }

    interface LongClickDeleteInterface {
        fun onPressDelete(settingsItem: SettingsItem)
    }

    interface InitiateSettingsInterface {
        fun changePreferenceSettings(settingsItem: SettingsItem)
    }

    interface UpdateSettingsInterface {
        fun updatePreferenceSettings(state: Boolean,startTime: String)
    }
}