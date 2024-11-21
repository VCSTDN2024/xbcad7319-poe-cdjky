package com.example.workwise_prototype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvCourseTitle: TextView
    private lateinit var tvCourseDescription: TextView
    private lateinit var btnWatchVideo: Button
    private lateinit var btnEnroll: Button

    private var courseId: String? = null
    private var courseTitle: String? = null
    private var courseDescription: String? = null
    private var videoLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance()
        tvCourseTitle = findViewById(R.id.tvCourseTitle)
        tvCourseDescription = findViewById(R.id.tvCourseDescription)
        btnWatchVideo = findViewById(R.id.btnWatchVideo)
        btnEnroll = findViewById(R.id.btnEnroll)

        // Retrieve course details passed from  the ViewCourseActivity
        courseId = intent.getStringExtra("courseId")
        courseTitle = intent.getStringExtra("courseTitle")
        courseDescription = intent.getStringExtra("courseDescription")
        videoLink = intent.getStringExtra("videoLink")

        // Populate UI with course details
        populateCourseDetails()

        // Set up click listener for video link
        btnWatchVideo.setOnClickListener {
            videoLink?.let { openVideoLink(it) }
        }

        // Set up click listener for enroll button
        btnEnroll.setOnClickListener {
            enrollInCourse()
        }
    }

    private fun populateCourseDetails() {
        tvCourseTitle.text = courseTitle ?: "No Title Available"
        tvCourseDescription.text = courseDescription ?: "No Description Available"
        btnWatchVideo.isEnabled = !videoLink.isNullOrEmpty()
    }

    private fun enrollInCourse() {
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        val loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null)

        if (loggedInEmployeeId == null || courseId == null) {
            Toast.makeText(this, "Failed to enroll. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch Employee FullName
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { employeeDocument ->
                val fullName = employeeDocument.getString("FullName") ?: "Unknown Employee"

                // Fetch Course Title
                db.collection("ActualCourses").document(courseId!!).get()
                    .addOnSuccessListener { courseDocument ->
                        val courseTitle = courseDocument.getString("title") ?: "Unnamed Course"

                        // Prepare Enrollment Data
                        val enrollmentData = hashMapOf(
                            "courseId" to courseId,
                            "title" to courseTitle,
                            "actual_employee_id" to loggedInEmployeeId,
                            "FullName" to fullName
                        )

                        // Save to CourseEnrollments Collection
                        db.collection("CourseEnrollments").add(enrollmentData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Successfully enrolled in $courseTitle!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to enroll: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to fetch course details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch employee details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openVideoLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}
