package com.example.workwise_prototype

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class JobsAndOnboardingActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var jobsRecyclerView: RecyclerView
    private val jobsList = mutableListOf<JobPosting>()
    private lateinit var actualEmployeeId: String
    private var employeeName: String = "Unknown"
    private var employeeEmail: String = "Unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs_and_onboarding)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView)

        // Retrieve logged-in employee ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmployeePrefs", MODE_PRIVATE)
        actualEmployeeId = sharedPreferences.getString("actual_employee_id", null) ?: ""

        if (actualEmployeeId.isEmpty()) {
            Toast.makeText(this, "Error: Employee ID is missing. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch employee details (FullName and Email)
        fetchEmployeeDetails()

        // Setup RecyclerView and fetch jobs
        setupRecyclerView()
        fetchJobsFromFirestore()
    }

    private fun setupRecyclerView() {
        jobsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = JobAdapter(jobsList) { job ->
            applyToJob(job)
        }
        jobsRecyclerView.adapter = adapter
    }

    private fun fetchJobsFromFirestore() {
        db.collection("job_postings")
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                jobsList.clear()
                for (document in snapshot.documents) {
                    val job = JobPosting(
                        id = document.id,
                        title = document.getString("title") ?: "No title",
                        description = document.getString("description") ?: "No description",
                        department = document.getString("department") ?: "No department",
                        requirements = document.getString("requirements") ?: "No requirements",
                        location = document.getString("location") ?: "No location"
                    )
                    jobsList.add(job)
                }
                jobsRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch jobs: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchEmployeeDetails() {
        db.collection("actual_employees").document(actualEmployeeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    employeeName = document.getString("FullName") ?: "Unknown"
                    employeeEmail = document.getString("Email") ?: "Unknown"
                } else {
                    Toast.makeText(this, "Failed to fetch employee details.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching employee details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applyToJob(job: JobPosting) {
        if (employeeName == "Unknown" || employeeEmail == "Unknown") {
            Toast.makeText(this, "Error: Employee details not loaded yet. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val applicationData = hashMapOf(
            "actual_employee_id" to actualEmployeeId,
            "FullName" to employeeName,
            "Email" to employeeEmail,
            "job_id" to job.id,
            "job_title" to job.title,
            "status" to "Applied",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("job_applicants")
            .add(applicationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully applied for ${job.title}.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to apply: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
