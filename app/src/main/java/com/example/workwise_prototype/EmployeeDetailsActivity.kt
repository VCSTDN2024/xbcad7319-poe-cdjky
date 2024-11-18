package com.example.workwise_prototype

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvEmployeeName: TextView
    private lateinit var tvEmployeePronouns: TextView
    private lateinit var tvEmployeeRole: TextView
    private lateinit var tvEmployeeReportingTo: TextView
    private lateinit var tvEmployeePhone: TextView
    private lateinit var tvEmployeeEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set the title of the toolbar to "Back"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable the back button
        supportActionBar?.title = "Back"  // Set the title to "Back"

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        tvEmployeeName = findViewById(R.id.tvEmployeeName)
        tvEmployeePronouns = findViewById(R.id.tvEmployeePronouns)
        tvEmployeeRole = findViewById(R.id.tvEmployeeRole)
        tvEmployeeReportingTo = findViewById(R.id.tvEmployeeReportingTo)
        tvEmployeePhone = findViewById(R.id.tvEmployeePhone)
        tvEmployeeEmail = findViewById(R.id.tvEmployeeEmail)

        // Get the employee ID from the intent
        val employeeId = intent.getStringExtra("employeeId")

        if (employeeId != null) {
            fetchEmployeeDetails(employeeId)
        } else {
            Toast.makeText(this, "No employee ID found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchEmployeeDetails(employeeId: String) {
        db.collection("employees").document(employeeId).get().addOnSuccessListener { document ->
            if (document != null) {
                tvEmployeeName.text = document.getString("name") ?: "N/A"
                tvEmployeePronouns.text = document.getString("pronouns") ?: "N/A"
                tvEmployeeRole.text = document.getString("job_role") ?: "N/A"
                tvEmployeeReportingTo.text = document.getString("reporting_to") ?: "N/A"
                tvEmployeePhone.text = document.getString("phone") ?: "N/A"
                tvEmployeeEmail.text = document.getString("email") ?: "N/A"
            } else {
                Toast.makeText(this, "Employee details not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load employee details", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle back button in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Go back to the EmployeeRecordsActivity
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
