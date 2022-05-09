package com.serhat.kotlintravelbook.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.serhat.kotlintravelbook.model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao          //PlaceDao döndürür.
}