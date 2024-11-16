package com.example.homework.images

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.homework.databinding.ImageItemBinding
import com.example.homework.models.ImageItems

class ImagePagedAdapter(private val onClick: (ImageItems) -> Unit) :
    PagingDataAdapter<ImageItems, ImageViewHolder>(
        DiffUtilCallback()
    ) {

    var areAllItems = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            item?.let {
                Glide
                    .with(imageView.context)
                    .load(it.previewUrl)
                    .into(imageView)
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<ImageItems>() {
    override fun areItemsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean =
        oldItem.previewUrl == newItem.previewUrl

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean =
        oldItem == newItem
}