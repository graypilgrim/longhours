package com.example.longhours

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "rate") val rate: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)

@Dao
interface JobDao {
    @Query("SELECT * FROM Job")
    fun getAll(): List<Job>

    @Query("SELECT * from Job WHERE id IN (:jobIds)")
    fun getJob(jobIds: IntArray): Job

    @Update
    fun updateAll(vararg jobs: Job)

    @Insert
    fun insertAll(vararg jobs: Job)

    @Delete
    fun delete(job: Job)
}

@Entity(
    foreignKeys = [ForeignKey(entity = Job::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("jobId"),
        onDelete = CASCADE)]
)
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name ="jobId") val jobId: Int,
    @ColumnInfo(name = "start") val start: Long,
    @ColumnInfo(name = "end") val end: Long
)

@Dao
interface EntryDao {
    @Query("SELECT * FROM Entry")
    fun getAll(): List<Entry>

    @Insert
    fun insertAll(vararg entries: Entry)

    @Delete
    fun delete(entry: Entry)
}

@Database(entities = arrayOf(Job::class, Entry::class), version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun entryDao(): EntryDao
}

