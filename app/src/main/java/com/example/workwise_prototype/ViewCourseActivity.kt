package com.example.workwise_prototype

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ViewCourseActivity : AppCompatActivity() {

    private lateinit var courseListView: ListView
    private lateinit var btnBack: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        courseListView = findViewById(R.id.courseListView)
        btnBack = findViewById(R.id.btnBack)

        fetchCourses()

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchCourses() {
        db.collection("courses").get()
            .addOnSuccessListener { result ->
                val courseList = mutableListOf<String>()
                for (document in result) {
                    val courseName = document.getString("name")
                    val courseDescription = document.getString("description")
                    courseList.add("$courseName: $courseDescription")
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, courseList)
                courseListView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch courses.", Toast.LENGTH_SHORT).show()
            }
    }
}
