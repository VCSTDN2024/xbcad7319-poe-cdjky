package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PersonalPerformanceActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var employeeName: TextView
    private lateinit var avgScore: TextView
    private lateinit var performanceLevel: TextView
    private lateinit var feedbackList: ListView
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
        feedbackList = findViewById(R.id.feedbackList)



        // Fetch and Populate Data
        fetchUserDetails()
        fetchGoalsFeedback()
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

    private fun fetchGoalsFeedback() {
        db.collection("goals_feedback")
            .whereEqualTo("actual_employee_id", loggedInEmployeeId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val feedbackData = mutableListOf<String>()
                    var totalScore = 0.0
                    var count = 0

                    // Log total number of documents fetched
                    println("Total feedback documents retrieved: ${documents.size()}")

                    for (document in documents) {
                        val description = document.getString("description") ?: "No description"
                        val feedback = document.getString("feedback") ?: "No feedback"
                        val score = try {
                            document.getDouble("score") ?: 0.0 // Default to 0.0 if missing
                        } catch (e: Exception) {
                            0.0 // Handle cases where score is not a number
                        }
                        val startDate = document.getTimestamp("start_date")?.toDate()?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                        } ?: "N/A"
                        val endDate = document.getTimestamp("end_date")?.toDate()?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                        } ?: "N/A"

                        // Log each feedback document
                        println("Feedback Document: $description, Score: $score, Feedback: $feedback")

                        totalScore += score
                        count++

                        feedbackData.add(
                            "Goal: $description\nStart: $startDate\nEnd: $endDate\nScore: $score\nFeedback: $feedback"
                        )
                    }

                    // Update performance level and average score
                    avgScore.text = "Average Score: %.2f".format(totalScore / count)
                    performanceLevel.text = "Performance Level: ${
                        when {
                            totalScore / count >= 8 -> "Outstanding"
                            totalScore / count >= 6 -> "Good"
                            else -> "Needs Improvement"
                        }
                    }"

                    // Display feedback
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, feedbackData)
                    feedbackList.adapter = adapter
                } else {
                    avgScore.text = "Average Score: N/A"
                    performanceLevel.text = "Performance Level: N/A"
                    Toast.makeText(this, "No feedback found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching feedback: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}