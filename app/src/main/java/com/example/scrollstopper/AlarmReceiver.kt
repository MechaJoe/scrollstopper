package com.example.scrollstopper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.util.Log
import android.widget.Toast


class AlarmReceiver : BroadcastReceiver() {
    lateinit var notification: Notification

    override fun onReceive(context: Context, intent: Intent) {
        val channelID = "spring2019.cis195.notifications"

        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if (intent.action == "FOO_ACTION") {
            val fooString = intent.getStringExtra("KEY_FOO_STRING")
            Toast.makeText(context, fooString, Toast.LENGTH_LONG).show()
            val resultIntent = Intent(context, WakeUpActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                    context, 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val message = intent.getStringExtra("message")
            notification = Notification.Builder(context, channelID)
                    .setContentTitle("Stop Scrolling!")
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

            sendNotification(context)

            if (!Settings.System.canWrite(context)) {
                changeWriteSettingsPermission(context);
                Log.d("TAG", "Permissions Granted")
            } else {
                changeScreenBrightness(context, 0);
            }

            Log.d("TAG", "Alarm Fired")
        }
    }


    fun sendNotification(context: Context) {
        val notificationManager : NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(
            "spring2019.cis195.notifications",
            "Declaration",
            importance
        )

        channel.description = "Declaration"
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(101, notification)
    }
    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            screenBrightnessValue
        )
    }

    private fun changeWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }


}