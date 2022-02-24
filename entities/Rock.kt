package com.teampogo.pogofish.entities
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.teampogo.pogofish.R
import com.teampogo.pogofish.utils.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Rocks are randomly spawned entities that float around and can damage the player
 */
class Rock (context: Context, private val xScreen: Int, private val yScreen: Int) :
        BaseEntity() {

    override var width: Float = ((xScreen + yScreen) / 32f) * (Random.nextFloat() * 2f + 1f)
    override var height: Float = ((xScreen + yScreen) / 32f) * (Random.nextFloat() * 2f + 1f)

    override val collisionBox: Polygon = Polygon(b = 255)

    override val collisionGroupIds: MutableSet<Int> = mutableSetOf(
            CollisionGroupIds.HOSTILE
    )

    override val debugTag: String = "Rock"

    init {
        val resources = listOf(R.drawable.rock1, R.drawable.rock2, R.drawable.rock3)
        val rockResource = resources[Random.nextInt(0, 3)]

        bitmap = BitmapFactory.decodeResource(context.resources, rockResource)
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)

        val xSpawn = if (Random.nextInt(0, 2) == 1) { xScreen + width } else { -width }

        initAll(xSpawn, Random.nextFloat() * yScreen / 2f)
        initTrajectory()
    }

    private fun initTrajectory() {
        currentAngleRad = MathUtils.getAngleByPoints(
                Vec2d(x, y),
                Vec2d(xScreen * Random.nextFloat(), yScreen * Random.nextFloat())
        )

        val xAcceleration = max(Random.nextFloat() * RockConsts.MAX_X_ACCELERATION, RockConsts.MIN_X_ACCELERATION)
        val yAcceleration = max(Random.nextFloat() * RockConsts.MAX_Y_ACCELERATION, RockConsts.MIN_Y_ACCELERATION)

        applyAccelerationVec(
                cos(currentAngleRad) * xAcceleration,
                sin(currentAngleRad) * yAcceleration
        )
    }

    override fun initCollisionBoxModel() {
        collisionBox.initialModel = MathUtils.getRegularConvexPolygon(
                xBox,
                yBox,
                (width - 10) / 2,
                (height - 10) / 2,
                8
        )
    }

    private fun checkDespawn() {
        for (entity in collidedEntities) {
            if (entity.collisionGroupIds.contains(CollisionGroupIds.POGO)) {
                despawn = true
                return
            }
        }
    }

    override fun update() {
        updateVelocityVec()
        updateDirection()
        updatePosition()
        updateCollisionBox()

        resetAccelerationVec()
        checkDespawn()
        clearCollided()
    }
}