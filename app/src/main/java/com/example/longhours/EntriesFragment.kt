package com.example.longhours

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView


class EntriesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_entries, container, false)

        entriesList.clear()
        val ma = activity as MainActivity
        val entries = ma.dataBase?.entryDao()?.getAll()
        for (i in entries!!) {
            val details = EntryDetails(ma.dataBase!!, i.jobId, i.start, i.end)
            entriesList.add(details)
        }

        val adapter = TwoColumnAdapter(context!!, R.layout.two_column_list, entriesList)
        val lv = view.findViewById<ListView>(R.id.entries_list)
        lv.adapter = adapter

        return view
    }

    var entriesList = mutableListOf<EntryDetails>()

}