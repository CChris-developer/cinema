package com.example.homework.repository

import com.example.homework.api.retrofit
import com.example.homework.db.Actions
import com.example.homework.db.ActionsDao
import com.example.homework.db.Collections
import com.example.homework.db.MovieCount
import com.example.homework.db.Movies
import com.example.homework.db.RelatedMovies
import com.example.homework.models.SimilarMovieList
import com.example.homework.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import javax.inject.Inject

class MovieRepository @Inject constructor(val actionsDao: ActionsDao) : BaseRepository {

    override var isException = false

    fun getAllCollections(): Flow<List<String>> {
        return actionsDao.getAllCollections()
    }

    fun getMoviesInCollection(collection: String): List<Movies> {
        return actionsDao.getMoviesInCollection(collection)
    }

    fun testGetAllFromCollection(): List<Collections> {
        return actionsDao.testGetAllFromCollection()
    }

    suspend fun deleteCollection(collection: String) {
        actionsDao.deleteCollection(collection)
    }

    fun getWantToSee(id: Int): Flow<Boolean> {
        return actionsDao.getWantToSee(id)
    }

    fun getFavourite(id: Int): Flow<Boolean> {
        return actionsDao.getFavourite(id)
    }

    fun getFlowViewed(id: Int): Flow<Boolean> {
        return actionsDao.getFlowViewed(id)
    }

    fun getViewed(id: Int): Boolean {
        return actionsDao.getViewed(id)
    }

    fun getMoviesFromCollection(name: String): Flow<List<Int>> {
        return actionsDao.getMoviesFromCollection(name)
    }

    fun getMoviesFromCollection2(name: String): List<Int> {
        return actionsDao.getMoviesFromCollection2(name)
    }

    fun getCollectionInfo(id: Int): String {
        return actionsDao.getCollectionInfo(id)
    }

    fun getMovie(id: Int): List<Actions> {
        return actionsDao.getMovie(id)
    }

    /* suspend fun insertFavouriteValueToActions(id: Int) {
          actionsDao.insert(
              Actions(
                  id = id,
                  favourite = true,
                  wantToSee = false,
                  viewed = false,
                  collection = "Любимые"
              )
          )
      }

      suspend fun insertWantToSeeValueToActions(id: Int) {
          actionsDao.insert(
              Actions(
                  id = id,
                  favourite = false,
                  wantToSee = true,
                  viewed = false,
                  collection = "Хочу посмотреть"
              )
          )
      }

      suspend fun insertViewedValueToActions(id: Int) {
          actionsDao.insert(
              Actions(
                  id = id,
                  favourite = false,
                  wantToSee = false,
                  viewed = true,
                  collection = "Просмотренные"
              )
          )
      }

     */

    suspend fun insertValueToActions(
        id: Int,
        favourite: Boolean,
        wantToSee: Boolean,
        viewed: Boolean,
        collection: String
    ) {
        actionsDao.insert(
            Actions(
                id = id,
                favourite = favourite,
                wantToSee = wantToSee,
                viewed = viewed,
                collection = collection
            )
        )
    }

    suspend fun updateFavourite(id: Int, favourite: Boolean) {
        actionsDao.updateFavourite(id, favourite)
    }

    suspend fun updateWantToSee(id: Int, wantToSee: Boolean) {
        actionsDao.updateWantToSee(id, wantToSee)
    }

    suspend fun updateViewed(id: Int, viewed: Boolean) {
        actionsDao.updateViewed(id, viewed)
    }

    fun addToCollection(id: Int, existedCollection: String) {
        return actionsDao.addToCollection(id, existedCollection)
    }

    fun getCollection(name: String): List<Collections> {
        return actionsDao.getCollection(name)
    }

    fun getCollectionSize(): List<String> {
        return actionsDao.getCollectionSize()
    }

    suspend fun insertValueToCollections(
        collection_name: String,
        checkboxId: Int,
        textviewId: Int,
        cardId: Int,
        linearLayoutId: Int,
        imageButtonId: Int,
        textView1Id: Int,
        textView2Id: Int
    ) {
        actionsDao.insertCollection(
            Collections(
                collection = collection_name,
                checkboxId = checkboxId,
                textviewId = textviewId,
                profileCardId = cardId,
                profileLinearLayoutId = linearLayoutId,
                profileImageButtonId = imageButtonId,
                profileTextView1Id = textView1Id,
                profileTextView2Id = textView2Id
            )
        )
    }

    fun getViewedMovie(): List<Int> {
        return actionsDao.getViewedMovie()
    }

    suspend fun getRelatedMoviesFromNet(id: Int): SimilarMovieList? {
        try {
            val result = retrofit.getRelatedMovies(id)
            return result
        } catch (e: Exception) {
            isException = true
            return null
        }
    }

    fun getRelatedMoviesFromDb(id: Int): List<Movies> {
        return actionsDao.getRelatedMovies(id)
    }

    fun getViewedMoviesInfo(): List<Movies> {
        return actionsDao.getViewedMoviesInfo()
    }

    fun getInterestedState(id: Int): Boolean {
        return actionsDao.getInterestedState(id)
    }

    suspend fun updateInterested(id: Int, interested: Boolean) {
        actionsDao.updateInterested(id, interested)
    }

    suspend fun insertRelatedMoviesToDb(movieId: Int, relatedMovieId: Int) {
        actionsDao.insertRelatedMovies(
            RelatedMovies(
                id = 0,
                movieId = movieId,
                relatedMovieId = relatedMovieId
            )
        )
    }

   /* suspend fun insertMovieCountToDb(
        id: Int,
        relatedMoviesCount: Int,
        imagesCount: Int,
        actorsCount: Int,
        workersCount: Int
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

    */

    suspend fun updateRelatedMoviesCount(id: Int, count: Int) {
        actionsDao.updateRelatedMoviesCount(id, count)
    }

    fun getRelatedMoviesCountFromDb(id: Int): Int {
        return actionsDao.getRelatedMoviesCountFromDb(id)
    }

    fun getRelatedMoviesID(id: Int): List<Int> {
        return actionsDao.getRelatedMoviesId(id)
    }

    fun getInterestedMovies(): List<Movies> {
        return actionsDao.getInterestedMovies()
    }
}