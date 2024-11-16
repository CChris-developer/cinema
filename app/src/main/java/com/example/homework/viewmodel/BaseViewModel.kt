package com.example.homework.viewmodel

import com.example.homework.repository.BaseRepository
import com.example.homework.db.ActionsDao
import com.example.homework.db.Movies
import com.example.homework.models.Movie

interface BaseViewModel {

    suspend fun insertMovieToDb(
        movie: Movie,
        interested: Boolean,
        repository: BaseRepository,
        actionsDao: ActionsDao
    ): Movie {
        val movieForLoad: Movie
        val nameOriginal = movie.nameOriginal ?: ""
        val posterUrl = movie.posterUrl ?: ""
        val posterUrlPreview = movie.posterUrlPreview ?: ""
        val ratingAgeLimits = movie.ratingAgeLimits ?: ""
        val description = movie.description ?: ""
        val shortDescription = movie.shortDescription ?: ""
        val nameRu = movie.nameRu ?: ""
        val nameEn = movie.nameEn ?: ""
        var genre = ""
        movie.genres.forEach {
            genre = genre + it.genre + "&" + it.id + "#"
        }
        var country = ""
        movie.countries.forEach {
            country = country + it.country + "&" + it.id + "#"
        }
        repository.insertMovie(
            Movies(
                movie.kinopoiskId,
                nameRu,
                nameEn,
                nameOriginal,
                movie.ratingKinopoisk.toString(),
                genre,
                country,
                posterUrl,
                posterUrlPreview,
                movie.year,
                movie.filmLength,
                ratingAgeLimits,
                movie.serial,
                description,
                shortDescription,
                interested
            ), actionsDao
        )
        movieForLoad = Movie(
            movie.kinopoiskId,
            nameRu,
            nameEn,
            nameOriginal,
            movie.year,
            posterUrl,
            posterUrlPreview,
            movie.countries,
            movie.genres,
            movie.ratingKinopoisk,
            movie.filmLength,
            ratingAgeLimits,
            false,
            movie.serial,
            description,
            shortDescription
        )
        return movieForLoad
    }
}