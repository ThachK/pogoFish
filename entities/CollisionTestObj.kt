package com.teampogo.pogofish.entities

import android.content.Context
import android.util.Log
import com.teampogo.pogofish.utils.CollisionGroupIds
import com.teampogo.pogofish.utils.Polygon
import com.teampogo.pogofish.utils.MathUtils

class CollisionTestObj(context: Context,
                       xScreen: Int,
                       yScreen: Int)
    : ScoreEntity(hasBitmap = false, overlap = true) {
    override var width: Float = xScreen / 5f
    override var height: Float = yScreen / 5f

    override val collisionBox: Polygon = Polygon(b = 255)

    override val collisionGroupIds: MutableSet<Int> = mutableSetOf(
            CollisionGroupIds.TEST
    )

    override val debugTag: String = "TestObject"

    override var value: Int = 1

    init {
        initAll(xScreen / 2f, yScreen / 2f)
    }

    override fun initCollisionBoxModel() {
        // Let's make this test object, nearly a circle or a convex polygon with many sides
        collisionBox.initialModel = MathUtils.getRegularConvexPolygon(
                xBox,
                yBox,
                (width - 10) / 2,
                (height - 10) / 2,
                8
        )
    }

    override fun update() {
        if (collidedEntities.isNotEmpty()) {
            if (collisionBox.r == 255) {
                collisionBox.r = 0
                collisionBox.b = 255
            } else if (collisionBox.b == 255) {
                collisionBox.r = 255
                collisionBox.b = 0
            }
        }
        updateCollisionBox()
        checkDespawn()
        clearCollided()
    }
}