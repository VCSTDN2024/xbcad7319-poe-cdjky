package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeRecordsActivity : AppCompatActivity() {

    private lateinit var etEmployeeName: EditText
    private lateinit var etEmployeePronouns: EditText
    private lateinit var etEmployeeRole: EditText
    private lateinit var etEmployeeReportingTo: EditText
    private lateinit var etEmployeePhone: EditText
    private lateinit var etEmployeeEmail: EditText
    private lateinit var btnSaveEmployee: Button
    private lateinit var etSearchEmployee: EditText
    private lateinit var lvEmployeeResults: ListView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_records)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        etEmployeeName = findViewById(R.id.etEmployeeName)
        etEmployeePronouns = findViewById(R.id.etEmployeePronouns)
        etEmployeeRole = findViewById(R.id.etEmployeeRole)
        etEmployeeReportingTo = findViewById(R.id.etEmployeeReportingTo)
        etEmployeePhone = findViewById(R.id.etEmployeePhone)
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail)
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee)
        etSearchEmployee = findViewById(R.id.etSearchEmployee)
        lvEmployeeResults = findViewById(R.id.lvEmployeeResults)

        // Fetch current user details if already saved
        val userId = auth.currentUser?.uid
        if (userId != null) {
            fetchEmployeeDetails(userId)
        }

        // Save Employee details
        btnSaveEmployee.setOnClickListener {
            saveEmployeeDetails(userId)
        }

        // Search employees
        etSearchEmployee.addTextChangedListener {
            searchEmployees(it.toString())
        }
    }

    private fun fetchEmployeeDetails(userId: String?) {
        if (userId == null) return

        db.collection("employees").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                etEmployeeName.setText(document.getString("name"))
                etEmployeePronouns.setText(document.getString("pronouns"))
                etEmployeeRole.setText(document.getString("job_role"))
                etEmployeeReportingTo.setText(document.getString("reporting_to"))
                etEmployeePhone.setText(document.getString("phone"))
                etEmployeeEmail.setText(document.getString("email"))
            }
        }
    }

    private fun saveEmployeeDetails(userId: String?) {
        if (userId == null) return

        val employeeData = hashMapOf(
            "name" to etEmployeeName.text.toString(),
            "pronouns" to etEmployeePronouns.text.toString(),
            "job_role" to etEmployeeRole.text.toString(),
            "reporting_to" to etEmployeeReportingTo.text.toString(),
            "phone" to etEmployeePhone.text.toString(),
            "email" to etEmployeeEmail.text.toString()
        )

        db.collection("employees").document(userId)
            .set(employeeData)
            .addOnSuccessListener {
                Toast.makeText(this, "Employee details saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchEmployees(query: String) {
        db.collection("employees")
            .get() // Fetch all employees
            .addOnSuccessListener { documents ->
                val employeeList = mutableListOf<Pair<String, String>>() // Pair of (employee name, employee ID)

                for (document in documents) {
                    val name = document.getString("name")
                    val id = document.id // Get employee ID
                    if (name != null && id != null) {
                        // Filter names that contain the query, ignoring case
                        if (name.contains(query, ignoreCase = true)) {
                            employeeList.add(Pair(name, id))
                        }
                    }
                }

                // Extract just the names for display
                val employeeNames = employeeList.map { it.first }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, employeeNames)
                lvEmployeeResults.adapter = adapter

                // Handle list item clicks
                lvEmployeeResults.setOnItemClickListener { _, _, position, _ ->
                    val selectedEmployee = employeeList[position]
                    val employeeId = selectedEmployee.second // Get the employee ID
                    // Start EmployeeDetailsActivity and pass the employee ID
                    val intent = Intent(this, EmployeeDetailsActivity::class.java)
                    intent.putExtra("employeeId", employeeId)
                    startActivity(intent)
                }
            }
    }
}
