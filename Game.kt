package com.teampogo.pogofish

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import com.teampogo.pogofish.entities.*
import com.teampogo.pogofish.utils.LineSeg
import com.teampogo.pogofish.utils.Vec2d
import java.lang.Math.floorMod

/**
 * This class handles all game logic and updates and manages game objects
 */
class Game(private val context: Context,
           resolutionPointSize: Point) {

    val background: MutableList<Background> = mutableListOf()

    private val xScreen: Int = resolutionPointSize.x
    private val yScreen: Int = resolutionPointSize.y

    private val pogo: Pogo = Pogo(context, xScreen, yScreen)
    private val testObj: CollisionTestObj = CollisionTestObj(context, xScreen, yScreen)

    private val dxCamera: Float
        get() = pogo.dx

    private val xCenter: Float
        get() = pogo.x

    private val spawnManager: SpawnManager = SpawnManager(context, xScreen, yScreen)

    private val floors: MutableList<BaseEntity> = mutableListOf()

    val entitiesExisting: MutableList<BaseEntity> = mutableListOf(
            pogo
    )

    var gameOver: Boolean = false

    init {
        initBackground()
    }

    private fun initBackground() {
        for (i in  -1..1) {
            background.add(Background(
                    context,
                    xScreen,
                    yScreen,
                    Vec2d(xCenter + i * xScreen, yScreen / 2f)
            ))
            floors.add(FloorBoundary(
                    context,
                    xScreen,
                    yScreen,
                    Vec2d(xCenter + i * xScreen, yScreen.toFloat() - 150f)
            ))
        }
        entitiesExisting.addAll(floors)
    }

    fun update() {
        for (i in 0 until entitiesExisting.size) {
            entitiesExisting[i].update()
            entitiesExisting[i].reposition(dxCamera, xScreen)

            for (j in i + 1 until entitiesExisting.size) {
                if (entitiesExisting[i].inView && entitiesExisting[j].inView) {
                    checkCollision(entitiesExisting[i], entitiesExisting[j])
                }
            }
        }

        updateBackground()
        spawnManager.update(entitiesExisting, xCenter)
        checkGameOver()
    }

    fun pogoCharge() {
        pogo.charging = true
    }

    fun pogoMove(motionEvent: MotionEvent) {
        pogo.applyMove(motionEvent.x, motionEvent.y)
    }

    private fun updateBackground() {
        for (image in background) {
            image.reposition(dxCamera, xScreen)
        }

        val backgroundTooFarFromXCenter = background[0].x < xCenter - 2 * xScreen ||
                background[2].x > xCenter + 2 * xScreen

        if (backgroundTooFarFromXCenter) {
            var j = -1
            for (i in 0 until background.size) {
                val xNew = xCenter + j * xScreen
                background[i].x = xNew
                floors[i].x = xNew
                j++
            }
        }
    }

    private fun checkGameOver() {
        if (pogo.despawn) {
            gameOver = true
        }
    }

    fun pogoLife(): Int {
        return pogo.livesRemaining
    }

    fun getScore(): Long {
        return pogo.score
    }

    /**
     * Checks for overlap/collision of another [other] object, returning a bool
     *
     * This check for collision uses OneLoneCoder's alternative to the Separated Axis Theorem (SAT)
     * This alternative checks for diagonals of a convex polygon A against the edge segment of another convex polygon B
     * Polygon B's diagonals are then checked against the edge segment of polygon A
     *
     * It uses a common line segment intersection algorithm to accomplish this
     * This uses static collision
     *
     * Ref: [Convex Polygon Collisions #1](https://www.youtube.com/watch?v=7Ik2vowGcU0)
     */
    @TargetApi(Build.VERSION_CODES.N)

    fun checkCollision(e1: BaseEntity, e2: BaseEntity) {

        //e1.collided = e1.collided or false

        var poly1 = e1.collisionBox
        var poly2 = e2.collisionBox
        var intersected = false

        e1.displaced = false

        for (poly: Int in 0..1) {

            // Switch the polygons for second comparison
            if (poly == 1) {
                poly1 = e2.collisionBox
                poly2 = e1.collisionBox
            }

            // Check diagonals of polygon 1...
            for (poly1Point: Vec2d in poly1.mutatedModel) {
                val line1 = LineSeg(poly1.position, poly1Point)

                val displacement = Vec2d()

                // ..against the edges of polygon 2
                for (poly2PointIndex: Int in 0 until poly2.mutatedModel.size) {
                    val line2 = LineSeg(
                            poly2.mutatedModel[poly2PointIndex],
                            poly2.mutatedModel[floorMod(poly2PointIndex + 1, poly2.mutatedModel.size)]
                    )

                    // Standard line segment intersection algorithm
                    val h = (line2.end.x - line2.start.x) * (line1.start.y - line1.end.y) - (line1.start.x - line1.end.x) * (line2.end.y - line2.start.y)
                    val t1 = ((line2.start.y - line2.end.y) * (line1.start.x - line2.start.x) + (line2.end.x - line2.start.x) * (line1.start.y - line2.start.y)) / h
                    val t2 = ((line1.start.y - line1.end.y) * (line1.start.x - line2.start.x) + (line1.end.x - line1.start.x) * (line1.start.y - line2.start.y)) / h

                    val lineIntersect = t1 >= 0f && t1 < 1f && t2 >= 0f && t2 < 1f

                    if (lineIntersect) {
                        e1.collideTime = System.currentTimeMillis()
                        e2.collideTime = System.currentTimeMillis()
                        e1.addCollisionEntity(e2, intersected)
                        e1.setCollisionPos(intersected)
                        e2.setCollisionPos(intersected)
                        displacement.x += (1f - t1) * (line1.end.x - line1.start.x)
                        displacement.y += (1f - t1) * (line1.end.y - line1.start.y)
                        intersected = true
                    }
                }

                if (!e1.overlap && !e2.overlap) {
                    e1.setDisplacePos(
                            displacement.x * if (poly == 0) { -1f } else { 1f },
                            displacement.y * if (poly == 0) { -1f } else { 1f }
                    )
                } else {
                    e1.displace = false
                }
            }
        }
    }

}