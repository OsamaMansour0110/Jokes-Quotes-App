package com.learining.JokesQuotes.QuotesApi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuotesViewModel : ViewModel() {
    private val _quote = MutableLiveData(QuoteState())
    val quote:LiveData<QuoteState> = _quote

    init {
        fetchingQuote()
    }

    fun fetchingQuote (){
        _quote.value = _quote.value?.copy(
            loading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val response = quoteService.getQuote()
                _quote.value =
                    QuoteState(
                        loading = false,
                        quote = response.elementAt(0),
                        error = null
                    )
            }catch (e:Exception){
                _quote.value = QuoteState(
                    loading = false,
                    quote = null,
                    error = "ERROR FETCHING DATA: ${e.message}"
                )
            }
        }
    }

    data class QuoteState(
        val loading:Boolean = true,
        val quote:QuoteResponse? = null,
        val error:String? = null)
}