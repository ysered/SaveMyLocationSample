package com.ysered.savemylocationsample.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface MyLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(myLocation: MyLocationEntity)

    @get:Query("SELECT * FROM my_location")
    val allLocations: LiveData<List<MyLocationEntity>>

    @Query("SELECT * FROM my_location WHERE latitude = :arg0 AND longitude = :arg1")
    fun getLocation(latitude: Double, longitude: Double): LiveData<MyLocationEntity>
}
