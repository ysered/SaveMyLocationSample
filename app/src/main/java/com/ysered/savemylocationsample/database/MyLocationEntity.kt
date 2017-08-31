package com.ysered.savemylocationsample.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "my_location")
data class MyLocationEntity(
        @ColumnInfo(name = "id") @PrimaryKey var id: String = "",
        @ColumnInfo(name = "latitude") var latitude: Double = 0.0,
        @ColumnInfo(name = "longitude") var longitude: Double = 0.0,
        @ColumnInfo(name = "place") var place: String = ""
)
