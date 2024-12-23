package com.example.cimon_chilimonitoring

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.data.local.room.blog.BlogDao
import com.example.cimon_chilimonitoring.databinding.ActivityMainBinding
import com.example.cimon_chilimonitoring.ui.chatbot.ChatbotActivity
import com.example.cimon_chilimonitoring.ui.detection.history.HistoryActivity
import com.example.cimon_chilimonitoring.ui.pofile.ProfileActivity
import com.example.cimon_chilimonitoring.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: MainViewModel by viewModels()

    // networking
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(layoutInflater)
        // action bar color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.white)))

        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_tracking, R.id.navigation_detection, R.id.navigation_forum, R.id.navigation_blog
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // firebase
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        } else {
            if (viewModel.isFirstLaunch) {
                // Show loading
                val loadingDialog = ProgressDialog(this)
                loadingDialog.setMessage("Memulai Aplikasi ...")
                loadingDialog.setCancelable(true)
                loadingDialog.show()

                // Log the user token
                firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        val idToken = task.result?.token
                        if (idToken != null) {
                            TokenManager.idToken = idToken
                            TokenManager.userId = firebaseUser.uid
                            TokenManager.email = firebaseUser.email
                        }
                    } else {
                        Toast.makeText(this, "Tidak ada koneksi Internet", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.isFirstLaunch = false
            }
        }

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val loadingDialog = ProgressDialog(this@MainActivity)
                loadingDialog.setMessage("Memulai Aplikasi ...")
                loadingDialog.setCancelable(true)
                loadingDialog.show()
                super.onAvailable(network)
                if (TokenManager.idToken == null) {
                    firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
                        loadingDialog.dismiss()
                        if (task.isSuccessful) {
                            val idToken = task.result?.token
                            if (idToken != null) {
                                TokenManager.idToken = idToken
                                TokenManager.userId = firebaseUser.uid
                                TokenManager.email = firebaseUser.email
                                Toast.makeText(this@MainActivity, "Terhubung ke internet", Toast.LENGTH_SHORT).show()
                                recreate()
                            }
                        } else {
                        }
                    }
                }
                loadingDialog.dismiss()
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_action_bar, menu)

        val historyIcon = menu.findItem(R.id.menu_history)
        val chatbotIcon = menu.findItem(R.id.menu_chat)
        val accountIcon = menu.findItem(R.id.menu_account)

        // icon clicked
        historyIcon?.setOnMenuItemClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
            true
        }

        chatbotIcon?.setOnMenuItemClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
            true
        }

        accountIcon?.setOnMenuItemClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
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

            chatbotIcon?.isVisible = destination.id != R.id.navigation_home

            accountIcon?.isVisible = destination.id != R.id.navigation_detection
                    && destination.id != R.id.navigation_tracking
                    && destination.id != R.id.navigation_forum
                    && destination.id != R.id.navigation_blog
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        HistoryDatabase.getInstance(this).close()
    }
}