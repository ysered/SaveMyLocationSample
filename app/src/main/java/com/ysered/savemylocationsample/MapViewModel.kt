package com.ysered.savemylocationsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.ysered.extension.debug
import java.util.*

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val geoCoder = Geocoder(application.applicationContext, Locale.getDefault())

    private val locationUpdates = LocationUpdatesLiveData(application.applicationContext)

    private val lastAddedCoordinate = object : MutableLiveData<LatLng>() {
        override fun getValue(): LatLng? {
            return coordinates.last()
        }

        override fun setValue(value: LatLng?) {
            super.setValue(value)
            value?.let {
                coordinates += value
            }
        }
    }

    val coordinates: MutableList<LatLng> = mutableListOf()

    var cameraPosition: CameraPosition? = null

    val cameraTarget: LatLng?
        get() = cameraPosition?.target

    val cameraZoom: Float
        get() = cameraPosition?.zoom ?: 16f

    fun observeLocationUpdates(lifecycleOwner: LifecycleOwner, observer: Observer<Location>) {
        locationUpdates.observe(lifecycleOwner, observer)
    }

    fun observeLastAddedLocation(lifecycleOwner: LifecycleOwner, observer: Observer<LatLng>) {
        lastAddedCoordinate.observe(lifecycleOwner, observer)
    }

    fun addCoordinate(latLng: LatLng) {
        lastAddedCoordinate.value = latLng
    }

    fun resolveAddress(marker: Marker) {
        val addresses = geoCoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
        if (addresses.isNotEmpty()) {
            val address = addresses.first()
            val fullAddress = "${address.getAddressLine(0)}, ${address.locality}, ${address.countryName}"
            debug(fullAddress)
            // TODO: 1) resolve in different thread 2) store in database
        }
    }
}
