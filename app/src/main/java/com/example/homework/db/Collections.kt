package com.example.homework.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class Collections(
    @PrimaryKey
    @ColumnInfo(name = "collection")
    val collection: String,
    @ColumnInfo(name = "checkboxId")
    val checkboxId : Int,
    @ColumnInfo(name = "textviewId")
    val textviewId : Int,
    @ColumnInfo(name = "profileLinearLayoutId")
    val profileLinearLayoutId : Int,
    @ColumnInfo(name = "profileImageButtonId")
    val profileImageButtonId : Int,
    @ColumnInfo(name = "profileTextView1Id")
    val profileTextView1Id : Int,
    @ColumnInfo(name = "profileTextView2Id")
    val profileTextView2Id : Int,
    @ColumnInfo(name = "profileCardId")
    val profileCardId : Int
)