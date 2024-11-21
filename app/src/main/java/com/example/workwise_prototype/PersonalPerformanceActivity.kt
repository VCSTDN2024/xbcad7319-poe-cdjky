package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PersonalPerformanceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var employeeName: TextView
    private lateinit var avgScore: TextView
    private lateinit var performanceLevel: TextView
    private lateinit var goalList: ListView
    private lateinit var reviewList: ListView
    private lateinit var btnBack: Button

    private lateinit var loggedInEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_performance)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()

        // Retrieve logged-in employee ID
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI Components
        employeeName = findViewById(R.id.employee_name)
        avgScore = findViewById(R.id.avgScore)
        performanceLevel = findViewById(R.id.performanceLevel)
        goalList = findViewById(R.id.goalList)
        reviewList = findViewById(R.id.reviewList)
        btnBack = findViewById(R.id.btnBack)

        // Back Button Functionality
        btnBack.setOnClickListener {
            finish()
        }

        // Fetch and Populate Data
        fetchUserDetails()
        fetchPerformanceData()
        fetchCurrentGoals()
        fetchRecentReviews()
    }

    private fun fetchUserDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    employeeName.text = document.getString("FullName") ?: "Unknown User"
                } else {
                    employeeName.text = "Unknown User"
                }
            }
            .addOnFailureListener {
                employeeName.text = "Error fetching user details"
            }
    }

    private fun fetchPerformanceData() {
        db.collection("performance_data").whereEqualTo("employeeId", loggedInEmployeeId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val avg = documents.map { it.getDouble("score") ?: 0.0 }.average()
                    avgScore.text = "Average Score: %.2f".format(avg)
                    performanceLevel.text = "Performance Level: ${
                        if (avg >= 8) "Outstanding"
                        else if (avg >= 6) "Good"
                        else "Needs Improvement"
                    }"
                } else {
                    avgScore.text = "Average Score: N/A"
                    performanceLevel.text = "Performance Level: N/A"
                }
            }
            .addOnFailureListener {
                avgScore.text = "Error fetching performance data"
                performanceLevel.text = "Error fetching performance data"
            }
    }

    private fun fetchCurrentGoals() {
        db.collection("goals").whereEqualTo("employeeId", loggedInEmployeeId).get()
            .addOnSuccessListener { documents ->
                val goals = documents.map { it.getString("description") ?: "No description" }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, goals)
                goalList.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching goals", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchRecentReviews() {
        db.collection("performance_reviews").whereEqualTo("employeeId", loggedInEmployeeId).get()
            .addOnSuccessListener { documents ->
                val reviews = documents.map {
                    val feedback = it.getString("feedback") ?: "No feedback"
                    val rating = it.getDouble("rating") ?: 0.0
                    "Feedback: $feedback, Rating: $rating"
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reviews)
                reviewList.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching reviews", Toast.LENGTH_SHORT).show()
            }
    }
}
