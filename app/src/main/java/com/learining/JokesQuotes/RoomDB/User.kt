package com.learining.JokesQuotes.RoomDB

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val username:String,
    val mail:String,
    val password:String,
    val dateOfBirth:String
)
