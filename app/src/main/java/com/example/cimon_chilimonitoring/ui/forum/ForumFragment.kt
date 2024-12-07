package com.example.cimon_chilimonitoring.ui.forum

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.remote.response.ListStory
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItem
import com.example.cimon_chilimonitoring.databinding.FragmentDetectionBinding
import com.example.cimon_chilimonitoring.databinding.FragmentForumBinding
import com.example.cimon_chilimonitoring.helper.OnEventClickListener
import com.example.cimon_chilimonitoring.ui.chatbot.ChatbotActivity
import com.example.cimon_chilimonitoring.ui.forum.addPost.AddPostActivity
import kotlinx.coroutines.launch

class ForumFragment : Fragment(), OnEventClickListener {

    private val viewModel: ForumViewModel by viewModels()
    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.rvForum
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = ForumAdapter(this, viewModel)
        recyclerView.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide loading indicator
            binding.progressBarForum.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.listStory.observe(viewLifecycleOwner) { stories ->
            adapter.submitList(stories?.toList())
        }

        val token = TokenManager.idToken
        lifecycleScope.launch {
            if (token != null) {
                viewModel.getStory(token)
            } else {
                // Handle the case where the token is null
                Toast.makeText(requireContext(), "Anda tidak terhubung ke internet", Toast.LENGTH_SHORT).show()
            }
        }

        with(binding){
            fabAddPost.setOnClickListener {
                val intent = Intent(requireContext(), AddPostActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivityForResult(intent, REQUEST_CODE_ADD_POST, options.toBundle())
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE_ADD_POST || requestCode == REQUEST_CODE_UPDATE_POST) && resultCode == Activity.RESULT_OK) {
            val token = TokenManager.idToken
            if (token != null) {
                viewModel.refreshStories(token)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_POST = 1001
        const val REQUEST_CODE_UPDATE_POST = 1002
    }

    override fun onEventClick(event: ResultsItem) {
        Toast.makeText(requireContext(), event.title, Toast.LENGTH_SHORT).show()
    }

}