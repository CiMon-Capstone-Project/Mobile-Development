package com.example.cimon_chilimonitoring.ui.splashscreen

import android.app.ActivityOptions
import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cimon_chilimonitoring.R

class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    // mp3
    private lateinit var sp: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        handler = Handler(Looper.getMainLooper())

        runnable = Runnable {
            val intent = Intent(this, IntroActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
            finish()
        }
        handler.postDelayed(runnable, 5500)

        // sfx
        sp = SoundPool.Builder()
            .setMaxStreams(10)
            .build()
        soundId = sp.load(this, R.raw.cimon_intro_sfx, 1)
        sp.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId == soundId) {
                spLoaded = true
                handler.postDelayed({
                    sp.play(soundId, 1f, 1f, 0, 0, 1f)
                }, 700) // delay
            } else {
                Toast.makeText(this@SplashActivity, "Gagal load SFX", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}