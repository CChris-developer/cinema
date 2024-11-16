package com.example.homework.models

import com.google.gson.annotations.SerializedName

class Movie (
    @SerializedName("kinopoiskId") val kinopoiskId: Int,
    @SerializedName("nameRu") val nameRu: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("nameOriginal") val nameOriginal: String,
    @SerializedName("year") val year: Int,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("posterUrlPreview") val posterUrlPreview: String,
    @SerializedName("countries") val countries: List<Country>,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("ratingKinopoisk") var ratingKinopoisk: Float,
    @SerializedName("filmLength") val filmLength: Int,
    @SerializedName("ratingAgeLimits") val ratingAgeLimits: String,
    @SerializedName("viewed") var viewed: Boolean,
    @SerializedName("serial") val serial: Boolean,
    @SerializedName("description") val description: String,
    @SerializedName("shortDescription") val shortDescription: String
)