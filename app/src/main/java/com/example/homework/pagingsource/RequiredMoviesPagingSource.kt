package com.example.homework.pagingsource

import com.example.homework.repository.MovieListRepository

class RequiredMoviesPagingSource(
    override var isFullList: Boolean,
    private val service: MovieListRepository,
    override var movieList: List<Int>,
    override var name: String,
    country: Int,
    genre: Int,
    order: String,
    type: String,
    ratingFrom: Float,
    ratingTo: Float,
    yearFrom: Int,
    yearTo: Int
) : BasePagingSource(
    {
        service.getRequiredMovies( country,
            genre,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            it)
    }
)