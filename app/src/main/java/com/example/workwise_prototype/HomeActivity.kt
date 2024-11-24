package com.example.workwise_prototype

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        // Setup gradient backgrounds
        setupGradients()

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

    private fun setupGradients() {
        applyGradient(R.id.employeeRecords, R.color.gradientBlueStart, R.color.gradientBlueEnd)
        applyGradient(R.id.attendance, R.color.gradientPurpleStart, R.color.gradientPurpleEnd)
        applyGradient(R.id.payroll, R.color.gradientGreenStart, R.color.gradientGreenEnd)
        applyGradient(R.id.performance, R.color.gradientOrangeStart, R.color.gradientOrangeEnd)
        applyGradient(R.id.training, R.color.gradientRedStart, R.color.gradientRedEnd)
        applyGradient(R.id.jobs, R.color.gradientBlueStart, R.color.gradientBlueEnd)
    }

    private fun applyGradient(viewId: Int, startColorRes: Int, endColorRes: Int) {
        val view = findViewById<LinearLayout>(viewId)
        val startColor = ContextCompat.getColor(this, startColorRes)
        val endColor = ContextCompat.getColor(this, endColorRes)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(startColor, endColor)
        )
        gradient.cornerRadius = 16f
        view.background = gradient
    }

    private fun setupClickListeners() {
        val navigationMap = mapOf(
            R.id.employeeRecords to EmployeeRecordsActivity::class.java,
            R.id.attendance to AttendanceActivity::class.java, // This must work!
            R.id.payroll to PayrollActivity::class.java,
            R.id.performance to PerformanceManagementActivity::class.java,
            R.id.training to ViewCourseActivity::class.java,
            R.id.jobs to JobsAndOnboardingActivity::class.java
        )

        navigationMap.forEach { (viewId, activityClass) ->
            findViewById<LinearLayout>(viewId).apply {
                setOnClickListener {
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
                showToast("Failed to fetch user data: ${e.message}")
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
        showToast("Profile feature coming soon!")
    }

    private fun handleNotificationsClick() {
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
