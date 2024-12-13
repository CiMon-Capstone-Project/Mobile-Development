package com.example.cimon_chilimonitoring.ui.login

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.cimon_chilimonitoring.MainActivity
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.ActivityLoginBinding
import com.example.cimon_chilimonitoring.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private  lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.isEnabled = false

        binding.btnTxtRegister.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        with(binding){
            edEmailLogin.addTextChangedListener {
                enableLoginButton()
            }

            edPasswordLogin.addTextChangedListener {
                enableLoginButton()
            }

            btnLogin.setOnClickListener{
                loginUser()
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun loginUser(){
        with(binding){
            val email = edEmailLogin.text.toString()
            val password = edPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                progressBar.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.signInWithEmailAndPassword(email, password).await()

                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            updateUI()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            progressBar.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                        }
                    }
                }
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun enableLoginButton() {
        binding.btnLogin.isEnabled = binding.edEmailLogin.text.toString().isNotEmpty() &&
                binding.edPasswordLogin.text.toString().isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(binding.edEmailLogin.text.toString()).matches() &&
                binding.edPasswordLogin.text.toString().length >= 8
    }

    private fun updateUI(){
        if (auth.currentUser != null){
            Toast.makeText(this@LoginActivity, "Berhasil masuk!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this@LoginActivity)
        startActivity(intent, options.toBundle())
    }
}