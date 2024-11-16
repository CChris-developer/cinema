package com.example.homework.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homework.api.Consts.FIRST_PAGE
import com.example.homework.models.ImageItems
import com.example.homework.repository.ImageRepository

class ImagePagingSource(
    private val isFullList: Boolean,
    private val filmId: Int,
    private val repository: ImageRepository,
    private val imageType: String
) : PagingSource<Int, ImageItems>() {

    private fun checkIsFullList(list: List<ImageItems>): Boolean {
        if (isFullList) {
            return (list.isEmpty())
        } else
            return (list.size >= 20)
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItems>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItems> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            repository.getTypedImages(filmId, imageType, page)
        }.fold(
            onSuccess = {

                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (checkIsFullList(it)) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }
}