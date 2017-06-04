package com.ysered.savemylocationsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class MapViewModel(application: Application) : AndroidViewModel(application) {

    val locationUpdates = LocationUpdatesLiveData(application.applicationContext)

    val coordinates: MutableList<LatLng> = mutableListOf()

    val lastAddedCoordinate = object : MutableLiveData<LatLng>() {
        override fun getValue(): LatLng? {
            value = null
            return coordinates.last()
        }

        override fun setValue(value: LatLng?) {
            super.setValue(value)
            value?.let {
                coordinates += value
            }
        }
    }

    var cameraPosition: CameraPosition? = null
}
