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
import com.ysered.savemylocationsample.util.debug
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MapViewModel @Inject constructor(private val addressResolver: AddressResolver,
                                       private val locationUpdates: LocationUpdatesLiveData,
                                       private val myLocationDao: MyLocationDao)
    : ViewModel() {

    private val DEFAULT_CAMERA_ZOOM = 16f

    private var updateEntityObservable: Observable<MyLocationEntity>? = null

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
        val position = marker.position
        val entity = MyLocationEntity(marker.id, position.latitude, position.longitude)
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg unit: Unit?) {
                myLocationDao.save(entity)
            }
        }.execute()
    }

    fun resolveAddress(marker: Marker) {
        val fullAddress = addressResolver.getFullAddress(marker.position)
        debug("Resolved address: $fullAddress")
    }

    fun startUpdatingLocation(marker: Marker?) {
        marker?.let {
            updateEntityObservable = Observable.fromCallable {
                myLocationDao.getLocationById(marker.id)
            }
        }
    }

    fun finishUpdatingLocation(marker: Marker?) {
        marker?.let {
            val position = marker.position
            updateEntityObservable?.subscribeOn(Schedulers.newThread())
                    ?.subscribe { myLocationToUpdate ->
                        myLocationToUpdate.latitude = position.latitude
                        myLocationToUpdate.longitude = position.longitude
                        myLocationDao.update(myLocationToUpdate)
                    }
        }
    }

    fun updateMarkers(vararg myLocations: MyLocationEntity) {
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg unit: Unit?) {
                myLocationDao.update(*myLocations)
            }
        }.execute()
    }
}
