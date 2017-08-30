package com.ysered.savemylocationsample.database

import android.arch.persistence.room.*

@Dao
interface MyLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(myLocation: MyLocationEntity)

    @Update
    fun update(myLocation: MyLocationEntity)

    @Query("SELECT * FROM my_location")
    fun getAllLocations(): List<MyLocationEntity>

    @Query("SELECT * FROM my_location WHERE id = :arg0")
    fun getLocationById(id: String): MyLocationEntity
}
