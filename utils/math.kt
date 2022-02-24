package com.teampogo.pogofish.utils

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MathUtils {
    fun getRegularConvexPolygon(cx: Float,
                                cy: Float,
                                xRadius: Float,
                                yRadius: Float,
                                sides: Int): MutableList<Vec2d> {
        val polyPoints = mutableListOf<Vec2d>()

        for (i: Int in 0 until sides) {
            val angleRad = 2 * PI.toFloat() * i / sides
            val point = Vec2d(
                    cx + xRadius * cos(angleRad),
                    cy - yRadius * sin(angleRad)
            )
            polyPoints.add(point)
        }

        return polyPoints
    }

    fun getCollisionPoints(model: MutableList<Vec2d>): FloatArray {
        val points = FloatArray(model.size * 4)
        var j = 0
        for (i in 0 until model.size - 1) {
            points[j++] = model[i].x
            points[j++] = model[i].y
            points[j++] = model[i + 1].x
            points[j++] = model[i + 1].y
        }

        points[j++] = model[model.size - 1].x
        points[j++] = model[model.size - 1].y
        points[j++] = model[0].x
        points[j] = model[0].y

        return points
    }

    fun getRelativePoints(center: Vec2d, points: MutableList<Vec2d>): MutableList<Vec2d> {
        val relativePoints = mutableListOf<Vec2d>()
        for (i in points.indices) {
            relativePoints.add(Vec2d(
                    points[i].x - center.x,
                    points[i].y - center.y
            ))
        }
        return relativePoints
    }

    fun getAngleByPoints(p1: Vec2d, p2: Vec2d): Float {
        return atan2(-(p2.y - p1.y), p2.x - p1.x)
    }
}