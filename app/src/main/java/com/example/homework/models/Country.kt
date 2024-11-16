package com.example.homework.models

import com.google.gson.annotations.SerializedName

class Country (
    @SerializedName("id") val id: Int,
    @SerializedName("country") val country: String
)