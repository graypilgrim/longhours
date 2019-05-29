package com.example.longhours

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class EditJobFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.job_layout, container, false)

        val jobNameField = view.findViewById<EditText>(R.id.job_name)
        jobNameField.setText(jobDetails?.name, TextView.BufferType.EDITABLE)

        val rateValueField = view.findViewById<EditText>(R.id.rate_value)
        rateValueField.setText(jobDetails?.rate.toString(), TextView.BufferType.EDITABLE)

        val doneBtn = view.findViewById<Button>(R.id.done_btn)
        doneBtn.setOnClickListener{
            val ma = activity as MainActivity
            val jd = Job(jobDetails!!.id, jobNameField.text.toString(), rateValueField.text.toString().toInt(), jobDetails!!.latitude, jobDetails!!.longitude)
            ma.dataBase?.jobDao()?.updateAll(jd)
            ma.selectFragment(ma.jobsFragment)
        }

        val removeBtn = view.findViewById<Button>(R.id.remove_btn)
        removeBtn.setOnClickListener{
            val ma = activity as MainActivity
            val jd = Job(jobDetails!!.id, "", 0, 0.0, 0.0)
            ma.dataBase?.jobDao()?.delete(jd)
            ma.selectFragment(ma.jobsFragment)
        }

        setClockInBtn(view)

        val clockInTextView = view.findViewById<TextView>(R.id.job_started_time_text_view)
//        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefValue = prefs?.getLong(prefKey(), 0)
        if (prefValue!! != 0L) {
            clockInTextView.text = SimpleDateFormat("HH:mm").format(Date(prefValue)).toString()
        }


        val clockInBtn = view.findViewById<Button>(R.id.clock_in_btn)
        clockInBtn.setOnClickListener {
            if (prefValue!! == 0L) {
                clockInBtn.text = "CLOCK OUT"
                clockInBtn?.setBackgroundColor(resources.getColor(R.color.colorRedBtn, resources.newTheme()))
                val now = System.currentTimeMillis()
                Log.e("deadbeef", "Pref: " + prefKey() + " empty. Time: " + now.toString())
                clockInTextView.text = SimpleDateFormat("HH:mm").format(Date(now)).toString()
                with(prefs.edit()) {
                    this!!.putLong(prefKey(), now)
                    this!!.putBoolean(prefKeyForWorker(), false)
                    commit()
                }
            } else {
                clockInBtn.text = "CLOCK IN"
                clockInBtn?.setBackgroundColor(resources.getColor(R.color.colorGreenBtn, resources.newTheme()))
                val now = System.currentTimeMillis()

                val ma = activity as MainActivity
                ma.dataBase?.entryDao()?.insertAll(Entry(0, jobDetails!!.id, prefs.getLong(prefKey(), 0), now))

                Log.e("deadbeef", "Pref: " + prefKey() + " found. Time: " + (now - prefValue).toString())
                with(prefs.edit()) {
                    this!!.putLong(prefKey(), 0)
                    this!!.putBoolean(prefKeyForWorker(), false)
                    commit()
                }
                clockInTextView.text = ""
            }
        }

        val latitudeTextView = view.findViewById<TextView>(R.id.latitude_text_view)
        latitudeTextView.text = jobDetails?.latitude.toString()

        val longitudeTextView = view.findViewById<TextView>(R.id.longitude_text_view)
        longitudeTextView.text = jobDetails?.longitude.toString()

        val locationBtn = view.findViewById<Button>(R.id.location_btn)
        locationBtn.setOnClickListener{
            val ma = activity as MainActivity
            val mapFragment = MapFragment()
            mapFragment.jobDetails = jobDetails
            ma.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment).commit()
        }

        return view
    }

    private fun prefKey() = BuildConfig.APPLICATION_ID + "." + jobDetails?.id.toString()
    private fun prefKeyForWorker() = BuildConfig.APPLICATION_ID + "." + jobDetails?.id.toString() + ".worker"

    private fun setClockInBtn(view : View?) {
        val clockInBtn = view?.findViewById<Button>(R.id.clock_in_btn)
//        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefValue = prefs?.getLong(prefKey(), 0)
        Log.e("deadbeef", "setClockInBtn: ${prefValue}")

        if (prefValue!! == 0L) {
            clockInBtn?.text = "CLOCK IN"
            clockInBtn?.setBackgroundColor(resources.getColor(R.color.colorGreenBtn, resources.newTheme()))
        } else {
            clockInBtn?.text = "CLOCK OUT"
            clockInBtn?.setBackgroundColor(resources.getColor(R.color.colorRedBtn, resources.newTheme()))
        }
    }

    var jobDetails : JobDetails? = null
}