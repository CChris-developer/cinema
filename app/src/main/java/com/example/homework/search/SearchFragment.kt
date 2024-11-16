package com.example.homework.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.CHANGED_SETTINGS
import com.example.homework.api.Consts.COUNTRY_SEARCH
import com.example.homework.api.Consts.COUNTRY_SEARCH_STRING
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.GENRE_SEARCH
import com.example.homework.api.Consts.GENRE_SEARCH_STRING
import com.example.homework.api.Consts.ORDER
import com.example.homework.api.Consts.RATING_FROM
import com.example.homework.api.Consts.RATING_TO
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Consts.TYPE
import com.example.homework.api.Consts.VIEWED_STATE
import com.example.homework.api.Consts.YEAR_FROM
import com.example.homework.api.Consts.YEAR_TO
import com.example.homework.api.Consts.countryMap
import com.example.homework.api.Consts.genreMap
import com.example.homework.api.Utils
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentSearchBinding
import com.example.homework.db.App
import com.example.homework.viewmodel.MovieListViewModel
import com.example.homework.movies.PagedMovieAdapter
import com.example.homework.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private val viewModel: MovieListViewModel by viewModels()
    private val viewModelDB: MovieViewModel by viewModels()

    private val pagedMovieAdapter = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@SearchFragment,
            R.id.action_searchFragment_to_movieFragment2
        )
    }
    val country = intPreferencesKey(COUNTRY_SEARCH)
    val genre = intPreferencesKey(GENRE_SEARCH)
    val order = stringPreferencesKey(ORDER)
    val type = stringPreferencesKey(TYPE)
    private val ratingFrom = floatPreferencesKey(RATING_FROM)
    private val ratingTo = floatPreferencesKey(RATING_TO)
    private val yearFrom = intPreferencesKey(YEAR_FROM)
    private val yearTo = intPreferencesKey(YEAR_TO)
    private val viewedState = booleanPreferencesKey(VIEWED_STATE)
    private var selectedCountryString = ""
    private var selectedGenreString = ""
    private var selectedCountry = 0
    private var selectedGenre = 0
    private var selectedOrder = ""
    private var selectedType = ""
    private var selectedRatingFrom = 0.0f
    private var selectedRatingTo = 0.0f
    private var selectedYearFrom = 0
    private var selectedYearTo = 0
    private var selectedViewedState = false
    private var searchSettingsDefault = SearchSettings()

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            context?.dataStore?.data?.collect {
                val dataStoreCountry = it[country]
                if (dataStoreCountry != null) {
                    selectedCountry = dataStoreCountry
                } else
                    selectedCountry = searchSettingsDefault.country
                val dataStoreGenre = it[genre]
                if (dataStoreGenre != null)
                    selectedGenre = dataStoreGenre
                else
                    selectedGenre = searchSettingsDefault.genre

                val dataStoreOrder = it[order]
                if (dataStoreOrder != null)
                    selectedOrder = dataStoreOrder
                else
                    selectedOrder = searchSettingsDefault.order

                val dataStoreType = it[type]
                if (dataStoreType != null)
                    selectedType = dataStoreType
                else
                    selectedType = searchSettingsDefault.type

                val dataStoreRatingFrom = it[ratingFrom]
                if (dataStoreRatingFrom != null)
                    selectedRatingFrom = dataStoreRatingFrom
                else
                    selectedRatingFrom = searchSettingsDefault.ratingFrom

                val dataStoreRatingTo = it[ratingTo]
                if (dataStoreRatingTo != null)
                    selectedRatingTo = dataStoreRatingTo
                else
                    selectedRatingTo = searchSettingsDefault.ratingTo

                val dataStoreYearFrom = it[yearFrom]
                if (dataStoreYearFrom != null)
                    selectedYearFrom = dataStoreYearFrom
                else
                    selectedYearFrom = searchSettingsDefault.yearFrom

                val dataStoreYearTo = it[yearTo]
                if (dataStoreYearTo != null)
                    selectedYearTo = dataStoreYearTo
                else
                    selectedYearTo = searchSettingsDefault.yearTo

                val dataStoreViewedState = it[viewedState]
                Log.d("dataStoreViewedState", dataStoreViewedState.toString())
                if (dataStoreViewedState != false)
                    selectedViewedState = dataStoreViewedState == true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // arguments?.let {
        //  isChanged = it.getBoolean(CHANGED_SETTINGS)
//Log.d("isChanged", isChanged.toString())
        Utils.fragmentsList = emptyList<String>().toMutableList()
        Utils.movieTransitionsCount = 0
        if (arguments?.getString(CHANGED_SETTINGS) != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                selectedCountryString =
                    arguments?.getString(COUNTRY_SEARCH_STRING).toString()
                selectedCountry =
                    countryMap.filterValues { it == selectedCountryString }.keys.first()
                selectedGenreString = arguments?.getString(GENRE_SEARCH_STRING).toString()
                selectedGenre =
                    genreMap.filterValues { it == selectedGenreString }.keys.first()
                selectedOrder = arguments?.getString(ORDER).toString()
                selectedType = arguments?.getString(TYPE).toString()
                selectedRatingFrom = arguments?.getFloat(RATING_FROM)!!
                selectedRatingTo = arguments?.getFloat(RATING_TO)!!
                selectedYearFrom = arguments?.getInt(YEAR_FROM)!!
                selectedYearTo = arguments?.getInt(YEAR_TO)!!
                selectedViewedState = arguments?.getBoolean(VIEWED_STATE) == true
                saveSearchSettings()
            }
        } else readSavedSettings()
        _binding = FragmentSearchBinding.inflate(inflater)
        binding.progressBarSearch.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.notFound.visibility = View.GONE
            binding.searchRecycler.visibility = View.VISIBLE
            binding.searchRecycler.adapter = pagedMovieAdapter
            pagedMovieAdapter.addLoadStateListener { loadStates ->
                val errorState = when {
                    loadStates.prepend is LoadState.Error -> loadStates.prepend as LoadState.Error
                    loadStates.append is LoadState.Error -> loadStates.append as LoadState.Error
                    loadStates.refresh is LoadState.Error -> loadStates.refresh as LoadState.Error
                    else -> null
                }
                if (errorState != null) {
                    binding.progressBarSearch.isInvisible = true
                    binding.notFound.isInvisible = true
                    binding.searchRecycler.isInvisible = true
                    ShowAlert(getString(R.string.loading_error)).show(
                        childFragmentManager,
                        SHARE_DIALOG
                    )
                } else {
                    if (loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached) {
                        if (pagedMovieAdapter.itemCount == 0) {
                            binding.notFound.isVisible = true
                            binding.searchRecycler.isInvisible = true
                        }
                    }
                    binding.progressBarSearch.isVisible = loadStates.refresh is LoadState.Loading
                    binding.progressBarSearch.isInvisible =
                        loadStates.refresh is LoadState.NotLoading
                }
            }

            viewModel.getRequiredMovies(
                true,
                viewModelDB.getViewedMovie(),
                selectedCountry,
                selectedGenre,
                selectedOrder,
                selectedType,
                selectedRatingFrom,
                selectedRatingTo,
                selectedYearFrom,
                selectedYearTo,
                ""
            ).onEach {
                pagedMovieAdapter.submitData(it.filter { movie -> movie.viewed == selectedViewedState && movie.ratingKinopoisk != 0.0f })
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.searchString.doAfterTextChanged { text ->
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.notFound.visibility = View.GONE
                    viewLifecycleOwner.lifecycleScope.launch {
                        pagedMovieAdapter.submitData(PagingData.empty())
                    }
                    viewModel.getRequiredMovies(
                        true,
                        viewModelDB.getViewedMovie(),
                        selectedCountry,
                        selectedGenre,
                        selectedOrder,
                        selectedType,
                        selectedRatingFrom,
                        selectedRatingTo,
                        selectedYearFrom,
                        selectedYearTo,
                        text.toString()
                    ).onEach {
                        delay(1000)
                        pagedMovieAdapter.submitData(
                            it.filter { movie -> movie.viewed == selectedViewedState }
                        )
                        binding.searchRecycler.visibility = View.VISIBLE
                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                }, 3000)
            }

            binding.textInputLayout.setEndIconOnClickListener {
                val bundle = Bundle().apply {
                    putString(FRAGMENT_NAME, "fromSearchFragment")
                    putString(COUNTRY_SEARCH_STRING, countryMap[selectedCountry])
                    putString(GENRE_SEARCH_STRING, genreMap[selectedGenre])
                    putString(ORDER, selectedOrder)
                    putString(TYPE, selectedType)
                    putFloat(RATING_FROM, selectedRatingFrom)
                    putFloat(RATING_TO, selectedRatingTo)
                    putInt(YEAR_FROM, selectedYearFrom)
                    putInt(YEAR_TO, selectedYearTo)
                    putBoolean(VIEWED_STATE, selectedViewedState)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_searchSettingsFragment, bundle
                )
            }
        }, 4000)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun saveSearchSettings() {
        context?.dataStore?.edit { settings ->
            settings[country] = selectedCountry
            settings[genre] = selectedGenre
            settings[order] = selectedOrder
            settings[type] = selectedType
            settings[ratingFrom] = selectedRatingFrom
            settings[ratingTo] = selectedRatingTo
            settings[yearFrom] = selectedYearFrom
            settings[yearTo] = selectedYearTo
            settings[viewedState] = selectedViewedState
        }
    }
}