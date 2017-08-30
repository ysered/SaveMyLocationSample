package com.ysered.savemylocationsample.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = arrayOf(MyLocationEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private val DATABASE_NAME = "save-my-location.db"

        fun create(context: Context): AppDatabase
                = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    abstract val myLocationDao: MyLocationDao
}
