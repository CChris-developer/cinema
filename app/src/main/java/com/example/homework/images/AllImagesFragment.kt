package com.example.homework.images

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
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.MOVIE_ID
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.onImageItemClick
import com.example.homework.databinding.FragmentAllImagesBinding
import com.example.homework.db.App
import com.example.homework.viewmodel.ImageListViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllImagesFragment : Fragment() {
    private var param1 = ""
    private var _binding: FragmentAllImagesBinding? = null
    private val binding: FragmentAllImagesBinding
        get() = _binding!!

    private val savedMovieId = intPreferencesKey(MOVIE_ID)
    var movieId = 0

    private val imageViewModel: ImageListViewModel by viewModels()

    private suspend fun saveInfo(movieId: Int) {
        context?.dataStore?.edit { settings ->
            settings[savedMovieId] = movieId
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreMovieId = it[savedMovieId]
                if (dataStoreMovieId != null) {
                    movieId = dataStoreMovieId
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1).toString()
        }
        _binding = FragmentAllImagesBinding.inflate(inflater)
        binding.progressBarGallery.visibility = View.GONE
        if (param1 == "") {
            readSavedSettings()
        } else {
           val passedValues = param1.split("&")
            movieId = passedValues[0].toInt()
            val isBack = passedValues[1].toBoolean()
            if (!isBack)
                Utils.fragmentsList.add("MovieFragment&${movieId}")
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(300)
                saveInfo(movieId)
            }
        }
        while (movieId == 0) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(300)
            }
        }
        val imagePagedAdapter = ImagePagedAdapter { imageItems ->
            onImageItemClick(
                movieId!!,
                imageItems,
                this@AllImagesFragment,
                R.id.action_allImagesFragment_to_fullImageFragment
            )
        }
        binding.galleryChipRecycler.adapter = imagePagedAdapter
        var i = 0
        val chipIdMap = mutableMapOf<Int, String>()
        //if (filmId != 0) {
        val itemsMap = imageViewModel.getInfoForChip(movieId!!)
        itemsMap.forEach { key, value ->
            val chip = layoutInflater.inflate(
                R.layout.single_chip,
                binding.chipGroupGallery,
                false
            ) as Chip
            chip.text = value
            chip.id = i
            chipIdMap[i] = key
            binding.chipGroupGallery.addView(chip)
            i++
        }
        chipIdMap.forEach { key, value ->
            binding.chipGroupGallery.findViewById<Chip>(key)
                .setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        binding.progressBarGallery.visibility = View.VISIBLE
                        viewLifecycleOwner.lifecycleScope.launch {
                            if (imagePagedAdapter.itemCount != 0) {
                                imagePagedAdapter.submitData(PagingData.empty())
                                imagePagedAdapter.notifyDataSetChanged()
                            }
                            delay(300)
                            imageViewModel.getPagingDataImage(movieId, true, value).collect {
                                binding.progressBarGallery.visibility = View.GONE
                                imagePagedAdapter.submitData(it)
                            }
                        }
                    } else
                        viewLifecycleOwner.lifecycleScope.launch {
                            imagePagedAdapter.submitData(PagingData.empty())
                            imagePagedAdapter.notifyDataSetChanged()
                        }
                }
        }
        binding.filmographyBack.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PARAM1, "AllImagesFragment&${Utils.getSavedId()}&true")
            }
            findNavController().navigate(R.id.action_allImagesFragment_to_movieFragment2, bundle)
        }
        imagePagedAdapter.addLoadStateListener { loadStates ->
            val errorState = when {
                loadStates.prepend is LoadState.Error -> loadStates.prepend as LoadState.Error
                loadStates.append is LoadState.Error -> loadStates.append as LoadState.Error
                loadStates.refresh is LoadState.Error -> loadStates.refresh as LoadState.Error
                else -> null
            }
            if (errorState != null && !imageViewModel.isException.value) {
                binding.progressBarGallery.visibility = View.GONE
                ShowAlert(getString(R.string.loading_error)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            imageViewModel.isException.collect {
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