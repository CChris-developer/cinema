package com.example.homework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.homework.models.ImageItems
import com.example.homework.pagingsource.ImagePagingSource
import com.example.homework.repository.ImageRepository
import com.example.homework.api.Consts.ITEMS_PER_PAGE
import com.example.homework.api.Consts.imageTypeList
import com.example.homework.db.MovieCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel(), BaseViewModel {

    private val _isException = MutableStateFlow(false)
    var isException = _isException.asStateFlow()
    var imagesCount = 0
    private val _images = MutableStateFlow<List<ImageItems>>(emptyList())
    val images = _images.asStateFlow()
    private lateinit var  movieCount: MovieCount

    private fun loadAllImagesFromNet(filmId: Int): List<ImageItems> {
        var isReady = false
        var error = false
        var imagesList = listOf<ImageItems>()
        viewModelScope.launch(Dispatchers.IO) {
            imagesList = repository.getMainImagesFromNet(filmId)
            if (imagesList.isEmpty() && repository.isException)
                error = true
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return imagesList
    }

    fun loadMainImages(filmId: Int): List<ImageItems> {
        var isReady = false
        var imagesList = listOf<ImageItems>()
        viewModelScope.launch(Dispatchers.IO) {
            val imagesFromDb = repository.getMainImagesFromDb(filmId)
            if (imagesFromDb.size == 1 && imagesFromDb[0].imageUrl == "" && imagesFromDb[0].previewUrl == "") {
                imagesCount = 0
                imagesList = emptyList()
            } else if (imagesFromDb.isNotEmpty()) {
                imagesCount = repository.getImagesCountFromDb(filmId)
                imagesList = imagesFromDb
            } else {
                val result = loadAllImagesFromNet(filmId)
                if (!_isException.value) {
                    imagesCount = repository.totalCount
                    movieCount = repository.getMovieFromMovieCountDb(filmId, repository.actionsDao)
                    if (!::movieCount.isInitialized)
                        repository.insertMovieCountToDb(filmId, 0, imagesCount, 0, 0, repository.actionsDao)
                    else {
                        repository.updateImagesCount(filmId, imagesCount)
                    }
                    if (result.isNotEmpty()) {
                        val size: Int
                        if (imagesCount > 20)
                            size = 20
                        else
                            size = imagesCount
                        for (i in 0..<size) {
                            repository.insertImageToDb(result[i], filmId)
                        }
                    } else {
                        repository.insertImageToDb(ImageItems("", ""), filmId)
                    }
                    imagesList = result
                } else
                    imagesList = emptyList()
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        return imagesList
    }

    fun getPagingDataImage(
        filmId: Int,
        isFullList: Boolean,
        imageType: String
    ): Flow<PagingData<ImageItems>> {
        val pagingMovies: Flow<PagingData<ImageItems>> = Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = { ImagePagingSource(isFullList, filmId, repository, imageType) }
        ).flow.cachedIn(viewModelScope)
        return pagingMovies
    }

    fun getInfoForChip(filmId: Int): MutableMap<String, String> {
        var error = false
        var check = 0
        val listForChip = mutableMapOf<String, String>()
        imageTypeList.forEach { key, value ->
            viewModelScope.launch(Dispatchers.IO) {
                delay(check.toLong())
                val result = repository.getTypedImages(filmId, key, 1)
                if (result.isNotEmpty())
                    listForChip[key] = value
                else if (repository.isException)
                    error = true
                check++
            }
        }
        while (check < imageTypeList.size) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(100)
            }
        }
        _isException.value = error
        return listForChip
    }
}