package com.teampogo.pogofish

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.ViewOverlay
import androidx.lifecycle.MutableLiveData
import com.teampogo.pogofish.entities.*
import com.teampogo.pogofish.utils.MathUtils

/**
 * This class manages the game's rendering and presentation logic
 */
class GameView(context: Context,
               resolutionPointSize: Point,
               var debug: Boolean = false)
    : SurfaceView(context),
        Runnable {

    // This is needed for the OS to call run()
    private val gameThread: Thread = Thread(this)

    private var running: Boolean = true
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()

    private var fps: Long = 0
    private var debugTextSize: Float = 50f
    private var currentDebugTextX: Float = 20f
    private var debugTextLines: Int = 1

    private val game: Game = Game(context, resolutionPointSize)

    val gameOver: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    override fun run() {
        while (running && !game.gameOver) {
            val startFrameTime = System.currentTimeMillis()

            game.update()
            draw()

            gameOver.postValue(game.gameOver)

            calcFps(startFrameTime)
        }
    }

    private fun draw() {
        if (!holder.surface.isValid) {
            return
        }
        // Lock the canvas for drawing
        canvas = holder.lockCanvas()

        canvas.drawColor(Color.WHITE)

        for (image in game.background) {
            canvas.drawBitmap(image.bitmap, image.bitmapLeft, image.bitmapTop, paint)
        }
        val remainingLives = game.pogoLife()
        drawLife(remainingLives)
        drawDebugLines()

        for (entity in game.entitiesExisting) {
            if (entity.inView) {
                val collisionBox = entity.collisionBox
                drawEntity(entity)
                drawEntityCollisionBox(entity, collisionBox.r, collisionBox.g, collisionBox.b)
            }
        }

        // Unlock the canvas and draw to screen
        holder.unlockCanvasAndPost(canvas)

    }

    private fun drawLife(lifeRemaining: Int) {
        var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.heart)
        var newBitmap = Bitmap.createScaledBitmap(bitmap,debugTextSize.toInt(),debugTextSize.toInt(),false)
        paint.color = Color.argb(255, 0, 100, 0)
        paint.textSize = debugTextSize
        canvas.drawBitmap(newBitmap, 10f, 10f, paint)
        canvas.drawText("x", currentDebugTextX + debugTextSize, debugTextSize * debugTextLines, paint)
        canvas.drawText("$lifeRemaining", currentDebugTextX + debugTextSize + debugTextSize*2/3 , debugTextSize * debugTextLines, paint)

    }

    private fun drawDebugLines() {
        debugTextLines = 1

        drawFps()
        drawCollisionDebugInfo()
    }

    private fun drawFps() {
        if (!debug) {
            return
        }

        paint.color = Color.argb(255, 0, 100, 0)
        paint.textSize = debugTextSize
        drawDebugTextLine("FPS: $fps")
    }

    private fun drawCollisionDebugInfo() {
        if (!debug) {
            return
        }

        paint.color = Color.argb(255, 0, 100, 0)
        paint.textSize = debugTextSize

        for (entity in game.entitiesExisting) {
            if (entity is Pogo && entity.inView) {
                drawDebugTextLine("${entity.debugTag} Collision Positions:")
                drawDebugTextLine(
                        "${entity.debugTag} " +
                        "x: ${entity.displacePos.x} " +
                        "y: ${entity.displacePos.y}"
                )

                for (collidedEntity in entity.collidedEntities) {
                    drawDebugTextLine(
                            "${collidedEntity.debugTag} " +
                            "x: ${collidedEntity.displacePos.x} " +
                            "y: ${collidedEntity.displacePos.y}"
                    )
                }
            }
        }

    }

    private fun drawDebugTextLine(text: String) {
        canvas.drawText(text, currentDebugTextX, debugTextSize * debugTextLines, paint)
        debugTextLines++
    }

    private fun drawEntity(entity: BaseEntity) {
        if (entity.hasBitmap) {
            canvas.drawBitmap(entity.bitmap, entity.bitmapLeft, entity.bitmapTop, paint)
        }
    }

    private fun drawEntityCollisionBox(entity: BaseEntity, r: Int, g: Int, b: Int) {
        if (!debug) {
            return
        }

        paint.color = Color.argb(255, r, g, b)
        paint.strokeWidth = 8f
        canvas.drawLines(MathUtils.getCollisionPoints(entity.collisionBox.mutatedModel), paint)
        canvas.drawPoint(entity.xBox, entity.yBox, paint)

        paint.color = Color.argb(255, 0, 0, 0)
        paint.strokeWidth = 14f
        canvas.drawPoint(entity.x, entity.y, paint)
    }

    private fun setTextSize(text: String, size: Float) {
        val bounds = Rect()
        val testTextSize = 48f

        paint.textSize = testTextSize
        paint.getTextBounds(text, 0, text.length, bounds)
        paint.textSize = testTextSize * size / bounds.width()
    }

    private fun calcFps(startFrameTime: Long) {
        val timeThisFrame = System.currentTimeMillis() - startFrameTime
        if (timeThisFrame > 1) {
            fps = 1000 / timeThisFrame
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                game.pogoCharge()
            }
            MotionEvent.ACTION_UP -> {
                game.pogoMove(motionEvent)
            }
        }
        return true
    }

    fun getScore(): Long {
        return game.getScore()
    }

    fun pause() {
        try {
            running = false
            gameThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        running = true
        gameThread.start()
    }
}