package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseMenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcome: TextView
    private lateinit var etSearch: EditText
    private lateinit var btnViewCourse: Button
    private lateinit var btnEnrollCourse: Button
    private lateinit var btnViewProgress: Button
    private lateinit var btnNotifications: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_menu)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome)
        etSearch = findViewById(R.id.etSearch)
        btnViewCourse = findViewById(R.id.btnViewCourse)
        btnEnrollCourse = findViewById(R.id.btnEnrollCourse)
        btnViewProgress = findViewById(R.id.btnViewProgress)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnBack = findViewById(R.id.btnBack)

        // Fetch and display user name
        fetchUserName()

        // Set up button click listeners
        btnViewCourse.setOnClickListener {
            startActivity(Intent(this, ViewCourseActivity::class.java))
        }

        btnEnrollCourse.setOnClickListener {
            startActivity(Intent(this, EnrollCourseActivity::class.java))
        }

        btnViewProgress.setOnClickListener {
            startActivity(Intent(this, ViewProgressActivity::class.java))
        }

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Feature Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "User"
                        tvWelcome.text = "Welcome, $userName!"
                    } else {
                        tvWelcome.text = "Welcome, User!"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
