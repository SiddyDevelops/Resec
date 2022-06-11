package com.siddydevelops.sms_kotlin.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.siddydevelops.sms_kotlin.R
import com.siddydevelops.sms_kotlin.data.db.entity.SettingsItem


class RVAdapter(
    private val context: Context,
    private val longClickDeleteInterface: LongClickDeleteInterface
) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    private val allSettings = ArrayList<SettingsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.setting_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.soundProfileTV.text = allSettings[position].soundProfile
        holder.ringTV.text = allSettings[position].volRing
        holder.mediaTV.text = allSettings[position].volMedia
        holder.notificationTV.text = allSettings[position].volNotification
        holder.brightnessTV.text = allSettings[position].brightness
        holder.timePeriodTV.text = context.getString(
            R.string.time_period_text,
            allSettings[position].startTime,
            allSettings[position].endTime
        )

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
    }

    interface LongClickDeleteInterface {
        fun onPressDelete(settingsItem: SettingsItem)
    }
}