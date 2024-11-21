package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewCourseActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        // Set up buttons for each course
        setupEnrollButton(findViewById(R.id.btnCourse1), "Leadership and Management Training")
        setupEnrollButton(findViewById(R.id.btnCourse2), "Technical Skills: Data Analysis")
        setupEnrollButton(findViewById(R.id.btnCourse3), "Soft Skills: Communication Workshop")
        setupEnrollButton(findViewById(R.id.btnCourse4), "Advanced Technical Writing")
        setupEnrollButton(findViewById(R.id.btnCourse5), "Intermediate Job Training")
        setupEnrollButton(findViewById(R.id.btnCourse6), "Front-end Development")
        setupEnrollButton(findViewById(R.id.btnCourse7), "Back-end Development")
        setupEnrollButton(findViewById(R.id.btnCourse8), "Leadership Team Training")
    }

    private fun setupEnrollButton(button: Button, courseName: String) {
        button.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val enrollmentData = hashMapOf(
                    "userId" to userId,
                    "courseName" to courseName,
                    "status" to "Enrolled"
                )
                db.collection("enrollments")
                    .add(enrollmentData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully enrolled in $courseName", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Enrollment failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
