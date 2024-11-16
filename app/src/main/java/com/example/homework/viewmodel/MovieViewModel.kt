package com.example.homework.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.repository.MovieRepository
import com.example.homework.api.Utils
import com.example.homework.api.Utils.countryList
import com.example.homework.api.Utils.createListFromString
import com.example.homework.api.Utils.genreList
import com.example.homework.db.Collections
import com.example.homework.db.MovieCount
import com.example.homework.db.Movies
import com.example.homework.models.Movie
import com.example.homework.models.SimilarMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor (private val repository: MovieRepository) : ViewModel(), BaseViewModel {

    private val _isException = MutableStateFlow(false)
    var isException = _isException.asStateFlow()
    private lateinit var movieInfo: Movie
    private lateinit var movieInDb: Movies
    private lateinit var movieCount: MovieCount
    var relatedMoviesCount = 0

    val listenCollection = this.repository.getAllCollections()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(3000L),
            initialValue = emptyList()
        )

    private val _isFavourite = MutableStateFlow(false)
    val isFavourite = _isFavourite.asStateFlow()

    private val _wantToSee = MutableStateFlow(false)
    val wantToSee = _wantToSee.asStateFlow()

    private val _viewed = MutableStateFlow(false)
    val viewed = _viewed.asStateFlow()

    private val _movieCollections = MutableStateFlow("")
    val movieCollections = _movieCollections.asStateFlow()

    var textview = 0

    private val _isListEmpty = MutableStateFlow(false)
    val isListEmpty = _isListEmpty.asStateFlow()

    private val _isListReady = MutableStateFlow(false)
    val isListReady = _isListReady.asStateFlow()

    fun testGetAllFromCollection(): List<Collections> {
        var result = emptyList<Collections>()
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            result = repository.testGetAllFromCollection()
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return result
    }

    fun viewedObserv(id: Int): StateFlow<Boolean> {
        val moviesFromCollection = repository.getFlowViewed(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(3000L),
                initialValue = false
            )
        return moviesFromCollection
    }

    fun wantToSeeObserv(id: Int): StateFlow<Boolean> {
        val moviesFromCollection = repository.getWantToSee(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(3000L),
                initialValue = false
            )
        return moviesFromCollection
    }

    fun favouriteObserv(id: Int): StateFlow<Boolean> {
        val moviesFromCollection = repository.getFavourite(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(3000L),
                initialValue = false
            )
        return moviesFromCollection
    }

    fun observingMoviesNumber1(name: String): StateFlow<List<Int>> {
        val moviesFromCollection = this.repository.getMoviesFromCollection(name)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(3000L),
                initialValue = emptyList()
            )
        return moviesFromCollection
    }

    fun getMoviesCountInCollection(name: String): Int {
        var count = 0
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val moviesList = repository.getMoviesFromCollection2(name)
            count = moviesList.size
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return count
    }

    fun getMoviesInCollection(collection: String): MutableList<Movie> {
        val listM = mutableListOf<Movie>()
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val moviesList = repository.getMoviesInCollection(collection)
            if (moviesList.isNotEmpty()) {
                moviesList.forEach {
                    genreList = mutableListOf()
                    countryList = mutableListOf()
                    createListFromString(it.genre, "Genre")
                    createListFromString(it.country, "Country")
                    val movie = Movie(
                        it.id,
                        it.nameRu ?: "",
                        it.nameEn ?: "",
                        it.nameOriginal ?: "",
                        it.year,
                        it.posterUrl,
                        it.posterUrlPreview,
                        countryList,
                        genreList,
                        it.rating.toFloat(),
                        it.filmLength,
                        it.ratingAgeLimits ?: "",
                        repository.getViewed(it.id),
                        it.serial,
                        it.fullDescription ?: "",
                        it.shortDescription ?: ""
                    )
                    listM.add(movie)
                }
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return listM
    }

    fun markedCollection(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieCollections.value = repository.getCollectionInfo(id)
        }
    }

    fun onChangeFavouriteState(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getMovie(id).isEmpty()) {
                _isFavourite.value = true
                repository.insertValueToActions(id, true, false, false, "Любимые")
            } else {
                repository.getMovie(id).onEach {
                    if (it.favourite) {
                        _isFavourite.value = false
                        deleteFromCollection(id, "Любимые")
                    } else {
                        _isFavourite.value = true
                        addToCollection(id, "Любимые")
                    }
                }
            }
        }
    }

    fun onChangeWantToSeeState(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getMovie(id).isEmpty()) {
                _wantToSee.value = true
                repository.insertValueToActions(id, false, true, false, "Хочу посмотреть")
            } else {
                repository.getMovie(id).onEach {
                    if (it.wantToSee) {
                        _wantToSee.value = false
                        deleteFromCollection(id, "Хочу посмотреть")
                    } else {
                        _wantToSee.value = true
                        addToCollection(id, "Хочу посмотреть")
                    }
                }
            }
        }
    }

    fun onChangeViewedState(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getMovie(id).isEmpty()) {
                _viewed.value = true
                repository.insertValueToActions(id, false, false, true, "Просмотренные")
            } else {
                repository.getMovie(id).onEach {
                    if (it.viewed) {
                        _viewed.value = false
                        deleteFromCollection(id, "Просмотренные")
                    } else {
                        _viewed.value = true
                        addToCollection(id, "Просмотренные")
                    }
                }
            }
        }
    }

    fun getViewedMovie(): List<Int> {
        var isReady = false
        var result = emptyList<Int>()
        viewModelScope.launch(Dispatchers.IO) {
            result = repository.getViewedMovie()
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return result
    }

    fun getViewedMoviesInfo(): MutableList<Movie> {
        var resultList: List<Movies>
        var isReady = false
        val listM = mutableListOf<Movie>()
        viewModelScope.launch(Dispatchers.IO) {
            resultList = repository.getViewedMoviesInfo()
            resultList.forEach {
                genreList = mutableListOf()
                countryList = mutableListOf()
                createListFromString(it.genre, "Genre")
                createListFromString(it.country, "Country")
                val viewedMovie = Movie(
                    it.id,
                    it.nameRu ?: "",
                    it.nameEn ?: "",
                    it.nameOriginal ?: "",
                    it.year,
                    it.posterUrl ?: "",
                    it.posterUrlPreview ?: "",
                    countryList,
                    genreList,
                    it.rating.toFloat(),
                    it.filmLength,
                    it.ratingAgeLimits ?: "",
                    repository.getViewed(it.id),
                    it.serial,
                    it.fullDescription ?: "",
                    it.shortDescription ?: ""
                )
                listM.add(viewedMovie)
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return listM
    }

    fun addToCollection(id: Int, collection: String) {
        val collection_name = collection.trim()
        var favouriteMovies = false
        var viewedMovies = false
        var wantToSeeMovies = false
        if (collection != "" && collection != " ") {
            viewModelScope.launch(Dispatchers.IO) {
                if (collection_name == "Любимые") {
                    favouriteMovies = true
                    _isFavourite.value = true
                } else if (collection_name == "Хочу посмотреть") {
                    wantToSeeMovies = true
                    _wantToSee.value = true
                } else if (collection_name == "Просмотренные") {
                    viewedMovies = true
                    _viewed.value = true
                }
                val res = repository.getMovie(id)
                if (res.isEmpty()) {
                    repository.insertValueToActions(
                        id,
                        favouriteMovies,
                        wantToSeeMovies,
                        viewedMovies,
                        collection_name
                    )
                } else {
                    if (collection_name == "Любимые")
                        repository.updateFavourite(id, true)
                    else if (collection_name == "Хочу посмотреть")
                        repository.updateWantToSee(id, true)
                    else if (collection_name == "Просмотренные")
                        repository.updateViewed(id, true)
                    var existedCollection = repository.getCollectionInfo(id)
                    if (existedCollection == "") {
                        existedCollection = collection_name
                        repository.addToCollection(id, existedCollection)
                    } else if (!existedCollection.contains(collection_name)) {
                        existedCollection = "$existedCollection&$collection_name"
                        repository.addToCollection(id, existedCollection)
                    } else
                        Log.d("ADD COLLECTION ERROR", "В базе уже есть такая коллекция")
                }
            }
        }
    }

    fun deleteFromCollection(id: Int, collection_name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (collection_name == "Любимые") {
                repository.updateFavourite(id, false)
            } else if (collection_name == "Хочу посмотреть") {
                repository.updateWantToSee(id, false)
            } else if (collection_name == "Просмотренные") {
                repository.updateViewed(id, false)
            }
            var string = repository.getCollectionInfo(id)
            if (string.contains("&$collection_name"))
                string = string.replace("&$collection_name", "")
            else if (string.contains("$collection_name&"))
                string = string.replace("$collection_name&", "")
            else
                string = string.replace("$collection_name", "")
            repository.addToCollection(id, string)
        }
    }

    fun getCollection(collection_name: String): List<Collections> {
        var result = listOf<Collections>()
        viewModelScope.launch(Dispatchers.IO) {
            result = repository.getCollection(collection_name)
        }
        return result
    }

    fun getCollectionSize(): Int {
        var size = 0
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCollectionSize()
            size = result.size + 1
            isReady = true
        }
        while (!isReady)
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        return size
    }

    fun addCollection(
        collection_name: String,
        checkboxId: Int,
        textviewId: Int,
        cardId: Int,
        linearLayoutId: Int,
        imageButtonId: Int,
        textView1Id: Int,
        textView2Id: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertValueToCollections(
                collection_name,
                checkboxId,
                textviewId,
                cardId,
                linearLayoutId,
                imageButtonId,
                textView1Id,
                textView2Id
            )
        }
    }

    fun loadAllRelativeMoviesToAllMoviesFragment(id: Int): MutableList<Movie> {
        var isReady = false
        val movieList = mutableListOf<Movie>()
        viewModelScope.launch(Dispatchers.IO) {
            val relativeMoviesList = loadAllRelatedMovies(id)
            if (!_isException.value) {
                if (relativeMoviesList.isNotEmpty()) {
                    relativeMoviesList.forEach {
                        val movieInDb =
                            repository.getMovieInfoFromDb(it.filmId, repository.actionsDao)
                        if (movieInDb != null) {
                            genreList = mutableListOf()
                            countryList = mutableListOf()
                            createListFromString(movieInDb.country, "Country")
                            createListFromString(movieInDb.genre, "Genre")
                            movieList.add(
                                Movie(
                                    movieInDb.id,
                                    movieInDb.nameRu ?: "",
                                    movieInDb.nameEn ?: "",
                                    movieInDb.nameOriginal ?: "",
                                    movieInDb.year,
                                    movieInDb.posterUrl ?: "",
                                    movieInDb.posterUrlPreview ?: "",
                                    countryList,
                                    genreList,
                                    movieInDb.rating.toFloat(),
                                    movieInDb.filmLength,
                                    movieInDb.ratingAgeLimits ?: "",
                                    repository.getViewed(movieInDb.id),
                                    movieInDb.serial,
                                    movieInDb.fullDescription ?: "",
                                    movieInDb.shortDescription ?: ""
                                )
                            )
                        } else {
                            val response = repository.getMovieInfoFromNet(it.filmId)
                            if (response != null) {
                                val movieInfoFromNet = response
                                movieList.add(
                                    insertMovieToDb(
                                        movieInfoFromNet,
                                        false,
                                        repository,
                                        repository.actionsDao
                                    )
                                )
                            } else
                                _isException.value = true
                        }
                    }
                }
            } else
                _isException.value = true
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return movieList
    }

    private fun loadAllRelatedMovies(id: Int): List<SimilarMovie> {
        var isReady = false
        var error = false
        var list = listOf<SimilarMovie>()
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getRelatedMoviesFromNet(id)
            if (response == null && repository.isException) {
                error = true
            } else
                list = response!!.items
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

    fun loadRelatedMovies(id: Int): MutableList<Movie> {
        var listM = mutableListOf<Movie>()
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val movieListFromDb = repository.getRelatedMoviesFromDb(id)
            if (movieListFromDb.isNotEmpty()) {
                relatedMoviesCount = repository.getRelatedMoviesCountFromDb(id)
                movieListFromDb.forEach {
                    genreList = mutableListOf()
                    countryList = mutableListOf()
                    createListFromString(it.genre, "Genre")
                    createListFromString(it.country, "Country")
                    val movie = Movie(
                        it.id,
                        it.nameRu ?: "",
                        it.nameEn ?: "",
                        it.nameOriginal ?: "",
                        it.year,
                        it.posterUrl,
                        it.posterUrlPreview,
                        countryList,
                        genreList,
                        it.rating.toFloat(),
                        it.filmLength,
                        it.ratingAgeLimits ?: "",
                        repository.getViewed(it.id),
                        it.serial,
                        it.fullDescription ?: "",
                        it.shortDescription ?: ""
                    )
                    listM.add(movie)
                }
            } else if (repository.getRelatedMoviesID(id).size == 1 && repository.getRelatedMoviesID(
                    id
                )[0] == 0
            ) {
                listM = emptyList<Movie>().toMutableList()
            } else {
                val result = loadAllRelatedMovies(id)
                if (!_isException.value) {
                    relatedMoviesCount = result.size
                    movieCount = repository.getMovieFromMovieCountDb(id, repository.actionsDao)
                    if (!::movieCount.isInitialized)
                        repository.insertMovieCountToDb(id, result.size, 0, 0, 0, repository.actionsDao)
                    else {
                        repository.updateRelatedMoviesCount(id, result.size)
                    }
                    if (result.isNotEmpty()) {
                        val size: Int
                        if (result.size > 20)
                            size = 20
                        else
                            size = result.size
                        for (i in 0..<size) {
                            repository.insertRelatedMoviesToDb(id, result[i].filmId)
                            val movieInDb =
                                repository.getMovieInfoFromDb(
                                    result[i].filmId,
                                    repository.actionsDao
                                )
                            if (movieInDb != null) {
                                genreList = mutableListOf()
                                countryList = mutableListOf()
                                createListFromString(movieInDb.country, "Country")
                                createListFromString(movieInDb.genre, "Genre")
                                listM.add(
                                    Movie(
                                        movieInDb.id,
                                        movieInDb.nameRu ?: "",
                                        movieInDb.nameEn ?: "",
                                        movieInDb.nameOriginal ?: "",
                                        movieInDb.year,
                                        movieInDb.posterUrl ?: "",
                                        movieInDb.posterUrlPreview ?: "",
                                        countryList,
                                        genreList,
                                        movieInDb.rating.toFloat(),
                                        movieInDb.filmLength,
                                        movieInDb.ratingAgeLimits ?: "",
                                        repository.getViewed(movieInDb.id),
                                        movieInDb.serial,
                                        movieInDb.fullDescription ?: "",
                                        movieInDb.shortDescription ?: ""
                                    )
                                )
                            } else {
                                val response = repository.getMovieInfoFromNet(result[i].filmId)
                                if (response != null) {
                                    val movieInfoFromNet = response
                                    listM.add(
                                        insertMovieToDb(
                                            movieInfoFromNet,
                                            false,
                                            repository,
                                            repository.actionsDao
                                        )
                                    )
                                } else
                                    _isException.value = true
                            }
                        }
                    } else {
                        repository.insertRelatedMoviesToDb(id, 0)
                    }
                }
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return listM
    }

    fun getMovieInfo(id: Int): Movie {
        var error = false
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            movieInDb = repository.getMovieInfoFromDb(id, repository.actionsDao)
            if (::movieInDb.isInitialized) {
                genreList = mutableListOf()
                countryList = mutableListOf()
                createListFromString(movieInDb.genre, "Genre")
                createListFromString(movieInDb.country, "Country")
                if (!repository.getInterestedState(movieInDb.id))
                    repository.updateInterested(movieInDb.id, true)
                movieInfo = Movie(
                    movieInDb.id,
                    movieInDb.nameRu,
                    movieInDb.nameEn,
                    movieInDb.nameOriginal,
                    movieInDb.year,
                    movieInDb.posterUrl,
                    movieInDb.posterUrlPreview,
                    countryList,
                    genreList,
                    movieInDb.rating.toFloat(),
                    movieInDb.filmLength,
                    movieInDb.ratingAgeLimits,
                    repository.getViewed(movieInDb.id),
                    movieInDb.serial,
                    movieInDb.fullDescription,
                    movieInDb.shortDescription
                )
            } else {
                val response = repository.getMovieInfoFromNet(id)
                if (response != null) {
                    val movieFromNet = response
                    repository.insertMovieCountToDb(movieFromNet.kinopoiskId, 0, 0, 0, 0, repository.actionsDao)
                    movieInfo =
                        insertMovieToDb(movieFromNet, true, repository, repository.actionsDao)
                } else {
                    error = true
                    movieInfo = Movie(
                        0,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        emptyList(),
                        emptyList(),
                        0.0f,
                        0,
                        "",
                        false,
                        false,
                        "",
                        ""
                    )
                }
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return movieInfo
    }

    fun getInterestedMovies(): MutableList<Movie> {
        var isReady = false
        val resultList = mutableListOf<Movie>()
        viewModelScope.launch(Dispatchers.IO) {
            val listFromDb = repository.getInterestedMovies()
            if (listFromDb.isNotEmpty()) {
                listFromDb.forEach {
                    genreList = mutableListOf()
                    countryList = mutableListOf()
                    createListFromString(it.genre, "Genre")
                    createListFromString(it.country, "Country")
                    val interestedMovie = Movie(
                        it.id,
                        it.nameRu,
                        it.nameEn,
                        it.nameOriginal,
                        it.year,
                        it.posterUrl,
                        it.posterUrlPreview,
                        countryList,
                        genreList,
                        it.rating.toFloat(),
                        it.filmLength,
                        it.ratingAgeLimits,
                        repository.getViewed(it.id),
                        it.serial,
                        it.fullDescription,
                        it.shortDescription
                    )
                    resultList.add(interestedMovie)
                }
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return resultList
    }

    fun clearMoviesHistory(list: List<Movie>, historyOf: String): Boolean {
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            if (historyOf == "interested") {
                list.forEach {
                    repository.updateInterested(it.kinopoiskId, false)
                }
            } else {
                list.forEach {
                    deleteFromCollection(it.kinopoiskId, "Просмотренные")
                }
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return isReady
    }

    fun deleteCollection(collection: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCollection(collection)
            val moviesInCollection = repository.getMoviesInCollection(collection)
            if (moviesInCollection.isNotEmpty()) {
                moviesInCollection.forEach {
                    deleteFromCollection(it.id, collection)
                }
            }
        }
    }
}