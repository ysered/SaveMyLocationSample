package com.ysered.savemylocationsample.util

import android.content.SharedPreferences
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * Stores some preferences of camera on the [com.google.android.gms.maps.GoogleMap].
 */
class MapCameraPreferences(prefs: SharedPreferences) {
    private val DEFAULT_CAMERA_ZOOM = 16f

    private var cameraLatitude by prefs.float()
    private var cameraLongitude by prefs.float()
    private var cameraTilt by prefs.float()
    private var cameraBearing by prefs.float()

    var cameraZoom by prefs.float(DEFAULT_CAMERA_ZOOM)

    var cameraTarget: LatLng? = null
        get() = LatLng(cameraLatitude.toDouble(), cameraLongitude.toDouble())

    var cameraPosition: CameraPosition? = null
        get() = CameraPosition(cameraTarget, cameraZoom, cameraTilt, cameraBearing)
        set(value) {
            field = value
            value?.let {
                cameraLatitude = value.target.latitude.toFloat()
                cameraLongitude = value.target.longitude.toFloat()
                cameraZoom = value.zoom
                cameraTilt = value.tilt
                cameraBearing = value.bearing
            }
        }
}
