package com.example.longhours

import android.content.Context
import android.graphics.Camera
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
//    private val sydney = LatLng(-34.0, 151.0)
    var jobDetails : JobDetails? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.maps_fragment, container, false)

        val mapView = mView?.findViewById<MapView>(R.id.map_view)
        if (mapView != null) {
            mapView.onCreate(null)
            mapView.onResume()
            mapView.getMapAsync(this)
        }

        val doneBtn = mView.findViewById<Button>(R.id.map_done_btn)
        doneBtn.setOnClickListener {
            val ma = activity as MainActivity
            val editJobFragment = EditJobFragment()
            editJobFragment.jobDetails = jobDetails
            ma.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, editJobFragment).commit()
        }

        val resetBtn = mView.findViewById<Button>(R.id.map_reset_btn)
        resetBtn.setOnClickListener {
            val ma = activity as MainActivity

            jobDetails?.latitude = 0.0
            jobDetails?.longitude = 0.0
            ma.dataBase?.jobDao()?.updateAll((Job(jobDetails!!.id, jobDetails!!.name, jobDetails!!.rate, jobDetails!!.latitude, jobDetails!!.longitude)))

            val editJobFragment = EditJobFragment()
            editJobFragment.jobDetails = jobDetails
            ma.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, editJobFragment).commit()
        }

        val sydneyBtn = mView.findViewById<Button>(R.id.sydney_btn)
        sydneyBtn.setOnClickListener {
            val ma = activity as MainActivity

            jobDetails?.latitude = -34.0
            jobDetails?.longitude = 151.0
            ma.dataBase?.jobDao()?.updateAll((Job(jobDetails!!.id, jobDetails!!.name, jobDetails!!.rate, jobDetails!!.latitude, jobDetails!!.longitude)))

            val editJobFragment = EditJobFragment()
            editJobFragment.jobDetails = jobDetails
            ma.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, editJobFragment).commit()
        }

        return mView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(context)

        mMap = googleMap!!

        Log.e("deadbeef", "lat: ${jobDetails!!.latitude}, long: ${jobDetails!!.longitude}")
        val markerLocation = LatLng(jobDetails!!.latitude, jobDetails!!.longitude)


        marker = mMap.addMarker(MarkerOptions().position(markerLocation).draggable(true))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation))

        mMap.setOnMapClickListener {
            marker.remove()
            marker = mMap.addMarker(MarkerOptions().position(it))
            Log.e("deadbeef", "lat: ${it.latitude}, long: ${it.longitude}")

            val ma = activity as MainActivity
            jobDetails?.latitude = it.latitude
            jobDetails?.longitude = it.longitude
            ma.dataBase?.jobDao()?.updateAll((Job(jobDetails!!.id, jobDetails!!.name, jobDetails!!.rate, jobDetails!!.latitude, jobDetails!!.longitude)))
        }
    }
}