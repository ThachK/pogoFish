package com.teampogo.pogofish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.os.Bundle
import androidx.lifecycle.LiveData

private const val SCOREBANK = "score_bank"

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var howToButton: Button
    private lateinit var scoreButton: Button
    private lateinit var creditsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startButton = findViewById(R.id.start_button)
        howToButton = findViewById(R.id.how_to_button)
        creditsButton = findViewById(R.id.credits_button)
        scoreButton = findViewById(R.id.score_button)

        startButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        howToButton.setOnClickListener {
            val intent = Intent(this, HowToActivity::class.java)
            startActivity(intent)
        }
        scoreButton.setOnClickListener {
            val intent = Intent(this, HighScoreActivity::class.java)
            startActivity(intent)
        }
        creditsButton.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
    }
}