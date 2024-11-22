package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Calendar

class AttendanceActivity : AppCompatActivity() {

    private lateinit var tvAttendanceTitle: TextView
    private lateinit var switchMarkAttendance: Switch
    private lateinit var btnClockOut: Button
    private lateinit var btnLeaveBalances: Button
    private lateinit var btnLeaveRequestForm: Button

    private val db = FirebaseFirestore.getInstance()
    private lateinit var loggedInEmployeeId: String
    private lateinit var fullName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize UI components
        tvAttendanceTitle = findViewById(R.id.tvAttendanceTitle)
        switchMarkAttendance = findViewById(R.id.switchMarkAttendance)
        btnClockOut = findViewById(R.id.btnClockOut)
        btnLeaveBalances = findViewById(R.id.btnLeaveBalances)
        btnLeaveRequestForm = findViewById(R.id.btnLeaveRequestForm)

        // Retrieve logged-in actual_employee_id
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Fetch FullName using actual_employee_id
        fetchEmployeeDetails()

        // Set up switch functionality for marking attendance
        setupAttendanceSwitch()

        // Set up Clock Out button functionality
        setupClockOutButton()

        // Set up Leave Balances button functionality
        btnLeaveBalances.setOnClickListener {
            fetchLeaveBalance() // New functionality to fetch leave balance
        }

        // Set up Leave Request Form button functionality
        btnLeaveRequestForm.setOnClickListener {
            startActivity(Intent(this, LeaveRequestFormActivity::class.java))
        }
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    fullName = document.getString("FullName") ?: "Employee"
                    checkAttendanceStatus()
                } else {
                    Toast.makeText(this, "Failed to fetch employee details.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchLeaveBalance() {
        if (loggedInEmployeeId.isEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch the leave balance from the actual_employee table
        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val leaveBalance = document.getLong("leaveBalance") ?: 0
                    Toast.makeText(this, "Your leave balance: $leaveBalance days", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No leave balance found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error fetching leave balances: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkAttendanceStatus() {
        // Check if attendance has already been marked for today
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        db.collection("attendance")
            .whereEqualTo("employeeId", loggedInEmployeeId)
            .whereGreaterThan("date", Timestamp(todayStart))
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    switchMarkAttendance.isChecked = false
                    switchMarkAttendance.isEnabled = true
                    btnClockOut.isEnabled = false
                } else {
                    switchMarkAttendance.isChecked = true
                    switchMarkAttendance.isEnabled = false
                    btnClockOut.isEnabled = true
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking attendance.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAttendanceSwitch() {
        switchMarkAttendance.setOnCheckedChangeListener { _, isChecked ->
            if (loggedInEmployeeId.isEmpty()) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                switchMarkAttendance.isChecked = false
                return@setOnCheckedChangeListener
            }

            val currentTimestamp = Timestamp.now() // Current timestamp

            if (isChecked) {
                val attendanceData = hashMapOf(
                    "employeeId" to loggedInEmployeeId,
                    "FullName" to fullName,
                    "date" to currentTimestamp,
                    "status" to "Present"
                )

                db.collection("attendance").document("$loggedInEmployeeId-${currentTimestamp.seconds}")
                    .set(attendanceData)
                    .addOnSuccessListener {
                        btnClockOut.isEnabled = true
                        switchMarkAttendance.isEnabled = false
                        Toast.makeText(this, "Attendance marked for today.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to mark attendance: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        switchMarkAttendance.isChecked = false
                    }
            }
        }
    }

    private fun setupClockOutButton() {
        btnClockOut.setOnClickListener {
            if (loggedInEmployeeId.isEmpty()) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clockOutTime = Timestamp.now()
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            db.collection("attendance")
                .whereEqualTo("employeeId", loggedInEmployeeId)
                .whereGreaterThan("date", Timestamp(todayStart))
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "No attendance record found to clock out.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    for (document in documents) {
                        db.collection("attendance").document(document.id)
                            .update("clock_out", clockOutTime)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Clock-out time recorded.", Toast.LENGTH_SHORT).show()
                                btnClockOut.isEnabled = false
                                switchMarkAttendance.isChecked = false
                                switchMarkAttendance.isEnabled = true
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
