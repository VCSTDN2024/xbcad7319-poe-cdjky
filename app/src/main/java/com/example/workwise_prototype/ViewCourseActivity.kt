package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ViewCourseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var lvCourses: ListView
    private val courseTitles = mutableListOf<String>()
    private val courseDescriptions = mutableListOf<String>()
    private val courseIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance()
        lvCourses = findViewById(R.id.lvCourses)

        // Fetch and display courses
        fetchCourses()
    }

    private fun fetchCourses() {
        db.collection("ActualCourses").get()
            .addOnSuccessListener { documents ->
                courseTitles.clear()
                courseDescriptions.clear()
                courseIds.clear()

                for (document in documents) {
                    val title = document.getString("title") ?: "Unnamed Course"
                    val description = document.getString("description") ?: "No description provided"
                    val courseId = document.id

                    courseTitles.add(title)
                    courseDescriptions.add(description)
                    courseIds.add(courseId)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, courseTitles)
                lvCourses.adapter = adapter

                lvCourses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val selectedCourseId = courseIds[position]
                    val selectedTitle = courseTitles[position]
                    val selectedDescription = courseDescriptions[position]

                    // Navigate to CourseDetailsActivity with the selected course details
                    val intent = Intent(this, CourseDetailsActivity::class.java)
                    intent.putExtra("courseId", selectedCourseId)
                    intent.putExtra("courseTitle", selectedTitle)
                    intent.putExtra("courseDescription", selectedDescription)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch courses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
