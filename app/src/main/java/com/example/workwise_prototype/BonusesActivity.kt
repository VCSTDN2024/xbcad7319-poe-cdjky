package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BonusesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonuses)

        // Handle Back Button
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }
}
