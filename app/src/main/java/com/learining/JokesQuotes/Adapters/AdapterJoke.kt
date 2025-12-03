package com.learining.JokesQuotes.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.databinding.JokeCardInListBinding

class AdapterJoke(
    private val jokeList: List<JokeResponse>,
    private val onDelete: (JokeResponse) -> Unit,
    private val onEdit: (JokeResponse) -> Unit
) :
    RecyclerView.Adapter<AdapterJoke.JokeViewHolder>() {

    private var filteredList = jokeList

    // Connect all card views IDs with VALs
    inner class JokeViewHolder(val binding: JokeCardInListBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Binding Card Layout and Return Object From JokeViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val binding =
            JokeCardInListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JokeViewHolder(binding)
    }

    // Using viewHolder variables and list to displaying
    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        val joke = filteredList[position]
        holder.binding.setUpInCard.text = joke.setup
        holder.binding.punchlineInCard.text = joke.punchline
        holder.binding.deleteJokeButton.setOnClickListener {
            onDelete(joke)
        }
        holder.binding.modifyJokeButton.setOnClickListener {
            onEdit(joke)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            jokeList.toMutableList()
        } else {
            jokeList.filter {
                it.setup.lowercase().contains(query.lowercase()) ||
                        it.punchline.lowercase().contains(query.lowercase())
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = filteredList.count()
}