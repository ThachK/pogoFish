package com.teampogo.pogofish

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HighScore(@PrimaryKey val name: String,
                     val value: Long)