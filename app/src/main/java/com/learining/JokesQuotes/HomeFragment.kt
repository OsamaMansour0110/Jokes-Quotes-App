package com.learining.JokesQuotes

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.learining.JokesQuotes.databinding.FragmentHomeBinding
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.JokesAPI.JokeViewModel
import com.learining.JokesQuotes.RoomDB.DataBaseBuilder
import com.learining.JokesQuotes.RoomDB.MyDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment :Fragment() {
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
        viewModel.joke.observe(viewLifecycleOwner){jokeState->
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
            if(binding.jokeSetup.text.isEmpty())
                Toast.makeText(requireContext(), "Get joke first", Toast.LENGTH_LONG).show()
            else if(binding.saveJoke.text == "SAVED ✔\uFE0F"){
                Toast.makeText(requireContext(), "The joke already saved", Toast.LENGTH_SHORT).show()
            }
            else {
                joke?.let { joke ->
                    // Add Joke
                    lifecycleScope.launch {
                        val nJoke = JokeResponse(type = joke.type,
                            setup = joke.setup,
                            punchline = joke.punchline)
                        db.jokeDAO().addJoke(nJoke)
                    }
                    binding.saveJoke.text = "SAVED ✔\uFE0F"
                }
                Toast.makeText(requireContext(), "Joke Saved Successfully ✔\uFE0F", Toast.LENGTH_SHORT).show()
            }
        }

        // ADD USER JOKE
        fun showDialog(add : String){
            val view = layoutInflater.inflate(R.layout.adding_joke_dialog, null)
            val btnCancel = view.findViewById<Button>(R.id.cancelLayout_btn)
            val btnAddJoke = view.findViewById<Button>(R.id.addJoke_btn)
            val setUp = view.findViewById<EditText>(R.id.addSetup_input)
            val punchLine = view.findViewById<EditText>(R.id.addPunchline_input)
            val setLayout = view.findViewById<TextInputLayout>(R.id.addSetUp_layout)
            val punLayout = view.findViewById<TextInputLayout>(R.id.addPunchline_layout)

            val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
            dialog.setCanceledOnTouchOutside(true)

            btnAddJoke.text = add
            btnAddJoke.setOnClickListener {
                var hasError = false

                if (setUp.text.isEmpty()) {
                    setLayout.error = "Can't be empty"
                    hasError = true
                } else setLayout.error = null

                if (punchLine.text.isEmpty()) {
                    punLayout.error = "Can't be empty"
                    hasError = true
                } else punLayout.error = null

                if (hasError) return@setOnClickListener

                // Coroutine stop when lifecycle end
                lifecycleScope.launch {
                    val joke = JokeResponse(type = "user joke",
                        setup = setUp.text.toString(),
                        punchline = punchLine.text.toString())
                    db.jokeDAO().addJoke(joke)
                }
                btnAddJoke.setBackgroundColor(Color.parseColor("#4CAF50"))
                btnAddJoke.text = "Added ✔\uFE0F"
                Toast.makeText(requireContext(), "Joke Added Successfully ✔\uFE0F", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                },500)
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        binding.AddMyOwnJoke.setOnClickListener { showDialog("ADD") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // important to avoid memory leaks
    }
}