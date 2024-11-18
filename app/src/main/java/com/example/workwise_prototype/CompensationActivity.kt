package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CompensationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tblCompensationData: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compensation)

        db = FirebaseFirestore.getInstance()
        tblCompensationData = findViewById(R.id.tblCompensationData)

        // Handle Back Button
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        // Fetch data and populate the table
        fetchAndDisplayCompensationData()
    }

    private fun fetchAndDisplayCompensationData() {
        db.collection("employees").get()
            .addOnSuccessListener { employeesSnapshot ->
                val employeeData = mutableMapOf<String, String>()

                // Collect employee data
                for (document in employeesSnapshot.documents) {
                    val employeeId = document.id
                    val employeeName = document.getString("name") ?: "Unknown"
                    employeeData[employeeId] = employeeName
                }

                db.collection("goals").get()
                    .addOnSuccessListener { goalsSnapshot ->
                        val compensationData = mutableListOf<Triple<String, Double, Double>>()

                        // Collect and calculate compensation data
                        for (document in goalsSnapshot.documents) {
                            val employeeId = document.getString("user_id") ?: continue
                            val hoursWorked = document.getDouble("hours") ?: 0.0
                            val compensation = hoursWorked * 300 // 300 per hour

                            if (employeeData.containsKey(employeeId)) {
                                val employeeName = employeeData[employeeId] ?: "Unknown"
                                compensationData.add(Triple(employeeName, hoursWorked, compensation))
                            }
                        }

                        // Populate the table
                        populateCompensationTable(compensationData)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to fetch goals data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch employees: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun populateCompensationTable(compensationData: List<Triple<String, Double, Double>>) {
        for (data in compensationData) {
            val (employeeName, hoursWorked, compensation) = data

            val row = TableRow(this)
            val nameTextView = TextView(this)
            val hoursTextView = TextView(this)
            val compensationTextView = TextView(this)

            nameTextView.text = employeeName
            nameTextView.setPadding(8, 8, 8, 8)

            hoursTextView.text = String.format("%.2f hours", hoursWorked)
            hoursTextView.setPadding(8, 8, 8, 8)

            compensationTextView.text = String.format("R%.2f", compensation)
            compensationTextView.setPadding(8, 8, 8, 8)

            row.addView(nameTextView)
            row.addView(hoursTextView)
            row.addView(compensationTextView)

            tblCompensationData.addView(row)
        }
    }
}
