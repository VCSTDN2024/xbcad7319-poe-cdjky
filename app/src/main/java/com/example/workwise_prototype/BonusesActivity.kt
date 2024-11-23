package com.example.workwise_prototype

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BonusesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var bonusesTable: TableLayout
    private lateinit var etPassword: EditText
    private lateinit var btnUnlockBonuses: Button
    private lateinit var btnBack: Button

    private lateinit var actualEmployeeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonuses)

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve actual_employee_id from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        // Initialize UI elements
        bonusesTable = findViewById(R.id.bonusesTable)
        etPassword = findViewById(R.id.etPassword)
        btnUnlockBonuses = findViewById(R.id.btnUnlockBonuses)
        btnBack = findViewById(R.id.btnBack)

        // Handle Back Button
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Handle Unlock Bonuses Button
        btnUnlockBonuses.setOnClickListener {
            val password = etPassword.text.toString().trim()
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
            } else {
                verifyPasswordAndFetchBonuses(password)
            }
        }
    }

    private fun verifyPasswordAndFetchBonuses(password: String) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = user.email ?: ""
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Fetch bonuses after successful authentication
                fetchBonuses()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBonuses() {
        db.collection("employee_bonuses")
            .whereEqualTo("actual_employee_id", actualEmployeeId)
            .get()
            .addOnSuccessListener { documents ->
                bonusesTable.removeAllViews() // Clear the table before adding new data
                if (documents.isEmpty) {
                    Toast.makeText(this, "No bonuses found.", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val bonusAmount = document.getDouble("bonus_amount") ?: 0.0

                        // Add a new row to the table
                        addTableRow(bonusAmount)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch bonuses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTableRow(bonusAmount: Double) {
        val tableRow = TableRow(this)

        val bonusTextView = TextView(this).apply {
            text = "R%.2f".format(bonusAmount)
            setPadding(16, 16, 16, 16)
        }

        tableRow.addView(bonusTextView)
        bonusesTable.addView(tableRow)
    }
}
