package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PayslipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_slips)

        // Handle Back Button
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }
}
