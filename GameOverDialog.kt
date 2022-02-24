package com.teampogo.pogofish

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

/**
 * This dialog opens when the game is over such as when the player dies
 */
class GameOverDialog(private val activityContext: Context): Dialog(activityContext) {

    interface Callbacks {
        fun onRestart()
        fun onReturnMainMenu()
    }

    private lateinit var scoreTextSlot: TextView
    private lateinit var nameTextSlot: EditText
    private lateinit var restartButton: Button
    private lateinit var mainMenuButton: Button

    private var callbacks: Callbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_game_over)

        callbacks = activityContext as Callbacks?

        restartButton = findViewById(R.id.restartButton)
        mainMenuButton = findViewById(R.id.mainMenuButton)

        nameTextSlot = findViewById(R.id.editText)
        scoreTextSlot = findViewById(R.id.score)

        nameTextSlot.filters = arrayOf<InputFilter>(AllCaps())

        nameTextSlot.setOnClickListener { view ->
            nameTextSlot.setText("")
        }

        restartButton.setOnClickListener { view ->
            callbacks?.onRestart()
        }

        mainMenuButton.setOnClickListener {
            callbacks?.onReturnMainMenu()
        }
    }

    fun getName(): String {
        return nameTextSlot.text.toString()
    }

    fun setScore(score: Long) {
        scoreTextSlot.text = score.toString()
    }
}