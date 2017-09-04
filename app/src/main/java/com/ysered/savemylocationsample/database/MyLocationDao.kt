package com.ysered.savemylocationsample.database

import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

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

/**
 * Wraps [MyLocationDao.getAllLocations] into coroutine.
 */
fun MyLocationDao.getAllLocationsAsync() = async(CommonPool) {
    getAllLocations()
}

/**
 * Wraps [MyLocationDao.getLocationByPositionId] into coroutine.
 */
fun MyLocationDao.getLocationPositionByIdAsync(positionId: String) = async(CommonPool) {
    getLocationByPositionId(positionId)
}

/**
 * Wraps [MyLocationDao.updateAsync] into coroutine.
 */
fun MyLocationDao.updateAsync(entity: MyLocationEntity) = async(CommonPool) {
    update(entity)
}
