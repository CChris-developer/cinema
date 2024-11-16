package com.example.homework.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.repository.SerialRepository
import com.example.homework.api.Utils.serialList
import com.example.homework.models.Episodes
import com.example.homework.models.Items
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SerialViewModel @Inject constructor(private val repository: SerialRepository) : ViewModel(), BaseViewModel {

    private val _seasonsCount = MutableStateFlow(0)
    val seasonsCount = _seasonsCount.asStateFlow()

    private val _serialsCount = MutableStateFlow(0)
    val serialsCount = _serialsCount.asStateFlow()

    private val _isException = MutableStateFlow<Boolean>(false)
    var isException = _isException.asStateFlow()


    fun getSeasonsSerialsCount(id: Int): String {
        var error = false
        var count = 0
        var resultCount = ""
        var isReady = false
        viewModelScope.launch(Dispatchers.IO) {
            val seasons = repository.getSeasonsFromDb(id)
            if (seasons.isNotEmpty()) {
                val seasonsMaxValue = seasons.maxOrNull() ?: 0
                resultCount = "${seasonsMaxValue}&${seasons.size}"
            } else {
                serialList = emptyList()
                val response = repository.getSeasonsFromNet(id)
                Log.d("reponse", response.toString())
                if (response != null) {
                    val resultFromNet = response
                    _seasonsCount.value = resultFromNet.total
                    resultFromNet.items.forEach {
                        count = count + it.episodes.size
                    }
                    _serialsCount.value = count
                    resultCount = "${resultFromNet.total}&${count}"
                    serialList = resultFromNet.items
                } else if (repository.isException)
                    error = true
            }
            isReady = true
        }
        while (!isReady) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        _isException.value = error
        return resultCount
    }

    fun getSerialsInfo(id: Int): List<Items> {
        var error = false
        var count = 0
        var resultCount = ""
        var isReady = false
        var resultList = listOf<Items>()
        viewModelScope.launch(Dispatchers.IO) {
            var i = 1
            var episodes = mutableListOf<Episodes>()
            val items = mutableListOf<Items>()
            val resultFromDb = repository.getSerialsCountFromDb(id)
            if (resultFromDb.isNotEmpty()) {
                val serialsListSize = resultFromDb.size
                var j = 0
                resultFromDb.forEach { serial ->
                    j++
                    if (serial.seasonNumber == i) {
                        episodes.add(
                            Episodes(
                                serial.seasonNumber,
                                serial.episodeNumber,
                                serial.nameRu,
                                serial.nameEn,
                                serial.synopsis,
                                serial.releaseDate
                            )
                        )
                        if (j >= serialsListSize) {
                            items.add(Items(i, episodes))
                        }
                    } else if (serial.seasonNumber > i) {
                        items.add(Items(i, episodes))
                        episodes = emptyList<Episodes>().toMutableList()
                        i++
                        episodes.add(
                            Episodes(
                                serial.seasonNumber,
                                serial.episodeNumber,
                                serial.nameRu,
                                serial.nameEn,
                                serial.synopsis,
                                serial.releaseDate
                            )
                        )
                        if (j >= serialsListSize) {
                            items.add(Items(i + 1, episodes))
                        }
                    }
                }
                resultList = items
            } else if (serialList.isNotEmpty()) {
                serialList.forEach {
                    it.episodes.forEach { episode ->
                        repository.insertSerial(
                            id,
                            episode.seasonNumber,
                            episode.episodeNumber,
                            episode.nameRu ?: "",
                            episode.nameEn ?: "",
                            episode.synopsis ?: "",
                            episode.releaseDate ?: ""
                        )
                    }
                    Log.d("isready", "isReady")
                }
                resultList = serialList
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
}