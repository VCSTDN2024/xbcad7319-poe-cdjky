package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerformanceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvEmployeeName: TextView
    private lateinit var tvJobRole: TextView
    private lateinit var tvPerformanceScore: TextView
    private lateinit var tvPerformanceStatus: TextView
    private lateinit var btnSaveGoal: Button
    private lateinit var btnBack: Button
    private lateinit var goalDescription: EditText
    private lateinit var goalStartDate: EditText
    private lateinit var goalEndDate: EditText
    private lateinit var goalHours: EditText
    private lateinit var loggedInEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get logged-in employee ID
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        tvEmployeeName = findViewById(R.id.employee_name)
        tvJobRole = findViewById(R.id.job_role)
        tvPerformanceScore = findViewById(R.id.performance_score)
        tvPerformanceStatus = findViewById(R.id.performance_status)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        btnBack = findViewById(R.id.btnBack)
        goalDescription = findViewById(R.id.goalDescription)
        goalStartDate = findViewById(R.id.goalStartDate)
        goalEndDate = findViewById(R.id.goalEndDate)
        goalHours = findViewById(R.id.goalHours)

        // Fetch and display user details
        fetchAndDisplayUserDetails()

        // Save goal functionality
        btnSaveGoal.setOnClickListener {
            saveGoal()
        }

        // Set up back button
        btnBack.setOnClickListener {
            finish()
        }

        // Load existing goals
        loadGoals()
    }

    private fun fetchAndDisplayUserDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("FullName") ?: "Unknown User"
                    val jobRole = document.getString("Role") ?: "N/A"

                    tvEmployeeName.text = fullName
                    tvJobRole.text = jobRole
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveGoal() {
        val description = goalDescription.text.toString().trim()
        val startDate = goalStartDate.text.toString().trim()
        val endDate = goalEndDate.text.toString().trim()
        val hours = goalHours.text.toString().trim().toDoubleOrNull()

        if (description.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && hours != null) {
            val goalData = hashMapOf(
                "actual_employee_id" to loggedInEmployeeId,
                "description" to description,
                "start_date" to startDate,
                "end_date" to endDate,
                "hours" to hours,
                "status" to "In Progress"
            )

            db.collection("goals").add(goalData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Goal saved successfully.", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                    loadGoals()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save goal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadGoals() {
        db.collection("goals")
            .whereEqualTo("actual_employee_id", loggedInEmployeeId)
            .get()
            .addOnSuccessListener { documents ->
                val goalList = ArrayList<String>()
                for (document in documents) {
                    val description = document.getString("description") ?: "No Description"
                    val hours = document.getDouble("hours") ?: 0.0
                    val status = document.getString("status") ?: "No Status"
                    goalList.add("$description (Hours: $hours, Status: $status)")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load goals.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        goalDescription.text.clear()
        goalStartDate.text.clear()
        goalEndDate.text.clear()
        goalHours.text.clear()
    }
}
