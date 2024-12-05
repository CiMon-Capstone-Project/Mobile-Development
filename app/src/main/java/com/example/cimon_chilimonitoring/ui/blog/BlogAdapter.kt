package com.example.cimon_chilimonitoring.ui.blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.data.remote.response.ListStory
import com.example.cimon_chilimonitoring.data.remote.response.Results
import com.example.cimon_chilimonitoring.databinding.ItemCardViewBlogBinding
import com.example.cimon_chilimonitoring.helper.OnEventClickListener

class BlogAdapter : ListAdapter<Results, BlogAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardViewBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(val binding: ItemCardViewBlogBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(blog: Results) {
            with(binding){
                tvItemTitle.text = blog.title
                tvItemDate.text = blog.createdAt
                tvItemSource.text = blog.description
            }
            Glide.with(itemView.context)
                .load(blog.imageUrl)
                .into(binding.imgPoster)
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(blog.imageUrl)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    companion object {
        // untuk memeriksa apakah suatu data masih sama atau tidak
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Results>() {
            override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem == newItem
            }
        }
    }
}