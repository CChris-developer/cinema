package com.example.homework.serials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.homework.R
import com.example.homework.api.Consts
import com.example.homework.api.Utils.serialList
import com.example.homework.databinding.FragmentSerialsBinding
import com.example.homework.db.App
import com.example.homework.models.Episodes
import com.example.homework.viewmodel.SerialViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SerialsFragment : Fragment() {

    private var param1: String? = null
    private var _binding: FragmentSerialsBinding? = null
    private val binding: FragmentSerialsBinding
        get() = _binding!!

    private val movieDetailViewModel: SerialViewModel by viewModels()

    private lateinit var adapter: NumberAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSerialsBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(Consts.ARG_PARAM1)
        }
        val seasonsInfo = mutableListOf<List<Int>>()
        val serialsInfo = mutableListOf<List<Episodes>>()
        val params = param1!!.split("&")
        val id = params[0].toInt()
        serialList = movieDetailViewModel.getSerialsInfo(id)
        val serialName = params[1]
        val seasonsCount = params[2].toInt()
        val seasonsTab = mutableListOf<Int>()
        var i = 1
        while (i <= seasonsCount) {
            seasonsTab.add(i)
            i++
        }
        binding.serialName.text = serialName
        serialList.forEach {
            seasonsInfo.add(listOf(it.number, it.episodes.size))
            serialsInfo.add(it.episodes)
        }

        adapter = NumberAdapter(this, seasonsCount)
        viewPager = binding.pager
        viewPager.adapter = adapter
        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0)
                tab.setIcon(R.drawable.selected)
            else
                tab.setIcon(R.drawable.unselected)
            tab.setText(seasonsTab[position].toString()).setCustomView(R.layout.custom_tab)
        }.attach()
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.setIcon(R.drawable.tab_selected_icon)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.setIcon(R.drawable.unselected)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab!!.setIcon(R.drawable.tab_selected_icon)
            }
        })
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_serialsFragment_to_movieFragment2)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}