package com.example.homework.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homework.api.Consts.FIRST_PAGE
import com.example.homework.models.Movie
import com.example.homework.repository.MovieListRepository

abstract class BasePagingSource(
    private val request: suspend (Int) -> List<Movie>
) : PagingSource<Int, Movie>() {

    abstract var isFullList: Boolean
    abstract var movieList: List<Int>
    abstract var name: String
    private val repository = MovieListRepository()

    private fun checkIsFullList(list: List<Movie>): Boolean {
        if (isFullList) {
            return (list.isEmpty())
        } else
            return (list.size >= 20)
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            request(page)
        }.fold(
            onSuccess = {
                if (movieList.isNotEmpty()) {
                    it.forEach { movie ->
                        for (i in movieList) {
                            if (movie.kinopoiskId == i) {
                                movie.viewed = true
                                break
                            }
                        }
                    }
                }
                if (name != "") {
                    val moviesByPersonList = mutableListOf<Movie>()
                    val moviesByNameList: List<Movie>
                    val resultList: List<Movie>
                    val testList: List<Movie>
                    val moviesList = it.filter { it.ratingKinopoisk != 0.0f }
                    if (moviesList.isNotEmpty()) {
                        moviesByNameList =
                            moviesList.filter { it.nameRu == name || it.nameEn == name || it.nameOriginal == name }
                        if (moviesByNameList.isNotEmpty())
                            testList = moviesByNameList
                        else {
                            moviesList.forEach { movie ->
                                val actorList = repository.getActors(movie.kinopoiskId)
                                for (i in 0..<actorList.size) {
                                    if (actorList[i].nameRu == name || actorList[i].nameEn == name) {
                                        moviesByPersonList.add(movie)
                                        break
                                    }
                                }
                            }
                            testList = moviesByPersonList
                        }
                    } else
                        testList = emptyList()
                    if (testList.isEmpty())
                        resultList = it.filter { it.ratingKinopoisk == 11.0f }
                    else
                        resultList = testList

                    LoadResult.Page(
                        data = resultList,
                        prevKey = null,
                        nextKey = if (it.isEmpty()) null else page + 1
                    )

                } else {
                    LoadResult.Page(
                        data = it.filter { it.ratingKinopoisk != 0.0f },
                        prevKey = null,
                        nextKey = if (checkIsFullList(it)) null else page + 1
                    )
                }
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }
}