package com.example.workwise_prototype

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
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

        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etReason = findViewById(R.id.etReason)
        btnSubmitLeaveRequest = findViewById(R.id.btnSubmitLeaveRequest)

        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        fetchEmployeeDetails()

        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        btnSubmitLeaveRequest.setOnClickListener {
            submitLeaveRequest()
        }
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                fullName = document.getString("FullName") ?: "Employee"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker(targetField: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                targetField.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun submitLeaveRequest() {
        val startDate = etStartDate.text.toString().trim()
        val endDate = etEndDate.text.toString().trim()
        val reason = etReason.text.toString().trim()

        if (startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val startTimestamp = convertDateToTimestamp(startDate)
            val endTimestamp = convertDateToTimestamp(endDate)

            val leaveRequestData = hashMapOf(
                "actual_employee_id" to loggedInEmployeeId,
                "FullName" to fullName,
                "startDate" to startTimestamp,
                "endDate" to endTimestamp,
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
        val parts = dateString.split("-").map { it.toInt() }
        val calendar = Calendar.getInstance()
        calendar.set(parts[0], parts[1] - 1, parts[2])
        return Timestamp(calendar.time)
    }

    private fun clearFields() {
        etStartDate.text.clear()
        etEndDate.text.clear()
        etReason.text.clear()
    }
}
