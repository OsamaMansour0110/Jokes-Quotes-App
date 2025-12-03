package com.learining.JokesQuotes

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.RoomDB.MyDatabase
import com.learining.JokesQuotes.databinding.FragmentDetailsPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailsJokeFragment : Fragment() {

    private var _binding: FragmentDetailsPageBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var db: MyDatabase
    private var joke: JokeResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsPageBinding.inflate(inflater, container, false)
        arguments?.let { bundle ->
            joke = bundle.getSerializable("Joke") as JokeResponse
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DataBaseBuilder.getInstance(requireContext())

        fun closeDetails() {
            parentFragmentManager.popBackStack()
            requireActivity().finish()
            requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        binding.setup.setText(joke?.setup ?: "")
        binding.punchline.setText(joke?.punchline ?: "")
        binding.buttonSave.setOnClickListener {
            val itemObj = joke?.id?.let { id ->
                JokeResponse(
                    id = id,
                    type = joke?.type.toString(),
                    setup = binding.setup.text.toString(),
                    punchline = binding.punchline.text.toString()
                )
            }
            lifecycleScope.launch(Dispatchers.IO) {
                if (itemObj != null) {
                    db.jokeDAO().updateJoke(itemObj)
                    Snackbar.make(
                        binding.root, "Joke's Modifications Saved Successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                closeDetails()
            }, 1500)
        }
        binding.buttonBack.setOnClickListener {
            closeDetails()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}