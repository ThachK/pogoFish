package com.teampogo.pogofish.entities

import android.content.Context
import com.teampogo.pogofish.utils.CollisionGroupIds
import com.teampogo.pogofish.utils.Vec2d
import com.teampogo.pogofish.utils.Polygon

class FloorBoundary(context: Context,
                    xScreen: Int,
                    yScreen: Int,
                    position: Vec2d)
    : BaseEntity(overlap = false, hasBitmap = false) {

    override var width: Float = xScreen.toFloat()
    override var height: Float = yScreen / 20f

    override val collisionBox: Polygon = Polygon()

    override val collisionGroupIds: MutableSet<Int> = mutableSetOf(
            CollisionGroupIds.FLOOR
    )

    override val debugTag: String = "Floor"

    init {
        initAll(position.x, position.y)
    }

    override fun initCollisionBoxModel() {
        val topLeft = Vec2d(xBox - width / 2f, yBox - height / 2.5f)
        val botLeft = Vec2d(xBox - width / 2f, yBox + height / 2.5f)
        val botRight = Vec2d(xBox + width / 2f, yBox + height / 2.5f)
        val topRight = Vec2d(xBox + width / 2f, yBox - height / 2.5f)

        collisionBox.initialModel = mutableListOf(topLeft, botLeft, botRight, topRight)
    }

    override fun update() {
        updateCollisionBox()
        clearCollided()
    }
}