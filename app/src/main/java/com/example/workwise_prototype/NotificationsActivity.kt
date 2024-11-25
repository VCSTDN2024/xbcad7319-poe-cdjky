package com.example.workwise_prototype

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        // Set up edge-to-edge insets for the layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display a "Feature Coming Soon" message
        val comingSoonMessage = findViewById<TextView>(R.id.tvComingSoonMessage)
        comingSoonMessage.text = "Notifications Feature Coming Soon!"

        // Optional: Display a Toast message when the activity is opened
        Toast.makeText(this, "Notifications feature is under development.", Toast.LENGTH_LONG).show()
    }
}
