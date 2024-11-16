package com.example.homework.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.api.Consts.GENRE_SEARCH_STRING
import com.example.homework.databinding.FragmentGenreBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class GenreFragment : Fragment() {
    private var _binding: FragmentGenreBinding? = null
    private val binding: FragmentGenreBinding
        get() = _binding!!
    private var selectedGenre = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreBinding.inflate(inflater)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility =
            View.GONE
        arguments?.let {
            selectedGenre = it.getString(GENRE_SEARCH_STRING).toString()
        }
        val genreMap = mapOf(
            Genre.COMEDY.value to binding.radioBtnComedy,
            Genre.MELODRAMA.value to binding.radioBtnMelodrama,
            Genre.ACTION.value to binding.radioBtnAction,
            Genre.WESTERN.value to binding.radioBtnWestern,
            Genre.DRAMA.value to binding.radioBtnDrama
        )
        genreMap[selectedGenre]?.isChecked = true
        binding.searchStringGenre.doOnTextChanged { _, _, _, _ ->
            val searchedGenre = binding.searchStringGenre.text.toString()
            if (searchedGenre != "") {
                if (genreMap.containsKey(searchedGenre)) {
                    binding.textInputLayoutGenre.isErrorEnabled = false
                    genreMap[searchedGenre]?.isChecked = true
                } else {
                    binding.textInputLayoutGenre.error = getString(R.string.genre_is_absent)
                    binding.textInputLayoutGenre.isErrorEnabled = true
                }
            }
        }

        binding.genreRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedGenre = genreMap.filterValues { it.id == checkedId }.keys.first()
        }
        binding.back.setOnClickListener {
            val genreBundle = Bundle().apply {
                putString(GENRE_SEARCH_STRING, selectedGenre)
                putString(FRAGMENT_NAME, "fromGenreFragment")
            }
            findNavController().navigate(
                R.id.action_genreFragment_to_searchSettingsFragment,
                genreBundle
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}