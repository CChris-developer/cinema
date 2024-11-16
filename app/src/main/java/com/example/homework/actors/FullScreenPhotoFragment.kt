package com.example.homework.actors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.api.Consts
import com.example.homework.api.Consts.ARG_PARAM1
import com.example.homework.databinding.FragmentFullScreenPhotoBinding

class FullScreenPhotoFragment : Fragment() {
    private var param1: String? = null
    private var _binding: FragmentFullScreenPhotoBinding? = null
    private val binding: FragmentFullScreenPhotoBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullScreenPhotoBinding.inflate(inflater)
        arguments?.let {
            param1 = it.getString(Consts.ARG_PARAM1)
        }
        val passedValue = param1?.split("&")
        if (passedValue!!.isNotEmpty()) {
            context?.let {
                Glide.with(it)
                    .load(passedValue?.get(0))
                    .into(binding.actorPhotoFull)
            }
            binding.photoActorFullBack.setOnClickListener {
               findNavController().navigate(R.id.action_fullScreenPhotoFragment_to_actorInfoFragment)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}