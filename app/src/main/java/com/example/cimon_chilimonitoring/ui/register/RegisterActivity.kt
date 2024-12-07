package com.example.cimon_chilimonitoring.ui.register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cimon_chilimonitoring.MainActivity
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.ActivityRegisterBinding
import com.example.cimon_chilimonitoring.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        with(binding){
            btnRegister.setOnClickListener{
                registerUser()
            }
        }
    }

    private fun registerUser() {
        with(binding) {
            val email = edEmailRegister.text.toString()
            val password = edPasswordRegister.text.toString()
            val displayName = edName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        auth.currentUser?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build()
                        )?.await()

                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            updateUI()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            progressBar.visibility = View.GONE

            btnTxtRegister.setOnClickListener {
                onLoginClick(it)
            }
        }
    }

    private fun updateUI(){
        if (auth.currentUser != null){
            Toast.makeText(this@RegisterActivity, "You are Logged in!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this@RegisterActivity)
        startActivity(intent, options.toBundle())
    }
}