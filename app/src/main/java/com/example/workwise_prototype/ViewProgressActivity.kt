package com.example.workwise_prototype

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewProgressActivity : AppCompatActivity() {

    private lateinit var listViewProgress: ListView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val progressList = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_progress)

        listViewProgress = findViewById(R.id.lvProgress)

        loadProgressData()
    }

    private fun loadProgressData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    progressList.clear()
                    for (document in documents) {
                        val courseName = document.getString("courseName") ?: "Unknown Course"
                        val status = document.getString("status") ?: "In Progress"

                        val dataMap = HashMap<String, String>()
                        dataMap["courseName"] = courseName
                        dataMap["status"] = status
                        progressList.add(dataMap)
                    }

                    val adapter = SimpleAdapter(
                        this,
                        progressList,
                        android.R.layout.simple_list_item_2,
                        arrayOf("courseName", "status"),
                        intArrayOf(android.R.id.text1, android.R.id.text2)
                    )

                    listViewProgress.adapter = adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch progress: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
