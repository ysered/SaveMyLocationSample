package com.ysered.savemylocationsample

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.ysered.savemylocationsample.util.error
import javax.inject.Inject


interface AddressResolver {

    fun getFullAddress(latLng: LatLng): String
}

class AddressResolverImpl @Inject constructor(private val geoCoder: Geocoder) : AddressResolver {

    override fun getFullAddress(latLng: LatLng): String {
        return try {
            val addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.isNotEmpty()) {
                val address = addresses.first()
                "${address.getAddressLine(0)}, ${address.locality}, ${address.countryName}"
            } else {
                ""
            }
        } catch (ex: Exception) {
            error("Couldn't resolve address: ${ex.message}", ex)
            ""
        }
    }
}
