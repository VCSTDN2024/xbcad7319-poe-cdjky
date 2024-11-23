package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var profileImage: ImageView
    private lateinit var notificationIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        initializeUI()

        // Setup click listeners
        setupClickListeners()

        // Fetch user data
        fetchUserData()
    }

    private fun initializeUI() {
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        profileImage = findViewById(R.id.profileImage)
        notificationIcon = findViewById(R.id.notificationIcon)

        // Set click listeners for profile and notifications
        profileImage.setOnClickListener { handleProfileClick() }
        notificationIcon.setOnClickListener { handleNotificationsClick() }
    }

    private fun setupClickListeners() {
        // Create a map of view IDs to their corresponding activities
        val navigationMap = mapOf(
            R.id.employeeRecords to EmployeeRecordsActivity::class.java,
            R.id.attendance to AttendanceActivity::class.java,
            R.id.payroll to PayrollActivity::class.java,
            R.id.performance to PerformanceManagementActivity::class.java,
            R.id.training to ViewCourseActivity::class.java,
            R.id.jobs to JobsAndOnboardingActivity::class.java
        )

        // Set up click listeners for all feature cards
        navigationMap.forEach { (viewId, activityClass) ->
            findViewById<LinearLayout>(viewId).apply {
                setOnClickListener {
                    // Add ripple effect
                    it.isPressed = true
                    navigateToActivity(activityClass)
                }
            }
        }
    }

    private fun fetchUserData() {
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        val employeeId = sharedPreferences.getString("actual_employee_id", null)

        if (employeeId.isNullOrEmpty()) {
            handleInvalidSession()
            return
        }

        db.collection("actual_employees").document(employeeId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    updateUIWithUserData(document.getString("FullName"))
                } else {
                    handleMissingEmployeeRecord()
                }
            }
            .addOnFailureListener { e ->
                showError("Failed to fetch user data: ${e.message}")
            }
    }

    private fun updateUIWithUserData(fullName: String?) {
        tvWelcomeMessage.text = "Welcome, ${fullName ?: "Employee"}!"
    }

    private fun handleInvalidSession() {
        showToast("Session expired. Please login again.")
        redirectToLogin()
    }

    private fun handleMissingEmployeeRecord() {
        showToast("Employee record not found.")
        tvWelcomeMessage.text = "Welcome, Employee!"
    }

    private fun handleProfileClick() {
        // Implement profile click action
        showToast("Profile feature coming soon!")
    }

    private fun handleNotificationsClick() {
        // Implement notifications click action
        showToast("Notifications feature coming soon!")
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showError(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Optional: Add animation overrides for better transitions

}