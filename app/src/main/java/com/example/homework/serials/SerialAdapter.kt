package com.example.homework.serials

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.api.Utils.formatDate
import com.example.homework.databinding.SerialItemBinding
import com.example.homework.models.Episodes

class SerialAdapter(private val series: List<Episodes>) : RecyclerView.Adapter<SerialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerialViewHolder {
            return SerialViewHolder(
                SerialItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun getItemCount() = series.size

        override fun onBindViewHolder(holder: SerialViewHolder, position: Int) {
            val item = series.getOrNull(position)
            with(holder.binding) {
                    var str = ""
                    if (item?.episodeNumber != null)
                        str = "${item?.episodeNumber} серия. "
                    if (item?.nameRu != "" && item?.nameRu != null) {
                        str = "$str${item?.nameRu}"
                        if (item?.nameEn != "" && item?.nameRu != null)
                            serialOriginalName.text = item?.nameEn ?: ""
                        else
                            serialOriginalName.visibility = View.GONE
                    } else {
                        if (item?.nameEn != "" && item?.nameRu != null)
                            str = "$str${item?.nameEn}"
                        else
                            str = "${str}Нет названия"
                        // serialOriginalName.text = item?.nameEn ?: ""
                        serialOriginalName.visibility = View.GONE
                    }
                    serialNumber.text = str
                    if (item?.synopsis != "" && item?.synopsis != null)
                        serialDescription.text = item?.synopsis
                    else
                        serialDescription.text = "Нет описания"
                    if (item?.releaseDate != "" && item?.releaseDate != null)
                        serialReleaseDate.text = formatDate(item!!.releaseDate)
                    else
                        serialReleaseDate.text = "Нет даты выхода"
            }
        }
    }

    class SerialViewHolder(val binding: SerialItemBinding) : RecyclerView.ViewHolder(binding.root)