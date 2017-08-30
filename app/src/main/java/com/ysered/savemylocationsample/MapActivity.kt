package com.ysered.savemylocationsample

import android.app.Activity
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ysered.savemylocationsample.database.MyLocationEntity
import com.ysered.savemylocationsample.util.processPermissionResults
import com.ysered.savemylocationsample.util.requestLocationPermissionsIfNeeded
import com.ysered.savemylocationsample.util.showToast
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


@SuppressWarnings("MissingPermission")
class MapActivity : AppCompatActivity(),
        LifecycleRegistryOwner, HasActivityInjector, OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1

    private lateinit var mapFragment: SupportMapFragment

    private val registry = LifecycleRegistry(this)

    @Inject lateinit var injector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mapViewModel: MapViewModel

    override fun activityInjector(): AndroidInjector<Activity> = injector

    override fun getLifecycle(): LifecycleRegistry = registry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapViewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

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
                val marker = addMarker(it, latLng)
                mapViewModel.saveMarker(marker)
            }
            it.setOnCameraMoveListener {
                mapViewModel.cameraPosition = it.cameraPosition
            }
            it.setOnMarkerClickListener { marker ->
                mapViewModel.resolveAddress(marker)
                return@setOnMarkerClickListener true
            }
            it.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker?) {
                    mapViewModel.startUpdatingLocation(marker)
                }

                override fun onMarkerDragEnd(marker: Marker?) {
                    mapViewModel.finishUpdatingLocation(marker)
                }

                override fun onMarkerDrag(marker: Marker?) {}
            })

            mapViewModel.loadCoordinatesAsync()
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
        val cameraZoom = mapViewModel.cameraZoom
        mapViewModel.observeLocationUpdates(this, Observer { location ->
            location?.let {
                val target = mapViewModel.cameraTarget ?: LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(target))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, cameraZoom))
            }
        })
        mapViewModel.coordinates.observe(this, Observer { entities ->
            entities?.map { addMarker(googleMap, LatLng(it.latitude, it.longitude)) }
                    ?.map { MyLocationEntity(it.id, it.position.latitude, it.position.longitude) }
                    ?.toTypedArray()
                    ?.let {
                        mapViewModel.updateMarkers(*it)
                    }
        })
    }

    private fun addMarker(googleMap: GoogleMap, latLng: LatLng): Marker {
        val options = MarkerOptions()
                .position(latLng)
                .draggable(true)
        return googleMap.addMarker(options)
    }
}
