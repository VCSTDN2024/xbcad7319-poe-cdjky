package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PerformanceManagementActivity : AppCompatActivity() {

    private lateinit var tvWelcomeMessage: TextView
    private lateinit var btnGoals: Button
    private lateinit var btnFeedbackSubmission: Button
    private lateinit var btnPersonalPerformance: Button
    private val db = FirebaseFirestore.getInstance()
    private lateinit var loggedInEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_management)

        // Retrieve logged-in actual_employee_id
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI components
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        btnGoals = findViewById(R.id.btnGoals)
        btnFeedbackSubmission = findViewById(R.id.btnFeedbackSubmission)
        btnPersonalPerformance = findViewById(R.id.btnPersonalPerformance)

        // Fetch welcome message
        fetchWelcomeMessage()

        // Set click listeners for navigation
        setupFeatureButtons()
    }

    private fun fetchWelcomeMessage() {
        if (loggedInEmployeeId.isEmpty()) {
            tvWelcomeMessage.text = "Welcome, Employee!"
            return
        }

        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("FullName") ?: "Employee"
                    tvWelcomeMessage.text = "Welcome, $fullName!"
                } else {
                    tvWelcomeMessage.text = "Welcome, Employee!"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch user details: ${e.message}", Toast.LENGTH_SHORT).show()
                tvWelcomeMessage.text = "Welcome, Employee!"
            }
    }

    private fun setupFeatureButtons() {
        btnGoals.setOnClickListener {
            try {
                startActivity(Intent(this, PerformanceActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error navigating to Goals: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        btnFeedbackSubmission.setOnClickListener {
            startActivity(Intent(this, FeedbackSubmissionActivity::class.java))
        }

        btnPersonalPerformance.setOnClickListener {
            startActivity(Intent(this, PersonalPerformanceActivity::class.java))
        }
    }
}
