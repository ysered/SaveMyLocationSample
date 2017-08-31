package com.ysered.savemylocationsample

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

@SuppressWarnings("MissingPermission")
class LocationUpdatesLiveData(context: Context) : MutableLiveData<Location>(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 1000
        fastestInterval = 1000
        numUpdates = 3
        setExpirationDuration(3000)
    }

    override fun onActive() {
        super.onActive()
        googleApiClient.connect()
    }

    override fun onInactive() {
        super.onInactive()
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient.disconnect()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        LocationServices.FusedLocationApi.getLastLocation(googleApiClient)?.let {
            value = it
        }
        if (hasObservers()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    override fun onConnectionSuspended(cause: Int) {}

    override fun onConnectionFailed(result: ConnectionResult) {}

    override fun onLocationChanged(location: Location?) {
        location?.let {
            value = it
        }
    }
}