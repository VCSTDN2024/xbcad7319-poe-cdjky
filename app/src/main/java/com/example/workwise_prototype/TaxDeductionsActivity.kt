package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity

class TaxDeductionsActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var taxDeductionsTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tax_deductions)

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack)
        taxDeductionsTable = findViewById(R.id.taxDeductionsTable)

        // Back Button functionality
        btnBack.setOnClickListener {
            finish() // Ends the current activity and goes back to the previous one
        }

        // (Optional) Placeholder for dynamically managing the table if needed in the future
        populateTaxDeductions()
    }

    private fun populateTaxDeductions() {

    }
}
