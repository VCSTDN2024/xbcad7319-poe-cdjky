package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PerformanceDataActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnSubmit: Button
    private lateinit var employeeName: EditText
    private lateinit var month: EditText
    private lateinit var year: EditText
    private lateinit var performanceReview: EditText

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_data)

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        btnSubmit = findViewById(R.id.btnSubmit)
        employeeName = findViewById(R.id.employeeName)
        month = findViewById(R.id.month)
        year = findViewById(R.id.year)
        performanceReview = findViewById(R.id.performanceReview)

        // Back button functionality
        btnBack.setOnClickListener {
            finish() // Navigate back to the previous screen
        }

        // Submit button functionality
        btnSubmit.setOnClickListener {
            val name = employeeName.text.toString()
            val monthText = month.text.toString()
            val yearText = year.text.toString()
            val review = performanceReview.text.toString()

            if (name.isNotEmpty() && monthText.isNotEmpty() && yearText.isNotEmpty() && review.isNotEmpty()) {
                savePerformanceData(name, monthText, yearText, review)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePerformanceData(name: String, month: String, year: String, review: String) {
        val performanceData = hashMapOf(
            "employeeName" to name,
            "month" to month,
            "year" to year,
            "review" to review
        )

        firestore.collection("performance_data").add(performanceData)
            .addOnSuccessListener {
                Toast.makeText(this, "Performance data saved successfully!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        employeeName.text.clear()
        month.text.clear()
        year.text.clear()
        performanceReview.text.clear()
    }
}
