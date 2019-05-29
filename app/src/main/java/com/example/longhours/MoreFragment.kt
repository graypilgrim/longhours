package com.example.longhours

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment.getExternalStorageDirectory
import android.support.v4.content.FileProvider
import android.widget.*


class MoreFragment : Fragment() {
    lateinit var spinner: Spinner
    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        val ma = activity as MainActivity
        val jobsList = mutableListOf<JobDetails>()
        val jobs = ma.dataBase?.jobDao()?.getAll()
        for (i in jobs!!) {
            val jd = JobDetails(i.id, i.name, i.rate, i.latitude, i.longitude)
            jobsList.add(jd)
        }

        val adapter = ArrayAdapter<JobDetails>(context!!, android.R.layout.simple_list_item_1 , jobsList)
        spinner = view.findViewById<Spinner>(R.id.jobs_spinner)
        spinner.adapter = adapter

        val generateBtn = view.findViewById<Button>(R.id.generate_raport_btn)
        generateBtn.setOnClickListener(generateReportListener)

        val sendViaEmailBtn = view.findViewById<Button>(R.id.send_via_email_btn)
        sendViaEmailBtn.setOnClickListener(sendViaEmailListener)

        mView = view
        return view
    }

    val generateReportListener = View.OnClickListener {
        createReport()

        val text = "Report created"
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }

    val sendViaEmailListener = View.OnClickListener {
        createReport()
        val baseDir = getExternalStorageDirectory()

        val fileNameField = mView.findViewById<EditText>(R.id.report_file_name_edit_text)
        val fileName = fileNameField.text.toString() + ".csv"
        val fileIn = File(baseDir, fileName)

        val sharedFileUri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".myfileprovider", fileIn)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, sharedFileUri)

        }
        startActivity(Intent.createChooser(intent, "Select e-mail client:"))
    }

    fun createReport() {
        val baseDir = getExternalStorageDirectory()

        val fileNameField = mView.findViewById<EditText>(R.id.report_file_name_edit_text)
        val fileName = fileNameField.text.toString() + ".csv"
        val fileIn = File(baseDir, fileName)
        Log.e("deadbeef", "filePath: $fileIn")

        val writer = CSVWriter(FileWriter(fileIn))
        val data = arrayOf("Date", "Started", "Finished", "Gain")
        writer.writeNext(data)

        val selectedJob = spinner.selectedItem as JobDetails
        val ma = activity as MainActivity

        val entries = ma.dataBase?.entryDao()?.getAll()
        for (en in entries!!) {
            if (en.jobId != selectedJob.id) {
                continue
            }

            val ed = EntryDetails(ma.dataBase!!, en.jobId, en.start, en.end)
            val date = SimpleDateFormat("YYYY-MM-dd").format(Date(en.start)).toString()
            val start = SimpleDateFormat("HH:mm").format(Date(en.start)).toString()
            val end = SimpleDateFormat("HH:mm").format(Date(en.end)).toString()
            val gain = ed.rate.toString()

            writer.writeNext(arrayOf(date, start, end, gain))
        }

        writer.close()
    }
}