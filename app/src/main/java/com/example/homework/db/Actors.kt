package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actors")
data class Actors(
    @PrimaryKey
    @ColumnInfo(name = "personId")
    val personId: Int,
   @ColumnInfo(name = "nameRu")
    val nameRu : String,
    @ColumnInfo(name = "nameEn")
    val nameEn : String,
    @ColumnInfo(name = "posterUrl")
    val posterUrl : String,
    @ColumnInfo(name = "profession")
    val profession : String,
    @ColumnInfo(name = "gender")
    val gender : String
)