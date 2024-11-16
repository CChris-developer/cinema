package com.example.homework.models

import com.google.gson.annotations.SerializedName

data class ImageList(
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("items") val items: MutableList<ImageItems>
)
