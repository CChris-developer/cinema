package com.example.homework.movies

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.actors.ActorAdapter
import com.example.homework.api.Consts
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.MOVIE_ID
import com.example.homework.api.Consts.MOVIE_URL
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.fragmentsList
import com.example.homework.api.Utils.getSavedFrag
import com.example.homework.api.Utils.getSavedId
import com.example.homework.api.Utils.getSeasonsCount
import com.example.homework.api.Utils.getSerialsCount
import com.example.homework.api.Utils.movieTransitionsCount
import com.example.homework.api.Utils.onActorItemClick
import com.example.homework.api.Utils.onImageItemClick
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentMovieBinding
import com.example.homework.db.App
import com.example.homework.images.ImageAdapter
import com.example.homework.models.Country
import com.example.homework.models.Genre
import com.example.homework.models.Movie
import com.example.homework.viewmodel.ActorViewModel
import com.example.homework.viewmodel.ImageListViewModel
import com.example.homework.viewmodel.SerialViewModel
import com.example.homework.viewmodel.MovieListViewModel
import com.example.homework.viewmodel.MovieViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private var param1: String? = null
    private var _binding: FragmentMovieBinding? = null
    private val binding: FragmentMovieBinding
        get() = _binding!!

    private val savedMovieId = intPreferencesKey(MOVIE_ID)
    private val savedFrag = stringPreferencesKey(FRAGMENT_NAME)
    var movieId = 0
    private var frag = ""
    private var movieI = 0
    private lateinit var movieInfo: Movie
    private var movieName = ""
    private var passedValues = listOf<String>()

    private val movieAdapter =
        MovieAdapter { movie -> onMovieItemClick(movie, this@MovieFragment, R.id.movieFragment2) }
    private val actorAdapter = ActorAdapter { actor ->
        onActorItemClick(
            actor,
            this@MovieFragment,
            R.id.action_movieFragment2_to_actorInfoFragment
        )
    }
    private val actorAdapter1 = ActorAdapter { actor ->
        onActorItemClick(
            actor,
            this@MovieFragment,
            R.id.action_movieFragment2_to_actorInfoFragment
        )
    }

    private val viewModel: MovieListViewModel by viewModels()
    private val imageViewModel: ImageListViewModel by viewModels()
    private val actorViewModel: ActorViewModel by viewModels()
    private val movieDetailViewModel: SerialViewModel by viewModels()
    private val viewModelDB: MovieViewModel by viewModels()
    private var ageLimit = ""
    private val blue = Color.rgb(55, 0, 179)
    private val white = Color.rgb(255, 255, 255)

    private suspend fun saveInfo(movieId: Int, frag: String) {
        context?.dataStore?.edit { settings ->
            settings[savedMovieId] = movieId
            settings[savedFrag] = frag
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreMovieId = it[savedMovieId]
                if (dataStoreMovieId != null) {
                    movieId = dataStoreMovieId
                }
                val dataStoreFrag = it[savedFrag]
                if (dataStoreFrag != null) {
                    frag = dataStoreFrag
                }
            }
        }
    }

    private fun onChangeStateButton(
        isClicked: StateFlow<Boolean>,
        button: ImageButton,
        isViewedButton: Boolean
    ) {
        var isImageResourceChanged = false
        viewLifecycleOwner.lifecycleScope.launch {
            isClicked
                .collect {
                    if (it) {
                        if (isViewedButton) {
                            button.setImageResource(R.drawable.viewed)
                            isImageResourceChanged = true
                        }
                        button.setColorFilter(blue)
                    } else {
                        if (isImageResourceChanged)
                            button.setImageResource(R.drawable.not_viewed)
                        button.setColorFilter(white)
                    }
                }
        }
    }

    private fun idIsNull() {
        Snackbar.make(
            requireView(),
            R.string.movie_load_error,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun getGenreString(list: List<Genre>): String {
        var i = 0
        var genres = ""
        val length = list.size
        list.forEach {
            if (i != length - 1)
                genres = genres + it.genre + ", "
            else
                genres += it.genre
            i++
        }
        return genres
    }

    private fun getCountryString(list: List<Country>): String {
        var i = 0
        var countries = ""
        val length = list.size
        list.forEach {
            if (i != length - 1)
                countries = countries + it.country + ", "
            else
                countries += it.country
            i++
        }
        return countries
    }

    private fun getView(movieName: String, id: Int, numberSeasons: String, frag: String) {
        binding.movieRating.text = getString(
            R.string.rating,
            movieInfo.ratingKinopoisk.toString(),
            movieName
        )
        binding.movieGenre.text = getString(
            R.string.genre,
            movieInfo.year,
            getGenreString(movieInfo.genres),
            numberSeasons
        )
        if (movieInfo.ratingAgeLimits != "")
            ageLimit = movieInfo.ratingAgeLimits.substring(3) + "+"
        binding.movieLength.text = getString(
            R.string.movie_length,
            getCountryString(movieInfo.countries),
            movieInfo.filmLength,
            ageLimit
        )
        Glide
            .with(requireContext())
            .load(movieInfo.posterUrl)
            .into(binding.poster)
        binding.back.setOnClickListener {
            if (movieTransitionsCount >= 3) {
                    val firstItem = fragmentsList[0].split("&")
                    if (firstItem[0] == "SearchFragment")
                        findNavController().navigate(R.id.action_movieFragment2_to_searchFragment)
                    else if (firstItem[0] == "ProfileFragment")
                        findNavController().navigate(R.id.action_movieFragment2_to_profileFragment)
                    else
                        findNavController().navigate(R.id.action_movieFragment2_to_homepageFragment)
            }
            else {
                val fragFromList = getSavedFrag()
                if (fragFromList == "ActorInfoFragment") {
                    val bundle1 = Bundle().apply {
                        putString(Consts.ARG_PARAM1, "MovieFragment&${getSavedId()}&true")
                    }
                    findNavController().navigate(
                        R.id.action_movieFragment2_to_actorInfoFragment,
                        bundle1
                    )
                } else if (fragFromList == "FilmographyFragment") {
                    val bundle2 = Bundle().apply {
                        putString(Consts.ARG_PARAM1, "${getSavedId()}&true")
                    }
                    findNavController().navigate(
                        R.id.action_movieFragment2_to_filmographyFragment,
                        bundle2
                    )
                }
                else if (fragFromList == "SearchFragment")
                    findNavController().navigate(R.id.action_movieFragment2_to_searchFragment)
                else if (fragFromList == "ProfileFragment")
                    findNavController().navigate(R.id.action_movieFragment2_to_profileFragment)
                else if (fragFromList == "AllMoviesFragment") {
                    val bundle3 = Bundle().apply {
                        putString(Consts.ARG_PARAM1, "MovieFragment&${getSavedId()}&true")
                    }
                    findNavController().navigate(
                        R.id.action_movieFragment2_to_allMoviesFragment2,
                        bundle3
                    )
                }
                else
                    findNavController().navigate(R.id.action_movieFragment2_to_homepageFragment)
            }
        }
        binding.addToFavourite.setOnClickListener {
            if (id != 0) {
                viewModelDB.onChangeFavouriteState(id)
                onChangeStateButton(viewModelDB.isFavourite, binding.addToFavourite, false)

            } else
                idIsNull()
        }
        binding.wantToSee.setOnClickListener {
            if (id != 0) {
                viewModelDB.onChangeWantToSeeState(id)
                onChangeStateButton(viewModelDB.wantToSee, binding.wantToSee, false)
            } else
                idIsNull()
        }
        binding.viewed.setOnClickListener {
            if (id != 0) {
                viewModelDB.onChangeViewedState(id)
                onChangeStateButton(viewModelDB.viewed, binding.viewed, true)
            } else
                idIsNull()
        }

        binding.share.setOnClickListener {
            if (id != 0)
                ShareDialogFragment(MOVIE_URL + "${id}/").show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            else
                idIsNull()
        }

        val data = arrayListOf(
            movieInfo.ratingKinopoisk.toString(),
            movieInfo.posterUrlPreview,
            movieName,
            movieInfo.year.toString(),
            getGenreString(movieInfo.genres),
            id.toString()
        )

        binding.optionalMenu.setOnClickListener {
            val modalBottomSheet = ModalBottomSheetFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("list", data)
            modalBottomSheet.arguments = bundle
            modalBottomSheet.show(
                // requireActivity().supportFragmentManager,
                childFragmentManager,
                "modalBottomSheet"
            )
        }
    }

    private fun transitionToAllActorsFragment(id: Int, person: String, rating: Float) {
        val bundle = Bundle().apply {
            putString(ARG_PARAM1, "$id%$person%$rating%false")
        }
        findNavController().navigate(R.id.action_movieFragment2_to_allActorsFragment, args = bundle)
    }

    private fun fillLayout(
        textviewNumber: TextView,
        imageViewMore: ImageView,
        id: Int,
        person: String,
        rating: Float
    ) {
        textviewNumber.isVisible = true
        imageViewMore.isVisible = true
        textviewNumber.setOnClickListener {
            viewModel.isFromAllActorsFragment = true
            transitionToAllActorsFragment(id, person, rating)
        }
        if (person == "actor")
            textviewNumber.text = actorViewModel.actorsCount.toString()
        else
            textviewNumber.text = actorViewModel.workersCount.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(Consts.ARG_PARAM1)
        }
        val handler = CoroutineExceptionHandler {
                _, _ ->  ShowAlert(
            getString(R.string.mistake)).show(
            childFragmentManager,
            SHARE_DIALOG
        )
        }
        viewLifecycleOwner.lifecycleScope.launch(handler) {
            var i = 0
                binding.progressBarFrameLayout.visibility = View.VISIBLE
                binding.constraintLayout.visibility = View.GONE
                if (param1 == null) {
                    readSavedSettings()
                } else {
                    passedValues = param1!!.split("&")
                    frag = passedValues[0]
                    movieId = passedValues[1].toInt()
                    val isBack = passedValues[2].toBoolean()
                    if (!isBack)
                    fragmentsList.add("$frag&${movieId}")
                    else
                        movieTransitionsCount += 1
                    saveInfo(movieId, frag)
                }
                while (movieId == 0) {
                    delay(300)
                }
                movieInfo = viewModelDB.getMovieInfo(movieId)
                if (movieInfo.kinopoiskId != 0) {
                    val id = movieInfo.kinopoiskId
                    movieI = id
                    if (movieInfo.nameOriginal.isNullOrEmpty())
                        movieName = movieInfo.nameRu
                    else
                        movieName = movieInfo.nameOriginal

                    val shortDescription = movieInfo.shortDescription
                    val fullDescription = movieInfo.description
                    if (fullDescription != null) {
                        val fullDescriptionLength = fullDescription.length
                        var str: String
                        if (fullDescriptionLength > 250) {
                            str = fullDescription.substring(0, 250)
                            str = "$str..."
                        } else
                            str = fullDescription
                        binding.fullDescription.filters = arrayOf<InputFilter>(LengthFilter(253))
                        binding.fullDescription.text = str
                        binding.fullDescription.setOnClickListener {
                            if (binding.fullDescription.text.length == 253) {
                                binding.fullDescription.filters =
                                    arrayOf<InputFilter>(LengthFilter(fullDescriptionLength))
                                binding.fullDescription.text = fullDescription
                            } else {
                                binding.fullDescription.filters =
                                    arrayOf<InputFilter>(LengthFilter(253))
                                binding.fullDescription.text = str
                            }
                        }
                    } else
                        binding.fullDescription.text = getString(R.string.no_description)
                    if (shortDescription.isNullOrEmpty())
                        binding.shortDescription.visibility = View.GONE
                    else {
                        binding.shortDescription.visibility = View.VISIBLE
                        binding.shortDescription.text = shortDescription
                    }
                    if (!movieInfo.serial) {
                        binding.movieLength.visibility = View.VISIBLE
                        binding.movieGenre.visibility = View.VISIBLE
                        binding.movieRating.visibility = View.VISIBLE
                        binding.panel.visibility = View.VISIBLE
                        getView(movieName, id, "", frag)
                    } else {
                        i = 0
                        val seasonsInfo = movieDetailViewModel.getSeasonsSerialsCount(id)
                        if (seasonsInfo != "") {
                            val resultCount = seasonsInfo.split("&")
                            var serialsNumber = ""
                            val seasonsCount = resultCount[0].toInt()
                            val serialsCount = resultCount[1].toInt()
                            val numberSeasons = getSeasonsCount(seasonsCount)
                            val seasonsString = ", ${numberSeasons}"
                            val seasonsSerials = "$numberSeasons, ${getSerialsCount(serialsCount)}"
                            binding.movieLength.visibility = View.VISIBLE
                            binding.movieGenre.visibility = View.VISIBLE
                            binding.movieRating.visibility = View.VISIBLE
                            binding.panel.visibility = View.VISIBLE
                            getView(movieName, id, seasonsString, frag)
                            binding.serials.visibility = View.VISIBLE
                            binding.serialsInfo.text = seasonsSerials

                            binding.allSerials.setOnClickListener {
                                val str = "$movieId&$movieName&$seasonsCount"
                                val bundle = Bundle().apply {
                                    putString(ARG_PARAM1, str)
                                }
                                findNavController().navigate(
                                    R.id.action_movieFragment2_to_serialsFragment,
                                    args = bundle
                                )
                            }
                        }
                    }
                    val imageAdapter = ImageAdapter { imageUrl ->
                        onImageItemClick(
                            movieId,
                            imageUrl,
                            this@MovieFragment,
                            R.id.action_movieFragment2_to_fullImageFragment
                        )
                    }
                    binding.galleryRecycler.adapter = imageAdapter
                    val imagesList = imageViewModel.loadMainImages(movieId)
                    if (imagesList.isEmpty()) {
                        binding.noImages.visibility = View.VISIBLE
                    } else {
                        binding.noImages.visibility = View.GONE
                        imageAdapter.setData(imagesList)
                        if (imageViewModel.imagesCount > 20) {
                            binding.galleryNumber.isVisible = true
                            binding.moreGallery.isVisible = true
                            binding.galleryNumber.text = imageViewModel.imagesCount.toString()
                            binding.galleryNumber.setOnClickListener {
                                val bundle = Bundle().apply {
                                    putString(ARG_PARAM1, "$id&false")
                                }
                                findNavController().navigate(
                                    R.id.action_movieFragment2_to_allImagesFragment,
                                    bundle
                                )
                            }
                        }
                    }
                    binding.relatedMoviesRecycler.adapter = movieAdapter
                    val relatedMoviesList = viewModelDB.loadRelatedMovies(movieId)
                    if (relatedMoviesList.isEmpty()) {
                        binding.noRelatedMovies.visibility = View.VISIBLE
                    } else {
                        binding.noRelatedMovies.visibility = View.INVISIBLE
                        movieAdapter.setData(relatedMoviesList)
                        if (viewModelDB.relatedMoviesCount > 20) {
                            binding.relatedMoviesNumber.isVisible = true
                            binding.relatedMoviesMore.isVisible = true
                            binding.relatedMoviesNumber.text =
                                viewModelDB.relatedMoviesCount.toString()
                            binding.relatedMoviesNumber.setOnClickListener {
                                val bundle = Bundle().apply {
                                    putString(ARG_PARAM1, "MovieFragment&%$id&false")
                                }
                                findNavController().navigate(
                                    R.id.action_movieFragment2_to_allMoviesFragment2,
                                    bundle
                                )
                            }
                        }
                    }

                    val personInMovieList =
                        actorViewModel.loadActors(movieId, movieInfo.ratingKinopoisk)
                    binding.actorsRecycler.adapter = actorAdapter
                    binding.workersRecycler.adapter = actorAdapter1
                    actorAdapter1.areWorkers = true
                    if (personInMovieList.isNotEmpty()) {
                        val actorsList = personInMovieList.filter { it.professionKey == "ACTOR" }
                            .distinctBy { it.staffId }
                        val workersList =
                            personInMovieList.filter { it.professionKey != "ACTOR" && it.professionKey != "HIMSELF" && it.professionKey != "HERSELF" }
                                .distinctBy { it.staffId }
                        if (actorsList.isNotEmpty()) {
                            binding.noActorsRecyclerTextview.visibility = View.GONE
                            actorAdapter.setData(actorsList)
                        } else {
                            binding.noActorsRecyclerTextview.visibility = View.VISIBLE
                        }
                        if (workersList.isNotEmpty()) {
                            binding.noWorkersRecyclerTextview.visibility = View.GONE
                            actorAdapter1.setData(workersList)
                        } else {
                            binding.noWorkersRecyclerTextview.visibility = View.VISIBLE
                        }
                        if (actorViewModel.actorsCount > 20)
                            fillLayout(
                                binding.actorsNumber,
                                binding.moreActors,
                                id,
                                "actor",
                                movieInfo.ratingKinopoisk
                            )
                        if (actorViewModel.workersCount > 6)
                            fillLayout(
                                binding.workersNumber,
                                binding.moreWorkers,
                                id,
                                "worker",
                                movieInfo.ratingKinopoisk
                            )
                    } else {
                        binding.noActorsRecyclerTextview.visibility = View.VISIBLE
                        binding.noWorkersRecyclerTextview.visibility = View.VISIBLE
                    }
                    binding.progressBarFrameLayout.visibility = View.GONE
                    binding.constraintLayout.visibility = View.VISIBLE
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModelDB.isException.collect {
                if (it) {
                    binding.progressBarFrameLayout.visibility = View.GONE
                    ShowAlert(getString(R.string.loading_error)).show(
                        childFragmentManager,
                        SHARE_DIALOG
                    )
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            while (movieI == 0) {
                delay(300)
                if (viewModelDB.isException.value)
                    break
            }
            if (movieI != 0) {
                viewModelDB.favouriteObserv(movieI).collect {
                    if (it)
                        binding.addToFavourite.setColorFilter(blue)
                    else
                        binding.addToFavourite.setColorFilter(white)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            while (movieI == 0) {
                delay(300)
                if (viewModelDB.isException.value)
                    break
            }
            if (movieI != 0) {
                viewModelDB.wantToSeeObserv(movieI).collect {
                    if (it)
                        binding.wantToSee.setColorFilter(blue)
                    else
                        binding.wantToSee.setColorFilter(white)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            while (movieI == 0) {
                delay(300)
                if (viewModelDB.isException.value)
                    break
            }
            if (movieI != 0) {
                viewModelDB.viewedObserv(movieI).collect {
                    if (it) {
                        binding.viewed.setImageResource(R.drawable.viewed)
                        binding.viewed.setColorFilter(blue)
                    } else {
                        binding.viewed.setImageResource(R.drawable.not_viewed)
                        binding.viewed.setColorFilter(white)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}