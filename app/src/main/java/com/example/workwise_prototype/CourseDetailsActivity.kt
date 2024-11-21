package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : AppCompatActivity() {

    private lateinit var tvCourseName: TextView
    private lateinit var tvCourseDescription: TextView
    private lateinit var tvCourseDetails: TextView
    private lateinit var btnEnroll: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)

        // Initialize UI elements
        tvCourseName = findViewById(R.id.tvCourseName)
        tvCourseDescription = findViewById(R.id.tvCourseDescription)
        tvCourseDetails = findViewById(R.id.tvCourseDetails)
        btnEnroll = findViewById(R.id.btnEnroll)

        // Get the course name from intent
        val courseName = intent.getStringExtra("COURSE_NAME") ?: "Unknown Course"
        tvCourseName.text = courseName

        // Fetch course details from Firebase
        fetchCourseDetails(courseName)

        // Handle enroll button click
        btnEnroll.setOnClickListener {
            enrollInCourse(courseName)
        }
    }

    private fun fetchCourseDetails(courseName: String) {
        db.collection("courses").whereEqualTo("name", courseName).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Course details not found.", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val description = document.getString("description") ?: "No description available."
                        val details = document.getString("details") ?: "No additional details."

                        tvCourseDescription.text = description
                        tvCourseDetails.text = details
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch course details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enrollInCourse(courseName: String) {
        val userId = "CURRENT_USER_ID" // Replace with actual user ID logic
        val enrollmentData = hashMapOf(
            "userId" to userId,
            "course" to courseName
        )

        db.collection("enrollments").add(enrollmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully enrolled in $courseName!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Enrollment failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
