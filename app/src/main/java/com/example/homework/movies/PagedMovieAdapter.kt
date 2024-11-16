package com.example.homework.movies

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.homework.databinding.MovieItemBinding.*
import com.example.homework.models.Movie

class PagedMovieAdapter(private val onClick: (Movie) -> Unit) : PagingDataAdapter<Movie, MovieViewHolder>(
    DiffUtilCallback()
) {

    var areAllItems = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            if (item!!.viewed)
                viewImage.isVisible = true
            else
                viewImage.isVisible = false
            movieName.text = item?.nameRu ?: item?.nameOriginal
            genre.text = item?.genres?.joinToString(", ") { it.genre }
            rating.text = item?.ratingKinopoisk.toString()
            item?.let {
                Glide
                    .with(poster.context)
                    .load(it.posterUrlPreview)
                    .into(poster)
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let { onClick(item)
            }
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.ratingKinopoisk == newItem.ratingKinopoisk

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}