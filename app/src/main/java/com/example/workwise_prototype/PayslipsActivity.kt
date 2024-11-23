package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PayslipsActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var btnUnlockPayslips: Button
    private lateinit var tvPayslipDetails: TextView
    private lateinit var btnBack: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var actualEmployeeId: String
    private val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) // Format as yyyy-MM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_slips)

        // Initialize UI elements
        etPassword = findViewById(R.id.etPassword)
        btnUnlockPayslips = findViewById(R.id.btnUnlockPayslips)
        tvPayslipDetails = findViewById(R.id.tvPayslipDetails)
        btnBack = findViewById(R.id.btnBack)

        // Retrieve actual_employee_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Set up unlock button
        btnUnlockPayslips.setOnClickListener {
            verifyPasswordAndFetchPayslip()
        }

        // Set up the back button
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun verifyPasswordAndFetchPayslip() {
        val enteredPassword = etPassword.text.toString().trim()
        if (enteredPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
            return
        }

        // Authenticate the user using FirebaseAuth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email ?: ""
            auth.signInWithEmailAndPassword(email, enteredPassword)
                .addOnSuccessListener {
                    // Fetch payslip data
                    fetchPayslipData()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Invalid password.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPayslipData() {
        db.collection("employee_payslips")
            .whereEqualTo("actual_employee_id", actualEmployeeId)
            .whereEqualTo("month", currentMonth)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payslip = documents.documents[0]
                    val monthlyHours = payslip.getDouble("monthly_hours") ?: 0.0
                    val overtimeHours = payslip.getDouble("overtime_hours") ?: 0.0
                    val totalPay = payslip.getDouble("total_pay") ?: 0.0
                    val paidAt = payslip.getTimestamp("paid_at")?.toDate()?.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it)
                    } ?: "N/A"

                    // Display payslip details
                    val details = """
                        Month: $currentMonth
                        Monthly Hours: $monthlyHours
                        Overtime Hours: $overtimeHours
                        Total Pay: R$totalPay
                        Paid At: $paidAt
                    """.trimIndent()
                    tvPayslipDetails.text = details
                } else {
                    tvPayslipDetails.text = "No payslip found for this month."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch payslip data.", Toast.LENGTH_SHORT).show()
            }
    }
}
