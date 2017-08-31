package com.ysered.savemylocationsample.database

import android.arch.persistence.room.*

@Dao
interface MyLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(myLocation: MyLocationEntity)

    @Update
    fun update(vararg myLocation: MyLocationEntity): Int

    @Delete
    fun delete(vararg myLocation: MyLocationEntity): Int

    @Query("SELECT * FROM my_location")
    fun getAllLocations(): List<MyLocationEntity>

    @Query("SELECT * FROM my_location WHERE position_id = :arg0")
    fun getLocationByPositionId(positionId: String): MyLocationEntity
}
