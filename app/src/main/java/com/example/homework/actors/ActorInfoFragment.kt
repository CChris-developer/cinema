package com.example.homework.actors

import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ACTOR_ID
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.FRAGMENT_NAME_ACTOR_INFO
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.fragmentsList
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentActorInfoBinding
import com.example.homework.db.App
import com.example.homework.movies.MovieAdapter
import com.example.homework.viewmodel.ActorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActorInfoFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentActorInfoBinding? = null
    private val binding: FragmentActorInfoBinding
        get() = _binding!!
    private val savedActorId = intPreferencesKey(ACTOR_ID)
    private val savedFrag = stringPreferencesKey(FRAGMENT_NAME_ACTOR_INFO)
    private var actorId = 0
    private var frag = ""
    private val movieAdapter = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@ActorInfoFragment,
            R.id.action_actorInfoFragment_to_movieFragment2
        )
    }
    private val actorViewModel: ActorViewModel by viewModels()

    private suspend fun saveInfo(actorId: Int, frag: String) {
        context?.dataStore?.edit { settings ->
            settings[savedActorId] = actorId
            settings[savedFrag] = frag
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreActorId = it[savedActorId]
                if (dataStoreActorId != null) {
                    actorId = dataStoreActorId
                }
                val dataStoreFrag = it[savedFrag]
                if (dataStoreFrag != null) {
                    frag = dataStoreFrag
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        _binding = FragmentActorInfoBinding.inflate(inflater)
        val handler = CoroutineExceptionHandler {
                _, _ ->  ShowAlert(
            getString(R.string.mistake)).show(
            childFragmentManager,
            SHARE_DIALOG
        )
        }
        viewLifecycleOwner.lifecycleScope.launch(handler) {
               binding.progressBarActorInfo.visibility = View.VISIBLE
               binding.actorInfoLayout.visibility = View.GONE
               val gotValues: List<String>
               if (param1 == null) {
                   readSavedSettings()
               } else {
                   gotValues = param1?.split("&")!!
                   actorId = gotValues[1].toInt()
                   frag = gotValues[0]
                   val isBack = gotValues[2].toBoolean()
                   if (!isBack)
                   fragmentsList.add("$frag&${actorId}")
                   delay(100)
                   saveInfo(actorId, frag)
               }
               while (actorId == 0) {
                   delay(300)
               }
               binding.fragmentActorInfoBack.setOnClickListener {
                   val fragFromList = Utils.getSavedFrag()
                   if (fragFromList == "AllActorsFragment") {
                       val bundle1 = Bundle().apply {
                           putString(ARG_PARAM1, "${Utils.getSavedId()}%0%true")
                       }
                       findNavController().navigate(
                           R.id.action_actorInfoFragment_to_allActorsFragment,
                           bundle1
                       )
                   }
                   else if (fragFromList == "MovieFragment") {
                       val bundle2 = Bundle().apply {
                           putString(ARG_PARAM1, "ActorInfoFragment&${Utils.getSavedId()}&true")
                       }
                       findNavController().navigate(
                           R.id.action_actorInfoFragment_to_movieFragment2,
                           bundle2
                       )
                   } else
                       findNavController().navigate(R.id.action_actorInfoFragment_to_homepageFragment)
               }
               val actorInfo = actorViewModel.loadActorInfo(actorId)
               if (actorInfo.personId != 0) {
                   if (!actorViewModel.isNoActorInfo) {
                       context?.let {
                           Glide.with(it)
                               .load(actorInfo.posterUrl)
                               .into(binding.actorPhoto)
                       }
                       binding.actorNameInfo.text = actorInfo.nameRu
                       binding.actorProfessionInfo.text = actorInfo.profession
                       binding.actorPhoto.setOnClickListener {
                           val bundle = Bundle().apply {
                               putString(ARG_PARAM1, "${actorInfo.posterUrl}&${actorId}")
                           }
                           findNavController().navigate(
                               R.id.action_actorInfoFragment_to_fullScreenPhotoFragment,
                               args = bundle
                           )
                       }
                       binding.toList.setOnClickListener {
                           val bundle = Bundle().apply {
                               putString(ARG_PARAM1, "$actorId&false")
                           }
                           findNavController().navigate(
                               R.id.action_actorInfoFragment_to_filmographyFragment,
                               args = bundle
                           )
                       }
                       binding.bestMoviesRecycler.adapter = movieAdapter
                       binding.actorMoviesNumber.text =
                           actorViewModel.getMoviesNumber(actorInfo.personId).toString()
                      actorViewModel.actorMovies.collect {
                           binding.progressBarActorInfo.visibility = View.GONE
                           binding.actorInfoLayout.visibility = View.VISIBLE
                           if (it.isEmpty()) {
                               binding.noBestMovies.visibility = View.VISIBLE
                               binding.noBestMovies.text = getString(R.string.null_rating)
                               binding.bestMoviesRecycler.visibility = View.GONE
                           } else {
                               binding.noBestMovies.visibility = View.GONE
                               binding.bestMoviesRecycler.visibility = View.VISIBLE
                               movieAdapter.setData(it)
                           }
                       }
                   } else
                       ShowAlert(getString(R.string.no_person_data)).show(
                           childFragmentManager,
                           SHARE_DIALOG
                       )

               }
        }
            viewLifecycleOwner.lifecycleScope.launch {
                actorViewModel.isException.collect {
                    if (it) {
                        binding.progressBarActorInfo.visibility = View.GONE
                        binding.actorInfoLayout.visibility = View.VISIBLE
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