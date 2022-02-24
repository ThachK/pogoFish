package com.teampogo.pogofish.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.teampogo.pogofish.R
import com.teampogo.pogofish.utils.Vec2d

class Background internal constructor(context: Context,
                                      private val width: Int,
                                      private val height: Int,
                                      private val position: Vec2d) {

    var x: Float
        get() = position.x
        set(x) {
            position.x = x
        }

    var y: Float
        get() = position.y
        set(y) {
            position.y = y
        }

    val bitmapLeft: Float
        get() = position.x - width / 2

    val bitmapTop: Float
        get() = position.y - height / 2

    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sea)

    var inView: Boolean = true

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    fun reposition(dxCamera: Float, xScreen: Int) {
        x -= dxCamera
        inView = x + width / 2f > 0 && x - width / 2f < xScreen
    }
}