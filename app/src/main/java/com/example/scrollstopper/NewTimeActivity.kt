package com.example.scrollstopper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_time.*
import java.util.*

class NewTimeActivity : AppCompatActivity() {
    private lateinit var mPreferences: SharedPreferences
    private val sharedPrefFile = "com.example.scrollstopper.sharedprefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_time)

        setButton.setOnClickListener {

            Log.d("Test", "Alarm SET")
            // Intent part
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.action = "FOO_ACTION"
            intent.putExtra("KEY_FOO_STRING", "Stop Scrolling!")
            intent.putExtra("message", message.text.toString())

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            // Set with system Alarm Service
            // Get AlarmManager instance
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Alarm time
            val hour = timePicker.hour
            val minute = timePicker.minute
            val calendar: Calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val alarmTimeAtUTC = calendar.timeInMillis
            Log.d("alarm time = ", alarmTimeAtUTC.toString())

            // Other possible functions: setExact() / setRepeating() / setWindow(), etc
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeAtUTC,
                pendingIntent
            )
            mPreferences = getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE);
            val preferencesEditor = mPreferences!!.edit()
            preferencesEditor.putString("time", alarmTimeAtUTC.toString())
            preferencesEditor.commit()
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }

    }

}