package com.learining.JokesQuotes

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.learining.JokesQuotes.JokesAPI.JokeViewModel
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.RoomDB.MyDatabase
import com.learining.JokesQuotes.databinding.AddingJokeDialogBinding
import com.learining.JokesQuotes.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var db: MyDatabase
    private val viewModel: JokeViewModel by viewModels()
    private var joke: JokeResponse? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SET DB
        db = DataBaseBuilder.getInstance(requireContext())
        // ANY UPDATE ON JOKE -> RUN
        viewModel.joke.observe(viewLifecycleOwner) { jokeState ->
            if (binding.jokeSetup.text.isNotEmpty())
                binding.progressBar.visibility = if (jokeState.loading) View.VISIBLE else View.GONE
            binding.jokeSetup.text = jokeState.joke?.setup ?: ""
            binding.jokePunchline.text = jokeState.joke?.punchline ?: ""
            jokeState.error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            joke = jokeState.joke
        }

        // FETCHING API
        binding.btnClick.setOnClickListener {
            viewModel.fetchingJoke()
            binding.saveJoke.text = "Save"
        }
        // SAVING JOKE
        binding.saveJoke.setOnClickListener {
            if (binding.jokeSetup.text.isEmpty())
                Toast.makeText(requireContext(), "Get joke first", Toast.LENGTH_LONG).show()
            else if (binding.saveJoke.text == "SAVED ✔\uFE0F") {
                Toast.makeText(requireContext(), "The joke already saved", Toast.LENGTH_SHORT)
                    .show()
            } else {
                joke?.let { joke ->
                    // Add Joke
                    lifecycleScope.launch {
                        val nJoke = JokeResponse(
                            type = joke.type,
                            setup = joke.setup,
                            punchline = joke.punchline
                        )
                        db.jokeDAO().addJoke(nJoke)
                    }
                    binding.saveJoke.text = "SAVED ✔\uFE0F"
                }
                Toast.makeText(
                    requireContext(),
                    "Joke Saved Successfully ✔\uFE0F",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ADD USER JOKE
        fun showDialog() {
            val binding: AddingJokeDialogBinding = AddingJokeDialogBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()
            dialog.show()
            dialog.setCanceledOnTouchOutside(true)

            binding.addJokeBtn.text = "Add"
            binding.addJokeBtn.setOnClickListener {
                var hasError = false

                if (binding.addSetupInput.text?.isEmpty() == true) {
                    binding.addSetUpLayout.error = "Can't be empty"
                    hasError = true
                } else binding.addSetUpLayout.error = null

                if (binding.addPunchlineInput.text?.isEmpty() == true) {
                    binding.addPunchlineLayout.error = "Can't be empty"
                    hasError = true
                } else binding.addPunchlineLayout.error = null

                if (hasError) return@setOnClickListener

                // Coroutine stop when lifecycle end
                lifecycleScope.launch {
                    val joke = JokeResponse(
                        type = "user joke",
                        setup = binding.addSetupInput.text.toString(),
                        punchline = binding.addPunchlineInput.text.toString()
                    )
                    db.jokeDAO().addJoke(joke)
                }
                binding.addJokeBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
                binding.addJokeBtn.text = "Added ✔\uFE0F"
                Toast.makeText(
                    requireContext(),
                    "Joke Added Successfully ✔\uFE0F",
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 500)
            }

            binding.cancelLayoutBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.AddMyOwnJoke.setOnClickListener { showDialog() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // important to avoid memory leaks
    }
}