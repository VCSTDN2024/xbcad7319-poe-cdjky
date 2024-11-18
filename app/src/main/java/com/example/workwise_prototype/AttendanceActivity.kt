package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var switchMarkAttendance: Switch
    private lateinit var btnLeaveBalances: Button
    private lateinit var btnLeaveRequestForm: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize UI elements
        switchMarkAttendance = findViewById(R.id.switchMarkAttendance)
        btnLeaveBalances = findViewById(R.id.btnLeaveBalances)
        btnLeaveRequestForm = findViewById(R.id.btnLeaveRequestForm)

        // Set up switch functionality for marking attendance
        setupAttendanceSwitch()

        // Set up leave balances button
        btnLeaveBalances.setOnClickListener {
            fetchLeaveBalances()
        }

        // Set up leave request form button
        btnLeaveRequestForm.setOnClickListener {
            val intent = Intent(this, LeaveRequestFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAttendanceSwitch() {
        val userId = auth.currentUser?.uid ?: return

        switchMarkAttendance.setOnCheckedChangeListener { _, isChecked ->
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val attendanceData = hashMapOf(
                "date" to currentDate,
                "present" to isChecked
            )

            db.collection("attendance").document(userId)
                .collection("records").document(currentDate)
                .set(attendanceData)
                .addOnSuccessListener {
                    val status = if (isChecked) "Marked Present" else "Marked Absent"
                    Toast.makeText(this, "Attendance $status for $currentDate", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update attendance", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchLeaveBalances() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("leave_balances").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val leaveBalance = document.getString("balance") ?: "No data"
                    Toast.makeText(this, "Your leave balance: $leaveBalance", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No leave balance found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching leave balances", Toast.LENGTH_SHORT).show()
            }
    }
}
