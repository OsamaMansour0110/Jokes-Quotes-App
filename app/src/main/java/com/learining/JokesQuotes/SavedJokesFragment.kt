package com.learining.JokesQuotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.learining.JokesQuotes.databinding.FragmentSavedJokesBinding
import com.learining.JokesQuotes.Adapters.AdapterJoke
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.RoomDB.MyDatabase
import kotlinx.coroutines.launch

class SavedJokesFragment : Fragment() {
    private var _binding: FragmentSavedJokesBinding? = null
    private val binding get() = _binding!!
    private lateinit var  db : MyDatabase

    private lateinit var adapter: AdapterJoke

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedJokesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseBuilder.getInstance(requireContext())
        val jokesList = mutableListOf<JokeResponse>()

        fun deleteJoke (jokeResponse: JokeResponse){
            lifecycleScope.launch {
                db.jokeDAO().deleteJoke(jokeResponse)
            }
        }

        fun EditJoke (jokeResponse: JokeResponse){
            val intent = Intent(requireContext(),JokeDetails::class.java)
            intent.putExtra("Joke",jokeResponse)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }

        // :: Mean passing reference to function itself
        adapter = AdapterJoke(jokesList, ::deleteJoke, ::EditJoke)

        binding.savedJokesListRecyclerView.adapter = adapter
        binding.savedJokesListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.searchBarList.addTextChangedListener { text ->
            adapter.filter(text.toString())
        }

        // Get All jokes from ROOM when Fragment opened for first time
        // Any Modify on data observe with LiveData will feel that and run this code again
        viewLifecycleOwner.lifecycleScope.launch {
            db.jokeDAO().getAllJokes().observe(viewLifecycleOwner){saveJokes->
                jokesList.clear()
                jokesList.addAll(saveJokes)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // important to avoid memory leaks
    }
}