package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "actions")
data class Actions(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "favourite")
    val favourite: Boolean,
    @ColumnInfo(name = "want_to_see")
    val wantToSee: Boolean,
    @ColumnInfo(name = "viewed")
    val viewed : Boolean,
    @ColumnInfo(name = "collection")
    val collection : String
)