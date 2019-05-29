package com.example.longhours

import java.text.SimpleDateFormat
import java.util.*

class EntryDetails {

    constructor(db: AppDatabase, jobId: Int, start: Long, end: Long) {
        val arr = IntArray(1)
        arr[0] = jobId
        val job = db.jobDao().getJob(arr)
        jobName = job.name

        val interval = end - start
//        val mins = (interval.toDouble() / (1000 * 60 * 60)) % 60
//        val hours = interval.toDouble() / (1000 * 60 * 60)

        val decTime = interval.toDouble() / (1000 * 60 * 60)
        rate = decTime * job.rate
        time = String.format("%.2f", decTime) + "h"
    }

    var jobName = ""
    var time = ""
    var rate = 0.0
}