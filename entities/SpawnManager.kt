package com.teampogo.pogofish.entities

import android.content.Context
import com.teampogo.pogofish.utils.CollisionGroupIds
import com.teampogo.pogofish.utils.GenericConsts
import com.teampogo.pogofish.utils.SpawnConsts
import java.util.*
import kotlin.random.Random

/**
 * Spawn manager manages all spawning/despawning logic in the game
 */
class SpawnManager(private val context: Context,
                   private val xScreen: Int,
                   private val yScreen: Int) {

    private var numScores: Int = 0
        set (n) {
            if (n >= 0 && n <= GenericConsts.MAX_NUM_SCORES) {
                field = n
            }
        }

    private var numHostiles: Int = 0
        set (n) {
            if (n >= 0 && n <= GenericConsts.MAX_NUM_HOSTILES) {
                field = n
            }
        }

    private var prevScoreSpawnTime: Long = 0L
    private var prevHostileSpawnTime: Long = 0L

    private var spawnRng: Float = 0f

    private fun spawnScores(entities: MutableList<BaseEntity>) {
        val timeFrame = (System.currentTimeMillis() - prevScoreSpawnTime) / 1000f

        if (timeFrame > SpawnConsts.SCORE_SPAWN_DELAY &&
                checkSpawnChance(SpawnConsts.SCORE_CHANCE) &&
                numScores < GenericConsts.MAX_NUM_SCORES) {
            entities.add(Score(context, xScreen, yScreen))
            numScores++
            prevScoreSpawnTime = System.currentTimeMillis()
        } else if (numScores >= GenericConsts.MAX_NUM_SCORES) {
            prevScoreSpawnTime = System.currentTimeMillis()
        }
    }

    private fun spawnHostiles(entities: MutableList<BaseEntity>) {
        val timeFrame = (System.currentTimeMillis() - prevHostileSpawnTime) / 1000f

        if (timeFrame > SpawnConsts.HOSTILE_SPAWN_DELAY &&
                checkSpawnChance(SpawnConsts.ROCK_CHANCE) &&
                numHostiles < GenericConsts.MAX_NUM_HOSTILES) {
            entities.add(Rock(context, xScreen, yScreen))
            numHostiles++
            prevHostileSpawnTime = System.currentTimeMillis()
        } else if (numHostiles >= GenericConsts.MAX_NUM_HOSTILES) {
            prevHostileSpawnTime = System.currentTimeMillis()
        }
    }

    private fun checkDespawn(entities: MutableList<BaseEntity>, xCenter: Float) {
        val despawnIdList = mutableListOf<UUID>()

        for (entity in entities) {
            if (entity.despawn || checkOffScreenDespawn(entity, xCenter)) {

                for (collisionGroupId in entity.collisionGroupIds) {
                    when (collisionGroupId) {
                        CollisionGroupIds.SCORE -> numScores--
                        CollisionGroupIds.HOSTILE -> numHostiles--
                    }
                }

                despawnIdList.add(entity.id)
            }
        }

        entities.removeAll { entity ->
            despawnIdList.contains(entity.id)
        }
    }

    private fun checkSpawnChance(spawnChance: Float): Boolean {
        return spawnChance < spawnRng
    }

    private fun checkOffScreenDespawn(entity: BaseEntity, xCenter: Float): Boolean {
        return (entity.x < xCenter - 3 * xScreen ||
                entity.x > xCenter + 3 * xScreen ||
                entity.y < 0 - GenericConsts.MAX_SPAWN_Y_OFFSET * 2 - entity.height ||
                entity.y > yScreen + entity.height)
    }

    fun update(entities: MutableList<BaseEntity>, xCenter: Float) {
        spawnRng = Random.nextFloat()

        spawnScores(entities)
        spawnHostiles(entities)
        checkDespawn(entities, xCenter)
    }
}