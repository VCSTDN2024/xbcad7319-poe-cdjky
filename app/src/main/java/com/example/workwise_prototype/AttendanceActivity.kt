package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceActivity : AppCompatActivity() {

    private lateinit var tvAttendanceTitle: TextView
    private lateinit var switchMarkAttendance: Switch
    private lateinit var btnClockOut: Button
    private lateinit var btnLeaveBalances: Button
    private lateinit var btnLeaveRequestForm: Button

    private val db = FirebaseFirestore.getInstance()
    private lateinit var loggedInEmployeeId: String
    private lateinit var fullName: String
    private var hasClockedIn = false // Tracks if the employee has clocked in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize UI components
        tvAttendanceTitle = findViewById(R.id.tvAttendanceTitle)
        switchMarkAttendance = findViewById(R.id.switchMarkAttendance)
        btnClockOut = findViewById(R.id.btnClockOut)
        btnLeaveBalances = findViewById(R.id.btnLeaveBalances)
        btnLeaveRequestForm = findViewById(R.id.btnLeaveRequestForm)

        // Retrieve logged-in employee ID
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Fetch employee details
        fetchEmployeeDetails()

        // Set up attendance switch functionality
        setupAttendanceSwitch()

        // Set up clock-out button functionality
        setupClockOutButton()

        // Set up Leave Balances button
        btnLeaveBalances.setOnClickListener {
            fetchLeaveBalance()
        }

        // Set up Leave Request Form button
        btnLeaveRequestForm.setOnClickListener {
            startActivity(Intent(this, LeaveRequestFormActivity::class.java))
        }
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    fullName = document.getString("FullName") ?: "Employee"
                } else {
                    Toast.makeText(this, "Failed to fetch employee details.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchLeaveBalance() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                val leaveBalance = document.getLong("leaveBalance") ?: 0
                Toast.makeText(this, "Your leave balance: $leaveBalance days", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching leave balances.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAttendanceSwitch() {
        switchMarkAttendance.setOnCheckedChangeListener { _, isChecked ->
            val currentTimestamp = Timestamp.now()
            if (isChecked) {
                val attendanceData = hashMapOf(
                    "employeeId" to loggedInEmployeeId,
                    "FullName" to fullName,
                    "date" to currentTimestamp,
                    "status" to "Present"
                )

                db.collection("attendance")
                    .document("$loggedInEmployeeId-${currentTimestamp.seconds}")
                    .set(attendanceData)
                    .addOnSuccessListener {
                        hasClockedIn = true // Mark the employee as clocked in
                        btnClockOut.isEnabled = true // Enable the clock-out button
                        Toast.makeText(this, "Attendance marked successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        switchMarkAttendance.isChecked = false
                        Toast.makeText(this, "Failed to mark attendance.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupClockOutButton() {
        btnClockOut.setOnClickListener {
            if (!hasClockedIn) {
                Toast.makeText(this, "You need to clock in before clocking out.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clockOutTime = Timestamp.now()

            db.collection("attendance")
                .whereEqualTo("employeeId", loggedInEmployeeId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "You need to clock in before clocking out.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Update the first clock-in record for today
                    for (document in documents) {
                        db.collection("attendance").document(document.id)
                            .update("clockOut", clockOutTime)
                            .addOnSuccessListener {
                                hasClockedIn = false // Reset the clocked-in status
                                btnClockOut.isEnabled = false // Disable the clock-out button
                                Toast.makeText(this, "Clock-out time recorded successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to record clock-out: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to fetch attendance records: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
