package com.example.homework.repository

import com.example.homework.api.retrofit
import com.example.homework.db.ActionsDao
import com.example.homework.db.Serials
import com.example.homework.models.Seasons
import com.example.homework.repository.BaseRepository
import java.lang.Exception
import javax.inject.Inject

class SerialRepository @Inject constructor (val actionsDao: ActionsDao) : BaseRepository {

    override var isException = false

    suspend fun getSeasonsFromNet(id: Int): Seasons? {
        try {
            val result = retrofit.seasons(id)
            return result
        } catch (e: Exception) {
            isException = true
            return null
        }
    }

    fun getSeasonsFromDb(id: Int): List<Int> {
        return actionsDao.getSeasonsFromDb(id)
    }

    fun getSerialsCountFromDb(id: Int): List<Serials> {
        return actionsDao.getSerialsCountFromDb(id)
    }

    suspend fun insertSerial(
        movieId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        nameRu: String,
        nameEn: String,
        synopsis: String,
        releaseDate: String
    ) {
        actionsDao.insertSerial(
            Serials(
                id = 0,
                movieId = movieId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                nameRu = nameRu,
                nameEn = nameEn,
                synopsis = synopsis,
                releaseDate = releaseDate
            )
        )
    }
}