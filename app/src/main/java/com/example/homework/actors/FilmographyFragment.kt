package com.example.homework.actors

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ACTOR_ID
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.fragmentsList
import com.example.homework.api.Utils.getSavedFrag
import com.example.homework.api.Utils.getSavedId
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentFilmographyBinding
import com.example.homework.db.App
import com.example.homework.movies.MovieAdapter
import com.example.homework.viewmodel.ActorViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmographyFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentFilmographyBinding? = null
    private val binding: FragmentFilmographyBinding
        get() = _binding!!

    private val savedActorId = intPreferencesKey(ACTOR_ID)
    var personId = 0
    private val movieAdapter = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@FilmographyFragment,
            R.id.action_filmographyFragment_to_movieFragment2
        )
    }

    private val actorViewModel: ActorViewModel by viewModels()

    private suspend fun saveActorId(actorId: Int) {
        context?.dataStore?.edit { settings ->
            settings[savedActorId] = actorId
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreActorId = it[savedActorId]
                if (dataStoreActorId != null) {
                    personId = dataStoreActorId
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilmographyBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        if (param1 == null) {
            readSavedSettings()
        } else {
            val passedValues = param1!!.split("&")
            personId = passedValues[0].toInt()
            val isBack = passedValues[1].toBoolean()
            if (!isBack)
            fragmentsList.add("ActorInfoFragment&$personId")
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(300)
                saveActorId(personId)
            }
        }
        while (personId == 0) {
          viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(300)
          }
        }

        val actor = actorViewModel.getActorInfoFromDb(personId!!)
        if (actor.personId != 0) {
            binding.filmographyActorName.text = actor.nameRu
            val professionMap = actorViewModel.moviesSorting(personId)
            if (!professionMap.containsKey("")) {
                var i = 1
                val chipIdMap = mutableMapOf<Int, String>()
                professionMap.forEach { t, u ->
                    val chip = layoutInflater.inflate(
                        R.layout.single_chip,
                        binding.chipGroup,
                        false
                    ) as Chip
                    chip.text = getString(R.string.chip, t, u.size)
                    chip.id = i
                    chipIdMap[i] = t
                    //  chip.chipStrokeColor = colorList
                    binding.chipGroup.addView(chip)
                    i++
                }
                binding.movieByProfRecycler.adapter = movieAdapter
                chipIdMap.forEach { key, value ->
                    binding.chipGroup.findViewById<Chip>(key)
                        .setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                binding.progressBarFilmography.visibility = View.VISIBLE
                                movieAdapter.setData(emptyList())
                                if (professionMap[value]!!.isNotEmpty()) {
                                    actorViewModel.loadMoviesToChip(professionMap[value]!!.toList())
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        movieAdapter.areAllItems = true
                                        actorViewModel.actorMovies.collect{
                                        if (it.isNotEmpty()) {
                                            binding.progressBarFilmography.visibility = View.GONE
                                            movieAdapter.setData(it)
                                        }
                                        }
                                    }
                                } else
                                    ShowAlert(getString(R.string.no_data)).show(
                                        childFragmentManager,
                                        SHARE_DIALOG
                                    )
                            } else
                                movieAdapter.setData(emptyList())
                            movieAdapter.notifyDataSetChanged()
                        }
                }
            }
        }
        binding.filmographyBack.setOnClickListener {
           val fragFromList = getSavedFrag()
            val bundle = Bundle().apply {
                putString(ARG_PARAM1, "FilmographyFragment&${getSavedId()}&true")
            }
            findNavController().navigate(
                R.id.action_filmographyFragment_to_actorInfoFragment, bundle
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            actorViewModel.isException.collect {
                if (it) {
                    ShowAlert(getString(R.string.loading_error)).show(
                        childFragmentManager,
                        SHARE_DIALOG
                    )
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}