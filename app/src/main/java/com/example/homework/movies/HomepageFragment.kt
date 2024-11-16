package com.example.homework.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.alertmessage.ShowAlert
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Consts.SHARE_DIALOG
import com.example.homework.api.Utils
import com.example.homework.api.Utils.fragmentsList
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.api.Utils.podborka1
import com.example.homework.api.Utils.randomMoviesListHeader
import com.example.homework.databinding.FragmentHomepageBinding
import com.example.homework.db.App
import com.example.homework.models.Movie
import com.example.homework.viewmodel.MovieListViewModel
import com.example.homework.viewmodel.MovieViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding: FragmentHomepageBinding
        get() = _binding!!
    private val viewModel: MovieListViewModel by viewModels()
    private val viewModelDB: MovieViewModel by viewModels()
    private val movieAdapter1 = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val pagedMovieAdapter2 = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val pagedMovieAdapter3 = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val pagedMovieAdapter4 = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val pagedMovieAdapter5 = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val pagedMovieAdapter6 = PagedMovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@HomepageFragment,
            R.id.action_homepageFragment_to_movieFragment2
        )
    }
    private val buttonAllAdapter1 = ButtonAllAdapter { allMovies() }
    private val buttonAllAdapter2 = ButtonAllAdapter { allMovies() }
    private val buttonAllAdapter3 = ButtonAllAdapter { allMovies() }
    private val buttonAllAdapter4 = ButtonAllAdapter { allMovies() }
    private val buttonAllAdapter5 = ButtonAllAdapter { allMovies() }
    private val buttonAllAdapter6 = ButtonAllAdapter { allMovies() }
    private val concatAdapter1 = ConcatAdapter(movieAdapter1)
    private val concatAdapter2 = ConcatAdapter(pagedMovieAdapter2)
    private val concatAdapter3 = ConcatAdapter(pagedMovieAdapter3)
    private val concatAdapter4 = ConcatAdapter(pagedMovieAdapter4)
    private val concatAdapter5 = ConcatAdapter(pagedMovieAdapter5)
    private val concatAdapter6 = ConcatAdapter(pagedMovieAdapter6)
    private var pagedAdapterException = false



    private fun allMovies() {
        when {
            buttonAllAdapter1.areAllMovies -> {
                movieAdapter1.areAllItems = true
                movieAdapter1.notifyDataSetChanged()
                buttonAllAdapter1.areAllMovies = false
            }

            buttonAllAdapter2.areAllMovies -> {
                allMoviesInAdapter(binding.recycler2, pagedMovieAdapter2, 2, false)
                buttonAllAdapter2.areAllMovies = false
            }

            buttonAllAdapter3.areAllMovies -> {
                allMoviesInAdapter(binding.recycler3, pagedMovieAdapter3, 3, true)
                buttonAllAdapter3.areAllMovies = false
            }

            buttonAllAdapter4.areAllMovies -> {
                allMoviesInAdapter(binding.recycler4, pagedMovieAdapter4, 4, true)
                buttonAllAdapter4.areAllMovies = false
            }

            buttonAllAdapter5.areAllMovies -> {
                allMoviesInAdapter(binding.recycler5, pagedMovieAdapter5, 5, false)
                buttonAllAdapter5.areAllMovies = false
            }

            buttonAllAdapter6.areAllMovies -> {
                allMoviesInAdapter(binding.recycler6, pagedMovieAdapter6, 6, false)
                buttonAllAdapter6.areAllMovies = false
            }
        }
    }

    private fun allMoviesInAdapter(
        recycler: RecyclerView,
        pagedMovieAdapter: PagedMovieAdapter,
        id: Int,
        isRandomMovie: Boolean
    ) {
        showRecycleView(
            pagedMovieAdapter,
            viewModel.getMovies(id, true, viewModelDB.getViewedMovie()),
            isRandomMovie
        )
        recycler.layoutManager!!.scrollToPosition(21)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomepageBinding.inflate(inflater)
        viewModel.getRandomMovieInfo()
        val view = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val handler = CoroutineExceptionHandler {
                _, _ ->  ShowAlert(
            getString(R.string.mistake)).show(
            childFragmentManager,
            SHARE_DIALOG
        )
        }

        fragmentsList = emptyList<String>().toMutableList()
        Utils.movieTransitionsCount = 0
        viewLifecycleOwner.lifecycleScope.launch(handler) {
                delay(100)
                binding.recycler1.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler1.adapter = concatAdapter1

                binding.recycler2.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler2.adapter = concatAdapter2

                binding.recycler3.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler3.adapter = concatAdapter3

                binding.recycler4.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler4.adapter = concatAdapter4

                binding.recycler5.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler5.adapter = concatAdapter5

                binding.recycler6.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recycler6.adapter = concatAdapter6


                binding.everythingButton1.setOnClickListener {
                    transitionToAllMoviesFragment(binding.premieres)
                }
                binding.everythingButton2.setOnClickListener {
                    transitionToAllMoviesFragment(binding.popular)
                }
                binding.everythingButton3.setOnClickListener {
                    transitionToAllMoviesFragment(binding.podborka1)
                }
                binding.everythingButton4.setOnClickListener {
                    transitionToAllMoviesFragment(binding.podborka2)
                }
                binding.everythingButton5.setOnClickListener {
                    transitionToAllMoviesFragment(binding.top250)
                }
                binding.everythingButton6.setOnClickListener {
                    transitionToAllMoviesFragment(binding.serial)
                }

                showRecycleView(
                    pagedMovieAdapter2,
                    viewModel.getMovies(2, false, viewModelDB.getViewedMovie()),
                    false
                )
                showRecycleView(
                    pagedMovieAdapter3,
                    viewModel.getMovies(3, false, viewModelDB.getViewedMovie()),
                    true
                )
                showRecycleView(
                    pagedMovieAdapter4,
                    viewModel.getMovies(4, false, viewModelDB.getViewedMovie()),
                    true
                )
                showRecycleView(
                    pagedMovieAdapter5,
                    viewModel.getMovies(5, false, viewModelDB.getViewedMovie()),
                    false
                )
                showRecycleView(
                    pagedMovieAdapter6,
                    viewModel.getMovies(6, false, viewModelDB.getViewedMovie()),
                    false
                )

                while (randomMoviesListHeader.size == 0 || randomMoviesListHeader.size < 2) {
                    delay(300)
                    if (viewModel.isException.value)
                        break
                }
                if (randomMoviesListHeader.isNotEmpty()) {
                    binding.podborka1.text = randomMoviesListHeader[0]
                    binding.podborka2.text = randomMoviesListHeader[1]

                } else ShowAlert(getString(R.string.no_random_movie)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
                var loadingError = false
                while (pagedMovieAdapter3.itemCount == 0) {
                    delay(300)
                    if (viewModel.isException.value || pagedAdapterException) {
                        loadingError = true
                        break
                    }
                }
                if (!loadingError) {
                    if (viewModel.checkMoviesCount["podborka0"]!! >= 20) {
                        binding.everythingButton3.isVisible = true
                        concatAdapter3.addAdapter(buttonAllAdapter3)
                    }
                }
                loadingError = false
                while (pagedMovieAdapter4.itemCount == 0) {
                    delay(300)
                    if (viewModel.isException.value || pagedAdapterException) {
                        loadingError = true
                        break
                    }
                }
                if (!loadingError) {
                    if (viewModel.checkMoviesCount["podborka1"]!! >= 20) {
                        binding.everythingButton4.isVisible = true
                        concatAdapter4.addAdapter(buttonAllAdapter4)
                    }
                }
                loadingError = false
                while (pagedMovieAdapter2.itemCount == 0) {
                    delay(300)
                    if (viewModel.isException.value || pagedAdapterException) {
                        loadingError = true
                        break
                    }
                }
                if (!loadingError) {
                    if (viewModel.checkMoviesCount["popular"]!! > 20) {
                        binding.everythingButton2.isVisible = true
                        concatAdapter2.addAdapter(buttonAllAdapter2)
                    }
                }
                loadingError = false
                while (pagedMovieAdapter5.itemCount == 0) {
                    delay(300)
                    if (viewModel.isException.value || pagedAdapterException) {
                        loadingError = true
                        break
                    }
                }
                if (!loadingError) {
                    if (viewModel.checkMoviesCount["topList"]!! > 20) {
                        binding.everythingButton5.isVisible = true
                        concatAdapter5.addAdapter(buttonAllAdapter5)
                    }
                }
                loadingError = false
                while (pagedMovieAdapter6.itemCount == 0) {
                    delay(300)
                    if (viewModel.isException.value || pagedAdapterException) {
                        loadingError = true
                        break
                    }
                }
                if (!loadingError) {
                    if (viewModel.checkMoviesCount["tvSeries"]!! > 20) {
                        binding.everythingButton6.isVisible = true
                        concatAdapter6.addAdapter(buttonAllAdapter6)
                    }
                }
                viewModel.loadPremieres(viewModelDB.getViewedMovie())
                viewModel.premieres.collect {
                    if (viewModel.isException.value && !pagedAdapterException) {
                        binding.progress.visibility = View.GONE
                        binding.homePageFragment.visibility = View.VISIBLE
                        ShowAlert(getString(R.string.loading_error)).show(
                            childFragmentManager,
                            SHARE_DIALOG
                        )
                    } else if (viewModel.noRating && !pagedAdapterException)
                        ShowAlert(getString(R.string.no_rating)).show(
                            childFragmentManager,
                            SHARE_DIALOG
                        )
                    else {
                        movieAdapter1.setData(it)
                        binding.progress.isVisible = false
                        binding.homePageFragment.isInvisible = false
                        view.isInvisible = false

                        if (movieAdapter1.setEverythingButtonVisible()) {
                            binding.everythingButton1.isVisible = true
                            concatAdapter1.addAdapter(buttonAllAdapter1)
                        }
                    }
                }
            }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progress.visibility = View.VISIBLE
            binding.homePageFragment.visibility = View.GONE
            view.visibility = View.GONE
        }
        return binding.root
    }

    private fun transitionToAllMoviesFragment(view: TextView) {
        val bundle = Bundle().apply {
            var str = view.text.toString()
            if (view == binding.podborka1) {
                str = str + "#podborka1"
                podborka1 = true
            }
            else if (view == binding.podborka2) {
                str = str + "#podborka2"
                podborka1 = false
            }
            putString(ARG_PARAM1, "HomepageFragment&$str&false")
        }
        findNavController().navigate(
            R.id.action_homepageFragment_to_allMoviesFragment2,
            args = bundle
        )
    }

    private fun showRecycleView(
        pagedMovieAdapter: PagedMovieAdapter,
        pagedMovies: Flow<PagingData<Movie>>,
        isRandomMovie: Boolean
    ) {
        val view = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        pagedMovieAdapter.addLoadStateListener { loadStates ->
            val errorState = when {
                loadStates.prepend is LoadState.Error -> loadStates.prepend as LoadState.Error
                loadStates.append is LoadState.Error -> loadStates.append as LoadState.Error
                loadStates.refresh is LoadState.Error -> loadStates.refresh as LoadState.Error
                else -> null
            }

            if (errorState != null && !viewModel.isException.value  && !pagedAdapterException) {
                pagedAdapterException = true
                binding.progress.visibility = View.GONE
                binding.homePageFragment.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
                ShowAlert(getString(R.string.loading_error)).show(
                    childFragmentManager,
                    SHARE_DIALOG
                )
            }
        }

        pagedMovies.onEach {
            if (!pagedAdapterException) {
                if (isRandomMovie) {
                    pagedMovieAdapter.submitData(viewModel.removeNullRatings(it))

                } else {
                    pagedMovieAdapter.submitData(it)
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}