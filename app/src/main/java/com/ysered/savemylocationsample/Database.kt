package com.ysered.savemylocationsample

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context

@Database(entities = arrayOf(MyLocationEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun get(context: Context): RoomDatabase.Builder<AppDatabase>
                = Room.databaseBuilder(context, AppDatabase::class.java, "locations.db")
    }
    abstract val myLocationDao: MyLocationDao
}

@Dao
interface MyLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(myLocation: MyLocationEntity)

    @get:Query("SELECT * FROM my_location")
    val allLocations: LiveData<List<MyLocationEntity>>

    @Query("SELECT * FROM my_location WHERE latitude = :arg0 AND longitude = :arg1")
    fun getLocation(latitude: Double, longitude: Double): LiveData<MyLocationEntity>
}

interface MyLocation {
    val latitude: Double
    val longitude: Double
    val place: String
}

@Entity(tableName = "my_location", primaryKeys = arrayOf("latitude", "longitude"))
data class MyLocationEntity (
        @ColumnInfo(name = "latitude") override var latitude: Double = 0.0,
        @ColumnInfo(name = "longitude") override var longitude: Double = 0.0,
        @ColumnInfo(name = "place") override var place: String = ""
) : MyLocation
