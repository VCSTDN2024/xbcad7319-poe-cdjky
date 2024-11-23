package com.example.workwise_prototype

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PerformanceActivity : AppCompatActivity() {

    // Firebase and Firestore instances
    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var tvEmployeeName: TextView
    private lateinit var tvJobRole: TextView
    private lateinit var btnSaveGoal: Button
    private lateinit var btnBack: Button
    private lateinit var goalDescription: EditText
    private lateinit var goalStartDate: TextView
    private lateinit var goalEndDate: TextView
    private lateinit var goalHours: EditText

    private var actualEmployeeId: String? = null // Store actual_employee_id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // UI Components
        tvEmployeeName = findViewById(R.id.employee_name)
        tvJobRole = findViewById(R.id.job_role)
        btnSaveGoal = findViewById(R.id.btnSaveGoal)
        btnBack = findViewById(R.id.btnBack)
        goalDescription = findViewById(R.id.goalDescription)
        goalStartDate = findViewById(R.id.goalStartDate)
        goalEndDate = findViewById(R.id.goalEndDate)
        goalHours = findViewById(R.id.goalHours)

        // Retrieve actual_employee_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", Context.MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null)

        // Check if actual_employee_id is available
        if (actualEmployeeId.isNullOrEmpty()) {
            Toast.makeText(this, "Employee ID not found. Contact admin.", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch and display user details from Firestore
        fetchAndDisplayUserDetails()

        // Set up DatePickerDialog for date selection
        goalStartDate.setOnClickListener { pickDate(goalStartDate) }
        goalEndDate.setOnClickListener { pickDate(goalEndDate) }

        // Button click listeners
        btnSaveGoal.setOnClickListener { saveGoal() }
        btnBack.setOnClickListener { finish() }
    }

    // Function to fetch and display user details
    private fun fetchAndDisplayUserDetails() {
        actualEmployeeId?.let {
            db.collection("actual_employees").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        tvEmployeeName.text = document.getString("FullName") ?: "Unknown"
                        tvJobRole.text = document.getString("Role") ?: "N/A"
                    } else {
                        Toast.makeText(this, "Employee details not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to show DatePickerDialog
    private fun pickDate(targetView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                targetView.text = selectedDate
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    // Function to save a goal to Firestore
    private fun saveGoal() {
        val description = goalDescription.text.toString().trim()
        val startDate = goalStartDate.text.toString().trim()
        val endDate = goalEndDate.text.toString().trim()
        val hoursText = goalHours.text.toString().trim()

        // Validation: Ensure all fields are filled
        if (description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || hoursText.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (actualEmployeeId.isNullOrEmpty()) {
            Toast.makeText(this, "Employee ID not found. Cannot save goal.", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert dates to Timestamps and validate hours
        val startTimestamp = convertToTimestamp(startDate)
        val endTimestamp = convertToTimestamp(endDate)
        val hours = hoursText.toDoubleOrNull()

        if (startTimestamp == null || endTimestamp == null || hours == null || startTimestamp > endTimestamp) {
            Toast.makeText(this, "Invalid input. Check dates and hours.", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare goal data
        val goalData = hashMapOf(
            "actual_employee_id" to actualEmployeeId, // Use actual_employee_id instead of user_id
            "description" to description,
            "start_date" to startTimestamp,
            "end_date" to endTimestamp,
            "hours" to hours,
            "status" to "In Progress"
        )

        // Save goal to Firestore
        db.collection("goals").add(goalData)
            .addOnSuccessListener {
                Toast.makeText(this, "Goal saved successfully.", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to save goal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to convert date string to Timestamp
    private fun convertToTimestamp(dateString: String): Timestamp? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            null
        }
    }

    // Function to clear input fields after saving a goal
    private fun clearInputFields() {
        goalDescription.text.clear()
        goalStartDate.text = "Select Start Date"
        goalEndDate.text = "Select End Date"
        goalHours.text.clear()
    }
}
