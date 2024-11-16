package com.example.homework.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Items (
    @SerializedName("number") val number: Int,
    @SerializedName("episodes") val episodes: List<Episodes>
): Parcelable