package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EnrollCourseActivity : AppCompatActivity() {

    private lateinit var etCourseName: EditText
    private lateinit var btnSubmitEnroll: Button
    private lateinit var btnBack: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_course)

        etCourseName = findViewById(R.id.etCourseName)
        btnSubmitEnroll = findViewById(R.id.btnSubmitEnroll)
        btnBack = findViewById(R.id.btnBack)

        btnSubmitEnroll.setOnClickListener {
            val courseName = etCourseName.text.toString().trim()
            if (courseName.isNotEmpty()) {
                enrollInCourse(courseName)
            } else {
                Toast.makeText(this, "Please enter a course name.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun enrollInCourse(courseName: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val enrollmentData = hashMapOf(
                "userId" to userId,
                "courseName" to courseName
            )
            db.collection("enrollments").add(enrollmentData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Enrolled successfully in $courseName.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to enroll.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
