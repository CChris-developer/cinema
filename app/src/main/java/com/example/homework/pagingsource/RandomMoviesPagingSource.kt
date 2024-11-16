package com.example.homework.pagingsource

import com.example.homework.repository.MovieListRepository

class RandomMoviesPagingSource(
    override var isFullList: Boolean,
    requestNumber: Int,
    private val service: MovieListRepository,
    override var movieList: List<Int>,
    override var name: String
) : BasePagingSource(
    {
        service.getRandomMovies(it, requestNumber)
    }
)