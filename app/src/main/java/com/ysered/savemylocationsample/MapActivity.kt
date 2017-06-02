package com.ysered.savemylocationsample

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ysered.savemylocationsample.util.processPermissionResults
import com.ysered.savemylocationsample.util.requestLocationPermissionsIfNeeded
import com.ysered.savemylocationsample.util.showToast

@SuppressWarnings("MissingPermission")
class MapActivity : LifecycleActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private val CAMERA_ZOOM = 18f

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationViewModel: LocationViewModel

    private val initMap: () -> Unit = { mapFragment.getMapAsync(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        requestLocationPermissionsIfNeeded(LOCATION_PERMISSION_REQUEST, initMap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> processPermissionResults(permissions, grantResults,
                    onGranted = initMap,
                    onDenied = { showToast(R.string.enable_location_permission) })
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            it.setOnMapClickListener { locationViewModel.latLng.value = it }
            // TODO: setOnMarkerClickListener
            // TODO: setOnMarkerDragListener
            it.isMyLocationEnabled = true
            it.uiSettings.isMapToolbarEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false
            bindObservers(it)
        }
    }

    private fun bindObservers(googleMap: GoogleMap) {
        locationViewModel.locationUpdates.observe(this, Observer { location ->
            location?.let { // move camera
                val current = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(current))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, CAMERA_ZOOM))
            }
        })
        locationViewModel.latLng.observe(this, Observer { myLocationEntity ->
            myLocationEntity?.let { // add marker
                googleMap.addMarker(MarkerOptions().position(it))
            }
        })
    }
}
