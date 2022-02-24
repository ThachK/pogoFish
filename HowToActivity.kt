package com.teampogo.pogofish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes

data class Instructions(@StringRes val textResId:Int)
class HowToActivity : AppCompatActivity() {

    private lateinit var nextinstruction: Button
    private lateinit var previnstruction: Button
    private lateinit var backButton: Button

    private lateinit var instructionsTextView: TextView

    private val instructionBank = listOf(
            Instructions(R.string.instruction_pogo),
            Instructions(R.string.instruction_move),
            Instructions(R.string.instruction_obstacles),
            Instructions(R.string.instruction_points),
            Instructions(R.string.instruction_ready))

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to)

        backButton = findViewById(R.id.back_button)
        nextinstruction = findViewById(R.id.next_instruction)
        previnstruction = findViewById(R.id.prev_instruction)
        instructionsTextView = findViewById(R.id.instruction_text_view)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        nextinstruction.setOnClickListener {
            incrementQuestion(1)
            updateInstructions()
        }
        previnstruction.setOnClickListener {
            incrementQuestion(-1)
            updateInstructions()
        }
        val instructionsTextResId = instructionBank[currentIndex].textResId
        instructionsTextView.setText(instructionsTextResId)
    }

    private fun incrementQuestion(increment: Int) {
        currentIndex += increment

        if (currentIndex < 0) { currentIndex = instructionBank.size - 1 }
        else if (currentIndex > instructionBank.size - 1 ) { currentIndex = 0 }
    }

    private fun updateInstructions(){
        val instructionsTextResId = instructionBank[currentIndex].textResId
        instructionsTextView.setText(instructionsTextResId)
    }
}

