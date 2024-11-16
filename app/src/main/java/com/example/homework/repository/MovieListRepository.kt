package com.example.homework.repository

import com.example.homework.api.Consts.countriesIdMap
import com.example.homework.api.Consts.countriesList
import com.example.homework.api.Consts.genresIdMap
import com.example.homework.api.Consts.genresList
import com.example.homework.api.Utils.randomMoviesRequest
import com.example.homework.api.retrofit
import com.example.homework.models.Movie
import com.example.homework.models.MovieList
import kotlinx.coroutines.delay
import java.lang.Exception
import javax.inject.Inject
import kotlin.random.Random

class MovieListRepository @Inject constructor() : BaseRepository {

    private var mapId = mutableMapOf<Int, Int>()
    var id = 0
    var totalCountMovies = mutableMapOf<String, Int>()
    override var isException = false

    suspend fun getPremieres(year: Int, month: String): MovieList? {
        try {
            return retrofit.premieres(year, month)
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getTvSeries(page: Int): List<Movie> {
        val result = retrofit.tvSeries(page)
        totalCountMovies["tvSeries"] = result.total
        return result.items
    }

    suspend fun getPopularMovies(page: Int): List<Movie> {
        val result = retrofit.popularMovies(page)
        totalCountMovies["popular"] = result.total
        return result.items
    }

    private suspend fun getId(): MutableMap<Int, Int> {
        var searchGenreId: Int
        var searchCountryId = 0
        val movieId = mutableMapOf<Int, Int>()
        var result = mutableListOf<Movie>()
        var i = 0
        var countryId = 0
        var genreId = 0
        try {
            while (movieId.size < 2) {
                while (result.isEmpty() || movieId.containsKey(searchCountryId) || (result.size == i)
                ) {
                    searchCountryId = Random.nextInt(0, countriesList.size)
                    countryId = countriesList[searchCountryId]
                    searchGenreId = Random.nextInt(0, genresList.size)
                    genreId = genresList[searchGenreId]
                    result = retrofit.randomMovies(countryId, genreId, 1).items
                    i = 0
                    result.onEach {
                        if (it.ratingKinopoisk == 0.0f)
                            i++
                    }
                }
                result = mutableListOf()
                movieId[countryId] = genreId
            }
        } catch (e: Exception) {
            movieId[0] = 0
            isException = true
        }
        return movieId
    }

    suspend fun getRandomMovieInfo() {
        mapId = getId()
        if (!mapId.containsKey(0)) {
            mapId.forEach { key, value ->
                randomMoviesRequest.add(
                    listOf(
                        key.toString(),
                        countriesIdMap[key],
                        value.toString(),
                        genresIdMap[value]
                    ) as List<String>
                )
            }
        }
    }

    suspend fun getRandomMovies(page: Int, requestNumber: Int): List<Movie> {
        while (randomMoviesRequest.isEmpty()) {
            delay(300)
            if (isException)
                break
        }
        if (randomMoviesRequest.isNotEmpty()) {
            val result = retrofit.randomMovies(
                randomMoviesRequest[requestNumber][0].toInt(),
                randomMoviesRequest[requestNumber][2].toInt(),
                page
            )
            totalCountMovies["podborka$requestNumber"] = result.total
           return result.items
        } else
            return emptyList()
    }

    suspend fun topList(page: Int): List<Movie> {
        val result = retrofit.topList(page)
        totalCountMovies["topList"] = result.total
        return result.items
    }

    suspend fun getRequiredMovies(
        country: Int, genre: Int, order: String, type: String, ratingFrom: Float, ratingTo: Float,
        yearFrom: Int, yearTo: Int, page: Int
    ): List<Movie> {
        return retrofit.getRequiredMovies(
            country,
            genre,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            page
        ).items
    }
}