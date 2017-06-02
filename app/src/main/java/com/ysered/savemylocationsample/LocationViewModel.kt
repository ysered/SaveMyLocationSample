package com.ysered.savemylocationsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val locationUpdates = LocationUpdatesLiveData(application.applicationContext)
    val latLng = MutableLiveData<LatLng>()
}
