package com.teampogo.pogofish.entities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.teampogo.pogofish.R
import com.teampogo.pogofish.utils.MathUtils
import com.teampogo.pogofish.utils.Polygon
import kotlin.random.Random

data class ScoreDetails(val spawnChance: Float,
                        val drawableId: Int,
                        val value: Int,
                        val spawnAreaMinHeight: Float,
                        val widthHeightRatio: Float = 1f
)

private val spawnChanceBitmapScore: List<ScoreDetails> = listOf(
        ScoreDetails(0.1f, R.drawable.trophy, 10, 0.3f, 1.2f),
        ScoreDetails(0.3f, R.drawable.coinstack, 5, 0.5f)
)

class Score(context: Context,
            xScreen: Int,
            yScreen: Int)
    : ScoreEntity(hasBitmap = true, overlap = true) {

    override var value: Int = 1

    override val debugTag: String = "ScoreObject"

    override var width: Float = ((xScreen + yScreen) / 32f)
    override var height: Float = ((xScreen + yScreen) / 32f)

    override val collisionBox: Polygon = Polygon(r = 255, g = 255)

    init {
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)

        var spawnAreaMinHeight = 0.7f

        val rng = Random.nextFloat()

        for (score in spawnChanceBitmapScore) {
            if (score.spawnChance > rng) {
                value = score.value
                width *= score.widthHeightRatio
                height /= score.widthHeightRatio
                bitmap = BitmapFactory.decodeResource(context.resources, score.drawableId)
                bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
                spawnAreaMinHeight = score.spawnAreaMinHeight
                break
            }
        }

        initAll(
                Random.nextFloat() * xScreen / 2f,
                yScreen * spawnAreaMinHeight - Random.nextFloat() * yScreen * spawnAreaMinHeight
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

    override fun update() {
        updatePosition()
        updateCollisionBox()
        checkDespawn()
        clearCollided()
    }
}