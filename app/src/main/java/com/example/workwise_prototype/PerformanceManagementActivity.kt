package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PerformanceManagementActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnGoals: Button
    private lateinit var btnPerformanceData: Button
    private lateinit var btnFeedbackSubmission: Button
    private lateinit var btnPersonalPerformance: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_management)

        // Initialize buttons
        btnBack = findViewById(R.id.btnBack)
        btnGoals = findViewById(R.id.btnGoals)
        btnPerformanceData = findViewById(R.id.btnPerformanceData)
        btnFeedbackSubmission = findViewById(R.id.btnFeedbackSubmission)
        btnPersonalPerformance = findViewById(R.id.btnPersonalPerformance)

        // Back Button functionality
        btnBack.setOnClickListener {
            finish() // Ends the current activity and goes back to the previous one
        }
        // Set click listeners for navigation
        btnGoals.setOnClickListener {
            startActivity(Intent(this, PerformanceActivity::class.java))
        }

        btnPerformanceData.setOnClickListener {
            startActivity(Intent(this, PerformanceDataActivity::class.java))
        }

        btnFeedbackSubmission.setOnClickListener {
            startActivity(Intent(this, FeedbackSubmissionActivity::class.java))
        }

        btnPersonalPerformance.setOnClickListener {
            startActivity(Intent(this, PersonalPerformanceActivity::class.java))
        }
        // Back Button functionality
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}
