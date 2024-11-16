package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class Movies(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id : Int,
    @ColumnInfo(name = "nameRu")
    val nameRu : String,
    @ColumnInfo(name = "nameEn")
    val nameEn : String,
    @ColumnInfo(name = "nameOriginal")
    val nameOriginal : String,
    @ColumnInfo(name = "rating")
    val rating : String,
    @ColumnInfo(name = "genre")
    val genre : String,
    @ColumnInfo(name = "country")
    val country : String,
    @ColumnInfo(name = "posterUrl")
    val posterUrl : String,
    @ColumnInfo(name = "posterUrlPreview")
    val posterUrlPreview: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "filmLength")
    val filmLength: Int,
    @ColumnInfo(name = "ratingAgeLimits")
    val ratingAgeLimits: String,
    @ColumnInfo(name = "serial")
    val serial: Boolean,
    @ColumnInfo(name = "fullDescription")
    val fullDescription: String,
    @ColumnInfo(name = "shortDescription")
    val shortDescription: String,
    @ColumnInfo(name = "interested")
    val interested: Boolean
)