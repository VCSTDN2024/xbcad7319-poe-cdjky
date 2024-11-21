package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseMenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvCourseMenuTitle: TextView
    private lateinit var btnViewCoursesContainer: LinearLayout
    private lateinit var btnEnrollCoursesContainer: LinearLayout
    private lateinit var btnViewProgressContainer: LinearLayout
    private lateinit var btnNotificationsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_menu)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        tvCourseMenuTitle = findViewById(R.id.tvCourseMenuTitle)
        btnViewCoursesContainer = findViewById(R.id.btnViewCoursesContainer)
        btnEnrollCoursesContainer = findViewById(R.id.btnEnrollCoursesContainer)
        btnViewProgressContainer = findViewById(R.id.btnViewProgressContainer)
        btnNotificationsContainer = findViewById(R.id.btnNotificationsContainer)

        // Fetch and display the user name
        fetchUserName()

        // Set up button click listeners
        btnViewCoursesContainer.setOnClickListener {
            startActivity(Intent(this, ViewCourseActivity::class.java))
        }

        btnEnrollCoursesContainer.setOnClickListener {
            startActivity(Intent(this, EnrollCourseActivity::class.java))
        }

        btnViewProgressContainer.setOnClickListener {
            startActivity(Intent(this, ViewProgressActivity::class.java))
        }

        btnNotificationsContainer.setOnClickListener {
            Toast.makeText(this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "User"
                        tvCourseMenuTitle.text = "Welcome, $userName!"
                    } else {
                        tvCourseMenuTitle.text = "Welcome, User!"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvCourseMenuTitle.text = "Welcome, User!"
        }
    }
}
