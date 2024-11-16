package com.example.homework.pagingsource

import com.example.homework.repository.MovieListRepository

class TvSeriesPagingSource(
    override var isFullList: Boolean,
    private val service: MovieListRepository,
    override var movieList: List<Int>,
    override var name: String
) : BasePagingSource(
    {
        service.getTvSeries(it)
    }
)