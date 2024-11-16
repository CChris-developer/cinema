package com.example.homework.viewmodel

import android.text.TextUtils.split
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.homework.pagingsource.PopularMoviesPagingSource
import com.example.homework.pagingsource.RandomMoviesPagingSource
import com.example.homework.pagingsource.RequiredMoviesPagingSource
import com.example.homework.pagingsource.TopListPagingSource
import com.example.homework.pagingsource.TvSeriesPagingSource
import com.example.homework.api.Consts.ITEMS_PER_PAGE
import com.example.homework.api.Consts.genres
import com.example.homework.api.Utils.formatCountryName
import com.example.homework.api.Utils.getDateForPremieres
import com.example.homework.api.Utils.randomMoviesListHeader
import com.example.homework.api.Utils.randomMoviesRequest
import com.example.homework.models.Movie
import com.example.homework.repository.MovieListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieListRepository
) : ViewModel(), BaseViewModel {
 //   constructor() : this(MovieListRepository())

    private val _isException = MutableStateFlow<Boolean>(false)
    var isException = _isException.asStateFlow()
    var noRating = false
    var test = listOf<Movie>()
    private val date = SimpleDateFormat("dd/M/yyyy")
    private val currentDate = date.format(Date())
    var isFromAllActorsFragment = false
    val checkMoviesCount = repository.totalCountMovies
    private var pageSize = ITEMS_PER_PAGE
    private val _premieres = MutableStateFlow<List<Movie>>(emptyList())
    val premieres = _premieres.asStateFlow()

    private fun getPagingDataMovie(
        pagingSource: PagingSource<Int, Movie>,
        pageSize: Int
    ): Flow<PagingData<Movie>> {
        val pagingMovies: Flow<PagingData<Movie>> = Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = { pagingSource }
        ).flow.cachedIn(viewModelScope)
        return pagingMovies
    }

    fun getMovies(id: Int, isFullList: Boolean, movieList: List<Int>): Flow<PagingData<Movie>> {
        if (isFullList)
            pageSize = 21
        val movies: Flow<PagingData<Movie>>
        if (id == 2)
            movies = getPagingDataMovie(
                PopularMoviesPagingSource(isFullList, repository, movieList, ""),
                pageSize
            )
        else if (id == 3)
            movies = getPagingDataMovie(
                RandomMoviesPagingSource(
                    isFullList,
                    0,
                    repository,
                    movieList,
                    ""
                ), pageSize
            )
        else if (id == 4)
            movies = getPagingDataMovie(
                RandomMoviesPagingSource(
                    isFullList,
                    1,
                    repository,
                    movieList,
                    ""
                ), pageSize
            )
        else if (id == 5)
            movies = getPagingDataMovie(
                TopListPagingSource(isFullList, repository, movieList, ""),
                pageSize
            )
        else
            movies = getPagingDataMovie(
                TvSeriesPagingSource(isFullList, repository, movieList, ""),
                pageSize
            )
        return movies
    }

    fun getRequiredMovies(
        isFullList: Boolean,
        movieList: List<Int>,
        country: Int,
        genre: Int,
        order: String,
        type: String,
        ratingFrom: Float,
        ratingTo: Float,
        yearFrom: Int,
        yearTo: Int,
        name: String
    ): Flow<PagingData<Movie>> {
        return getPagingDataMovie(
            RequiredMoviesPagingSource(
                isFullList,
                repository,
                movieList,
                name,
                country,
                genre,
                order,
                type,
                ratingFrom,
                ratingTo,
                yearFrom,
                yearTo
            ),
            pageSize
        )
    }

    fun loadPremieres(movieList: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            val requestDate = split(getDateForPremieres(currentDate), "-")
            val response1 = repository.getPremieres(requestDate[0].toInt(), requestDate[1])
            if (response1 != null) {
                test = response1.items
                if (movieList.isNotEmpty()) {
                    test.forEach { movie ->
                        for (i in movieList) {
                            if (movie.kinopoiskId == i) {
                                movie.viewed = true
                                break
                            }
                        }
                    }
                }
                test.forEach { movie ->
                    val response2 = repository.getMovieInfoFromNet(movie.kinopoiskId)
                    if (response2 != null) {
                        val movieInfo = response2
                        if (movieInfo.ratingKinopoisk == null)
                            movie.ratingKinopoisk = 0.0f
                        else
                            movie.ratingKinopoisk = movieInfo.ratingKinopoisk
                    } else {
                        noRating = true
                    }
                }
                _premieres.value = test
            } else
                _isException.value = true
        }
    }

    fun getRandomMovieInfo() {
        randomMoviesListHeader = mutableListOf()
        randomMoviesRequest = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getRandomMovieInfo()
             if (randomMoviesRequest.isNotEmpty()) {
                 for (i in 0..<randomMoviesRequest.size) {
                    val genre = genres[randomMoviesRequest[i][3]]
                    val country = formatCountryName(randomMoviesRequest[i][1])
                    randomMoviesListHeader.add("$genre $country")
                 }
            }
        }
    }

    fun getNumberOfAllRandomMovies(nameCollection: String): Int {
        val key = randomMoviesListHeader.indexOf(nameCollection)
        return key
    }

    fun removeNullRatings(
        pagingData: PagingData<Movie>
    ): PagingData<Movie> {
        return pagingData.filter { it.ratingKinopoisk != 0.0f }
    }
}