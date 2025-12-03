package com.learining.JokesQuotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learining.JokesQuotes.RoomDB.JokeResponse
import com.learining.JokesQuotes.databinding.ActivityJokeDetailsBinding

class JokeDetails : AppCompatActivity() {

    private lateinit var binding: ActivityJokeDetailsBinding
    private val detailsJokeFragment = DetailsJokeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJokeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Get joke and send it to fragment
        val joke = intent.getSerializableExtra("Joke") as JokeResponse
        val bundle = Bundle()
        bundle.putSerializable("Joke", joke)
        detailsJokeFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right
            )
            .replace(R.id.FragmentDetails, detailsJokeFragment).addToBackStack(null).commit()
        /*
        .addToBackStack : Navigate Backward to last Activity "in this case when you're in Fragment Details
                             ,The Last Activity is this activity = jokeDetailsActivity"
        */
    }
}