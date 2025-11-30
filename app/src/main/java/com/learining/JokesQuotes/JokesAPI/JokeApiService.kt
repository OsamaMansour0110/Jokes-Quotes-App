package com.learining.JokesQuotes.JokesAPI

import com.learining.JokesQuotes.RoomDB.JokeResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder().baseUrl("https://official-joke-api.appspot.com/")
    .addConverterFactory(GsonConverterFactory.create()).build()

val jokeService = retrofit.create(ApiService::class.java)

interface ApiService{
    @GET("random_joke")
    suspend fun getJoke() : JokeResponse
}
