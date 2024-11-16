package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actorInMovies")
data class ActorInMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "staffId")
    val staffId: Int,
    @ColumnInfo(name = "filmId")
    val filmId : Int,
    @ColumnInfo(name = "description")
    val description : String,
    @ColumnInfo(name = "professionKey")
    val professionKey : String,
    @ColumnInfo(name = "professionText")
    val professionText : String,
    @ColumnInfo(name = "rating")
    val rating : Float
)