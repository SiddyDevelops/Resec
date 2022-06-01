package com.siddydevelops.sms_kotlin.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.siddydevelops.sms_kotlin.MainActivity
import com.siddydevelops.sms_kotlin.R


class SetNotification(context: Context) {

    init {
        createNotificationChannel(context)

        val remoteView = RemoteViews(context.packageName,R.layout.custom_notification_layout)

        val switchIntent = Intent(context, ToggleButtonListener::class.java)
        val pendingSwitchIntent = PendingIntent.getBroadcast(
            context, 0,
            switchIntent, 0
        )
        remoteView.setOnClickPendingIntent(R.id.toggleBtn,pendingSwitchIntent)

        val channelId = "all_notifications" // Use same Channel ID
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        val builder = NotificationCompat.Builder(
            context,
            channelId
        ) // Create notification with channel Id
            .setContent(remoteView)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
        builder.setContentIntent(pendingIntent).setAutoCancel(true)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(mNotificationManager) {
            notify(123, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications"
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

}