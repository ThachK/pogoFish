package com.teampogo.pogofish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A fragment class for high score list items
 */
class HighScoreListFragment : Fragment() {

    private val pogoRepository: PogoRepository = PogoRepository.get()

    private var scoreBank: LiveData<List<HighScore>> = pogoRepository.getHighScores()

    private lateinit var highScoreRecyclerView: RecyclerView
    private var adapter: HighScoreAdapter? = HighScoreAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score_list, container, false)

        highScoreRecyclerView = view.findViewById(R.id.score_recycler_view)
        highScoreRecyclerView.layoutManager = LinearLayoutManager(context)
        highScoreRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreBank.observe(
            viewLifecycleOwner,
            Observer { highScores ->
                highScores?.let {
                    updateUI(highScores)
                }
            }
        )
    }

    private fun updateUI(highScores: List<HighScore>) {
        adapter = HighScoreAdapter(highScores)
        highScoreRecyclerView.adapter = adapter
    }

    private inner class HighScoreHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        private lateinit var highScore: HighScore

        val scoreName: TextView = itemView.findViewById(R.id.score_name)
        val scoreValue: TextView = itemView.findViewById(R.id.score_value)

        fun bind(highScore: HighScore) {
            this.highScore = highScore
            scoreName.text = highScore.name
            scoreValue.text = highScore.value.toString()
        }
    }

    private inner class HighScoreAdapter(val highScores: List<HighScore>)
        : RecyclerView.Adapter<HighScoreHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreHolder {
            val view = layoutInflater.inflate(R.layout.list_item_score, parent, false)
            return HighScoreHolder(view)
        }

        override fun onBindViewHolder(holder: HighScoreHolder, position: Int) {
            val crime = highScores[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int = highScores.size
    }

    companion object {
        fun newInstance(): HighScoreListFragment {
            return HighScoreListFragment()
        }
    }
}