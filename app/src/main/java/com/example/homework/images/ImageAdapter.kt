package com.example.homework.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.databinding.ImageItemBinding
import com.example.homework.models.ImageItems


class ImageAdapter(private val onClick: (ImageItems) -> Unit) :
    RecyclerView.Adapter<ImageViewHolder>() {

    private var data: List<ImageItems> = emptyList()
    var areAllItems = false

    fun setData(data: List<ImageItems>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
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
            if (data.size > 20)
                20
            else
                data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = data.getOrNull(position)
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

class ImageViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)