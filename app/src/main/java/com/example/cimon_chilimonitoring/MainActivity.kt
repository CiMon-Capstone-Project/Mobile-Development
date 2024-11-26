package com.example.cimon_chilimonitoring

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cimon_chilimonitoring.databinding.ActivityMainBinding
import com.example.cimon_chilimonitoring.ui.chatbot.ChatbotActivity
import com.example.cimon_chilimonitoring.ui.detection.history.HistoryActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_tracking, R.id.navigation_detection, R.id.navigation_forum, R.id.navigation_blog
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // action bar color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.white)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_action_bar, menu)

        val historyIcon = menu.findItem(R.id.menu_history)
        val chatbotIcon = menu.findItem(R.id.menu_chat)
        val searchView = historyIcon?.actionView as? SearchView

        // icon clicked
        historyIcon?.setOnMenuItemClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            true
        }

        chatbotIcon?.setOnMenuItemClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
            true
        }

        // disable top bar icon
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // ngilangin search di fragment home, settings, fav
            historyIcon?.isVisible = destination.id != R.id.navigation_home
                    && destination.id != R.id.navigation_tracking
                    && destination.id != R.id.navigation_forum
                    && destination.id != R.id.navigation_blog
        }
        return true
    }
}