package com.example.homework.models

import com.google.gson.annotations.SerializedName

data class ImageItems(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("previewUrl") val previewUrl: String
)
