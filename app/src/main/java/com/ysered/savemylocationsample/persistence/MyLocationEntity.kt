package com.ysered.savemylocationsample.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(tableName = "my_location", primaryKeys = arrayOf("latitude", "longitude"))
data class MyLocationEntity (
        @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
        @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
        @ColumnInfo(name = "place") var place: String = ""
)
