package com.example.homework.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.homework.databinding.MovieItemBinding
import com.example.homework.models.Movie

@GlideModule
class CustomGlide : AppGlideModule()
class MovieAdapter(private val onClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieViewHolder>() {
    var isViewedMovie = false

    private var data: List<Movie> = emptyList()
    var areAllItems = false
    var areViewedMovies = false
    fun setData(data:List<Movie>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
       return MovieViewHolder(
           MovieItemBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
           )
       )
    }

    override fun getItemCount(): Int {
        return if (areAllItems)
            data.size
        else
            if (areViewedMovies)
            6
        else
            if (data.size > 20)
                20
            else
                data.size
    }

   fun setEverythingButtonVisible() = data.size > 20

    fun test(param: Boolean) {
            isViewedMovie = param
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = data.getOrNull(position)
        if (item != null) {
           with(holder.binding) {
               viewImage.isVisible = item.viewed
                if (item.nameRu != "")
                    movieName.text = item.nameRu
                else if (item.nameEn != "")
                    movieName.text = item.nameEn
                else if (item.nameOriginal != "")
                    movieName.text = item.nameOriginal
                else
                    movieName.text = ""
                genre.text = item.genres.joinToString(", ") { it.genre }
                rating.text = item.ratingKinopoisk.toString()
               item.let {
                   Glide
                       .with(poster.context)
                       .load(it.posterUrlPreview)
                       .into(poster)
               }
            }
            holder.binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

class MovieViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)