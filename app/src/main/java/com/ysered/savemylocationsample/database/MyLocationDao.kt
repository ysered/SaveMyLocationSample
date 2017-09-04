package com.ysered.savemylocationsample.database

import android.arch.persistence.room.*
import com.ysered.savemylocationsample.util.debug
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
 * Coroutine wrappers.
 */

fun MyLocationDao.saveAsync(myLocation: MyLocationEntity) = async(CommonPool) {
    save(myLocation)
}

fun MyLocationDao.deleteAsync(vararg myLocation: MyLocationEntity) = async(CommonPool) {
    myLocation.forEach {
        debug("Deleting entity: $it")
    }
    val rowsDeleted = delete(*myLocation)
    debug("Deleted rows: $rowsDeleted")
}

fun MyLocationDao.getAllLocationsAsync() = async(CommonPool) {
    getAllLocations()
}

fun MyLocationDao.getLocationByPositionIdAsync(positionId: String) = async(CommonPool) {
    getLocationByPositionId(positionId)
}

fun MyLocationDao.updateAsync(entity: MyLocationEntity) = async(CommonPool) {
    update(entity)
}
