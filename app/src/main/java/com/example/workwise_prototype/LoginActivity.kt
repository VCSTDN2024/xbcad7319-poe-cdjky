package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Initializing views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        // Navigate to sign-up screen
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }


    }

    // Validate the input fields
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters long"
            return false
        }

        return true
    }

    // Log-in user with Firebase
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Navigate to the next activity (e.g., HomeActivity)
                    startActivity(Intent(this, HomeActivity::class.java)) // Example navigation
                    finish()
                } else {
                    handleLoginError(task.exception?.message.toString())
                }
            }
    }

    // Handle login errors
    private fun handleLoginError(errorMessage: String) {
        when {
            errorMessage.contains("There is no user record") -> {
                etEmail.error = "No account found with this email"
            }
            errorMessage.contains("The password is invalid") -> {
                etPassword.error = "Incorrect password"
            }
            else -> {
                Toast.makeText(this, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
