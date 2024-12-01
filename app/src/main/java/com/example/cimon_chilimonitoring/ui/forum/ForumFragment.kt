package com.example.cimon_chilimonitoring.ui.forum

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.FragmentDetectionBinding
import com.example.cimon_chilimonitoring.databinding.FragmentForumBinding
import com.example.cimon_chilimonitoring.ui.forum.addPost.AddPostActivity

class ForumFragment : Fragment() {

    private val viewModel: ForumViewModel by viewModels()
    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        with(binding){
            fabAddPost.setOnClickListener {
                startActivity(Intent(requireContext(), AddPostActivity::class.java))
            }
        }

        return root
    }

    companion object {
        fun newInstance() = ForumFragment()
    }
}