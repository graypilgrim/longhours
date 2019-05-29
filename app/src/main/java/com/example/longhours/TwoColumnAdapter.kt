package com.example.longhours

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class TwoColumnAdapter(val ctx: Context, val resource: Int, val items: List<EntryDetails>) : ArrayAdapter<EntryDetails>(ctx, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(resource, null)
        val item = getItem(position)

        val leftCol = view.findViewById<TextView>(R.id.left_col)
        leftCol.text = item.jobName

        val rightCol = view.findViewById<TextView>(R.id.right_col)
        rightCol.text = item.time

        return view
    }
}