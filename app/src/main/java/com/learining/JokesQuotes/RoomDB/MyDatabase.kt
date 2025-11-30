package com.learining.JokesQuotes.RoomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [JokeResponse::class, User::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun jokeDAO(): JokeDAO
    abstract fun UserDAO(): UserDAO
}