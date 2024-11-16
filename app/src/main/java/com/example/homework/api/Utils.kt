package com.example.homework.api

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.homework.models.ImageItems
import com.example.homework.api.Consts.months
import com.example.homework.api.Consts.monthsInRussion
import com.example.homework.models.ActorInfo
import com.example.homework.models.Country
import com.example.homework.models.Genre
import com.example.homework.models.Items
import com.example.homework.models.Movie


object Utils {

    val Context.dataStore by preferencesDataStore("settings")
    var genreList = mutableListOf<Genre>()
    var countryList = mutableListOf<Country>()
    var serialList = emptyList<Items>()
    var podborka1 = false
    var fragmentsList = mutableListOf<String>()
    var movieTransitionsCount = 0
    var randomMoviesListHeader = mutableListOf<String>()
    var randomMoviesRequest = mutableListOf<List<String>>()

    fun createListFromString(str: String, className: String) {
        val getStr = str.split("#")
        getStr.forEach { item1 ->
            if (item1 != "") {
                val getItems = item1.split("&")
                if (className == "Genre")
                    genreList.add(Genre(getItems[1].toInt(), getItems[0]))
                else if (className == "Country")
                    countryList.add(Country(getItems[1].toInt(), getItems[0]))
            }
        }
    }

    fun getSeasonsCount(count: Int): String {
        val seasons: String = when (count) {
            1 -> "$count сезон"
            in 2..4 -> "$count сезона"
            else -> "$count сезонов"
        }
        return seasons
    }

    fun getSerialsCount(count: Int): String {
        val serialsString = count.toString()
        val lastNumber = serialsString.substring(serialsString.length - 1).toInt()
        return if (count in 5..20)
            "$count серий"
        else if (lastNumber == 1)
            "$count серия"
        else if (lastNumber in 2..4)
            "$count серии"
        else
            "$count серий"
    }

    fun formatDate(date: String): String {
        val arr = date.split("-")
        val year = arr[0]
        val month = monthsInRussion[arr[1]]
        val day = arr[2]
        return ("$day $month $year")

    }

    fun formatCountryName(str: String): String {
        val lettersArray = TextUtils.split(str, " ")
        var replaced: String
        var totalReplaced = ""
        if (lettersArray.size > 1) {
            if (lettersArray[0].contains("[а-яА-Я]+(ея)".toRegex()) || lettersArray[0].contains("[а-яА-Я]+(ия)".toRegex())) {
                val placesChange = lettersArray[0]
                lettersArray[0] = lettersArray[1]
                lettersArray[1] = placesChange
            }
            for (i in lettersArray.indices) {
                replaced = if (lettersArray[i].contains("[а-яА-Я]+(ая)".toRegex()))
                    lettersArray[i].replace("ая", "ой")
                else if (lettersArray[i].contains("[а-яА-Я]+(ия)".toRegex()))
                    lettersArray[i].replace("ия", "ии")
                else if (lettersArray[i].contains("[а-яА-Я]+(ея)".toRegex()))
                    lettersArray[i].replace("ея", "еи")
                else if (lettersArray[i].contains("[а-яА-Я]+(ие)".toRegex()))
                    lettersArray[i].replace("ие", "их")
                else if (lettersArray[i].contains("[а-яА-Я]+(ва)$".toRegex()))
                    lettersArray[i].replace("ва", "вов")
                else
                    lettersArray[i]
                totalReplaced = "$totalReplaced $replaced"
            }
        } else {
            if (lettersArray[0].contains("[а-яА-Я]+(я)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "и"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+(ша)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "и"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+[днтб]а$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "ы"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+[бвгджзклмнпрстфхцчшщ]$".toRegex()))
                totalReplaced = lettersArray[0] + "а"
            else if (lettersArray[0].contains("[а-яА-Я]+(й)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "я"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+(сь)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "и"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+(ь)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "я"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+(ды)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    "ов"
                )
            else if (lettersArray[0].contains("[а-яА-Я]+(ны)$".toRegex()))
                totalReplaced = lettersArray[0].replaceRange(
                    lettersArray[0].length - 1,
                    lettersArray[0].length,
                    ""
                )
            else
                totalReplaced = lettersArray[0]
        }
        return totalReplaced
    }

    fun getProfession(professionKey: String): String {
        val profession: String
        when (professionKey) {
            "PRODUCER" -> {
                profession = "Продюсер"
            }
            "DIRECTOR" -> {
                profession = "Режиссер"
            }
            "WRITER" -> {
                profession = "Сценарист"
            }
            "COMPOSER" -> {
                profession = "Композитор"
            }
            "DESIGN" -> {
                profession = "Художник"
            }
            "OPERATOR" -> {
                profession = "Оператор"
            }
            else -> profession = professionKey
        }
        return profession
    }

    fun getDateForPremieres(date: String): String {
        var year: Int
        val month: String
        val monthId: Int
        val result = TextUtils.split(date, "/")
        year = result[2].toInt()
        if (result[1].toInt() == 2) {
            monthId = if (result[0].toInt() > 22)
                result[1].toInt() + 1
            else
                result[1].toInt()
        } else if (result[1].toInt() == 4 || result[1].toInt() == 6 || result[1].toInt() == 9 || result[1].toInt() == 11) {
            monthId = if (result[0].toInt() > 24)
                result[1].toInt() + 1
            else
                result[1].toInt()
        } else if (result[1].toInt() == 1 || result[1].toInt() == 3 || result[1].toInt() == 5 || result[1].toInt() == 7 || result[1].toInt() == 8 || result[1].toInt() == 10) {
            monthId = if (result[0].toInt() > 25)
                result[1].toInt() + 1
            else
                result[1].toInt()
        } else {
            if (result[0].toInt() > 25) {
                monthId = 1
                year += 1
            } else
                monthId = result[1].toInt()
        }
        month = months[monthId].toString()
        val str = "$year-$month"
        return (str)
    }


    fun onMovieItemClick(movie: Movie, frag: Fragment, resId: Int) {
        var str: String
        val fragString = frag.toString()
        if (fragString.contains("HomepageFragment"))
            str = "HomepageFragment&"
        else if (fragString.contains("MovieFragment"))
            str = "MovieFragment&"
        else if (fragString.contains("ActorInfoFragment"))
            str = "ActorInfoFragment&"
        else if (fragString.contains("FilmographyFragment"))
            str = "FilmographyFragment&"
        else if (fragString.contains("SearchFragment"))
            str = "SearchFragment&"
        else if (fragString.contains("ProfileFragment"))
            str = "ProfileFragment&"
        else if (fragString.contains("AllMoviesFragment"))
            str = "AllMoviesFragment&"
        else if (fragString.contains("AllImagesFragment"))
            str = "AllImagesFragment&"
        else
            str = "&"
        str = "$str${movie.kinopoiskId}&false"
        val bundle = Bundle().apply {
            putString("param1", str)
        }
        findNavController(frag).navigate(resId, args = bundle)
    }

    fun onActorItemClick(id: ActorInfo, frag: Fragment, resId: Int) {
        var str: String
        val fragString = frag.toString()
        str = if (fragString.contains("MovieFragment"))
            "MovieFragment&"
        else if (fragString.contains("AllActorsFragment"))
            "AllActorsFragment&"
        else
            "&"
        str = "$str${id.staffId}&false"
        val bundle = Bundle().apply {
            putString("param1", str)
        }
        findNavController(frag).navigate(resId, args = bundle)
    }

    fun onImageItemClick(id: Int, imageUrl: ImageItems, frag: Fragment, resId: Int) {
        var str: String
        val fragString = frag.toString()
        str = if (fragString.contains("MovieFragment"))
            "MovieFragment&"
        else if (fragString.contains("AllImagesFragment"))
            "AllImagesFragment&"
        else
            "&"
        str = "$str${imageUrl.previewUrl}&$id"
        val bundle = Bundle().apply {
            putString("param1", str)
        }
        findNavController(frag).navigate(resId, args = bundle)
    }

    fun getSavedId(): String {
        var idFromList = ""
        if (fragmentsList.size >= 2) {
            val fragBeforeLatest = fragmentsList[fragmentsList.size - 2].split("&")
            idFromList = fragBeforeLatest[1]
            val newList = mutableListOf<String>()
            for (i in 0..<fragmentsList.size-1) {
                newList.add(fragmentsList[i])
            }
            fragmentsList = newList
        }
       return idFromList
    }

    fun getSavedFrag(): String {
        var fragFromList = ""
        if (fragmentsList.isNotEmpty()) {
            val latestFrag = fragmentsList[fragmentsList.size - 1].split("&")
            fragFromList = latestFrag[0]
        }
        return fragFromList
    }
}