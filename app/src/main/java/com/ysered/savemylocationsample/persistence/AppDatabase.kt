package com.ysered.savemylocationsample.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(MyLocationEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun get(context: Context): AppDatabase
                = Room.databaseBuilder(context, AppDatabase::class.java, "locations.db").build()
    }

    abstract val myLocationDao: MyLocationDao
}