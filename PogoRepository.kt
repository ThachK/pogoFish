package com.teampogo.pogofish

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.lang.IllegalStateException
import java.util.concurrent.Executors

private const val DATABASE_NAME = "pogo-database"

class PogoRepository private constructor(context: Context) {

    private val database: PogoDatabase = Room.databaseBuilder(
        context.applicationContext,
        PogoDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val highScoreDao: HighScoreDao = database.highScoreDao()
    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        private var INSTANCE: PogoRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PogoRepository(context)
            }
        }

        fun get(): PogoRepository {
            return INSTANCE?: throw IllegalStateException("PogoRepository must be initialized")
        }
    }

    fun getHighScores(): LiveData<List<HighScore>> = highScoreDao.getHighScores()

    fun getHighScore(name: String): LiveData<HighScore?> = highScoreDao.getHighScore(name)

    fun insertHighScore(highScore: HighScore) {
        executor.execute {
            highScoreDao.insertHighScore(highScore)
        }
    }

    fun limitTopTenScores() {
        executor.execute {
            highScoreDao.limitTopTenScores()
        }
    }
}