package com.example.homework.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.api.Consts.COUNTRY_SEARCH_STRING
import com.example.homework.api.Consts.FRAGMENT_NAME
import com.example.homework.databinding.FragmentCountryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null
    private val binding: FragmentCountryBinding
        get() = _binding!!

    private var selectedCountry = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryBinding.inflate(inflater)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility =
            View.GONE
        arguments?.let {
            selectedCountry = it.getString(COUNTRY_SEARCH_STRING).toString()

        }
        val countryMap = mapOf(
            Country.RUSSIA.value to binding.radioBtnRussia,
            Country.GB.value to binding.radioBtnGb,
            Country.USA.value to binding.radioBtnUsa,
            Country.GERMANY.value to binding.radioBtnGermany,
            Country.FRANCE.value to binding.radioBtnFrance
        )
        countryMap[selectedCountry]?.isChecked = true

        binding.searchStringCountry.doOnTextChanged { _, _, _, _ ->
            val searchedCountry = binding.searchStringCountry.text.toString()
            if (searchedCountry != "") {
                if (countryMap.containsKey(searchedCountry)) {
                    binding.textInputLayoutCountry.isErrorEnabled = false
                    countryMap[searchedCountry]?.isChecked = true
                } else {
                    binding.textInputLayoutCountry.error = getString(R.string.country_is_absent)
                    binding.textInputLayoutCountry.isErrorEnabled = true
                }
            }
        }
        binding.countryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedCountry = countryMap.filterValues { it.id == checkedId }.keys.first()
        }

        binding.back.setOnClickListener {
            val countryBundle = Bundle().apply {
                putString(COUNTRY_SEARCH_STRING, selectedCountry)
                putString(FRAGMENT_NAME, "fromCountryFragment")
            }
            findNavController().navigate(
                R.id.action_countryFragment_to_searchSettingsFragment,
                countryBundle
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}