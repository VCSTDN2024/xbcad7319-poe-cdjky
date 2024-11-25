package com.example.workwise_prototype

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PerformanceDataActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvEmployeeName: TextView
    private lateinit var btnSaveGoal: Button
    private lateinit var goalDescription: EditText
    private lateinit var goalStartDate: TextView
    private lateinit var goalEndDate: TextView
    private lateinit var goalHours: EditText
    private var startDate: Timestamp? = null
    private var endDate: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvEmployeeName = findViewById(R.id.employee_name)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        goalDescription = findViewById(R.id.goalDescription)
        goalStartDate = findViewById(R.id.goalStartDate)
        goalEndDate = findViewById(R.id.goalEndDate)
        goalHours = findViewById(R.id.goalHours)

        fetchAndDisplayUserDetails()

        goalStartDate.setOnClickListener { pickDate(true) }
        goalEndDate.setOnClickListener { pickDate(false) }
        btnSaveGoal.setOnClickListener { saveGoal() }
    }

    private fun pickDate(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val timestamp = Timestamp(selectedDate.time)

                if (isStartDate) {
                    startDate = timestamp
                    goalStartDate.text = "${selectedDay}-${selectedMonth + 1}-${selectedYear}"
                } else {
                    endDate = timestamp
                    goalEndDate.text = "${selectedDay}-${selectedMonth + 1}-${selectedYear}"
                }
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    private fun fetchAndDisplayUserDetails() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("actual_employees").document(it).get()
                .addOnSuccessListener { document ->
                    tvEmployeeName.text = document.getString("FullName") ?: "Unknown"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveGoal() {
        val description = goalDescription.text.toString().trim()
        val hoursText = goalHours.text.toString().trim()

        if (description.isEmpty() || hoursText.isEmpty() || startDate == null || endDate == null) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate!! > endDate!!) {
            Toast.makeText(this, "Start date must be before end date.", Toast.LENGTH_SHORT).show()
            return
        }

        val hours = hoursText.toDoubleOrNull()
        if (hours == null) {
            Toast.makeText(this, "Please enter valid hours.", Toast.LENGTH_SHORT).show()
            return
        }

        val goalData = hashMapOf(
            "user_id" to (auth.currentUser?.uid ?: ""),
            "description" to description,
            "start_date" to startDate,
            "end_date" to endDate,
            "hours" to hours,
            "status" to "In Progress"
        )

        db.collection("goals").add(goalData)
            .addOnSuccessListener {
                Toast.makeText(this, "Goal saved successfully.", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save goal.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        goalDescription.text.clear()
        goalStartDate.text = "Select Start Date"
        goalEndDate.text = "Select End Date"
        goalHours.text.clear()
        startDate = null
        endDate = null
    }
}
