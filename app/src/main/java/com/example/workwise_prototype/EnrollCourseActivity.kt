package com.example.workwise_prototype

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EnrollCourseActivity : AppCompatActivity() {

    private lateinit var lvEnrolledCourses: ListView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val enrolledCourses = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_course)

        lvEnrolledCourses = findViewById(R.id.lvEnrolledCourses)
        fetchEnrolledCourses()
    }

    private fun fetchEnrolledCourses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("enrollments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                enrolledCourses.clear()
                for (document in documents) {
                    val courseName = document.getString("course") ?: "Unknown Course"
                    enrolledCourses.add(courseName)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, enrolledCourses)
                lvEnrolledCourses.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch enrolled courses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
