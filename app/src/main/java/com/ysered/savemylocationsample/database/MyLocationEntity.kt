package com.ysered.savemylocationsample.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity


@Entity(tableName = "my_location", primaryKeys = arrayOf("latitude", "longitude"))
data class MyLocationEntity(
        @ColumnInfo(name = "latitude") override var latitude: Double = 0.0,
        @ColumnInfo(name = "longitude") override var longitude: Double = 0.0,
        @ColumnInfo(name = "place") override var place: String = ""
) : MyLocation
