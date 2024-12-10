package com.example.cimon_chilimonitoring.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.cimon_chilimonitoring.databinding.ActivityIntroBinding
import com.example.cimon_chilimonitoring.ui.welcome.WelcomeActivity

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // cuman buat first launch
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        if (!isFirstLaunch) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        enableEdgeToEdge()

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}