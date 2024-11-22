package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("actual_employees").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("FullName") ?: "Unknown User"
                        val jobRole = document.getString("Role") ?: "N/A"

                        tvEmployeeName.text = userName
                        tvJobRole.text = jobRole
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveGoal() {
        val description = goalDescription.text.toString().trim()
        val startDate = goalStartDate.text.toString().trim()
        val endDate = goalEndDate.text.toString().trim()
        val hours = goalHours.text.toString().trim()

        if (description.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && hours.isNotEmpty()) {
            val userId = auth.currentUser?.uid ?: return

            // Convert date strings to Timestamps
            val startTimestamp = convertToTimestamp(startDate)
            val endTimestamp = convertToTimestamp(endDate)

            if (startTimestamp == null || endTimestamp == null) {
                Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show()
                return
            }

            val goalData = hashMapOf(
                "user_id" to userId,
                "description" to description,
                "start_date" to startTimestamp,
                "end_date" to endTimestamp,
                "hours" to hours.toDoubleOrNull(),
                "status" to "In Progress"
            )

            db.collection("goals").add(goalData) // Save to "goals" collection
                .addOnSuccessListener {
                    Toast.makeText(this, "Goal saved successfully.", Toast.LENGTH_SHORT).show()
                    clearInputFields() // Clear inputs after saving
                    loadGoals()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save goal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToTimestamp(dateString: String): Timestamp? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            null
        }
    }

    private fun loadGoals() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("goals")
            .whereEqualTo("user_id", userId) // Filter by logged-in user's goals
            .get()
            .addOnSuccessListener { documents ->
                val goalList = ArrayList<String>()
                for (document in documents) {
                    val description = document.getString("description") ?: "No Description"
                    val hours = document.getDouble("hours") ?: 0.0
                    val status = document.getString("status") ?: "No Status"
                    goalList.add("$description (Hours: $hours, Status: $status)")
                }
                // Use goalList to update the UI or display the goals
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
