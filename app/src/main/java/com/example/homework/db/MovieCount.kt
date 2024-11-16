package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movieCount")
data class MovieCount(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "relatedMoviesCount")
    val relatedMoviesCount: Int,
    @ColumnInfo(name = "imagesCount")
    val imagesCount: Int,
    @ColumnInfo(name = "actorsCount")
    val actorsCount: Int,
    @ColumnInfo(name = "workersCount")
    val workersCount: Int
)
