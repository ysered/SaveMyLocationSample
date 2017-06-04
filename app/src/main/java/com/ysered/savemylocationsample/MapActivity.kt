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
import com.ysered.extension.processPermissionResults
import com.ysered.extension.requestLocationPermissionsIfNeeded
import com.ysered.extension.showToast


@SuppressWarnings("MissingPermission")
class MapActivity : LifecycleActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        requestLocationPermissionsIfNeeded(LOCATION_PERMISSION_REQUEST, onGranted = this::initMap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST ->
                processPermissionResults(permissions,
                        grantResults,
                        onGranted = this::initMap,
                        onDenied = { showToast(R.string.enable_location_permission) })
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            it.isMyLocationEnabled = true
            it.uiSettings.isMapToolbarEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false

            it.setOnMapClickListener { latLng ->
                mapViewModel.addCoordinate(latLng)
            }
            it.setOnCameraMoveListener {
                mapViewModel.cameraPosition = it.cameraPosition
            }
            it.setOnMarkerClickListener { marker ->
                mapViewModel.resolveAddress(marker)
                return@setOnMarkerClickListener true
            }

            mapViewModel.coordinates.forEach {
                addMarker(googleMap, it)
            }
            bindObservers(it)
        }
    }

    private fun initMap() {
        mapFragment.getMapAsync(this)
    }

    /**
     * Start listening on changes from [MapViewModel]
     */
    private fun bindObservers(googleMap: GoogleMap) {
        mapViewModel.observeLocationUpdates(this, Observer { location ->
            location?.let {
                val target = mapViewModel.cameraTarget ?: LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(target))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, mapViewModel.cameraZoom))
            }
        })
        mapViewModel.observeLastAddedLocation(this, Observer { latLng ->
            latLng?.let {
                addMarker(googleMap, it)
            }
        })
    }

    private fun addMarker(googleMap: GoogleMap, latLng: LatLng) {
        googleMap.addMarker(MarkerOptions().position(latLng))
    }
}
