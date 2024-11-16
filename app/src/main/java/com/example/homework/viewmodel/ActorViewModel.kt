package com.example.homework.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.repository.ActorRepository
import com.example.homework.api.Utils.countryList
import com.example.homework.api.Utils.createListFromString
import com.example.homework.api.Utils.genreList
import com.example.homework.db.ActorInMovies
import com.example.homework.db.Actors
import com.example.homework.db.MovieCount
import com.example.homework.models.Actor
import com.example.homework.models.ActorInfo
import com.example.homework.models.ActorMovies
import com.example.homework.models.Country
import com.example.homework.models.Genre
import com.example.homework.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActorViewModel @Inject constructor(
    private val repository: ActorRepository
) : ViewModel(), BaseViewModel {

    private val _isException = MutableStateFlow<Boolean>(false)
    var isException = _isException.asStateFlow()
    var isNoActorInfo = false
    var isNoActorMoviesInfo = false
    var actorsCount = 0
    var workersCount = 0
    private val _actorMovies = MutableStateFlow(emptyList<Movie>())
    val actorMovies = _actorMovies.asStateFlow()
    private lateinit var actorInfo: Actor
    private lateinit var actor: Actors
    private lateinit var movie: Movie
    private lateinit var movieCount: MovieCount

    fun loadAllActorsInMovie(filmId: Int): List<ActorInfo> {
        var list = listOf<ActorInfo>()
        var error = false
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            list = repository.getActors(filmId)
            if (list.isEmpty() && repository.isException)
                error = true
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return list
    }

    fun loadActors(filmId: Int, rating: Float): List<ActorInfo> {
        var isReady = false
        var error = false
        var actorsList = mutableListOf<ActorInfo>()
        viewModelScope.launch(Dispatchers.IO) {
            val resultFromDb = repository.getMovieActorsFromDb(filmId)
            if (resultFromDb.isNotEmpty()) {
                actorsCount = repository.getActorsCountFromDb(filmId)
                workersCount = repository.getWorkersCountFromDb(filmId)
                val getExistedActorsMoviesList = repository.getAllActorsOfSomeMovie(filmId)
                if (actorsCount == 0 && workersCount == 0) {
                    val result = loadAllActorsInMovie(filmId)
                    var actorsSize = 0
                    if (result.isNotEmpty()) {
                        var actors = result.filter { it.professionKey == "ACTOR" }
                        var workers =
                            result.filter { it.professionKey != "ACTOR" && it.professionKey != "HIMSELF" && it.professionKey != "HERSELF" }
                                .distinctBy { it.staffId }
                        movieCount = repository.getMovieFromMovieCountDb(filmId, repository.actionsDao)
                        if (!::movieCount.isInitialized)
                            repository.insertMovieCountToDb(filmId, 0, 0, actors.size, workers.size, repository.actionsDao)
                        else {
                            repository.updateActorsCount(filmId, actors.size)
                            repository.updateWorkersCount(filmId, workers.size)
                        }
                        actorsCount = actors.size
                        workersCount = workers.size

                        if (getExistedActorsMoviesList.isNotEmpty()) {
                            for (i in 0..<getExistedActorsMoviesList.size) {
                                actors =
                                    actors.filter { it.staffId != getExistedActorsMoviesList[i] }
                                workers =
                                    workers.filter { it.staffId != getExistedActorsMoviesList[i] }
                            }
                        }
                        if (actors.size > 20)
                            actorsSize = 20
                        else
                            actorsSize = actors.size

                        for (i in 0..<actorsSize) {
                              repository.insertActorInMovies(
                                    ActorInMovies(
                                        0,
                                        actors[i].staffId,
                                        filmId,
                                        actors[i].description ?: "",
                                        actors[i].professionKey ?: "",
                                        actors[i].professionText ?: "",
                                        rating
                                    )
                                )
                                if (repository.getActorFromDb(actors[i].staffId) == null)
                                    repository.insertActorToDB(
                                        Actors(
                                            actors[i].staffId,
                                            actors[i].nameRu,
                                            actors[i].nameEn,
                                            actors[i].posterUrl ?: "",
                                            "",
                                            ""
                                        )
                                    )
                        }
                        if (workers.size > 6)
                            actorsSize = 6
                        else
                            actorsSize = workers.size
                        for (i in 0..<actorsSize) {
                            repository.insertActorInMovies(
                                ActorInMovies(
                                    0,
                                    workers[i].staffId,
                                    filmId,
                                    workers[i].description ?: "",
                                    workers[i].professionKey ?: "",
                                    workers[i].professionText ?: "",
                                    rating
                                )
                            )
                            if (repository.getActorFromDb(workers[i].staffId) == null)
                                repository.insertActorToDB(
                                    Actors(
                                        workers[i].staffId,
                                        workers[i].nameRu,
                                        workers[i].nameEn,
                                        workers[i].posterUrl ?: "",
                                        "",
                                        ""
                                    )
                                )
                        }
                        actorsList.addAll(actors)
                        actorsList.addAll(workers)
                        isNoActorInfo = true
                    }    else
                        actorsList = emptyList<ActorInfo>().toMutableList()
                }
                else {
                    actorsList = resultFromDb.toMutableList()
                    isNoActorInfo = true
                }
            } else {
                val result = loadAllActorsInMovie(filmId)
                var actorsSize = 0
                if (result.isNotEmpty()) {
                    val actors = result.filter { it.professionKey == "ACTOR" }
                    repository.updateActorsCount(filmId, actors.size)
                    actorsCount = actors.size
                    if (actors.size > 20)
                        actorsSize = 20
                    else
                        actorsSize = actors.size
                    for (i in 0..<actorsSize) {
                        repository.insertActorInMovies(
                            ActorInMovies(
                                0,
                                actors[i].staffId,
                                filmId,
                                actors[i].description ?: "",
                                actors[i].professionKey ?: "",
                                actors[i].professionText ?: "",
                                rating
                            )
                        )
                        if (repository.getActorFromDb(actors[i].staffId) == null)
                            repository.insertActorToDB(
                                Actors(
                                    actors[i].staffId,
                                    actors[i].nameRu,
                                    actors[i].nameEn,
                                    actors[i].posterUrl ?: "",
                                    "",
                                    ""
                                )
                            )
                    }
                    val workers =
                        result.filter { it.professionKey != "ACTOR" && it.professionKey != "HIMSELF" && it.professionKey != "HERSELF" }
                            .distinctBy { it.staffId }
                    repository.updateWorkersCount(filmId, workers.size)
                    workersCount = workers.size
                    if (workers.size > 6)
                        actorsSize = 6
                    else
                        actorsSize = workers.size
                    for (i in 0..<actorsSize) {
                        repository.insertActorInMovies(
                            ActorInMovies(
                                0,
                                workers[i].staffId,
                                filmId,
                                workers[i].description ?: "",
                                workers[i].professionKey ?: "",
                                workers[i].professionText ?: "",
                                rating
                            )
                        )
                        if (repository.getActorFromDb(workers[i].staffId) == null)
                            repository.insertActorToDB(
                                Actors(
                                    workers[i].staffId,
                                    workers[i].nameRu,
                                    workers[i].nameEn,
                                    workers[i].posterUrl ?: "",
                                    "",
                                    ""
                                )
                            )
                    }
                    actorsList.addAll(actors)
                    actorsList.addAll(workers)
                    isNoActorInfo = true
                } else
                    actorsList = emptyList<ActorInfo>().toMutableList()
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return actorsList
    }

    fun isActorInitialized() = ::actor.isInitialized

    fun loadActorInfo(id: Int): Actors {
        var isReady = false
        var error = false
        var filteredActorMovieList = listOf<ActorMovies>()
        var movieInDb = listOf<Int>()
        viewModelScope.launch(Dispatchers.IO) {
            actor = repository.getActorInfoFromDb(id)
            if (isActorInitialized()) {
                if (actor.profession == "" && actor.gender == "") {
                    actorInfo = repository.getActorInfo(id)!!
                    if (::actorInfo.isInitialized) {
                        repository.updateActorInfo(
                            id,
                            actorInfo.profession ?: "",
                            actorInfo.gender ?: ""
                        )
                        insertActorsMoviesToDb(actorInfo.films, id)
                        actor = repository.getActorInfoFromDb(id)
                    } else
                        if (repository.isException) {
                            actor = Actors(0, "", "", "", "", "")
                            error = true
                        }
                }
            } else {
                actorInfo = repository.getActorInfo(id)!!
                if (::actorInfo.isInitialized) {
                    val newActor = Actors(
                        actorInfo.personId,
                        actorInfo.nameRu ?: "",
                        actorInfo.nameEn ?: "",
                        actorInfo.posterUrl ?: "",
                        actorInfo.profession ?: "",
                        actorInfo.gender ?: ""
                    )
                    repository.insertActorToDB(newActor)
                    actor = newActor
                    insertActorsMoviesToDb(actorInfo.films, id)
                } else
                    if (repository.isException) {
                        actor = Actors(0, "", "", "", "", "")
                        error = true
                    }
            }
            getActorMovies(id)
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return actor
    }

    private fun getActorMovies(personId: Int) {
        var error = false
        viewModelScope.launch(Dispatchers.IO) {
            var listM = mutableListOf<Movie>()
            val result = repository.getActorMovies(personId)
            if (result.isEmpty())
                isNoActorMoviesInfo = true
            else {
                val movieIdList = repository.checkMovie(repository.actionsDao)
                val moviesMap = mutableMapOf<Int, Int>()
                result.forEach {
                    for (i in 0..<movieIdList.size) {
                        if (it.filmId == movieIdList[i].id) {
                            genreList = mutableListOf()
                            countryList = mutableListOf()
                            createListFromString(movieIdList[i].genre, "Genre")
                            createListFromString(movieIdList[i].country, "Country")
                            movie = Movie(
                                it.filmId,
                                movieIdList[i].nameRu,
                                movieIdList[i].nameEn,
                                movieIdList[i].nameOriginal,
                                movieIdList[i].year,
                                movieIdList[i].posterUrl,
                                movieIdList[i].posterUrlPreview,
                                countryList,
                                genreList,
                                movieIdList[i].rating.toFloat(),
                                movieIdList[i].filmLength,
                                movieIdList[i].ratingAgeLimits,
                                false,
                                movieIdList[i].serial,
                                movieIdList[i].fullDescription,
                                movieIdList[i].shortDescription
                            )
                            listM.add(movie)
                            moviesMap[it.filmId] = 1
                            break
                        }
                    }
                    if (moviesMap[it.filmId] != 1) {
                        val resultFromNet = repository.getMovieInfoFromNet(it.filmId)
                        if (resultFromNet != null) {
                            val movieInfo = resultFromNet
                            listM.add(
                                insertMovieToDb(
                                    movieInfo,
                                    false,
                                    repository,
                                    repository.actionsDao
                                )
                            )
                        } else if (repository.isException) {
                            listM = emptyList<Movie>().toMutableList()
                            error = true
                        }
                    }
                }
            }
            _isException.value = error
            _actorMovies.value = listM
        }
    }

    suspend fun insertActorsMoviesToDb(movies: List<ActorMovies>, id: Int) {
        var filteredActorMovieList = listOf<ActorMovies>()
        val movieInDb: List<Int>
        val actorMoviesList =
            movies.distinctBy { it.filmId }
        movieInDb = repository.getMoviesNumber(id)
        if (movieInDb.isNotEmpty()) {
            movieInDb.forEach { movieInDb ->
                filteredActorMovieList =
                    actorMoviesList.filter { it.filmId != movieInDb && it.rating != 0.0f }
            }
        } else
            filteredActorMovieList = actorMoviesList.filter { it.rating != 0.0f }
        if (filteredActorMovieList.isNotEmpty()) {
            filteredActorMovieList.forEach {
                val actorInMovies = ActorInMovies(
                    0,
                    id,
                    it.filmId ?: 0,
                    it.description ?: "",
                    it.professionKey ?: "",
                    it.professionText ?: "",
                    it.rating ?: 0.0f
                )
                repository.insertActorInMovies(actorInMovies)
            }
        }
    }

    fun getMoviesNumber(personId: Int): Int {
        var moviesNumber = 0
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val result1 = repository.getMoviesNumber(personId)
            val result2 = result1.distinct()
            moviesNumber = result2.size
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return moviesNumber
    }

    private fun getAllMoviesOfActor(personId: Int): Map<String, List<ActorInMovies>> {
        var isReady = false
        var groupingList = mapOf<String, List<ActorInMovies>>()
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAllMoviesOfActor(personId)
            groupingList = result.groupBy { it.professionKey }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return groupingList
    }

    fun getActorInfoFromDb(id: Int): Actors {
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            actor = repository.getActorInfoFromDb(id)
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return actor
    }

    fun getActorInfo(personId: Int): Actor {
        var isReady = false
        var error = false
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getActorInfo(personId)
            if (result != null)
                actorInfo = result
            else if (repository.isException) {
                error = true
                actorInfo = Actor(0, "", "", "", "", "", emptyList())
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return actorInfo
    }

    private fun fillList(actorListList: MutableList<List<ActorInMovies>>): MutableList<ActorInMovies> {
        val actorList = mutableListOf<ActorInMovies>()
        actorListList.forEach {
            it.forEach { item ->
                actorList.add(item)
            }
        }
        return actorList
    }

    fun moviesSorting(personId: Int): MutableMap<String, MutableList<ActorInMovies>> {
        val movies = getAllMoviesOfActor(personId)
        var profession = ""
        var isMale = true
        val professionMap = mutableMapOf<String, MutableList<ActorInMovies>>()
        val dublActor = mutableListOf<List<ActorInMovies>>()
        val selfActor = mutableListOf<List<ActorInMovies>>()
        val result = getActorInfoFromDb(personId)
        if (result != null) {
            val actor = result
            movies.forEach { t, u ->
                if (t == "ACTOR") {
                    if (actor.gender == "FEMALE")
                        profession = "Актриса"
                    else if (actor.gender == "MALE")
                        profession = "Актер"
                    else
                        profession = "Actor"
                    professionMap[profession] = u.toMutableList()
                } else if (t == "PRODUCER") {
                    professionMap["Продюсер"] = u.toMutableList()
                } else if (t == "DIRECTOR") {
                    professionMap["Режиссер"] = u.toMutableList()
                } else if (t == "WRITER") {
                    professionMap["Сценарист"] = u.toMutableList()
                } else if (t == "COMPOSER") {
                    professionMap["Композитор"] = u.toMutableList()
                } else if (t == "DESIGN") {
                    professionMap["Художник"] = u.toMutableList()
                } else if (t == "OPERATOR") {
                    professionMap["Оператор"] = u.toMutableList()
                } else if (t.contains("VOICE")) {
                    dublActor.add(u)
                } else if (t == "HIMSELF") {
                    isMale = true
                    val himself = u.groupBy { it.description }
                    himself.forEach { key, value ->
                        if ("озвучка" in key || "рассказчик" in key)
                            selfActor.add(value)
                        else
                            dublActor.add(value)
                    }
                } else if (t == "HERSELF") {
                    isMale = false
                    val herself = u.groupBy { it.description }
                    herself.forEach { key, value ->
                        if ("озвучка" in key || "рассказчик" in key)
                            selfActor.add(value)
                        else
                            dublActor.add(value)
                    }

                } else {
                    professionMap[t] = u.toMutableList()
                }
            }
            if (dublActor.isNotEmpty()) {
                if (isMale) {
                    professionMap["Актер дубляжа"] = fillList(dublActor)
                } else {
                    professionMap["Актриса дубляжа"] = fillList(dublActor)
                }
            }
            if (selfActor.isNotEmpty()) {
                if (isMale) {
                    professionMap["Актер: играет сам себя"] = fillList(selfActor)
                } else {
                    professionMap["Актриса: играет саму себя"] = fillList(selfActor)
                }
            }
        } else if (repository.isException) {
            _isException.value = true
            professionMap[""] = emptyList<ActorInMovies>().toMutableList()
        }
        return professionMap
    }

    fun loadMoviesToChip(list: List<ActorInMovies>) {
        var moviesList = mutableListOf<Movie>()
        var listM = mutableListOf<Movie>()
        var error = false
        val moviesMap = mutableMapOf<Int, Int>()
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val movieIdList = repository.checkMovie(repository.actionsDao)
            list.forEach {
                for (i in 0..<movieIdList.size) {
                    if (it.filmId == movieIdList[i].id) {
                        val genreList = mutableListOf<Genre>()
                        val countryList = mutableListOf<Country>()
                        val getStr = movieIdList[i].genre.split("#")
                        val getCountryStr = movieIdList[i].country.split("#")
                        getStr.forEach { item1 ->
                            if (item1 != "") {
                                val getItems = item1.split("&")
                                genreList.add(Genre(getItems[1].toInt(), getItems[0]))
                            }
                        }
                        getCountryStr.forEach { item ->
                            if (item != "") {
                                val getItems = item.split("&")
                                countryList.add(Country(getItems[1].toInt(), getItems[0]))
                            }
                        }
                        movie = Movie(
                            it.filmId,
                            movieIdList[i].nameRu,
                            movieIdList[i].nameEn,
                            movieIdList[i].nameOriginal,
                            movieIdList[i].year,
                            movieIdList[i].posterUrl,
                            movieIdList[i].posterUrlPreview,
                            countryList,
                            genreList,
                            movieIdList[i].rating.toFloat(),
                            movieIdList[i].filmLength,
                            movieIdList[i].ratingAgeLimits,
                            false,
                            movieIdList[i].serial,
                            movieIdList[i].fullDescription,
                            movieIdList[i].shortDescription
                        )
                        listM.add(movie)
                        moviesMap[it.filmId] = 1
                        break
                    }
                }
                if (moviesMap[it.filmId] != 1) {
                    val result = repository.getMovieInfoFromNet(it.filmId)
                    if (result != null) {
                        val movieInfo = result
                        listM.add(
                            insertMovieToDb(
                                movieInfo,
                                false,
                                repository,
                                repository.actionsDao
                            )
                        )
                    } else if (repository.isException) {
                        listM = emptyList<Movie>().toMutableList()
                        error = true
                    }
                }
                _isException.value = error
                _actorMovies.value = listM
            }
            isReady = true
        }
     }
}