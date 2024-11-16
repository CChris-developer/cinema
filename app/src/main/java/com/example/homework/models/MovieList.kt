package com.example.homework.models

import com.google.gson.annotations.SerializedName

class MovieList (
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("items") val items: MutableList<Movie>
)