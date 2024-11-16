package com.example.homework.models

import com.google.gson.annotations.SerializedName

class SimilarMovieList (
    @SerializedName("total") val total: Int,
    @SerializedName("items") val items: List<SimilarMovie>
)