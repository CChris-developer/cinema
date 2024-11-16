package com.example.homework.repository

import com.example.homework.api.retrofit
import com.example.homework.db.ActionsDao
import com.example.homework.db.ActorInMovies
import com.example.homework.db.Actors
import com.example.homework.models.Actor
import com.example.homework.models.ActorInfo
import java.lang.Exception
import javax.inject.Inject

class ActorRepository @Inject constructor (val actionsDao: ActionsDao) : BaseRepository {

    override var isException = false

    suspend fun getActorInfo(id: Int): Actor? {
        try {
            return retrofit.getActorInfo(id)
        } catch (e: Exception) {
            isException = true
            return null
        }
    }

    fun getActorInfoFromDb(id: Int): Actors {
        return actionsDao.getActorInfo(id)
    }

    suspend fun insertActorToDB(actor: Actors) {
        actionsDao.insertActor(
            Actors(
                personId = actor.personId,
                gender = actor.gender,
                profession = actor.profession,
                nameRu = actor.nameRu,
                nameEn = actor.nameEn,
                posterUrl = actor.posterUrl
            )
        )
    }

    suspend fun insertActorInMovies(actorInMovies: ActorInMovies) {
        actionsDao.insertActorMovies(
            ActorInMovies(
                id = 0,
                staffId = actorInMovies.staffId,
                filmId = actorInMovies.filmId,
                description = actorInMovies.description,
                professionKey = actorInMovies.professionKey,
                professionText = actorInMovies.professionText,
                rating = actorInMovies.rating
            )
        )
    }

    fun getActorMovies(personId: Int): List<ActorInMovies> {
        return actionsDao.getActorMovies(personId)
    }

    fun getAllActorsOfSomeMovie(filmId: Int): List<Int> {
        return actionsDao.getAllActorsOfSomeMovie(filmId)
    }

    fun getMoviesNumber(personId: Int): List<Int> {
        return actionsDao.getMoviesNumber(personId)
    }

    fun getAllMoviesOfActor(personId: Int): List<ActorInMovies> {
        return actionsDao.getAllMoviesOfActor(personId)
    }

    fun getMovieActorsFromDb(filmId: Int): List<ActorInfo> {
        return actionsDao.getMovieActorsFromDb(filmId)
    }

    fun getActorFromDb(personId: Int): Actors {
        return actionsDao.getActorFromDb(personId)
    }

    suspend fun updateActorsCount(id: Int, actorsCount: Int) {
        actionsDao.updateActorsCount(id, actorsCount)
    }

    suspend fun updateWorkersCount(id: Int, workersCount: Int) {
        actionsDao.updateWorkersCount(id, workersCount)
    }

    fun updateActorInfo(id: Int, profession: String, gender: String) {
        actionsDao.updateActorInfo(id, profession, gender)
    }

    fun getActorsCountFromDb(id: Int): Int {
        return actionsDao.getActorsCountFromDb(id)
    }

    fun getWorkersCountFromDb(id: Int): Int {
        return actionsDao.getWorkersCountFromDb(id)
    }
}