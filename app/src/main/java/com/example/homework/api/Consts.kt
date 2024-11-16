package com.example.homework.api

object Consts {
    const val MOVIE_URL = "https://www.imdb.com/title/"
    const val BASE_URL = "https://kinopoiskapiunofficial.tech/"
    const val ARG_PARAM1 = "param1"
    const val ITEMS_PER_PAGE = 20
    const val FIRST_PAGE = 1
    const val ARG_SERIAL = "serial_info"
    const val COUNTRY_SEARCH = "country"
    const val GENRE_SEARCH = "genre"
    const val COUNTRY_SEARCH_STRING = "country_string"
    const val GENRE_SEARCH_STRING = "genre_string"
    const val ORDER = "order"
    const val TYPE = "type"
    const val RATING_FROM = "rating_from"
    const val RATING_TO = "rating_to"
    const val YEAR_FROM = "year_from"
    const val YEAR_TO = "year_to"
    const val VIEWED_STATE = "viewed_state"
    const val FRAGMENT_NAME = "frag"
    const val FRAGMENT_NAME_ACTOR_INFO = "frag_actor_info"
    const val CHANGED_SETTINGS = "changed_settings"
    const val MOVIE_ID = "movie_id"
    const val FRAGMENTS_LIST = "fragments_list"
    const val ACTOR_ID = "actor_id"
    const val MOVIE_COLLECTION = "movie_collection"
    const val SHARE_DIALOG = "SHARE_DIALOG"
    const val REQUEST_KEY = "requestKey"
    const val BUNDLE_KEY = "bundleKey"

    val months = mapOf(
        1 to "JANUARY",
        2 to "FEBRUARY",
        3 to "MARCH",
        4 to "APRIL",
        5 to "MAY",
        6 to "JUNE",
        7 to "JULY",
        8 to "AUGUST",
        9 to "SEPTEMBER",
        10 to "OCTOBER",
        11 to "NOVEMBER",
        12 to "DECEMBER"
    )

    val genres = mapOf(
        "триллер" to "Триллеры",
        "драма" to "Драмы",
        "криминал" to "Криминальные фильмы",
        "мелодрама" to "Мелодрамы",
        "детектив" to "Детективы",
        "фантастика" to "Фантастика",
        "приключения" to "Приключения",
        "биография" to "Биографические фильмы",
        "фильм-нуар" to "Фильмы-нуар",
        "вестерн" to "Вестерны",
        "боевик" to "Боевики",
        "фэнтези" to "Фэнтези",
        "комедия" to "Комедии",
        "военный" to "Военные фильмы",
        "история" to "Исторические фильмы",
        "музыка" to "Музыка",
        "ужасы" to "Фильмы ужасов",
        "мультфильм" to "Мультфильмы",
        "семейный" to "Семейные фильмы",
        "мюзикл" to "Мюзиклы",
        "спорт" to "Спортивные фильмы",
        "документальный" to "Документальные фильмы",
        "короткометражка" to "Короткометражки",
        "анимэ" to "Анимэ",
        "новости" to "Новости",
        "концерт" to "Концерты",
        "церемония" to "Церемонии",
        "реальное TV" to "Реальное ТВ",
        "игра" to "Игры",
        "ток-шоу" to "Ток-шоу",
        "детский" to "Детские фильмы"
    )

    val monthsInRussion = mapOf(
        "01" to "января",
        "02" to "февраля",
        "03" to "марта",
        "04" to "апреля",
        "05" to "мая",
        "06" to "июня",
        "07" to "июля",
        "08" to "августа",
        "09" to "сентября",
        "10" to "октября",
        "11" to "ноября",
        "12" to "декабря"
    )

    val imageTypeList = mapOf(
        "SHOOTING" to "Со съемок",
        "STILL" to "Кадры из фильма",
        "POSTER" to "Постеры",
        "FAN_ART" to "Фан-Арты",
        "PROMO" to "Промо",
        "CONCEPT" to "Концепт-Арты",
        "WALLPAPER" to "Обои",
        "COVER" to "Обложки",
        "SCREENSHOT" to "Скриншоты"
    )

    val periodList = listOf(listOf(1987, 1998), listOf(1998, 2009), listOf(2010, 2021))

    val genresIdMap = mapOf(
        1 to "триллер",
        2 to "драма",
        3 to "криминал",
        4 to "мелодрама",
        5 to "детектив",
        6 to "фантастика",
        7 to "приключения",
        8 to "биография",
        11 to "боевик",
        12 to "фэнтези",
        13 to "комедия",
        14 to "военный",
        15 to "история",
        17 to "ужасы",
        18 to "мультфильм",
        19 to "семейный",
        33 to "детский"
    )
    val countriesIdMap = mapOf(
        1 to "США",
        3 to "Франция",
        5 to "Великобритания",
        7 to "Индия",
        8 to "Испания",
        9 to "Германия",
        10 to "Италия",
        13 to "Австралия",
        14 to "Канада",
        16 to "Япония",
        21 to "Китай",
        33 to "СССР",
        34 to "Россия",
        44 to "Турция",
        49 to "Южная Корея",
        105 to "Армения",
        111 to "Грузия",
        235 to "Белоруссия"
    )
    val countriesList = listOf(1, 3, 5, 7, 8, 9, 10, 13, 14, 16, 21, 33, 34, 44, 49, 105, 111, 235)
    val genresList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 17, 18, 19, 33)
    val countryMap = mapOf(1 to "США", 3 to "Франция", 5 to "Великобритания", 9 to "Германия", 34 to "Россия")
    val genreMap = mapOf(2 to "Драма", 4 to "Мелодрама", 10 to "Вестерн", 11 to "Боевик", 13 to "Комедия")
}