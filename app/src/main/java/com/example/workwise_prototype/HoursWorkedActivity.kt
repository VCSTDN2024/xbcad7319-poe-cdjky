package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

class HoursWorkedActivity : AppCompatActivity() {

    private lateinit var etMonthlyHours: EditText
    private lateinit var etOvertimeHours: EditText
    private lateinit var btnSubmitHours: Button

    private val db = FirebaseFirestore.getInstance()
    private lateinit var actualEmployeeId: String
    private val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) // Format as yyyy-MM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours_worked)

        // Initialize UI elements
        etMonthlyHours = findViewById(R.id.etMonthlyHours)
        etOvertimeHours = findViewById(R.id.etOvertimeHours)
        btnSubmitHours = findViewById(R.id.btnSubmitHours)

        // Retrieve actual_employee_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Check for existing entry and setup UI accordingly
        checkExistingEntry()

        // Set up the submit button
        btnSubmitHours.setOnClickListener {
            submitHours()
        }
    }

    private fun checkExistingEntry() {
        db.collection("employee_salaries")
            .whereEqualTo("employee_id", actualEmployeeId)
            .whereEqualTo("month", currentMonth)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Hours already submitted for this month
                    disableInputFields()
                    Toast.makeText(
                        this,
                        "Hours for this month have already been submitted.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    enableInputFields()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check existing hours.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableInputFields() {
        etMonthlyHours.isEnabled = false
        etOvertimeHours.isEnabled = false
        btnSubmitHours.isEnabled = false
    }

    private fun enableInputFields() {
        etMonthlyHours.isEnabled = true
        etOvertimeHours.isEnabled = true
        btnSubmitHours.isEnabled = true
    }

    private fun submitHours() {
        val monthlyHours = etMonthlyHours.text.toString().trim()
        val overtimeHours = etOvertimeHours.text.toString().trim().ifEmpty { "0" }

        if (monthlyHours.isEmpty()) {
            Toast.makeText(this, "Please enter monthly hours.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "employee_id" to actualEmployeeId,
            "month" to currentMonth,
            "monthly_hours" to monthlyHours.toDouble(),
            "overtime_hours" to overtimeHours.toDouble(),
            "timestamp" to System.currentTimeMillis() // For tracking submission time
        )

        db.collection("employee_salaries").document("$actualEmployeeId-$currentMonth")
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Hours submitted successfully.", Toast.LENGTH_SHORT).show()
                disableInputFields() // Lock fields after submission
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit hours: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
