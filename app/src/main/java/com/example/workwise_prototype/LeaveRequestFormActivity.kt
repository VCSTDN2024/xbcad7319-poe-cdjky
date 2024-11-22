package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LeaveRequestFormActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etReason: EditText
    private lateinit var btnSubmitLeaveRequest: Button

    private val db = FirebaseFirestore.getInstance()
    private lateinit var loggedInEmployeeId: String
    private lateinit var fullName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request_form)

        // Initialize UI components
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etReason = findViewById(R.id.etReason)
        btnSubmitLeaveRequest = findViewById(R.id.btnSubmitLeaveRequest)

        // Retrieve logged-in actual_employee_id
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Fetch FullName using actual_employee_id
        fetchEmployeeDetails()

        // Set up Submit button functionality
        btnSubmitLeaveRequest.setOnClickListener {
            submitLeaveRequest()
        }
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    fullName = document.getString("FullName") ?: "Employee"
                } else {
                    Toast.makeText(this, "Failed to fetch employee details.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun submitLeaveRequest() {
        val startDate = etStartDate.text.toString().trim()
        val endDate = etEndDate.text.toString().trim()
        val reason = etReason.text.toString().trim()

        if (startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val startTimestamp = convertDateToTimestamp(startDate)
            val endTimestamp = convertDateToTimestamp(endDate)

            val leaveRequestData = hashMapOf(
                "actual_employee_id" to loggedInEmployeeId,
                "FullName" to fullName,
                "dateRange" to mapOf(
                    "startDate" to startTimestamp,
                    "endDate" to endTimestamp
                ),
                "reason" to reason
            )

            db.collection("leave_requests").add(leaveRequestData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Leave request submitted successfully!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit leave request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid date format. Please use YYYY-MM-DD.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertDateToTimestamp(dateString: String): Timestamp {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
        return Timestamp(date)
    }

    private fun clearFields() {
        etStartDate.text.clear()
        etEndDate.text.clear()
        etReason.text.clear()
    }
}
