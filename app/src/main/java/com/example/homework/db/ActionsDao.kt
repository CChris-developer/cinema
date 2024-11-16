package com.example.homework.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.homework.models.ImageItems
import com.example.homework.models.ActorInfo
import dagger.Provides
import kotlinx.coroutines.flow.Flow


@Dao
interface ActionsDao {

    @Query("SELECT * FROM Actions where id = :id")
    fun getMovie(id: Int): List<Actions>

    @Query("SELECT viewed FROM Actions where id = :id")
    fun getViewed(id: Int): Boolean

    @Query("SELECT viewed FROM Actions where id = :id")
    fun getFlowViewed(id: Int): Flow<Boolean>

    @Query("SELECT favourite FROM Actions where id = :id")
    fun getFavourite(id: Int): Flow<Boolean>

    @Query("SELECT want_to_see FROM Actions where id = :id")
    fun getWantToSee(id: Int): Flow<Boolean>

    @Query("SELECT id FROM Actions where viewed LIKE 1")
    fun getViewedMovie(): List<Int>

    @Query("SELECT collection FROM Actions where id = :id")
    fun getCollectionInfo(id: Int): String

    @Query("SELECT collection FROM Actions")
    fun getAllCollections(): Flow<List<String>>

    @Insert(entity = Actions::class)
    suspend fun insert(action: Actions)

    @Query("DELETE FROM Actions where id = :id")
    suspend fun delete(id: Int)

    @Query("UPDATE actions SET favourite = :favourite WHERE id = :id")
    suspend fun updateFavourite(id: Int, favourite: Boolean)

    @Query("UPDATE actions SET want_to_see = :wantToSee WHERE id = :id")
    suspend fun updateWantToSee(id: Int, wantToSee: Boolean)

    @Query("UPDATE actions SET viewed = :viewed WHERE id = :id")
    suspend fun updateViewed(id: Int, viewed: Boolean)

    @Query("UPDATE actions SET collection = :collection WHERE id = :id")
    fun addToCollection(id: Int, collection: String)

    @Query("SELECT id FROM actions WHERE collection LIKE '%' || :collection || '%'")
    fun getMoviesFromCollection(collection: String): Flow<List<Int>>

   @Query("SELECT id FROM actions WHERE collection LIKE '%' || :collection || '%'")
    fun getMoviesFromCollection2(collection: String): List<Int>

    @Query("SELECT * FROM movies join actions on Movies.id = actions.id WHERE collection LIKE '%' || :collection || '%'")
    fun getMoviesInCollection(collection: String): List<Movies>

    @Insert(entity = Collections::class)
    suspend fun insertCollection(collection: Collections)

    @Query("SELECT * FROM collections WHERE collection = :collection")
    fun getCollection(collection: String): List<Collections>

    @Query("SELECT * FROM collections")
    fun getAllFromCollection(): List<Collections>

    @Query("SELECT * FROM collections")
    fun testGetAllFromCollection(): List<Collections>

    @Query("SELECT collection FROM collections")
    fun getCollectionSize(): List<String>

    @Query("DELETE FROM collections where collection = :collection")
    suspend fun deleteCollection(collection: String)

    @Query("SELECT * FROM actors where personId = :id")
    fun getActorInfo(id: Int): Actors

    @Query("SELECT * FROM Actors join ACTORINMOVIES on actorinmovies.staffId = actors.personId where filmId = :filmId")
    fun getActorsFromDb(filmId: Int): List<Actors>

    @Insert(entity = Actors::class)
    suspend fun insertActor(actor: Actors)

    @Insert(entity = MovieWorkers::class)
    suspend fun insertMovieWorker(worker: MovieWorkers)

    @Insert(entity = ActorInMovies::class)
    suspend fun insertActorMovies(movie: ActorInMovies)

    @Insert(entity = Movies::class)
    suspend fun insertMovie(movie: Movies)

    @Query("select * from ActorInMovies where staffId = :personId and rating != '0.0' order by rating desc limit 10")
    fun getActorMovies(personId: Int): List<ActorInMovies>

    @Query("SELECT filmId FROM ActorInMovies where staffId = :personId")
    fun getMoviesNumber(personId: Int): List<Int>

    @Query("SELECT * FROM actorInMovies where staffId = :personId")
    fun getAllMoviesOfActor(personId: Int): List<ActorInMovies>

    @Query("SELECT staffId FROM actorInMovies where filmId = :filmId")
    fun getAllActorsOfSomeMovie(filmId: Int): List<Int>

    @Query("UPDATE actors SET profession = :profession, gender = :gender WHERE personId = :id")
    fun updateActorInfo(id: Int, profession: String, gender: String)

    @Query("SELECT * FROM Movies")
    fun checkMovie(): List<Movies>

    @Query("UPDATE movies SET nameOriginal = :nameOriginal, year = :year, posterUrl = :posterUrl, " +
            "posterUrlPreview = :posterUrlPreview, genre = :genres, filmLength = :filmLength, ratingAgeLimits = :ratingAgeLimits," +
            "serial = :serial, fullDescription = :fullDescription, shortDescription = :shortDescription WHERE id = :id")
    fun addMovieInfo(id: Int, nameOriginal: String, year: Int, posterUrl: String, posterUrlPreview: String, genres: String, filmLength: Int, ratingAgeLimits: String,
                     serial: Boolean, fullDescription: String, shortDescription: String)

    @Query("SELECT * FROM Movies where id = :id")
    fun getMoviesInfoFromDb(id: Int): Movies

    @Query("SELECT * FROM Movies join Actions on movies.id = actions.id where viewed = 1")
    fun getViewedMoviesInfo(): List<Movies>

    @Query("UPDATE movies SET interested = :interested WHERE id = :id")
    suspend fun updateInterested(id: Int, interested: Boolean)

    @Query("SELECT interested FROM Movies where id = :id")
    fun getInterestedState(id: Int): Boolean

    @Query("SELECT * FROM Movies where id in (select relatedMovieId from relatedMovies where movieId = :id)")
    fun getRelatedMovies(id: Int): List<Movies>

    @Query("SELECT relatedMovieId FROM RelatedMovies where movieId = :id")
    fun getRelatedMoviesId(id: Int): List<Int>

    @Query("SELECT imageUrl, previewUrl FROM Images where movieId = :id")
    fun getImagesFromDb(id: Int): List<ImageItems>

    @Insert(entity = Images::class)
    suspend fun insertImage(image: Images)

    @Query("SELECT seasonNumber FROM Serials where movieId = :id")
    fun getSeasonsFromDb(id: Int): List<Int>

    @Query("SELECT * FROM Serials where movieId = :id")
    fun getSerialsCountFromDb(id: Int): List<Serials>

    @Insert(entity = Serials::class)
    suspend fun insertSerial(serial: Serials)

    @Insert(entity = RelatedMovies::class)
    suspend fun insertRelatedMovies(movie: RelatedMovies)

    @Insert(entity = MovieCount::class)
    suspend fun insertMovieCount(count: MovieCount)

    @Query("SELECT relatedMoviesCount FROM movieCount where id = :id")
    fun getRelatedMoviesCountFromDb(id: Int): Int

    @Query("UPDATE movieCount SET relatedMoviesCount = :count WHERE id = :id")
    suspend fun updateRelatedMoviesCount(id: Int, count: Int)


    @Query("SELECT imagesCount FROM movieCount where id = :id")
    fun getImagesCountFromDb(id: Int): Int

    @Query("UPDATE movieCount SET imagesCount = :count WHERE id = :id")
    suspend fun updateImagesCount(id: Int, count: Int)

    @Query("SELECT actorsCount FROM movieCount where id = :id")
    fun getActorsCountFromDb(id: Int): Int

    @Query("UPDATE movieCount SET actorsCount = :actorsCount WHERE id = :id")
    suspend fun updateActorsCount(id: Int, actorsCount: Int)

    @Query("SELECT workersCount FROM movieCount where id = :id")
    fun getWorkersCountFromDb(id: Int): Int

    @Query("UPDATE movieCount SET workersCount = :workersCount WHERE id = :id")
    suspend fun updateWorkersCount(id: Int, workersCount: Int)

    @Query("SELECT ActorInMovies.professionText, ActorInMovies.professionKey, ActorInMovies.description, ActorInMovies.staffId, nameRu, nameEn, posterUrl FROM Actors join actorInMovies on actorinmovies.staffId = actors.personId where filmId = :filmId")
    fun getMovieActorsFromDb(filmId: Int): List<ActorInfo>

    @Query("SELECT * FROM Actors  where personId = :personId")
    fun getActorFromDb(personId: Int): Actors

    @Query("SELECT * FROM Movies where interested = 1 LIMIT 15")
    fun getInterestedMovies(): List<Movies>

    @Query("SELECT * FROM movieCount where id = :id")
    fun getMovieFromMovieCount(id: Int): MovieCount
 }