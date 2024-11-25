package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackSubmissionActivity : AppCompatActivity() {

    private lateinit var etFeedback: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmitFeedback: Button
    private lateinit var db: FirebaseFirestore

    private lateinit var loggedInEmployeeId: String
    private var fullName: String = "Anonymous" // Default to anonymous

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_submission)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Retrieve logged-in employee ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI elements
        etFeedback = findViewById(R.id.etFeedback)
        ratingBar = findViewById(R.id.ratingBar)
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback)

        // Fetch the FullName of the logged-in employee
        fetchEmployeeFullName()

        // Set up the feedback submission button
        btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun fetchEmployeeFullName() {
        if (loggedInEmployeeId.isNotEmpty()) {
            db.collection("actual_employees").document(loggedInEmployeeId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        fullName = document.getString("FullName") ?: "Anonymous"
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to retrieve employee details. Defaulting to anonymous.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error fetching employee details. Defaulting to anonymous.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                this,
                "Employee ID not found. Defaulting to anonymous.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun submitFeedback() {
        val feedbackText = etFeedback.text.toString().trim()
        val rating = ratingBar.rating

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter feedback before submitting.", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackData = hashMapOf(
            "employeeName" to fullName,
            "feedback" to feedbackText,
            "rating" to rating
        )

        db.collection("feedback").add(feedbackData)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted successfully.", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit feedback: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etFeedback.text.clear()
        ratingBar.rating = 0f
    }
}
