package com.example.homework.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.api.Consts.CHANGED_SETTINGS
import com.example.homework.api.Consts.COUNTRY_SEARCH_STRING
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.GENRE_SEARCH_STRING
import com.example.homework.api.Consts.ORDER
import com.example.homework.api.Consts.RATING_FROM
import com.example.homework.api.Consts.RATING_TO
import com.example.homework.api.Consts.TYPE
import com.example.homework.api.Consts.VIEWED_STATE
import com.example.homework.api.Consts.YEAR_FROM
import com.example.homework.api.Consts.YEAR_TO
import com.example.homework.api.Utils.dataStore
import com.example.homework.databinding.FragmentSearchSettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.launch

class SearchSettingsFragment : Fragment() {

    private var _binding: FragmentSearchSettingsBinding? = null
    private val binding: FragmentSearchSettingsBinding
        get() = _binding!!
    val country = stringPreferencesKey(COUNTRY_SEARCH_STRING)
    val genre = stringPreferencesKey(GENRE_SEARCH_STRING)
    val order = stringPreferencesKey(ORDER)
    val type = stringPreferencesKey(TYPE)
    private val ratingFrom = floatPreferencesKey(RATING_FROM)
    private val ratingTo = floatPreferencesKey(RATING_TO)
    private val yearFrom = intPreferencesKey(YEAR_FROM)
    private val yearTo = intPreferencesKey(YEAR_TO)
    private val viewedState = booleanPreferencesKey(VIEWED_STATE)
    private var selectedCountry = ""
    private var selectedGenre = ""
    private var selectedOrder = ""
    private var selectedType = ""
    private var selectedRatingFrom = 0.0f
    private var selectedRatingTo = 0.0f
    private var selectedYearFrom = 0
    private var selectedYearTo = 0
    private var selectedViewedState = false
    private var frag = ""

    private fun readSavedSearchSettings(notSavedSetting: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            context?.dataStore?.data?.collect {
                selectedOrder = it[order].toString()
                selectedType = it[type].toString()
                selectedRatingFrom = it[ratingFrom]!!
                selectedRatingTo = it[ratingTo]!!
                selectedViewedState = it[viewedState]!!
                if (notSavedSetting == "country") {
                    selectedGenre = it[genre].toString()
                    selectedYearFrom = it[yearFrom]!!
                    selectedYearTo = it[yearTo]!!
                } else if (notSavedSetting == "genre") {
                    selectedCountry = it[country].toString()
                    selectedYearFrom = it[yearFrom]!!
                    selectedYearTo = it[yearTo]!!
                } else if (notSavedSetting == "period") {
                    selectedGenre = it[genre].toString()
                    selectedCountry = it[country].toString()
                } else {
                    selectedGenre = it[genre].toString()
                    selectedCountry = it[country].toString()
                    selectedYearFrom = it[yearFrom]!!
                    selectedYearTo = it[yearTo]!!
                }
            }
        }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            frag = it.getString(FRAGMENT_NAME).toString()
            if (frag == "fromSearchFragment") {
                selectedCountry = it.getString(COUNTRY_SEARCH_STRING).toString()
                selectedGenre = it.getString(GENRE_SEARCH_STRING).toString()
                selectedOrder = it.getString(ORDER).toString()
                selectedType = it.getString(TYPE).toString()
                selectedRatingFrom = it.getFloat(RATING_FROM)
                selectedRatingTo = it.getFloat(RATING_TO)
                selectedYearFrom = it.getInt(YEAR_FROM)
                selectedYearTo = it.getInt(YEAR_TO)
                selectedViewedState = it.getBoolean(VIEWED_STATE)
            } else if (frag == "fromCountryFragment") {
                selectedCountry = it.getString(COUNTRY_SEARCH_STRING).toString()
                readSavedSearchSettings("country")
            } else if (frag == "fromGenreFragment") {
                selectedGenre = it.getString(GENRE_SEARCH_STRING).toString()
                readSavedSearchSettings("genre")
            } else if (frag == "fromPeriodFragment") {
                selectedYearFrom = it.getInt(YEAR_FROM)
                selectedYearTo = it.getInt(YEAR_TO)
                readSavedSearchSettings("period")
            } else {
                readSavedSearchSettings("not_changed")
            }
        }
        _binding = FragmentSearchSettingsBinding.inflate(inflater)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility =
            View.GONE
        val movieTypeMap = mapOf(
            MovieType.ALL.value to binding.radioBtnAll,
            MovieType.FILM.value to binding.radioBtnFilms,
            MovieType.SERIES.value to binding.radioBtnSerials,
            MovieType.MINI_SERIES.value to binding.radioBtnSerials
        )
        val orderTypeMap = mapOf(
            OrderType.YEAR.value to binding.radioBtnDate,
            OrderType.VOTE.value to binding.radioBtnPopularity,
            OrderType.RATING.value to binding.radioBtnRating
        )
        binding.progressBarSearchSettings.visibility = View.VISIBLE
        binding.baseLayout.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBarSearchSettings.visibility = View.GONE
            binding.baseLayout.visibility = View.VISIBLE
            movieTypeMap[selectedType]?.isChecked = true
            binding.country.text = selectedCountry
            binding.genre.text = selectedGenre
            binding.yearPeriod.text =
                getString(R.string.year_period, selectedYearFrom, selectedYearTo)
            Log.d("selectedRating", "$selectedRatingFrom - $selectedRatingTo")
            binding.ratingTextView.text = getString(
                R.string.rating_textview,
                selectedRatingFrom.toString(),
                selectedRatingTo.toString()
            )
            binding.ratingRangeSlider.setValues(selectedRatingFrom, selectedRatingTo)
            orderTypeMap[selectedOrder]?.isChecked = true
            binding.viewedToggleButton.isChecked = selectedViewedState
            if (selectedViewedState) {
                binding.viewedImageView.setImageResource(R.drawable.viewed)
            } else {
                binding.viewedImageView.setImageResource(R.drawable.not_viewed)
            }
            binding.viewedToggleButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedViewedState = true
                    binding.viewedImageView.setImageResource(R.drawable.viewed)
                } else {
                    selectedViewedState = false
                    binding.viewedImageView.setImageResource(R.drawable.not_viewed)
                }
            }
            binding.movieType.setOnCheckedChangeListener { _, checkedId ->
                if (binding.movieType.findViewById<RadioButton>(checkedId).isChecked) {
                    selectedType = movieTypeMap.filterValues {
                        it == binding.movieType.findViewById<RadioButton>(checkedId)
                    }.keys.first()
                }
            }
            binding.orderType.setOnCheckedChangeListener { _, checkedId ->
                if (binding.orderType.findViewById<RadioButton>(checkedId).isChecked) {
                    selectedOrder = orderTypeMap.filterValues {
                        it == binding.orderType.findViewById<RadioButton>(checkedId)
                    }.keys.first()
                }
            }
            binding.ratingRangeSlider.addOnSliderTouchListener(object :
                RangeSlider.OnSliderTouchListener {
                @SuppressLint("RestrictedApi")
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                @SuppressLint("RestrictedApi")
                override fun onStopTrackingTouch(slider: RangeSlider) {
                }
            })
            binding.ratingRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
                val values = rangeSlider.values
                selectedRatingFrom = values[0]
                selectedRatingTo = values[1]
                binding.ratingTextView.text = getString(
                    R.string.rating_textview,
                    selectedRatingFrom.toString(),
                    selectedRatingTo.toString()
                )
            }
            binding.countryLayout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    saveSearchSettings()
                    val countryBundle = Bundle().apply {
                        putString(COUNTRY_SEARCH_STRING, selectedCountry)
                    }
                    findNavController().navigate(
                        R.id.action_searchSettingsFragment_to_countryFragment,
                        countryBundle
                    )
                }
            }
            binding.genreLayout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    saveSearchSettings()
                    val genreBundle = Bundle().apply {
                        putString(GENRE_SEARCH_STRING, selectedGenre)
                    }
                    findNavController().navigate(
                        R.id.action_searchSettingsFragment_to_genreFragment,
                        genreBundle
                    )
                }
            }
            binding.yearLayout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    saveSearchSettings()
                    val periodBundle = Bundle().apply {
                        putInt(YEAR_FROM, selectedYearFrom)
                        putInt(YEAR_TO, selectedYearTo)
                    }
                    findNavController().navigate(
                        R.id.action_searchSettingsFragment_to_periodFragment,
                        periodBundle
                    )
                }
            }
            binding.back.setOnClickListener {
                findNavController().navigate(R.id.action_searchSettingsFragment_to_searchFragment)
            }
            binding.submitButton.setOnClickListener {
                val changedSettingsBundle = Bundle().apply {
                    putString(CHANGED_SETTINGS, "changed_settings")
                    putString(COUNTRY_SEARCH_STRING, selectedCountry)
                    putString(GENRE_SEARCH_STRING, selectedGenre)
                    putString(ORDER, selectedOrder)
                    putString(TYPE, selectedType)
                    putFloat(RATING_FROM, selectedRatingFrom)
                    putFloat(RATING_TO, selectedRatingTo)
                    putInt(YEAR_FROM, selectedYearFrom)
                    putInt(YEAR_TO, selectedYearTo)
                    putBoolean(VIEWED_STATE, selectedViewedState)
                }
                findNavController().navigate(
                    R.id.action_searchSettingsFragment_to_searchFragment,
                    changedSettingsBundle
                )
            }
        }, 2000)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}