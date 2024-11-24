package com.example.workwise_prototype

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)

        // Initialize UI elements
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }
    }

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

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchEmployeeRecord(email)
                } else {
                    handleLoginError(task.exception?.message ?: "Unknown error")
                }
            }
    }

    private fun fetchEmployeeRecord(email: String) {
        db.collection("actual_employees").whereEqualTo("Email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val actualEmployeeId = document.getString("actual_employee_id") ?: ""
                    saveActualEmployeeId(actualEmployeeId)
                    navigateToHomeActivity()
                } else {
                    Toast.makeText(this, "Employee record cannot be found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch employee record: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveActualEmployeeId(actualEmployeeId: String) {
        sharedPreferences.edit().putString("actual_employee_id", actualEmployeeId).apply()
    }

    private fun navigateToHomeActivity() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

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
