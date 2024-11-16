package com.example.homework.serials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.homework.R
import com.example.homework.api.Consts.ARG_SERIAL
import com.example.homework.api.Utils.serialList
import com.example.homework.databinding.FragmentNumberBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NumberFragment : Fragment() {
    private var _binding: FragmentNumberBinding? = null
    private val binding: FragmentNumberBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumberBinding.inflate(inflater)
        arguments?.takeIf { it.containsKey(ARG_SERIAL) }?.apply {
            binding.progressBarSerial.visibility = View.VISIBLE
            val position = getInt(ARG_SERIAL)
            val list = serialList[position]
            val seasonNumber = list!!.number
            val serialCount = list!!.episodes.size
            val serial = list!!.episodes
            binding.textView.text = getString(R.string.season_info, seasonNumber, serialCount)
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                binding.progressBarSerial.visibility = View.GONE
                binding.serialRecycler.adapter = SerialAdapter(serial)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}