package com.example.homework.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.example.homework.api.Consts.MOVIE_COLLECTION
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.dataStore
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentAllMoviesBinding
import com.example.homework.db.App
import com.example.homework.models.Movie
import com.example.homework.viewmodel.MovieListViewModel
import com.example.homework.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMoviesFragment : Fragment() {

    private var param1: String? = null
    private var _binding: FragmentAllMoviesBinding? = null
    private val binding: FragmentAllMoviesBinding
        get() = _binding!!

    private val savedMovieCollection = stringPreferencesKey(MOVIE_COLLECTION)
    private var movieCollection = ""
    private var frag = ""
    private lateinit var randomMovies: Flow<PagingData<Movie>>
    private val viewModel: MovieListViewModel by viewModels()
    private val viewModelDB: MovieViewModel by viewModels()

    private val movieAdapter = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@AllMoviesFragment,
            R.id.action_allMoviesFragment2_to_movieFragment2
        )
    }
    private val pagedMovieAdapter = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@AllMoviesFragment,
            R.id.action_allMoviesFragment2_to_movieFragment2
        )
    }

    private suspend fun saveMovieCollection(movieCollection: String) {
        context?.dataStore?.edit { settings ->
            settings[savedMovieCollection] = movieCollection
        }
    }

    private fun readSavedSettings() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.collect {
                val dataStoreMovieCollection = it[savedMovieCollection]
                if (dataStoreMovieCollection != null) {
                    movieCollection = dataStoreMovieCollection
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        _binding = FragmentAllMoviesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBarAllMovies.visibility = View.VISIBLE
            binding.allMoviesFragment.visibility = View.GONE
            if (param1 == null) {
                readSavedSettings()
            } else {
                val passedValues = param1!!.split("&")
                frag = passedValues[0]
                movieCollection = passedValues[1]
                val isBack = passedValues[2].toBoolean()
                if (!isBack)
                    Utils.fragmentsList.add("$frag&$movieCollection")
                saveMovieCollection(movieCollection)
            }
            while (movieCollection == "") {
                delay(300)
            }
            binding.back.setOnClickListener {
                val fragFromList = Utils.getSavedFrag()
                if (fragFromList == "MovieFragment") {
                    val bundle = Bundle().apply {
                        putString(ARG_PARAM1, "AllMoviesFragment&${Utils.getSavedId()}&true")
                    }
                    findNavController().navigate(R.id.action_allMoviesFragment2_to_movieFragment2, bundle)
                }
                else
                    findNavController().navigate(R.id.action_allMoviesFragment2_to_homepageFragment)
            }
            val list: List<String>
            binding.description.text = movieCollection
            if (movieCollection == "Премьеры") {
                binding.recycler.adapter = movieAdapter
                movieAdapter.areAllItems = true
                viewModelDB.getViewedMovie()
                viewModel.loadPremieres(viewModelDB.getViewedMovie())

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.premieres.collect {
                        movieAdapter.setData((it))
                    }
                }
            } else if (movieCollection == "Популярное") {
                binding.recycler.adapter = pagedMovieAdapter
                viewModel.getMovies(2, true, viewModelDB.getViewedMovie()).onEach {
                    pagedMovieAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            } else if (movieCollection == "Топ-250") {
                binding.recycler.adapter = pagedMovieAdapter
                viewModel.getMovies(5, true, viewModelDB.getViewedMovie()).onEach {
                    pagedMovieAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            } else if (movieCollection == "Сериалы") {
                binding.recycler.adapter = pagedMovieAdapter
                viewModel.getMovies(6, true, viewModelDB.getViewedMovie()).onEach {
                    pagedMovieAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)

            } else if (movieCollection.contains("#")) {
                list = movieCollection.split("#")
                 if (viewModel.getNumberOfAllRandomMovies(list[0]) == 0)
                    randomMovies = viewModel.getMovies(3, true, viewModelDB.getViewedMovie())
                else
                    randomMovies = viewModel.getMovies(4, true, viewModelDB.getViewedMovie())
                binding.description.text = list[0]
                binding.recycler.adapter = pagedMovieAdapter
                randomMovies.onEach {
                    pagedMovieAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)

            } else if (movieCollection.contains("%")) {
                val list = movieCollection.split("%")
               // frag = list[0]
                binding.description.text = "Похожие фильмы"
                binding.recycler.adapter = movieAdapter
                movieAdapter.areAllItems = true
                val relatedMoviesList =
                    viewModelDB.loadAllRelativeMoviesToAllMoviesFragment(list[1].toInt())
                if (!viewModelDB.isException.value) {
                    movieAdapter.setData(relatedMoviesList)
                }
            } else
                ShowAlert(getString(R.string.no_data)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            binding.progressBarAllMovies.visibility = View.GONE
            binding.allMoviesFragment.visibility = View.VISIBLE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isException.collect {
                if (it) {
                    binding.progressBarAllMovies.visibility = View.GONE
                    binding.allMoviesFragment.visibility = View.VISIBLE
                    ShowAlert(getString(R.string.loading_error)).show(
                        childFragmentManager,
                        SHARE_DIALOG
                    )
                }
            }
        }

        pagedMovieAdapter.addLoadStateListener { loadStates ->
            val errorState = when {
                loadStates.prepend is LoadState.Error -> loadStates.prepend as LoadState.Error
                loadStates.append is LoadState.Error -> loadStates.append as LoadState.Error
                loadStates.refresh is LoadState.Error -> loadStates.refresh as LoadState.Error
                else -> null
            }
            if (errorState != null) {
                binding.progressBarAllMovies.visibility = View.GONE
                binding.allMoviesFragment.visibility = View.VISIBLE
                ShowAlert(getString(R.string.loading_error)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}