package com.learining.JokesQuotes.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.R
import java.util.Locale

class AdapterJoke (
    private val jokeList:List<JokeResponse>,
    private val onDelete:(JokeResponse) -> Unit,
    private val onEdit:(JokeResponse) -> Unit) :
    RecyclerView.Adapter<AdapterJoke.JokeViewHolder>() {

    private var filteredList = jokeList

    // Connect all card views IDs with VALs
    inner class JokeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setUp : TextView = itemView.findViewById(R.id.setUpInCard)
        val punchline : TextView = itemView.findViewById(R.id.punchlineInCard)
        val btnDelete : Button = itemView.findViewById(R.id.deleteJokeButton)
        val btnEdit : Button = itemView.findViewById(R.id.modifyJokeButton)
    }

    // Take card and inflate it to view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_card_in_list, parent,false)
        return JokeViewHolder(view)
    }

    // Using viewHolder variables and list to displaying
    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        val joke = filteredList[position]
        holder.setUp.text = joke.setup
        holder.punchline.text = joke.punchline

        holder.btnDelete.setOnClickListener {
            onDelete(joke)
        }
        holder.btnEdit.setOnClickListener {
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