package com.example.workwise_prototype

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var actualEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firestore and SharedPreferences
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)

        // Fetch actual_employee_id from SharedPreferences
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI elements
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)

        // Validate and fetch welcome message
        if (actualEmployeeId.isNotEmpty()) {
            fetchAndDisplayWelcomeMessage()
        } else {
            handleMissingEmployeeId()
        }

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Setup Feature Buttons
        setupFeatureButtons()
    }

    private fun fetchAndDisplayWelcomeMessage() {
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        val actualEmployeeId = sharedPreferences.getString("actual_employee_id", null)

        if (!actualEmployeeId.isNullOrEmpty()) {
            db.collection("actual_employees").document(actualEmployeeId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fullName = document.getString("FullName") ?: "Employee"
                        tvWelcomeMessage.text = "Welcome, $fullName!"
                    } else {
                        tvWelcomeMessage.text = "Welcome, Employee!"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch details: ${e.message}", Toast.LENGTH_SHORT).show()
                    tvWelcomeMessage.text = "Welcome, Employee!"
                }
        } else {
            tvWelcomeMessage.text = "Welcome, Employee!"
        }
    }


    private fun handleMissingEmployeeId() {
        Toast.makeText(this, "User not authenticated. Redirecting to login...", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun handleMissingEmployeeRecord() {
        Toast.makeText(this, "Employee record not found in the database.", Toast.LENGTH_SHORT).show()
        tvWelcomeMessage.text = "Welcome, Employee!"
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_self_service -> {
                    startActivity(Intent(this, SelfServiceActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFeatureButtons() {
        // Employee Records
        findViewById<LinearLayout>(R.id.employeeRecords).setOnClickListener {
            startActivity(Intent(this, EmployeeRecordsActivity::class.java))
        }

        // Attendance
        findViewById<LinearLayout>(R.id.attendance).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        // Payroll
        findViewById<LinearLayout>(R.id.payroll).setOnClickListener {
            startActivity(Intent(this, PayrollActivity::class.java))
        }

        // Performance Management
        findViewById<LinearLayout>(R.id.performance).setOnClickListener {
            startActivity(Intent(this, PerformanceManagementActivity::class.java))
        }

        // Training
        findViewById<LinearLayout>(R.id.training).setOnClickListener {
            startActivity(Intent(this, CourseMenuActivity::class.java))
        }
    }
}