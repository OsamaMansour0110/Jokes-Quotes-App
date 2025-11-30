package com.learining.JokesQuotes.RoomDB

import android.content.Context
import androidx.room.Room

object DataBaseBuilder{
    @Volatile
    private var INSTANCE: MyDatabase? = null

    fun getInstance(context: Context):MyDatabase{
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                MyDatabase::class.java,
                "joke_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}