package com.learining.JokesQuotes.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "jokes")
data class JokeResponse(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val type:String,
    val setup:String,
    val punchline:String
) : Serializable

