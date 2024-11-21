package com.example.workwise_prototype

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etFullName: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var spinnerDepartment: Spinner
    private lateinit var etContactNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        etFullName = findViewById(R.id.etFullName)
        spinnerRole = findViewById(R.id.spinnerRole)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        etContactNumber = findViewById(R.id.etContactNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val role = spinnerRole.selectedItem.toString()
            val department = spinnerDepartment.selectedItem.toString()
            val contactNumber = etContactNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (fullName.isNotEmpty() && contactNumber.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
            ) {
                registerUser(fullName, role, department, contactNumber, email, password)
            } else {
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(
        fullName: String,
        role: String,
        department: String,
        contactNumber: String,
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = UUID.randomUUID().toString() // Generate unique actual_employee_id
                    val employee = hashMapOf(
                        "actual_employee_id" to userId,
                        "FullName" to fullName,
                        "Role" to role,
                        "Department" to department,
                        "Phone" to contactNumber,
                        "Email" to email
                    )

                    db.collection("actual_employees").document(userId).set(employee)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
