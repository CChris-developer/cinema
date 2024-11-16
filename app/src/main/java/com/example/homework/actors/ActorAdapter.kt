package com.example.homework.actors

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.api.Utils
import com.example.homework.databinding.ActorItemBinding
import com.example.homework.models.ActorInfo

class ActorAdapter(private val onClick: (ActorInfo) -> Unit) :
    RecyclerView.Adapter<ActorViewHolder>() {
    var isViewedMovie = false
    private var data: List<ActorInfo> = emptyList()
    var areAllItems = false
    var areWorkers = false

    fun setData(data: List<ActorInfo>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        return ActorViewHolder(
            ActorItemBinding.inflate(
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
            if (areWorkers)
                6
            else
                if (data.size > 20)
                    20
                else
                    data.size
    }

    fun test(param: Boolean) {
        isViewedMovie = param
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            if (item?.nameRu != "")
                actorName.text = item?.nameRu
            else if (item?.nameEn != "")
                actorName.text = item?.nameEn
            else
                actorName.text = ""
            item?.professionKey?.let { Log.d("actorAdapter", it) }
            if (!areWorkers)
                characterName.text = item?.description ?: ""
            else
                characterName.text = item?.professionKey?.let {
                    Utils
                        .getProfession(it)
                }
            item?.let {
                Glide
                    .with(photo.context)
                    .load(it.posterUrl)
                    .into(photo)
            }
        }
        holder.binding.showItem.setOnClickListener() {
           // holder.binding.progressbar.visibility = View.VISIBLE
            //holder.binding.showItem.visibility = View.INVISIBLE
            item?.let {
                onClick(item)
            }
        }
    }
}

class ActorViewHolder(val binding: ActorItemBinding) : RecyclerView.ViewHolder(binding.root)