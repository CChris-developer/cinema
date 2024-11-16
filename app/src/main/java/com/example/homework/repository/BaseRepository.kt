package com.example.homework.repository

import com.example.homework.api.retrofit
import com.example.homework.db.ActionsDao
import com.example.homework.db.MovieCount
import com.example.homework.db.Movies
import com.example.homework.models.ActorInfo
import com.example.homework.models.Movie
import java.lang.Exception

interface BaseRepository {
    var isException: Boolean

    suspend fun getMovieInfoFromNet(id: Int): Movie? {
        try {
            return retrofit.movieInfo(id)
        } catch (e: Exception) {
            isException = true
            return null
        }
    }

    fun getMovieInfoFromDb(id: Int, actionsDao: ActionsDao): Movies {
        return actionsDao.getMoviesInfoFromDb(id)
    }

    suspend fun getActors(id: Int): List<ActorInfo> {
        try {
            return retrofit.getActors(id)
        } catch (e: Exception) {
            isException = true
            return emptyList()
        }
    }

    suspend fun insertMovie(movie: Movies, actionsDao: ActionsDao) {
        actionsDao.insertMovie(
            Movies(
                id = movie.id,
                nameRu = movie.nameRu,
                nameEn = movie.nameEn,
                nameOriginal = movie.nameOriginal,
                rating = movie.rating,
                genre = movie.genre,
                country = movie.country,
                posterUrl = movie.posterUrl,
                posterUrlPreview = movie.posterUrlPreview,
                year = movie.year,
                filmLength = movie.filmLength,
                ratingAgeLimits = movie.ratingAgeLimits,
                serial = movie.serial,
                fullDescription = movie.fullDescription,
                shortDescription = movie.shortDescription,
                interested = movie.interested
            )
        )
    }

    fun checkMovie(actionsDao: ActionsDao): List<Movies> {
        return actionsDao.checkMovie()
    }

    fun getMovieFromMovieCountDb(id: Int, actionsDao: ActionsDao): MovieCount {
        return actionsDao.getMovieFromMovieCount(id)
    }

    suspend fun insertMovieCountToDb(
        id: Int,
        relatedMoviesCount: Int,
        imagesCount: Int,
        actorsCount: Int,
        workersCount: Int,
        actionsDao: ActionsDao
    ) {
        actionsDao.insertMovieCount(
            MovieCount(
                id = id,
                relatedMoviesCount = relatedMoviesCount,
                imagesCount = imagesCount,
                actorsCount = actorsCount,
                workersCount = workersCount
            )
        )
    }
}