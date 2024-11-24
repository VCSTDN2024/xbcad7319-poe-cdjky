package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackSubmissionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etFeedback: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmitFeedback: Button
    private lateinit var feedbackTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_submission)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        etFeedback = findViewById(R.id.etFeedback)
        ratingBar = findViewById(R.id.ratingBar)
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback)
        feedbackTable = findViewById(R.id.feedbackTable)

        // Load existing feedback
        loadFeedback()

        // Submit feedback functionality
        btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun loadFeedback() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("feedback").get()
                .addOnSuccessListener { documents ->
                    feedbackTable.removeAllViews() // Clear existing rows
                    addTableHeader()

                    for (document in documents) {
                        val employeeName = document.getString("employeeName") ?: "Unknown"
                        val feedbackText = document.getString("feedback") ?: "No feedback"
                        val rating = document.getLong("rating")?.toInt() ?: 0

                        addFeedbackRow(employeeName, feedbackText, rating)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load feedback.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addTableHeader() {
        val headerRow = TableRow(this)

        val headerEmployee = TextView(this)
        headerEmployee.text = "Employee"
        headerEmployee.setPadding(8, 8, 8, 8)
        headerEmployee.textSize = 16f
        headerEmployee.setTypeface(null, android.graphics.Typeface.BOLD)

        val headerFeedback = TextView(this)
        headerFeedback.text = "Feedback"
        headerFeedback.setPadding(8, 8, 8, 8)
        headerFeedback.textSize = 16f
        headerFeedback.setTypeface(null, android.graphics.Typeface.BOLD)

        val headerRating = TextView(this)
        headerRating.text = "Rating"
        headerRating.setPadding(8, 8, 8, 8)
        headerRating.textSize = 16f
        headerRating.setTypeface(null, android.graphics.Typeface.BOLD)

        headerRow.addView(headerEmployee)
        headerRow.addView(headerFeedback)
        headerRow.addView(headerRating)

        feedbackTable.addView(headerRow)
    }

    private fun addFeedbackRow(employeeName: String, feedbackText: String, rating: Int) {
        val row = TableRow(this)

        val tvEmployee = TextView(this)
        tvEmployee.text = employeeName
        tvEmployee.setPadding(8, 8, 8, 8)

        val tvFeedback = TextView(this)
        tvFeedback.text = feedbackText
        tvFeedback.setPadding(8, 8, 8, 8)

        val tvRating = TextView(this)
        tvRating.text = rating.toString()
        tvRating.setPadding(8, 8, 8, 8)

        row.addView(tvEmployee)
        row.addView(tvFeedback)
        row.addView(tvRating)

        feedbackTable.addView(row)
    }

    private fun submitFeedback() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackText = etFeedback.text.toString().trim()
        val rating = ratingBar.rating.toInt()

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter feedback.", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackData = hashMapOf(
            "employeeName" to "Anonymous", // Replace with real name if available
            "feedback" to feedbackText,
            "rating" to rating
        )

        db.collection("feedback").add(feedbackData)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted.", Toast.LENGTH_SHORT).show()
                etFeedback.text.clear()
                ratingBar.rating = 0f
                loadFeedback() // Reload feedback
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show()
            }
    }
}
