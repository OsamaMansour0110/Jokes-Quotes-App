package com.learining.JokesQuotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.learining.JokesQuotes.databinding.FragmentSavedQuotesBinding
import com.learining.JokesQuotes.QuotesApi.QuotesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuotesFragment : Fragment() , OnClickListener{
    private var _binding: FragmentSavedQuotesBinding? = null
    private val binding get() = _binding!!
    val viewModel : QuotesViewModel by viewModels()
    var lastFetchTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedQuotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.quote.observe(viewLifecycleOwner) { QuoteState->
            binding.QuoteQuestion.text = QuoteState.quote?.q ?: ""
            binding.TheAuthor.text = QuoteState.quote?.a ?: ""

            QuoteState.error?.let {
                if (it.contains("429"))
                    Toast.makeText(requireContext(),
                        "Please try in hour, server not able to receive request",
                        Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        binding.TheAuthor.setOnClickListener (this)

        binding.btnClick.setOnClickListener {
            if (binding.progressBar.progress < 5) {
                resetTextView()
                val now = System.currentTimeMillis()
                if(now - lastFetchTime < 3000 ) {
                    Toast.makeText(
                        requireContext(), "Slow down bro, wait 3 seconds",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                binding.progressBar.progress += 1
                binding.progressCount.text = "${binding.progressBar.progress} / 5"

                binding.determinateBar.visibility = View.VISIBLE
                binding.determinateBar.progress = 0

                lifecycleScope.launch {
                    for (i in 0..100) {
                        delay(2)
                        binding.determinateBar.progress = i
                    }
                    binding.determinateBar.visibility = View.GONE

                    lastFetchTime = now
                    viewModel.fetchingQuote()
                }
            }else
                Toast.makeText(requireContext(),"You already got 5 Quotes",Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // important to avoid memory leaks
    }

    private fun resetTextView(){
        binding.TheAuthor.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.text_rectangle)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.TheAuthor ->{
                resetTextView()
                binding.TheAuthor.background =
                    ContextCompat.getDrawable(requireContext(),R.drawable.selected_text_rectangle)
            }
        }
    }
}