package com.example.workwise_prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(
    private val jobs: List<JobPosting>,
    private val onApplyClick: (JobPosting) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.job_title)
        val description: TextView = view.findViewById(R.id.job_description)
        val department: TextView = view.findViewById(R.id.job_department)
        val location: TextView = view.findViewById(R.id.job_location)
        val applyButton: Button = view.findViewById(R.id.apply_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        holder.title.text = job.title
        holder.description.text = job.description
        holder.department.text = job.department
        holder.location.text = job.location

        holder.applyButton.setOnClickListener {
            onApplyClick(job)
        }
    }

    override fun getItemCount(): Int = jobs.size
}
