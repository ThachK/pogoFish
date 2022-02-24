package com.teampogo.pogofish

import android.annotation.SuppressLint
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * This manages the game's lifecycle and logic that connects the game to the menu
 */
class GameActivity : AppCompatActivity(), GameOverDialog.Callbacks {

    private val pogoRepository: PogoRepository = PogoRepository.get()

    private var resolutionPointSize = Point()

    private lateinit var game: FrameLayout
    private lateinit var gameView: GameView
    private lateinit var gameWidgets: LinearLayout
    private lateinit var resetButton: Button
    private lateinit var debugButton: Button

    private lateinit var gameOverDialog: GameOverDialog

    private var debug: Boolean = false
        set(debug) {
            gameView.debug = debug
            field = debug
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay

        display.getSize(resolutionPointSize)

        initAll()
        hideSystemUi()
    }

    private fun initAll() {
        gameOverDialog = GameOverDialog(this)
        gameOverDialog.setOnShowListener {
            gameOverDialog.setScore(gameView.getScore())
        }
        game = FrameLayout(this)

        initGameView()
        initButtonsLayout()
        initButtons()

        setContentView(game)
    }

    private fun initGameView() {
        game.removeAllViews()
        gameView = GameView(this, resolutionPointSize, debug)
        gameView.gameOver.observe(
                this@GameActivity,
                Observer { gameOver ->
                    if (gameOver) {
                        gameOverDialog.show()
                    }
                }
        )
        game.addView(gameView)
    }

    private fun initButtonsLayout() {
        gameWidgets = LinearLayout(this)
        gameWidgets.orientation = LinearLayout.HORIZONTAL
        gameWidgets.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resolutionPointSize.y / 20
        )
        gameWidgets.gravity = Gravity.END
        game.addView(gameWidgets)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initButtons() {
        resetButton = Button(this)
        resetButton.setText(R.string.reset_button)
        resetButton.width = resolutionPointSize.x / 6
        resetButton.setOnClickListener { view ->
            reset()
        }
        gameWidgets.addView(resetButton)

        debugButton = Button(this)
        debugButton.setText(R.string.debug_button)
        debugButton.width = resolutionPointSize.x / 6
        debugButton.setOnClickListener { view ->
            debug = !debug
        }
        gameWidgets.addView(debugButton)
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onRestart() {
        addNewHighScore()
        super.onRestart()
        reset()
    }

    override fun onReturnMainMenu() {
        addNewHighScore()
        gameOverDialog.dismiss()
        onPause()
        finish()
    }

    private fun addNewHighScore() {
        val existingScore = pogoRepository.getHighScore(gameOverDialog.getName())
        if (existingScore.value != null) {
            if (existingScore.value!!.value < gameView.getScore()) {
                pogoRepository.insertHighScore(
                    HighScore(
                        gameOverDialog.getName(),
                        gameView.getScore()
                    )
                )
            }
        } else {
            pogoRepository.insertHighScore(
                HighScore(
                    gameOverDialog.getName(),
                    gameView.getScore()
                )
            )
            pogoRepository.limitTopTenScores()
        }
    }

    private fun reset() {
        gameOverDialog.dismiss()
        onPause()
        initAll()
        onResume()
    }

    private fun hideSystemUi() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE
        }
    }
}