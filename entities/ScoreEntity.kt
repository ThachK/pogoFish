package com.teampogo.pogofish.entities

import com.teampogo.pogofish.utils.CollisionGroupIds

abstract class ScoreEntity(hasBitmap: Boolean = true, overlap: Boolean = false)
    : CollectibleEntity(hasBitmap = hasBitmap, overlap = overlap) {

    abstract var value: Int

    override fun initAll(x: Float,
                         y: Float,
                         cxOffset: Float,
                         cyOffset: Float) {
        super.initAll(x, y, cxOffset, cyOffset)
        collisionGroupIds.add(CollisionGroupIds.SCORE)
    }
}