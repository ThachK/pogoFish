package com.teampogo.pogofish.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.teampogo.pogofish.R
import com.teampogo.pogofish.utils.*
import kotlin.math.*
import kotlin.random.Random

/**
 * This is the player class
 */
class Pogo(context: Context,
           private val xScreen: Int,
           yScreen: Int)
    : BaseEntity(overlap = false) {

    var livesRemaining: Int = 5

    var charging: Boolean = false
    var ducking: Boolean = false
    var bounce: Boolean = false

    var chargingTimeStart: Long = 0L
    var chargingTimeSec: Float = 0f

    var duckingTimeStart: Long = 0L
    var duckingTimeSec: Float = 0f

    var score: Long = 0

    private val initBouncePower: Vec2d = Vec2d(
        PogoConsts.MAX_X_BOUNCE_POWER / 3,
        PogoConsts.MAX_Y_BOUNCE_POWER / 3
    )

    private val chargeIncrement: Vec2d = Vec2d(
        PogoConsts.MAX_X_BOUNCE_POWER / 200,
        PogoConsts.MAX_Y_BOUNCE_POWER / 200
    )

    private var currentBouncePower: Vec2d = Vec2d(
        initBouncePower.x,
        initBouncePower.y
    )

    override var width: Float = xScreen / 5f
    override var height: Float = yScreen / 10f

    override val bitmapLeft: Float
        get() {
            var xMod = x - width / 2
            if (charging) {
                xMod += getChargeShakeMod()
            }
            return xMod
        }

    override val bitmapTop: Float
        get() {
            var yMod = y - width / 2
            if (charging) {
                yMod += getChargeShakeMod()
            }
            return yMod
        }

    override val collisionBox: Polygon = Polygon(g = 255)

    override val collisionGroupIds: MutableSet<Int> = mutableSetOf(
            CollisionGroupIds.POGO,
            CollisionGroupIds.FRIENDLY,
            CollisionGroupIds.COLLECTOR
    )

    override val debugTag: String = "Pogo"

    init {
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pogo)
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        initAll(xScreen / 2f,
                yScreen - height - 300f,
                0f,
                25f)
    }

    override fun initCollisionBoxModel() {
        // We're gonna make the fish collision model a skinny pentagon with its head pointed to the left
        // Start from noise of fish facing the left, going clockwise
        val left = Vec2d(xBox - width / 2f, yBox)
        val top = Vec2d(xBox - width / 3.0f, yBox - height / 3.2f)
        val topRight = Vec2d(xBox + width / 2.4f, yBox - height / 4.8f)
        val botRight = Vec2d(xBox + width / 2.4f, yBox + height / 4.8f)
        val bot = Vec2d(xBox - width / 3.0f, yBox + height / 3.2f)

        collisionBox.initialModel = mutableListOf(left, top, topRight, botRight, bot)

        moveAngleRad = acos(GenericConsts.STATIONARY)
    }

    fun applyMove(x: Float, y: Float) {
        val dx = x - this.x
        val dy = y - this.y

        moveAngleRad = atan2(-dy, dx)
        resetCharging()
        resetDucking()
    }

    private fun setStationary() {
        moveAngleRad = acos(GenericConsts.STATIONARY)
    }

    private fun getChargeShakeMod(): Float {
        val range = 6f * min(chargingTimeSec, PogoConsts.MAX_TIME_CHARGE_SHAKE_INTENSITY)
        return Random.nextFloat() * range - range / 2f
    }

    private fun resetMove() {
        currentBouncePower.x = initBouncePower.x
        currentBouncePower.y = initBouncePower.y
        resetCharging()
    }

    private fun resetCharging() {
        charging = false
        chargingTimeStart = 0L
        chargingTimeSec = 0f
    }

    private fun resetDucking() {
        ducking = false
        duckingTimeStart = 0L
        duckingTimeSec = 0f
    }

    private fun applyCharge() {
        if (!charging) {
            return
        }

        currentBouncePower.x += chargeIncrement.x
        currentBouncePower.y += chargeIncrement.y

        if (currentBouncePower.x > PogoConsts.MAX_X_BOUNCE_POWER) {
            currentBouncePower.x = PogoConsts.MAX_X_BOUNCE_POWER
        }
        if (currentBouncePower.y > PogoConsts.MAX_Y_BOUNCE_POWER) {
            currentBouncePower.y = PogoConsts.MAX_Y_BOUNCE_POWER
        }
    }

    private fun applyBounce() {
        if (!(bounce && !ducking)) {
            return
        }

        velocityVec.y = 0f
        velocityVec.x = 0f

        applyAccelerationVec(
                currentBouncePower.x * cos(moveAngleRad),
                currentBouncePower.y
        )

        setStationary()
        resetMove()
    }

    private fun applyDucking() {
        if (!ducking) {
            return
        }

        resetAccelerationVec()
        velocityVec.y = 0f
        velocityVec.x = 0f
    }

    private fun checkCollided() {
        bounce = false

        for (entity in collidedEntities) {
            for (collisionGroupId in entity.collisionGroupIds) {
                when (collisionGroupId) {
                    CollisionGroupIds.FLOOR -> bounce = true
                    CollisionGroupIds.SCORE -> {
                        if (entity is ScoreEntity) {
                            score += entity.value
                        }
                    }
                    CollisionGroupIds.HOSTILE -> {
                        livesRemaining -= 1

                        if (livesRemaining <= 0) {
                            despawn = true
                        }
                    }
                }
            }
        }
    }

    private fun checkCharging() {
        if (!charging) {
            return
        }

        if (chargingTimeStart == 0L) {
            chargingTimeStart = System.currentTimeMillis()
        } else {
            chargingTimeSec = (System.currentTimeMillis() - chargingTimeStart) / 1000f
        }
    }

    private fun checkDucking() {
        ducking = (bounce && charging) || ducking

        if (!ducking) {
            return
        }

        if (duckingTimeStart == 0L) {
            duckingTimeStart = System.currentTimeMillis()
        } else {
            duckingTimeSec = (System.currentTimeMillis() - duckingTimeStart) / 1000f
        }

        charging = charging && duckingTimeSec <= PogoConsts.MAX_DUCKING_CHARGE_TIME_FRAME

        if (!charging) {
            resetMove()
            ducking = true
        }
    }

    override fun reposition(dxCamera: Float, xScreen: Int) {
        x = xScreen / 2f
    }

    override fun update() {
        resetAccelerationVec()

        checkCollided()
        checkCharging()
        checkDucking()

        applyCharge()
        applyGravity()
        applyFriction()
        applyBounce()
        applyDucking()

        updateVelocityVec()
        updateDirection()
        updatePosition()
        updateCollisionBox()

        clearCollided()
    }
}