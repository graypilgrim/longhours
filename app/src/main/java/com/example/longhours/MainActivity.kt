package com.example.longhours

import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectFragment(jobsFragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        dataBase = Room.databaseBuilder(this, AppDatabase::class.java, "longhours-db").allowMainThreadQueries().fallbackToDestructiveMigration().build()
        Intent(this, LocationWorker::class.java).also { intent ->
            startService(intent)
        }

    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.navigation_jobs -> selectFragment(jobsFragment)
            R.id.navigation_entries  -> selectFragment(entriesFragment)
            R.id.navigation_more -> selectFragment(moreFragment)
        }

        true
    }

    fun selectFragment(fr : Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fr).commit()
    }

    val jobsFragment = JobsFragment()
    val entriesFragment = EntriesFragment()
    val moreFragment = MoreFragment()
    var dataBase : AppDatabase? = null
}
