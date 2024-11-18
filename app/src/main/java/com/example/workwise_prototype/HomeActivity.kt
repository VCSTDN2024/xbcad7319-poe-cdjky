package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcomeMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)

        // Check user authentication and setup UI
        checkUserAuthentication()

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Setup Feature Buttons
        setupFeatureButtons()
    }

    private fun checkUserAuthentication() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Fetch and display the user's name
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val employeeName = document.getString("name") ?: "User"
                    tvWelcomeMessage.text = "Welcome, $employeeName!"
                }
                .addOnFailureListener {
                    tvWelcomeMessage.text = "Welcome!"
                }
        } else {
            // Redirect to LoginActivity if the user is not authenticated
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // End current activity
        }
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


        // Leave Management
        //findViewById<LinearLayout>(R.id.leaveManagement).setOnClickListener {
        //    startActivity(Intent(this, LeaveManagementActivity::class.java))
        //}
    }
}
