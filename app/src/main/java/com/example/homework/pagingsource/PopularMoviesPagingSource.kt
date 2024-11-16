package com.example.homework.pagingsource

import com.example.homework.repository.MovieListRepository

class PopularMoviesPagingSource(
    override var isFullList: Boolean,
    private val service: MovieListRepository,
    override var movieList: List<Int>,
    override var name: String
) : BasePagingSource(
    {
        service.getPopularMovies(it)
    }
)