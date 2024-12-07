package com.example.cimon_chilimonitoring.ui.blog

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.BlogEntity
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.local.repository.BlogRepo
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemBlog
import com.example.cimon_chilimonitoring.databinding.FragmentBlogBinding
import com.example.cimon_chilimonitoring.databinding.FragmentForumBinding
import com.example.cimon_chilimonitoring.ui.forum.ForumAdapter
import com.example.cimon_chilimonitoring.ui.forum.ForumViewModel
import kotlinx.coroutines.launch

class BlogFragment : Fragment() {

    companion object {
        fun newInstance() = BlogFragment()
    }

    private val viewModel: BlogViewModel by viewModels()
    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.rvBlog
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = BlogAdapter()
        recyclerView.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBarBlog.visibility = if (it) View.VISIBLE else View.GONE
        }

        val blogDao = HistoryDatabase.getInstance(requireContext()).blogDao()
        blogDao.getBlog().observe(viewLifecycleOwner) { blogEntities ->
            val blogs = blogEntities.map { blogEntity ->
                ResultsItemBlog(
                    id = blogEntity.id,
                    title = blogEntity.title,
                    imageUrl = blogEntity.image_url,
                    description = blogEntity.description,
                    source = blogEntity.source,
                    createdAt = blogEntity.created_at
                )
            }
            Log.d("BlogFragment", "Blogs from DB: $blogs")
            adapter.submitList(blogs)
        }

        lifecycleScope.launch {
            val token = TokenManager.idToken
            if (token != null) {
                viewModel.getStory(token)
                Log.e("BlogFragment", "Token is $token")
                viewModel.listStory.observe(viewLifecycleOwner) { stories ->
//                    adapter.submitList(stories)
                    stories?.let { saveToDatabase(it) }
                }
            } else {
                Log.e("BlogFragment", "Token is null")
            }
        }
        return root
    }

    private fun saveToDatabase(stories: List<ResultsItemBlog>) {
        val blogRepo = BlogRepo.getInstance(HistoryDatabase.getInstance(requireContext()).blogDao())
        val historyEntities = stories.map { story ->
            BlogEntity(
                id = story.id ?: 0,
                title = story.title ?: "",
                image_url = story.imageUrl ?: "",
                description = story.description,
                source = story.source,
                created_at = story.createdAt ?: ""
            )
        }
        blogRepo.saveBlogToDatabase(historyEntities)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}