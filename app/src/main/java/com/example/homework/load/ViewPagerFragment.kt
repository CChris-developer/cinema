package com.example.homework.load

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.homework.R
import com.example.homework.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding: FragmentViewPagerBinding
        get() = _binding!!
    private lateinit var viewPager: ViewPager2

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (onBoardingFinished())
            findNavController().navigate(R.id.homepageFragment)
        _binding = FragmentViewPagerBinding.inflate(inflater)
        viewPager = binding.pager
        val fragsList = listOf(OnBoarding1Fragment(), OnBoarding2Fragment(), OnBoarding3Fragment())
        val pagedAdapter =
            ViewPagerAdapter(fragsList, requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = pagedAdapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}