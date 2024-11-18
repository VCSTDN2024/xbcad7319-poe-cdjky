package com.example.workwise_prototype



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnGetStarted: Button
    private lateinit var tvAlreadyHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        btnGetStarted = findViewById(R.id.btnGetStarted)
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount)

        // Button click listener to navigate to login screen
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // TextView click listener to navigate to login screen
        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
