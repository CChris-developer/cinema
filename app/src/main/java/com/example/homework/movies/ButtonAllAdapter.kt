package com.example.homework.movies

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.databinding.ButtonAllBinding

class ButtonViewHolder(val binding: ButtonAllBinding) :
    RecyclerView.ViewHolder(binding.root)

class ButtonAllAdapter(private val allMovies: () -> Unit) :
    RecyclerView.Adapter<ButtonViewHolder>() {

    var areAllMovies = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        return ButtonViewHolder(
            ButtonAllBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.binding.showAllMoviesButton.setOnClickListener {
            areAllMovies = true
            holder.binding.showAllMovies.visibility = INVISIBLE
            allMovies()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}