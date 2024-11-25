package com.example.workwise_prototype

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeDetailsActivity : AppCompatActivity() {

    private lateinit var tvEmployeeName: TextView
    private lateinit var tvEmployeeRole: TextView
    private lateinit var tvEmployeeDepartment: TextView
    private lateinit var tvEmployeePhone: TextView
    private lateinit var tvEmployeeEmail: TextView
    private lateinit var lvEmployeeResults: ListView

    private val employeeList = mutableListOf<String>()
    private val employeeIds = mutableListOf<String>()

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        tvEmployeeName = findViewById(R.id.tvEmployeeName)
        tvEmployeeRole = findViewById(R.id.tvEmployeeRole)
        tvEmployeeDepartment = findViewById(R.id.tvEmployeeDepartment)
        tvEmployeePhone = findViewById(R.id.tvEmployeePhone)
        tvEmployeeEmail = findViewById(R.id.tvEmployeeEmail)
        lvEmployeeResults = findViewById(R.id.lvEmployeeResults)

        // Get selected employee ID from intent
        val selectedEmployeeId = intent.getStringExtra("selected_employee_id")
        if (selectedEmployeeId != null) {
            fetchEmployeeDetails(selectedEmployeeId)
        } else {
            Toast.makeText(this, "Error: No employee ID provided.", Toast.LENGTH_SHORT).show()
        }

        // Load employee list if needed
        loadEmployeeList()
    }

    private fun fetchEmployeeDetails(employeeId: String) {
        db.collection("actual_employees").document(employeeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvEmployeeName.text = "Name: ${document.getString("FullName") ?: "N/A"}"
                    tvEmployeeRole.text = "Role: ${document.getString("Role") ?: "N/A"}"
                    tvEmployeeDepartment.text = "Department: ${document.getString("Department") ?: "N/A"}"
                    tvEmployeePhone.text = "Phone: ${document.getString("Phone") ?: "N/A"}"
                    tvEmployeeEmail.text = "Email: ${document.getString("Email") ?: "N/A"}"
                } else {
                    Toast.makeText(this, "Employee details not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadEmployeeList() {
        db.collection("actual_employees")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to listen for updates.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                employeeList.clear()
                employeeIds.clear()

                for (document in snapshots!!) {
                    val fullName = document.getString("FullName") ?: "Unnamed Employee"
                    val employeeId = document.id

                    employeeList.add(fullName)
                    employeeIds.add(employeeId)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, employeeList)
                lvEmployeeResults.adapter = adapter
            }
    }
}
