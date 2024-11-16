package com.example.homework.images

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.api.Consts
import com.example.homework.databinding.FragmentFullImageBinding
import kotlinx.coroutines.launch

class FullImageFragment : Fragment() {

    private var param1: String? = null
    private var _binding: FragmentFullImageBinding? = null
    private val binding: FragmentFullImageBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullImageBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(Consts.ARG_PARAM1)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBarFullImage.visibility = View.VISIBLE
            binding.noFullImage.visibility = View.GONE
            val passedValue = param1?.split("&")
            if (passedValue!!.isNotEmpty()) {
                val frag = passedValue[0]

                context?.let {
                    Glide.with(it)
                        .load(passedValue?.get(1))
                        .into(binding.imageFull)

                    binding.progressBarFullImage.visibility = View.GONE
                    binding.imageFullBack.setOnClickListener {
                        if (frag == "MovieFragment")
                            findNavController().navigate(R.id.action_fullImageFragment_to_movieFragment2)
                        else {
                            val bundle = Bundle().apply {
                                putInt("param1", passedValue[2].toInt())
                            }
                            findNavController().navigate(
                                R.id.action_fullImageFragment_to_allImagesFragment
                             )
                        }
                    }
                }
            } else {
                binding.noFullImage.visibility = View.VISIBLE
                binding.progressBarFullImage.visibility = View.GONE
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}