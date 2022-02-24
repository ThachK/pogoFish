package com.teampogo.pogofish

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM highscore ORDER BY value DESC")
    fun getHighScores(): LiveData<List<HighScore>>

    @Query("SELECT * FROM highscore WHERE name = (:name)")
    fun getHighScore(name: String): LiveData<HighScore?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighScore(highScore: HighScore)

    @Query("""
        DELETE FROM highscore 
        WHERE name NOT IN (
            SELECT name 
            FROM highscore
            ORDER BY value DESC
            LIMIT 10
        )""")
    fun limitTopTenScores()
}