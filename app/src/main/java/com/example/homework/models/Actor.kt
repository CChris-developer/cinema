package com.example.homework.models

import com.google.gson.annotations.SerializedName

data class Actor(
    @SerializedName("personId") val personId: Int,
    @SerializedName("nameRu") val nameRu: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("profession") val profession: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("films") val films: List<ActorMovies>
)
