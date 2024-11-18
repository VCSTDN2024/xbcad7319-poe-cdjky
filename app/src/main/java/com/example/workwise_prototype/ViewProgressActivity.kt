package com.example.workwise_prototype

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewProgressActivity : AppCompatActivity() {

    private lateinit var progressListView: ListView
    private lateinit var btnBack: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_progress)

        progressListView = findViewById(R.id.progressListView)
        btnBack = findViewById(R.id.btnBack)

        fetchProgress()

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchProgress() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("enrollments").whereEqualTo("userId", userId).get()
                .addOnSuccessListener { result ->
                    val progressList = mutableListOf<String>()
                    for (document in result) {
                        val courseName = document.getString("courseName")
                        progressList.add("Progress in $courseName: 75%")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, progressList)
                    progressListView.adapter = adapter
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch progress.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
