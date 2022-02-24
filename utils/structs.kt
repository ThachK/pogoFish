package com.teampogo.pogofish.utils

data class Vec2d(var x: Float = 0f,
                 var y: Float = 0f) {}

data class LineSeg(val start: Vec2d,
                   val end: Vec2d) {}

data class Polygon(var overlap: Boolean = false,
                   val position: Vec2d = Vec2d(),
                   var r: Int = 0,
                   var g: Int = 0,
                   var b: Int = 0) {
    var initialModel: MutableList<Vec2d> = mutableListOf()
        set(model) {
            field = model
            mutatedModel = model
            futureModel = model
            relativePoints = MathUtils.getRelativePoints(position, model)
            futurePos.x = position.x
            futurePos.y = position.y
        }

    val futurePos: Vec2d = Vec2d()

    lateinit var relativePoints: MutableList<Vec2d>

    lateinit var mutatedModel: MutableList<Vec2d>

    lateinit var futureModel: MutableList<Vec2d>

    fun mutateModel(position: Vec2d, model: MutableList<Vec2d>) {
        for (i in 0 until relativePoints.size) {
            model[i].x = position.x + relativePoints[i].x
            model[i].y = position.y + relativePoints[i].y
        }
        relativePoints = MathUtils.getRelativePoints(position, model)
    }

    fun flipModel(position: Vec2d, model: MutableList<Vec2d>) {
        for (i in 0 until relativePoints.size) {
            model[i].x = position.x - relativePoints[i].x
            model[i].y = position.y - relativePoints[i].y
        }
        relativePoints = MathUtils.getRelativePoints(position, model)
    }
}