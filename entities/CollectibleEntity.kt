package com.teampogo.pogofish.entities

import com.teampogo.pogofish.utils.CollisionGroupIds

/**
 * This is an abstract class that serves as the parent class for all entities that can be
 * collected by the player
 */
abstract class CollectibleEntity(hasBitmap: Boolean = true, overlap: Boolean = false)
    : BaseEntity(hasBitmap = hasBitmap, overlap = overlap) {

    override val collisionGroupIds: MutableSet<Int> = mutableSetOf(
            CollisionGroupIds.COLLECT
    )

    protected fun checkDespawn() {
        for (entity in collidedEntities) {
            if (entity.collisionGroupIds.contains(CollisionGroupIds.COLLECTOR)) {
                despawn = true
                return
            }
        }
    }
}