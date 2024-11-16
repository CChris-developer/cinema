package com.example.homework.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.databinding.ClearHistoryBinding

class ClearMovieHistoryViewHolder(val binding: ClearHistoryBinding) :
    RecyclerView.ViewHolder(binding.root)

class ClearMovieHistoryAdapter(private val clearHistory: () -> Unit) :
    RecyclerView.Adapter<ClearMovieHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClearMovieHistoryViewHolder {
        return ClearMovieHistoryViewHolder(
            ClearHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ClearMovieHistoryViewHolder, position: Int) {
        holder.binding.clearHistoryButton.setOnClickListener {
            clearHistory()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}