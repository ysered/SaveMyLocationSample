package com.ysered.savemylocationsample

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.ysered.savemylocationsample.util.debug
import com.ysered.savemylocationsample.util.processPermissionResults
import com.ysered.savemylocationsample.util.requestLocationPermissionsIfNeeded
import com.ysered.savemylocationsample.util.showToast

@SuppressWarnings("MissingPermission")
class MapActivity : LifecycleActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private val CAMERA_ZOOM = 16f

    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment }
    private val locationViewModel: LocationViewModel by lazy { ViewModelProviders.of(this).get(LocationViewModel::class.java) }
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        requestLocationPermissionsIfNeeded(LOCATION_PERMISSION_REQUEST, onGranted = { initMap() })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST ->
                processPermissionResults(permissions, grantResults,
                        onGranted = { initMap() },
                        onDenied = { showToast(R.string.enable_location_permission) })
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            // TODO: setOnMapClickListener
            // TODO: setOnMarkerClickListener
            // TODO: setOnMarkerDragListener
            it.isMyLocationEnabled = true
            it.uiSettings.isMapToolbarEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false

            locationViewModel.location.observe(this, Observer { location ->
                debug("Observed location update: $location")
                location?.let { moveCamera(googleMap, it) }
            })
            this.googleMap = it
        }
    }

    private fun initMap() {
        mapFragment.getMapAsync(this)
    }

    private fun moveCamera(googleMap: GoogleMap, location: Location) {
        val current = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, CAMERA_ZOOM))
    }
}
