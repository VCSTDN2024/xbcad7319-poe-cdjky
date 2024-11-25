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
    private var currentAttendanceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize UI components
        tvAttendanceTitle = findViewById(R.id.tvAttendanceTitle)
        switchMarkAttendance = findViewById(R.id.switchMarkAttendance)
        btnClockOut = findViewById(R.id.btnClockOut)
        btnLeaveBalances = findViewById(R.id.btnLeaveBalances)
        btnLeaveRequestForm = findViewById(R.id.btnLeaveRequestForm)

        // Retrieve logged-in employee details
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        if (loggedInEmployeeId.isEmpty()) {
            redirectToLogin()
            return
        }

        fetchEmployeeDetails()
        setupAttendanceSwitch()
        setupClockOutButton()

        btnLeaveBalances.setOnClickListener {
            fetchLeaveBalance()
        }

        btnLeaveRequestForm.setOnClickListener {
            startActivity(Intent(this, LeaveRequestFormActivity::class.java))
        }
    }

    private fun redirectToLogin() {
        Toast.makeText(this, "Session expired. Please log in.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    fullName = document.getString("FullName") ?: "Employee"
                    tvAttendanceTitle.text = "Welcome, $fullName"
                } else {
                    Toast.makeText(this, "Employee details not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAttendanceSwitch() {
        switchMarkAttendance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                markAttendance()
            } else {
                Toast.makeText(this, "Attendance unmarked.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markAttendance() {
        val currentTimestamp = Timestamp.now()
        val attendanceData = hashMapOf(
            "employeeId" to loggedInEmployeeId,
            "FullName" to fullName,
            "date" to currentTimestamp,
            "status" to "Present"
        )

        db.collection("attendance")
            .add(attendanceData)
            .addOnSuccessListener { documentReference ->
                currentAttendanceId = documentReference.id
                btnClockOut.isEnabled = true
                Toast.makeText(this, "Attendance marked successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to mark attendance.", Toast.LENGTH_SHORT).show()
                switchMarkAttendance.isChecked = false
            }
    }

    private fun setupClockOutButton() {
        btnClockOut.setOnClickListener {
            if (currentAttendanceId.isNullOrEmpty()) {
                Toast.makeText(this, "You need to clock in first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clockOutTime = Timestamp.now()
            db.collection("attendance").document(currentAttendanceId!!)
                .update("clockOut", clockOutTime)
                .addOnSuccessListener {
                    btnClockOut.isEnabled = false
                    Toast.makeText(this, "Clock-out recorded successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error recording clock-out.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchLeaveBalance() {
        db.collection("actual_employees").document(loggedInEmployeeId)
            .get()
            .addOnSuccessListener { document ->
                val leaveBalance = document.getLong("leaveBalance") ?: 0
                Toast.makeText(this, "Leave balance: $leaveBalance days.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch leave balance.", Toast.LENGTH_SHORT).show()
            }
    }
}
