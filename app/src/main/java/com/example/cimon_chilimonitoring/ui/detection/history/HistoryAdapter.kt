package com.example.cimon_chilimonitoring.ui.detection.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.databinding.ItemCardViewHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoryEntity, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK)  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardViewHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class MyViewHolder(val binding: ItemCardViewHistoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(history: HistoryEntity) {
            binding.apply {
                // Bind data to views
                with(binding){
                    tvItemTitle.text = history.result
                    tvItemConfidence.text = history.detail
                    tvItemDate.text = "Dianalisis pada : " + formatDate(history.analyzeTime)
                }
                Glide.with(binding.root.context)
                    .load(history.uriImage) // URL Gambar
                    .centerCrop()
                    .placeholder(R.drawable.ic_place_holder)
                    .into(binding.ivItemImage) // imageView
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoryEntity> =
            object : DiffUtil.ItemCallback<HistoryEntity>() {
                override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

private fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date)
}
