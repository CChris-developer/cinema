package com.example.homework.serials

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.homework.api.Consts.ARG_SERIAL

class NumberAdapter(
    fragment: Fragment,
    private val count: Int
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = count

    override fun createFragment(position: Int): Fragment {
        val fragment = NumberFragment()
        fragment.arguments = Bundle().apply {
           putInt(ARG_SERIAL, position)
        }
        return fragment
    }
}