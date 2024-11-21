package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var tvAttendanceTitle: TextView
    private lateinit var switchMarkAttendance: Switch
    private lateinit var btnLeaveBalances: Button
    private lateinit var btnLeaveRequestForm: Button

    private val db = FirebaseFirestore.getInstance()
    private lateinit var loggedInEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize UI components
        tvAttendanceTitle = findViewById(R.id.tvAttendanceTitle)
        switchMarkAttendance = findViewById(R.id.switchMarkAttendance)
        btnLeaveBalances = findViewById(R.id.btnLeaveBalances)
        btnLeaveRequestForm = findViewById(R.id.btnLeaveRequestForm)

        // Retrieve logged-in actual_employee_id
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Set up switch functionality for marking attendance
        setupAttendanceSwitch()

        // Set up Leave Balances button functionality
        btnLeaveBalances.setOnClickListener {
            fetchLeaveBalances()
        }

        // Set up Leave Request Form button functionality
        btnLeaveRequestForm.setOnClickListener {
            startActivity(Intent(this, LeaveRequestFormActivity::class.java))
        }
    }

    private fun setupAttendanceSwitch() {
        switchMarkAttendance.setOnCheckedChangeListener { _, isChecked ->
            if (loggedInEmployeeId.isEmpty()) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                switchMarkAttendance.isChecked = false
                return@setOnCheckedChangeListener
            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (isChecked) {
                val attendanceData = hashMapOf(
                    "employeeId" to loggedInEmployeeId,
                    "date" to currentDate,
                    "status" to "Present"
                )

                db.collection("attendance").document("$loggedInEmployeeId-$currentDate")
                    .set(attendanceData)
                    .addOnSuccessListener {
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
            } else {
                db.collection("attendance").document("$loggedInEmployeeId-$currentDate")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Attendance removed for today.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to remove attendance: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        switchMarkAttendance.isChecked = true
                    }
            }
        }
    }

    private fun fetchLeaveBalances() {
        if (loggedInEmployeeId.isEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("leave_balances").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val leaveBalance = document.getString("balance") ?: "No data"
                    Toast.makeText(this, "Your leave balance: $leaveBalance", Toast.LENGTH_LONG).show()
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
}
