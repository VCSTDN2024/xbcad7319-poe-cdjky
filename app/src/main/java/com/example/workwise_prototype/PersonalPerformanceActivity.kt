package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonalPerformanceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var profilePicture: ImageView
    private lateinit var employeeName: TextView
    private lateinit var avgScore: TextView
    private lateinit var performanceLevel: TextView
    private lateinit var goalList: ListView
    private lateinit var reviewList: ListView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_performance)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI Components
        profilePicture = findViewById(R.id.profile_picture)
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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        employeeName.text = document.getString("name") ?: "Unknown User"
                    } else {
                        employeeName.text = "Unknown User"
                    }
                }
                .addOnFailureListener {
                    employeeName.text = "Error fetching user details"
                }
        } else {
            employeeName.text = "No User Logged In"
        }
    }

    private fun fetchPerformanceData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("performance_data").get()
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
        } else {
            avgScore.text = "No User Logged In"
            performanceLevel.text = "No User Logged In"
        }
    }


    private fun fetchCurrentGoals() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("performance_goals").get()
                .addOnSuccessListener { documents ->
                    val goals = documents.map { it.getString("goal") ?: "No description" }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, goals)
                    goalList.adapter = adapter
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching goals", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchRecentReviews() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("performance_reviews").get()
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
}
