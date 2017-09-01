package com.ysered.savemylocationsample

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.ysered.savemylocationsample.database.MyLocationDao
import com.ysered.savemylocationsample.database.MyLocationEntity
import com.ysered.savemylocationsample.util.MapCameraPreferences
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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

    fun observeLocationUpdates(lifecycleOwner: LifecycleOwner, observer: Observer<Location>) {
        locationUpdates.observe(lifecycleOwner, observer)
    }

    fun loadCoordinatesAsync() {
        object : AsyncTask<Unit, Unit, List<MyLocationEntity>>() {
            override fun doInBackground(vararg unit: Unit?): List<MyLocationEntity>
                    = myLocationDao.getAllLocations()

            override fun onPostExecute(result: List<MyLocationEntity>?) {
                coordinates.value = result
            }
        }.execute()
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

    fun startUpdatingLocation(marker: Marker?) {
        updatePositionId = marker?.id
    }

    fun finishUpdatingLocation(marker: Marker?) {
        if (updatePositionId != null && marker != null) {
            val latitude = marker.position.latitude
            val longitude = marker.position.longitude
            Observable.just(updatePositionId)
                    .map { id ->
                        myLocationDao.getLocationByPositionId(id)
                    }
                    .doOnNext { entity ->
                        entity.latitude = latitude
                        entity.longitude = longitude
                        myLocationDao.update(entity)
                        updatePositionId = null
                    }
                    .subscribeOn(Schedulers.newThread())
                    .subscribe()
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

    /**
     * Wraps DAO call into suspend function.
     */
    private suspend fun getAllLocations(): List<MyLocationEntity>
            = myLocationDao.getAllLocations()
}
