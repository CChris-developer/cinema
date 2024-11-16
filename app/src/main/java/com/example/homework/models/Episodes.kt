package com.example.homework.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episodes (
    @SerializedName("seasonNumber") val seasonNumber: Int,
    @SerializedName("episodeNumber") val episodeNumber: Int,
    @SerializedName("nameRu") val nameRu: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("releaseDate") val releaseDate: String
): Parcelable