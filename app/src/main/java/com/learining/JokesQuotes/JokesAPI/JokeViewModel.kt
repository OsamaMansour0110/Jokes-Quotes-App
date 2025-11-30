package com.learining.JokesQuotes.JokesAPI

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learining.JokesQuotes.RoomDB.JokeResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class JokeViewModel : ViewModel() {

    private val _joke = MutableLiveData(JokeState())
    // Expose for Activity
    val joke: LiveData<JokeState> = _joke

     fun fetchingJoke(){
         _joke.value = _joke.value?.copy(
             loading = true,
             error = null
         )

        viewModelScope.launch {
            try {
                val response = jokeService.getJoke()
                _joke.value = JokeState(
                    loading = false,
                    joke = JokeResponse(
                        id = response.id,
                        type = response.type,
                        setup = response.setup,
                        punchline = response.punchline
                    ),
                    error = null
                )
            }catch (e: Exception){
                _joke.value = JokeState(
                    loading = false,
                    joke = null,
                    error = "ERROR FETCHING DATA: ${e.message}"
                )
            }
        }
    }

    // preparing State Error and Loading
    data class JokeState(
        val loading:Boolean = true,
        val joke: JokeResponse? = null,
        val error:String? = null )

}