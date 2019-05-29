package com.example.longhours

import android.app.AlertDialog
import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class JobsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_jobs, container, false)
        jobsList.clear()
        createJobsList(view)

        val newJobBtn = view.findViewById<FloatingActionButton>(R.id.new_job_button)
        newJobBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.dialog_new_job, null)
            val jobNameField = dialogView.findViewById<EditText>(R.id.new_job_name)
            val addJobBtn = dialogView.findViewById<Button>(R.id.add_job_button)

            dialogBuilder.setView(dialogView)
            val newJobDialog = dialogBuilder.create()

            addJobBtn.setOnClickListener {
                val ma = activity as MainActivity
                if (!jobNameField.text.isEmpty()) {
                    ma.dataBase?.jobDao()?.insertAll(Job(0, jobNameField.text.toString(), 0, 0.0, 0.0))
                    updateJobsList(view)
                    newJobDialog.hide()

                } else {
                    Toast.makeText(ma, "Empty job name", Toast.LENGTH_SHORT).show()
                }
            }
            newJobDialog.show()
        }

        return view
    }

    fun createJobsList(view: View) {
        val ma = activity as MainActivity
        val jobs = ma.dataBase?.jobDao()?.getAll()
        for (i in jobs!!) {
            val jd = JobDetails(i.id, i.name, i.rate, i.latitude, i.longitude)
            jobsList.add(jd)
        }

        val adapter = ArrayAdapter<JobDetails>(context!!, R.layout.my_list_item, R.id.my_list_text_view, jobsList)
        val lv = view.findViewById<ListView>(R.id.jobs_list)
        lv.adapter = adapter


        lv.setOnItemClickListener { parent, view, position, id ->
            val editJobFragment = EditJobFragment()
            val it = lv.getItemAtPosition(position)
            editJobFragment.jobDetails = it as JobDetails

            ma.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, editJobFragment).commit()
        }

    }

    fun updateJobsList(view: View) {
        val ma = activity as MainActivity
        jobsList.clear()
        val jobs = ma.dataBase?.jobDao()?.getAll()
        for (i in jobs!!) {
            val jd = JobDetails(i.id, i.name, i.rate, i.latitude, i.longitude)
            jobsList.add(jd)
        }
    }

    var jobsList = mutableListOf<JobDetails>()
}