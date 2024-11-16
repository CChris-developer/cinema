package com.example.homework.models

import com.google.gson.annotations.SerializedName

class Genre (
    @SerializedName("id") val id: Int,
    @SerializedName("genre") val genre: String
)