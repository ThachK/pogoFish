package com.teampogo.pogofish.entities

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.teampogo.pogofish.utils.*
import java.util.*
import kotlin.math.*

/**
 * This serves as the parent class of all "entities" in the game such as the player, score objects,
 * and hostiles
 */
abstract class BaseEntity(val overlap: Boolean = true,
                          val hasBitmap: Boolean = true) {

    val id: UUID = UUID.randomUUID()

    var dx: Float = 0f
    var dy: Float = 0f

    var xBox: Float
        get() = collisionBox.position.x
        set(x) {
            collisionBox.position.x = x
        }

    var yBox: Float
        get() = collisionBox.position.y
        set(y) {
            collisionBox.position.y = y
        }

    val xBoxRelative: Float = 0f
    private val boxPosOffset: Vec2d = Vec2d()

    var xClipped: Boolean = false
    var yClipped: Boolean = false
    var despawn: Boolean = false
    var displaced: Boolean = false
    var flipBox: Boolean = false
    var displace: Boolean = !overlap

    var displacePos: Vec2d = Vec2d()
    var collisionPos: Vec2d = Vec2d()

    var moveAngleRad: Float = 0f
    var currentAngleRad: Float = 0f

    open var faceDirection: Int = -1

    var accelerationVec: Vec2d = Vec2d()
    var velocityVec: Vec2d = Vec2d()

    var collideTime: Long = 0

    val collidedEntities: MutableList<BaseEntity> = mutableListOf()

    var x: Float = 0f
    var y: Float = 0f

    var xFuture: Float = 0f
    var yFuture: Float = 0f

    var inView: Boolean = true

    open val bitmapLeft: Float
        get() = x - width / 2

    open val bitmapTop: Float
        get() = y - height / 2

    lateinit var bitmap: Bitmap

    abstract var width: Float
    abstract var height: Float

    abstract val collisionBox: Polygon
    abstract val collisionGroupIds: MutableSet<Int>
    abstract val debugTag: String

    protected open fun initAll(x: Float,
                               y: Float,
                               cxOffset: Float = 0f,
                               cyOffset: Float = 0f) {
        initPosition(x, y)
        initCollisionBoxPos(cxOffset, cyOffset)
        initCollisionBoxModel()
    }

    private fun initPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
        dx = 0f
        dy = 0f
    }

    private fun initCollisionBoxPos(cxOffset: Float, cyOffset: Float) {
        xBox = x + cxOffset
        yBox = y + cyOffset
        boxPosOffset.x = cxOffset
        boxPosOffset.y = cyOffset
    }

    fun addCollisionEntity(collidedEntity: BaseEntity, intersected: Boolean) {
        if (intersected) {
            return
        }

        collidedEntities.add(collidedEntity)
        collidedEntity.collidedEntities.add(this)
    }

    fun setCollisionPos(intersected: Boolean) {
        if (collidedEntities.isEmpty() || intersected) {
            return
        }

        collisionPos.x = x
        collisionPos.y = y
    }

    fun setDisplacePos(xDisplacement: Float, yDisplacement: Float) {
        if (collidedEntities.isEmpty() || displaced) {
            return
        }
        displace = true
        displacePos.x = collisionPos.x + xDisplacement
        displacePos.y = collisionPos.y + yDisplacement
        displaced = true
    }

    private fun applyNormalForce() {
        if (collidedEntities.isEmpty()) {
            return
        }

        applyAccelerationVec(
                -cos(currentAngleRad) * velocityVec.x / 5,
                -sin(currentAngleRad) * velocityVec.y / 5
        )
    }

    protected fun resetAccelerationVec() {
        accelerationVec = Vec2d()
    }

    protected fun applyAccelerationVec(x: Float, y: Float) {
        accelerationVec.x += x
        accelerationVec.y += y
    }

    protected fun applyGravity() {
        applyAccelerationVec(
            0f,
            -GenericConsts.GRAVITY
        )
    }

    protected fun applyFriction() {
        applyAccelerationVec(
            -sign(velocityVec.x) * GenericConsts.FRICTION,
            0f
        )
    }

    protected fun updateVelocityVec() {
        velocityVec.x += accelerationVec.x
        velocityVec.y += accelerationVec.y
    }

    protected fun updatePosition() {
        if (collidedEntities.isNotEmpty() && displace) {
            val xDirection = cos(currentAngleRad)
            val yDirection = sin(currentAngleRad)

            if ((xDirection > 0.1 && x > displacePos.x)
                    || (xDirection < -0.1 && x < displacePos.x)) {
                xClipped = true
            }
            if ((yDirection > 0.1 && y < displacePos.y)
                    || (yDirection < -0.1 && y > displacePos.y)) {
                yClipped = true
            }
        }

        val xPrev = x
        val yPrev = y

        x += velocityVec.x
        y -= velocityVec.y

        dx = x - xPrev
        dy = y - yPrev
    }

    protected fun updateCollisionBox() {
        xBox = x + boxPosOffset.x
        yBox = y + boxPosOffset.y
        collisionBox.mutateModel(collisionBox.position, collisionBox.mutatedModel)

        if (flipBox) {
            collisionBox.flipModel(collisionBox.position, collisionBox.mutatedModel)
            flipBox = false
        }
    }

    protected fun updateDirection() {
        currentAngleRad = atan2(-round(dy), round(dx))

        val currentDirection = cos(currentAngleRad)

        if (currentAngleRad != 0f &&
                (faceDirection < 0 && currentDirection > 0.01) || (faceDirection > 0 && currentDirection < -0.01)) {
            val matrix = Matrix().apply { postScale(-1f, 1f, x, y) }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width.toInt(), height.toInt(), matrix, true)
            faceDirection *= -1
            flipBox = true
        }
    }

    protected fun clearCollided() {
        collidedEntities.clear()
    }

    open fun reposition(dxCamera: Float, xScreen: Int) {
        x -= dxCamera
        inView = x + width / 2f > 0 && x - width / 2f < xScreen
    }

    abstract fun initCollisionBoxModel()
    abstract fun update()
}