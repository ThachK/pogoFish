package com.teampogo.pogofish.utils

object GenericConsts {
    const val STATIONARY: Float = 0f
    const val GRAVITY: Float = 1f
    const val FRICTION: Float = 0.05f
    const val MAX_NUM_HOSTILES = 5
    const val MAX_NUM_SCORES = 5
    const val MAX_SPAWN_Y_OFFSET = 10f
    const val MAX_SPAWN_X_OFFSET = 10f

}

object PogoConsts {
    const val MAX_Y_BOUNCE_POWER: Float = 80f
    const val MAX_X_BOUNCE_POWER: Float = 40f
    const val MAX_TIME_CHARGE_SHAKE_INTENSITY: Float = 12f
    const val MAX_DUCKING_CHARGE_TIME_FRAME: Float = 0.5f
    const val MOVE_TIME_FRAME_MS: Long = 2000
}

object RockConsts {
    const val MAX_X_ACCELERATION: Float = 20f
    const val MAX_Y_ACCELERATION: Float = 20f
    const val MIN_X_ACCELERATION: Float = 5f
    const val MIN_Y_ACCELERATION: Float = 5f
}

object SpawnConsts {
    const val ROCK_CHANCE: Float = 0.2f
    const val SCORE_CHANCE: Float = 0.2f
    const val HOSTILE_SPAWN_DELAY = 2f
    const val SCORE_SPAWN_DELAY = 3f
}

object CollisionGroupIds {
    const val POGO = 0
    const val FLOOR = 1
    const val COLLECTOR = 2
    const val COLLECT = 3
    const val POWERUP = 4
    const val SCORE = 5
    const val FRIENDLY = 6
    const val HOSTILE = 7
    const val TEST = 99
}

object SpeedConsts{
    const val BACKGROUND_SPEED_RATE = 3
    const val SPEED_BASED_ON_RATIO = 7
    const val MIN_SPEED_RATIO = 2
}
object  Number_object {
    const val MAX_NUMBER_OBJECTS = 3
}