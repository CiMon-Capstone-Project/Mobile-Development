package com.example.cimon_chilimonitoring.ui.forum

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItem
import com.example.cimon_chilimonitoring.databinding.ItemCardViewForumBinding
import com.example.cimon_chilimonitoring.helper.OnEventClickListener
import com.example.cimon_chilimonitoring.ui.forum.ForumFragment.Companion.REQUEST_CODE_UPDATE_POST
import com.example.cimon_chilimonitoring.ui.forum.updatePost.UpdatePostActivity
import java.io.Serializable

class ForumAdapter(private val viewModel: ForumViewModel) : ListAdapter<ResultsItem, ForumAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardViewForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(val binding: ItemCardViewForumBinding, private val viewModel: ForumViewModel) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(stories: ResultsItem) {
            with(binding){
                tvProfileName.text = if (stories.displayName.isNullOrEmpty() && stories.email.isNullOrEmpty()) "Anonymous" else stories.displayName ?: stories.email
                tvItemTitle.text = stories.title
                itemDescription.text = stories.description

                // more
                ivMoreVert.setOnClickListener {
                    val id = stories.id
                    val userId = stories.userId
                    val firebaseEmail = TokenManager.email
                    val token = TokenManager.idToken
//                    Toast.makeText(itemView.context, "Delete ID: $id, User ID: $userId", Toast.LENGTH_SHORT).show()
                    Log.d("ForumAdapter", "article id is ${stories.userId} and $firebaseEmail")
//                    ${viewModel.listStory.value?.filter { it.userId == stories.userId }}
                    val popupMenu = PopupMenu(itemView.context, it)
                    popupMenu.menuInflater.inflate(R.menu.menu_forum, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_delete -> {
                                if (stories.email == firebaseEmail) {
                                    val token = TokenManager.idToken
                                    if (token != null) {
                                        stories.id?.let { it1 -> viewModel.deleteArticle(token, it1) }
                                        Toast.makeText(itemView.context, "Postingan berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(itemView.context, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(itemView.context, "Hanya bisa menghapus postingan milik sendiri", Toast.LENGTH_SHORT).show()
                                }
                                true
                            }
                            R.id.menu_update -> {
                                if (stories.email == firebaseEmail) {
                                    val filteredStories = viewModel.listStory.value?.filter { it.userId == stories.userId }
                                    Log.d("ForumAdapter", "article id is ${viewModel.listStory.value?.filter { it.userId == stories.userId }}")
                                    val intent = Intent(itemView.context, UpdatePostActivity::class.java).apply {
                                        putExtra("STORY_OBJECT", stories as Serializable)
                                    }
                                    (itemView.context as Activity).startActivityForResult(intent, REQUEST_CODE_UPDATE_POST, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                                } else {
                                    Toast.makeText(itemView.context, "Hanya bisa memperbarui postingan milik sendiri", Toast.LENGTH_SHORT).show()
                                }
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            }
            Glide.with(itemView.context)
                .load(stories.imageUrl)
                .into(binding.itemImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultsItem>() {
            override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
                return oldItem == newItem && oldItem.id == newItem.id
            }
        }
    }
}