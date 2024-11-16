package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serials")
data class Serials(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "movieId")
    val movieId: Int,
    @ColumnInfo(name = "seasonNumber")
    val seasonNumber: Int,
    @ColumnInfo(name = "episodeNumber")
    val episodeNumber: Int,
    @ColumnInfo(name = "nameRu")
    val nameRu: String,
    @ColumnInfo(name = "nameEn")
    val nameEn: String,
    @ColumnInfo(name = "synopsis")
    val synopsis: String,
    @ColumnInfo(name = "releaseDate")
    val releaseDate: String
)