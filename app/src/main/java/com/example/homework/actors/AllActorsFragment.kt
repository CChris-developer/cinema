package com.example.homework.actors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.MOVIE_ID
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.onActorItemClick
import com.example.homework.databinding.FragmentAllActorsBinding
import com.example.homework.db.App
import com.example.homework.models.ActorInfo
import com.example.homework.viewmodel.ActorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllActorsFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentAllActorsBinding? = null
    private val binding: FragmentAllActorsBinding
        get() = _binding!!

    private val savedMovieId = intPreferencesKey(MOVIE_ID)
    private val savedPerson = stringPreferencesKey(FRAGMENT_NAME)
    var movieId = 0
    private var person = ""
    var rating = 0.0f

    private val actorViewModel: ActorViewModel by viewModels()

    private val actorAdapter = ActorAdapter { actor ->
        onActorItemClick(
            actor, this@AllActorsFragment,
            R.id.action_allActorsFragment_to_actorInfoFragment
        )
    }

    private suspend fun saveInfo(movieId: Int, person: String) {
        context?.dataStore?.edit { settings ->
            settings[savedMovieId] = movieId
            settings[savedPerson] = person
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreMovieId = it[savedMovieId]
                if (dataStoreMovieId != null) {
                    movieId = dataStoreMovieId
                }
                val dataStorePerson = it[savedPerson]
                if (dataStorePerson != null) {
                    person = dataStorePerson
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        _binding = FragmentAllActorsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBarFrameLayoutAllActors.visibility = View.VISIBLE
            if (param1 == null) {
                readSavedSettings()
            } else {
                val gotValues = param1!!.split("%")
                movieId = gotValues[0].toInt()
                person = gotValues[1]
                rating = gotValues[2].toFloat()
                val isBack = gotValues[3].toBoolean()
                if (!isBack)
                    Utils.fragmentsList.add("MovieFragment&$movieId%$person")
                saveInfo(movieId, person)
            }
            while (movieId == 0) {
                delay(300)
            }
            val list: List<ActorInfo>
            actorAdapter.areAllItems = true
            val actorsInMovieList = actorViewModel.loadAllActorsInMovie(movieId)
            if (actorsInMovieList.isNotEmpty()) {
                binding.actorsRecycler.adapter = actorAdapter
                binding.progressBarFrameLayoutAllActors.visibility = View.GONE
                if (person == "actor") {
                    list = actorsInMovieList.filter { it.professionKey == "ACTOR" }
                        .distinctBy { it.staffId }
                    binding.actorsInMovieTextview.text = getString(R.string.all_actors)
                }
                else {
                    list =
                        actorsInMovieList.filter { it.professionKey != "ACTOR" && it.professionKey != "HIMSELF" && it.professionKey != "HERSELF" }
                            .distinctBy { it.staffId }
                    binding.actorsInMovieTextview.text = getString(R.string.people_work_over_movie)
                }
                actorAdapter.setData(list)
            }
        }
        binding.back.setOnClickListener {
            val fragFromList = Utils.getSavedFrag()
            if (fragFromList == "MovieFragment") {
                val bundle = Bundle().apply {
                    putString(ARG_PARAM1, "AllActorsFragment&${Utils.getSavedId()}&true")
                }
                findNavController().navigate(
                    R.id.action_allActorsFragment_to_movieFragment2,
                    bundle
                )
            }
            else
                findNavController().navigate(
                    R.id.action_allActorsFragment_to_homepageFragment
                )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            actorViewModel.isException.collect {
                if (it) {
                    binding.progressBarFrameLayoutAllActors.visibility = View.GONE
                    ShowAlert(getString(R.string.loading_error)).show(
                        childFragmentManager,
                        SHARE_DIALOG
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}