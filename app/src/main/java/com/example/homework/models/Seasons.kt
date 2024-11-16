package com.example.homework.models

import com.google.gson.annotations.SerializedName

data class Seasons (
    @SerializedName("total") val total: Int,
    @SerializedName("items") val items: List<Items>
)