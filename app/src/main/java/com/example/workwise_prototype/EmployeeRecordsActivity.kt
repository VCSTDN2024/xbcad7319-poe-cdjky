package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class EmployeeRecordsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var loggedInEmployeeId: String

    private lateinit var tvWelcomeEmployee: TextView
    private lateinit var etEmployeeName: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var spinnerDepartment: Spinner
    private lateinit var etEmployeePhone: EditText
    private lateinit var etEmployeeEmail: EditText
    private lateinit var btnSaveEmployee: Button
    private lateinit var lvEmployeeResults: ListView

    private val employeeList = mutableListOf<String>()
    private val employeeIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_records)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Retrieve logged-in actual_employee_id
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        loggedInEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI components
        tvWelcomeEmployee = findViewById(R.id.tvWelcomeEmployee)
        etEmployeeName = findViewById(R.id.etEmployeeName)
        spinnerRole = findViewById(R.id.spinnerRole)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        etEmployeePhone = findViewById(R.id.etEmployeePhone)
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail)
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee)
        lvEmployeeResults = findViewById(R.id.lvEmployeeResults)

        // Setup spinners
        setupRoleSpinner()
        setupDepartmentSpinner()

        // Fetch logged-in employee details
        fetchLoggedInEmployeeDetails()

        // Load list of employees
        loadEmployeeList()

        // Save updated employee details
        btnSaveEmployee.setOnClickListener {
            saveEmployeeDetails()
        }

        // Handle employee selection
        lvEmployeeResults.setOnItemClickListener { _, _, position, _ ->
            val selectedEmployeeId = employeeIds[position]
            val intent = Intent(this, EmployeeDetailsActivity::class.java)
            intent.putExtra("selected_employee_id", selectedEmployeeId)
            startActivity(intent)
        }
    }

    private fun setupRoleSpinner() {
        val roles = resources.getStringArray(R.array.role_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter
    }

    private fun setupDepartmentSpinner() {
        val departments = resources.getStringArray(R.array.department_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDepartment.adapter = adapter
    }

    private fun fetchLoggedInEmployeeDetails() {
        if (loggedInEmployeeId.isEmpty()) {
            Toast.makeText(this, "Error: Employee ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("actual_employees").document(loggedInEmployeeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvWelcomeEmployee.text = "Welcome, ${document.getString("FullName") ?: "Employee"}!"
                    etEmployeeName.setText(document.getString("FullName"))
                    etEmployeePhone.setText(document.getString("Phone"))
                    etEmployeeEmail.setText(document.getString("Email"))

                    val role = document.getString("Role") ?: "Junior"
                    spinnerRole.setSelection((spinnerRole.adapter as ArrayAdapter<String>).getPosition(role))

                    val department = document.getString("Department") ?: "IT"
                    spinnerDepartment.setSelection((spinnerDepartment.adapter as ArrayAdapter<String>).getPosition(department))
                } else {
                    Toast.makeText(this, "No details found for logged-in employee.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch logged-in employee details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadEmployeeList() {
        db.collection("actual_employees").get()
            .addOnSuccessListener { documents ->
                employeeList.clear()
                employeeIds.clear()
                for (document in documents) {
                    val fullName = document.getString("FullName") ?: "Unnamed Employee"
                    val employeeId = document.id
                    employeeList.add(fullName)
                    employeeIds.add(employeeId)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, employeeList)
                lvEmployeeResults.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load employee list.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEmployeeDetails() {
        if (loggedInEmployeeId.isEmpty()) {
            Toast.makeText(this, "Error: Employee ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedDetails = hashMapOf(
            "FullName" to etEmployeeName.text.toString().trim(),
            "Role" to spinnerRole.selectedItem.toString(),
            "Department" to spinnerDepartment.selectedItem.toString(),
            "Phone" to etEmployeePhone.text.toString().trim(),
            "Email" to etEmployeeEmail.text.toString().trim()
        )

        db.collection("actual_employees").document(loggedInEmployeeId)
            .set(updatedDetails, SetOptions.merge()) // Use merge to update fields without overwriting the document
            .addOnSuccessListener {
                Toast.makeText(this, "Employee details updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update employee details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
