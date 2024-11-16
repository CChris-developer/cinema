package com.example.homework.api

import com.example.homework.api.Consts.BASE_URL
import com.example.homework.models.Actor
import com.example.homework.models.ActorInfo
import com.example.homework.models.ImageList
import com.example.homework.models.Movie
import com.example.homework.models.MovieList
import com.example.homework.models.Seasons
import com.example.homework.models.SimilarMovieList
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieListApi {
    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/premieres")
    suspend fun premieres(@Query("year") year: Int, @Query("month") month: String): MovieList

    @Headers("X-API-KEY: $api_key2")
    @GET("/api/v2.2/films/{id}")
    suspend fun movieInfo(@Path("id") id: Int): Movie

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/collections?type=TOP_POPULAR_ALL")
    suspend fun popularMovies(@Query("page") page: Int): MovieList

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films?ratingFrom=8.1")
    suspend fun randomMovies(
        @Query("countries") countries: Int,
        @Query("genres") genres: Int,
        @Query("page") page: Int
    ): MovieList

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/collections?type=TOP_250_MOVIES")
    suspend fun topList(@Query("page") page: Int): MovieList

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films?countries=34&genres=5&type=TV_SERIES")
    suspend fun tvSeries(@Query("page") page: Int): MovieList

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/{id}/seasons")
    suspend fun seasons(@Path("id") id: Int): Seasons

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/{id}/images")
        suspend fun getImagesType(
        @Path("id") id: Int,
        @Query("type") type: String,
        @Query("page") page: Int
    ): ImageList

    @Headers("X-API-KEY: $api_key1")
    @GET("/api/v2.2/films/{id}/similars")
    suspend fun getRelatedMovies(@Path("id") id: Int): SimilarMovieList

    @Headers("X-API-KEY: $api_key3")
    @GET("/api/v1/staff")
    suspend fun getActors(@Query("filmId") filmId: Int): List<ActorInfo>

    @Headers("X-API-KEY: $api_key3")
    @GET("/api/v1/staff/{id}")
    suspend fun getActorInfo(@Path("id") id: Int): Actor

    @Headers("X-API-KEY: $api_key4")
    @GET("/api/v2.2/films")
    suspend fun getRequiredMovies(
        @Query("countries") countries: Int,
        @Query("genres") genres: Int,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Float,
        @Query("ratingTo") ratingTo: Float,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("page") page: Int
    ): MovieList

    private companion object {
        private const val api_key1 = "1b314350-896d-49fd-ab5a-ff5c5e1f94d8"
        private const val api_key2 = "175e44f3-0d31-4955-9cfe-70416cfe0634"
        private const val api_key3 = "4f9e6ac7-2c53-4175-9622-ed8d3a861def"
        private const val api_key4 = "23d041b8-5865-44b6-92a6-aba184d7956b"
    }
}

val gson = GsonBuilder()
    .serializeNulls()
    .create()!!
val retrofit: MovieListApi = Retrofit
    .Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
    .create(MovieListApi::class.java)