package com.ysered.savemylocationsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

@SuppressWarnings("MissingPermission")
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
}
