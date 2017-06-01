package com.ysered.savemylocationsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val location: LocationLiveData = LocationLiveData(application.applicationContext)
}