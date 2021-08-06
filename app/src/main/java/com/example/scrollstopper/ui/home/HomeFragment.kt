package com.example.scrollstopper.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.scrollstopper.MainActivity
import com.example.scrollstopper.NewTimeActivity
import com.example.scrollstopper.R
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var time : String? = ""
    private lateinit var mPreferences: SharedPreferences
    private val sharedPrefFile = "com.example.scrollstopper.sharedprefs"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            text_home.text = it
        })
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(activity)
        val url = "https://api.fisenko.net/quotes"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Display the first 500 characters of the response string.
                text_home.text = "${response.get("text")} \n \n -${response.get("author")}"
            },
            { text_home.text = "WiFi Connectivity Issue! Nooo!" })

//         Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
        val time_button: TextView = root.findViewById(R.id.time_button)
        time_button.setOnClickListener {
            val i = Intent(activity, NewTimeActivity::class.java)
            startActivity(i)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        mPreferences = (activity as MainActivity).getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE);
        val time = mPreferences.getString("time", null)
        if (time.isNullOrEmpty()) {
            return
        }

        Log.d("TIME = ", time)
        if (time != null && alarmTime != null) {
            val hour = (time.toFloat()/(1000*60*60) % 24).toInt()
            val minute = (time.toFloat()/(1000*60) % 60).toInt()
            alarmTime.text = String.format("You're set to stop scrolling at %02d:%02d UTC", hour, minute)
        }
    }

}