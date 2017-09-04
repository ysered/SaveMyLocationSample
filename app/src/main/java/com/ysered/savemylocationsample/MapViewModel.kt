package com.ysered.savemylocationsample

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.ysered.savemylocationsample.coroutines.experimental.Android
import com.ysered.savemylocationsample.database.*
import com.ysered.savemylocationsample.util.MapCameraPreferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MapViewModel @Inject constructor(private val addressResolver: AddressResolver,
                                       private val locationUpdates: LocationUpdatesLiveData,
                                       private val myLocationDao: MyLocationDao,
                                       val mapCameraPreferences: MapCameraPreferences)
    : ViewModel() {

    private var updatePositionId: String? = null

    val coordinates = MutableLiveData<List<MyLocationEntity>>()

    fun observeLocationUpdates(lifecycleOwner: LifecycleOwner, observer: Observer<Location>) =
            locationUpdates.observe(lifecycleOwner, observer)

    fun loadCoordinatesAsync() {
        launch(Android) {
            coordinates.value = myLocationDao.getAllLocationsAsync().await()
        }
    }

    fun saveMarker(marker: Marker) {
        val id = marker.id
        val position = marker.position
        launch(CommonPool) {
            val entity = MyLocationEntity(
                    positionId = id,
                    latitude = position.latitude,
                    longitude = position.longitude
            )
            myLocationDao.save(entity)
        }
    }

    fun resolveAddress(position: LatLng): String = addressResolver.getFullAddress(position)

    fun startUpdatingLocation(positionId: String) {
        updatePositionId = positionId
    }

    fun finishUpdatingLocation(marker: Marker) {
        updatePositionId?.let {
            launch(Android) {
                val entity = myLocationDao.getLocationPositionByIdAsync(updatePositionId!!).await()
                entity.latitude = marker.position.latitude
                entity.longitude = marker.position.longitude
                myLocationDao.updateAsync(entity)
            }
            updatePositionId = null
        }
    }

    fun updateMarkers(vararg myLocations: MyLocationEntity) {
        launch(CommonPool) {
            myLocationDao.update(*myLocations)
        }
    }

    fun removeMarkers(markers: List<Marker>) {
        val positionIdsToDelete = markers.map { it.id }.toList()
        launch(CommonPool) {
            val entitiesToDelete = positionIdsToDelete
                    .map { myLocationDao.getLocationByPositionId(it) }
                    .toTypedArray()
            myLocationDao.delete(*entitiesToDelete)
        }
    }
}
