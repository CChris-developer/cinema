package com.example.homework.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.api.Utils.onMovieItemClick
import com.example.homework.databinding.FragmentMoviesFromProfileBinding
import com.example.homework.db.App
import com.example.homework.models.Movie
import com.example.homework.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFromProfileFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentMoviesFromProfileBinding? = null
    private val binding: FragmentMoviesFromProfileBinding
        get() = _binding!!

    private var header = ""
    private val viewModelDB: MovieViewModel by viewModels()

    private val movieAdapter = MovieAdapter { movie ->
        onMovieItemClick(
            movie,
            this@MoviesFromProfileFragment,
            R.id.action_moviesFromProfileFragment_to_movieFragment2
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesFromProfileBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        binding.moviesFromProfileProgressBar.visibility = View.VISIBLE
        binding.noMoviesFromProfileTextview.visibility = View.GONE
        header = param1.toString()

        val list: MutableList<Movie>
        binding.descriptionMoviesFromProfile.text = header
        if (header == getString(R.string.header_of_viewed_movies))
            list = viewModelDB.getViewedMoviesInfo()
        else if (header == getString(R.string.header_of_interested_movies))
            list = viewModelDB.getInterestedMovies()
        else {
            binding.descriptionMoviesFromProfile.text =
                getString(R.string.movies_from_collection, header)
            list = viewModelDB.getMoviesInCollection(header)
        }

        if (list.isNotEmpty()) {
            binding.moviesFromProfileRecycler.adapter = movieAdapter
            movieAdapter.setData(list)
        } else {
            binding.moviesFromProfileProgressBar.visibility = View.GONE
            binding.noMoviesFromProfileTextview.visibility = View.VISIBLE
        }

        binding.backToProfile.setOnClickListener {
            findNavController().navigate(R.id.action_moviesFromProfileFragment_to_profileFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}