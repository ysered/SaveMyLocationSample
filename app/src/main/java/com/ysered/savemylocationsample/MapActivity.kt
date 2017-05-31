package com.ysered.savemylocationsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.ysered.savemylocationsample.util.processPermissionResults
import com.ysered.savemylocationsample.util.requestLocationPermissionsIfNeeded
import com.ysered.savemylocationsample.util.showToast

@SuppressWarnings("MissingPermission")
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1

    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment }

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

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            // TODO: setOnMapClickListener
            // TODO: setOnMarkerClickListener
            // TODO: setOnMarkerDragListener
            it.isMyLocationEnabled = true
            it.uiSettings.isMapToolbarEnabled = false
        }
    }

    private fun initMap() {
        mapFragment.getMapAsync(this)
    }
}
