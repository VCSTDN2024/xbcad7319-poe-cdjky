package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PayrollActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvPayrollUserName: TextView
    private lateinit var btnPayslips: Button
    private lateinit var btnBonuses: Button
    private lateinit var btnHoursWorked: Button

    private var actualEmployeeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payroll)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Retrieve actual_employee_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null)

        // Initialize UI components
        tvPayrollUserName = findViewById(R.id.tvPayrollUserName)
        btnPayslips = findViewById(R.id.btnPayslips)
        btnBonuses = findViewById(R.id.btnBonuses)
        btnHoursWorked = findViewById(R.id.btnHoursWorked)

        // Fetch and display employee name
        fetchAndDisplayEmployeeName()

        // Set up button functionality
        btnPayslips.setOnClickListener {
            startActivity(Intent(this, PayslipsActivity::class.java))
        }

        btnBonuses.setOnClickListener {
            startActivity(Intent(this, BonusesActivity::class.java))
        }

        btnHoursWorked.setOnClickListener {
            startActivity(Intent(this, HoursWorkedActivity::class.java))
        }
    }

    private fun fetchAndDisplayEmployeeName() {
        if (actualEmployeeId == null) {
            tvPayrollUserName.text = "Welcome, Employee!"
            return
        }

        db.collection("actual_employees").document(actualEmployeeId!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fullName = document.getString("FullName") ?: "Employee"
                    tvPayrollUserName.text = "Welcome, $fullName!"
                } else {
                    tvPayrollUserName.text = "Welcome, Employee!"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch employee details: ${e.message}", Toast.LENGTH_SHORT).show()
                tvPayrollUserName.text = "Welcome, Employee!"
            }
    }
}
