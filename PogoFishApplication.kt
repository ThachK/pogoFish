package com.teampogo.pogofish

import android.app.Application

class PogoFishApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PogoRepository.initialize(this)
    }
}