package com.example.longhours

import android.app.IntentService
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Looper
import android.os.SystemClock
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationWorker : IntentService(LocationWorker::class.java.simpleName) {

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: LocationCallback
    var dataBase : AppDatabase? = null

    override fun onHandleIntent(intent: Intent?) {

        dataBase = Room.databaseBuilder(this, AppDatabase::class.java, "longhours-db").allowMainThreadQueries().fallbackToDestructiveMigration().build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.e("LocationWorker", "inResults")
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.e("LocationWorker", "lat: ${location.latitude}, long: ${location.longitude}")
                    val jobs = dataBase?.jobDao()?.getAll()

                    val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    for (job in jobs!!) {
                        val prefValue = prefs.getLong(prefKey(job.id), 0)
                        val prefValueForWorker = prefs.getBoolean(prefKeyForWorker(job.id), false)
                        if (compareWithThreshold(job.latitude, location.latitude) && compareWithThreshold(job.longitude, location.longitude)) {
                            if (prefValue == 0L) {
                                val now = System.currentTimeMillis()
                                Log.e("LocationWorker", "job: ${job.name} started")
                                with(prefs.edit()) {
                                    this!!.putLong(prefKey(job.id), now)
                                    this!!.putBoolean(prefKeyForWorker(job.id), true)
                                    commit()
                                }
                            }
                        } else {
                            if (prefValue != 0L && prefValueForWorker) {
                                Log.e("LocationWorker", "job: ${job.name} ended")
                                val now = System.currentTimeMillis()
                                dataBase?.entryDao()?.insertAll(Entry(0, job.id, prefValue, now))
                                with(prefs.edit()) {
                                    this!!.putLong(prefKey(job.id), 0L)
                                    this!!.putBoolean(prefKeyForWorker(job.id), false)
                                    commit()
                                }
                            }
                        }
                    }

                }
            }
        }

        startLocationUpdates()
        Looper.loop()
    }

    private val THRESHOLD = 0.0001

    fun compareWithThreshold(f1: Double, f2: Double): Boolean {
        Log.e("LocationWorker", "f1: $f1, f2: $f2, compare: ${Math.abs(f1 - f2) <= THRESHOLD}")
        return Math.abs(f1 - f2) <= THRESHOLD
    }

    fun createLocationRequest() {
        val lr = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationRequest = lr!!

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            Log.e("LocationWorker", "success")
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    Log.e("LocationWorker", "failure")
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }

    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun prefKey(jobId: Int) = BuildConfig.APPLICATION_ID + "." + jobId.toString()
    private fun prefKeyForWorker(jobId: Int) = BuildConfig.APPLICATION_ID + "." + jobId.toString() + ".worker"
}