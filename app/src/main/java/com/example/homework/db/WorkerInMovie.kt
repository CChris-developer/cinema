package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workerInMovies")
data class WorkerInMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "personId")
    val personId: Int,
    @ColumnInfo(name = "filmId")
    val filmId: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "rating")
    val rating: Float,
    @ColumnInfo(name = "professionText")
    val professionText: String,
    @ColumnInfo(name = "professionKey")
    val professionKey: String
)
