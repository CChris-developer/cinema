package com.example.homework.repository

import com.example.homework.api.retrofit
import com.example.homework.db.ActionsDao
import com.example.homework.db.Images
import com.example.homework.models.ImageItems
import com.example.homework.models.ImageList
import com.example.homework.repository.BaseRepository
import java.lang.Exception
import javax.inject.Inject

class ImageRepository @Inject constructor (val actionsDao: ActionsDao) : BaseRepository {
    var totalCount = 0
    private var totalPages = 0
    override var isException = false
    lateinit var result: ImageList

    suspend fun getMainImagesFromNet(id: Int): List<ImageItems> {
        try {
            val result = retrofit.getImagesType(id, "", 1)
            totalCount = result.total
            totalPages = result.totalPages
            return result.items
        } catch (e: Exception) {
            isException = true
            return emptyList()
        }
    }

    fun getMainImagesFromDb(id: Int): List<ImageItems> {
        return actionsDao.getImagesFromDb(id)
    }

    suspend fun insertImageToDb(image: ImageItems, movieId: Int) {
        actionsDao.insertImage(
            Images(
                id = 0,
                movieId = movieId,
                imageUrl = image.imageUrl,
                previewUrl = image.previewUrl
            )
        )
    }

    suspend fun updateImagesCount(id: Int, count: Int) {
        actionsDao.updateImagesCount(id, count)
    }

    fun getImagesCountFromDb(id: Int): Int {
        return actionsDao.getImagesCountFromDb(id)
    }

    suspend fun getTypedImages(id: Int, imageType: String, page: Int): List<ImageItems> {
        try {
            when (imageType) {
                "SHOOTING" -> result = retrofit.getImagesType(id, imageType, page)
                "STILL" -> result = retrofit.getImagesType(id, imageType, page)
                "POSTER" -> result = retrofit.getImagesType(id, imageType, page)
                "FAN_ART" -> result = retrofit.getImagesType(id, imageType, page)
                "PROMO" -> result = retrofit.getImagesType(id, imageType, page)
                "CONCEPT" -> result = retrofit.getImagesType(id, imageType, page)
                "WALLPAPER" -> result = retrofit.getImagesType(id, imageType, page)
                "COVER" -> result = retrofit.getImagesType(id, imageType, page)
                "SCREENSHOT" -> result = retrofit.getImagesType(id, imageType, page)
            }
            return result.items
        } catch (e: Exception) {
            isException = true
            return emptyList()
        }
    }
}