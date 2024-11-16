package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movieWorkers")
data class MovieWorkers(
    @PrimaryKey
    @ColumnInfo(name = "personId")
    val personId: Int,
    @ColumnInfo(name = "nameRu")
    val nameRu : String,
    @ColumnInfo(name = "posterUrl")
    val posterUrl : String
)