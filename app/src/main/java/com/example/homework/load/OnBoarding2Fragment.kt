package com.example.homework.load

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.homework.R
import com.example.homework.databinding.FragmentOnBoarding2Binding

class OnBoarding2Fragment : Fragment() {

    private var _binding: FragmentOnBoarding2Binding? = null
    private val binding: FragmentOnBoarding2Binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoarding2Binding.inflate(inflater)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.pager)
        binding.skip2.setOnClickListener {
            viewPager?.currentItem = 2
        }
        return binding.root
    }
}