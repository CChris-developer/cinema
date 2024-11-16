package com.example.homework.models

import com.google.gson.annotations.SerializedName

data class ActorMovies(
    @SerializedName("filmId") val filmId: Int,
    @SerializedName("nameRu") val nameRu: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("description") val description: String,
    @SerializedName("professionKey") val professionKey: String,
    @SerializedName("professionText") val professionText: String
)