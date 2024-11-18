package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workwise_prototype.R
import com.google.firebase.firestore.FirebaseFirestore

class LeaveRequestFormActivity : AppCompatActivity() {

    private lateinit var etEmployeeName: EditText
    private lateinit var etDateRange: EditText
    private lateinit var etReason: EditText
    private lateinit var btnSubmit: Button

    private val db = FirebaseFirestore.getInstance() // Initialize Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request_form)

        // Initialize UI elements
        etEmployeeName = findViewById(R.id.etEmployeeName)
        etDateRange = findViewById(R.id.etDateRange)
        etReason = findViewById(R.id.etReason)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Handle Submit Button Click
        btnSubmit.setOnClickListener {
            submitLeaveRequest()
        }
    }

    private fun submitLeaveRequest() {
        // Get input values
        val employeeName = etEmployeeName.text.toString().trim()
        val dateRange = etDateRange.text.toString().trim()
        val reason = etReason.text.toString().trim()

        // Validate input
        if (employeeName.isEmpty() || dateRange.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create leave request object
        val leaveRequest = hashMapOf(
            "employeeName" to employeeName,
            "dateRange" to dateRange,
            "reason" to reason
        )

        // Save to Firestore
        db.collection("leave_requests").add(leaveRequest)
            .addOnSuccessListener {
                Toast.makeText(this, "Leave request submitted successfully", Toast.LENGTH_LONG).show()
                finish() // Close the activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting leave request: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}