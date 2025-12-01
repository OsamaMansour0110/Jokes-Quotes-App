package com.learining.JokesQuotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learining.JokesQuotes.databinding.ActivityStartBinding

class Start_activity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}