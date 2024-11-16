package com.example.homework.search

data class SearchSettings(
    var country: Int = 34,
    var genre: Int = 2,
    var order: String = "YEAR",
    var type: String = "ALL",
    var ratingFrom: Float = 1.0f,
    var ratingTo: Float = 10.0f,
    var yearFrom: Int = 1998,
    var yearTo: Int = 2009,
    var viewedState: Boolean = false
)