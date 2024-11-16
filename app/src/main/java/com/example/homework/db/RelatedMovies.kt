package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relatedMovies")
data class RelatedMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "movieId")
    val movieId: Int,
    @ColumnInfo(name = "relatedMovieId")
    val relatedMovieId: Int
)