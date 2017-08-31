package com.ysered.savemylocationsample

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.location.Location
import android.os.AsyncTask
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.ysered.savemylocationsample.database.MyLocationDao
import com.ysered.savemylocationsample.database.MyLocationEntity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MapViewModel @Inject constructor(private val addressResolver: AddressResolver,
                                       private val locationUpdates: LocationUpdatesLiveData,
                                       private val myLocationDao: MyLocationDao)
    : ViewModel() {

    private val DEFAULT_CAMERA_ZOOM = 16f

    private var updatePositionId: String? = null

    val coordinates = MutableLiveData<List<MyLocationEntity>>()

    var cameraPosition: CameraPosition? = null

    val cameraTarget: LatLng?
        get() = cameraPosition?.target

    val cameraZoom: Float
        get() = cameraPosition?.zoom ?: DEFAULT_CAMERA_ZOOM

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
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg unit: Unit?) {
                val entity = MyLocationEntity(
                        positionId = id,
                        latitude = position.latitude,
                        longitude = position.longitude
                )
                myLocationDao.save(entity)
            }
        }.execute()
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
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg unit: Unit?) {
                myLocationDao.update(*myLocations)
            }
        }.execute()
    }

    fun removeMarkers(markers: List<Marker>) {
        val positionsToDelete = markers.map { it.id }.toList()
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg unit: Unit?) {
                val entitiesToDelete = positionsToDelete
                        .map { myLocationDao.getLocationByPositionId(it) }
                        .toTypedArray()
                myLocationDao.delete(*entitiesToDelete)
            }
        }.execute()
    }
}
