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
    private lateinit var btnCompensation: Button
    private lateinit var btnTaxDeductions: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payroll)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        tvPayrollUserName = findViewById(R.id.tvPayrollUserName)
        btnPayslips = findViewById(R.id.btnPayslips)
        btnBonuses = findViewById(R.id.btnBonuses)
        btnCompensation = findViewById(R.id.btnCompensation)
        btnTaxDeductions = findViewById(R.id.btnTaxDeductions)

        // Fetch and display user name
        fetchAndDisplayUserName()

        // Button click listeners
        btnPayslips.setOnClickListener {
            startActivity(Intent(this, PayslipsActivity::class.java))
        }

        btnBonuses.setOnClickListener {
            startActivity(Intent(this, BonusesActivity::class.java))
        }

        btnCompensation.setOnClickListener {
            startActivity(Intent(this, CompensationActivity::class.java))
        }

        btnTaxDeductions.setOnClickListener {
            startActivity(Intent(this, TaxDeductionsActivity::class.java))
        }

        // Handle Back Button
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

    }

    private fun fetchAndDisplayUserName() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: "Unknown User"
                        tvPayrollUserName.text = "Welcome, $userName"
                    } else {
                        Toast.makeText(this, "No user details found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvPayrollUserName.text = "Welcome, Guest"
        }
    }
}
