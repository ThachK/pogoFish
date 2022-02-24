package com.teampogo.pogofish

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HighScore::class], version = 1)
abstract class PogoDatabase: RoomDatabase() {

    abstract fun highScoreDao(): HighScoreDao
}