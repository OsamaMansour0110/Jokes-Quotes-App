package com.learining.JokesQuotes.RoomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JokeDAO {
    @Insert
    suspend fun addJoke(jokeResponse: JokeResponse)

    @Update
    suspend fun updateJoke(jokeResponse: JokeResponse)

    @Delete
    suspend fun deleteJoke(jokeResponse: JokeResponse)

    @Query("SELECT * FROM jokes ORDER BY id DESC")
    fun getAllJokes(): LiveData<MutableList<JokeResponse>>
}