package com.learining.JokesQuotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val welcomed:TextView = findViewById(R.id.tvNotify)
        val notifyBox: LinearLayout = findViewById(R.id.notifyBox)

        val username = intent.getStringExtra("username")

        fun showTopMessage(message: String) {
            welcomed.text = message
            notifyBox.visibility = View.VISIBLE
            notifyBox.translationY = -200f
            notifyBox.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction {
                    Handler(Looper.getMainLooper()).postDelayed({
                        notifyBox.animate()
                            .translationY(-200f)
                            .setDuration(300)
                            .withEndAction {
                                notifyBox.visibility = View.GONE
                            }

                    }, 2000)
                }
        }
        showTopMessage("Welcome $username 😘")

        // Catch the drawer For navigationUI
        drawerLayout = findViewById(R.id.drawerLayout)

        // Catch the navigationView for menu
        val navView :NavigationView = findViewById(R.id.navView)

        // Using as container to display fragments that detected by nav graph
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Connect navController with navController in NavHostController to use navigate and popBackStack
        navController = navHostFragment.navController

        // Important for NavigationView
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Connect actionBar with navController and drawerLayout
        // Create Hum_burger Icon and make it opened and closed
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        // Connect menu items in navigationView with navController
        // Click on item -> navigate or "replace" the fragment inside the Container
        NavigationUI.setupWithNavController(navView, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}