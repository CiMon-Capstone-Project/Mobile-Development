package com.example.cimon_chilimonitoring.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemBlog
import com.example.cimon_chilimonitoring.databinding.FragmentHomeBinding
import com.example.cimon_chilimonitoring.ui.blog.BlogAdapter
import com.example.cimon_chilimonitoring.ui.chatbot.ChatbotActivity
import com.example.cimon_chilimonitoring.ui.detection.history.HistoryActivity
import com.example.cimon_chilimonitoring.ui.forum.ForumFragment.Companion.REQUEST_CODE_ADD_POST
import com.example.cimon_chilimonitoring.ui.forum.addPost.AddPostActivity
import com.example.cimon_chilimonitoring.ui.tracking.create.CreateTrackingActivity
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // image slider
        autoImageSlide()

        with(binding){
            imageView4.setOnClickListener{
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            }

            btnQuickAccessForum.setOnClickListener{
                val intent = Intent(requireContext(), AddPostActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivityForResult(intent, REQUEST_CODE_ADD_POST, options.toBundle())
            }

            btnQuickAccessChatbot.setOnClickListener{
                val intent = Intent(requireContext(), ChatbotActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivity(intent, options.toBundle())
            }

            btnQuickAccessHistory.setOnClickListener{
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivity(intent, options.toBundle())
            }

            btnQuickAccessTracking.setOnClickListener{
                val intent = Intent(requireContext(), CreateTrackingActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivity(intent, options.toBundle())
            }

            val recyclerView: RecyclerView = binding.rvHomeBlog
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager

            val adapter = BlogAdapter()
            recyclerView.adapter = adapter

            // Auto scroll
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    val itemCount = adapter.itemCount
                    if (itemCount > 0) {
                        val nextItem = (layoutManager.findFirstVisibleItemPosition() + 1) % itemCount
                        recyclerView.smoothScrollToPosition(nextItem)
                    }
                    handler.postDelayed(this, 5000)
                }
            }
            handler.post(runnable)

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
                adapter.submitList(blogs)
            }
        }

        return root
    }

    private fun autoImageSlide() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        tabLayout.setupWithViewPager(viewPager, true)
        val stringList = ArrayList<Int>()
        stringList.add(R.drawable.banner_hero_1)
        stringList.add(R.drawable.banner_hero_2)
        stringList.add(R.drawable.banner_3)
        viewPager.adapter = SliderAdapter(requireContext(), stringList)
        tabLayout.setupWithViewPager(viewPager, true)

        val DELAY_MS: Long = 3000
        val PERIOD_MS: Long = 3000

        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        val update = Runnable {
            if (viewPager.currentItem == stringList.size - 1) {
                viewPager.currentItem = 0
            } else {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    // image slider gjd dipake aku bikin manual
//    private fun setupImageSlider() {
//        val imageSlider = binding.isHeroBanner
//        val imageList = ArrayList<SlideModel>()
//
//        imageList.add(SlideModel(R.drawable.banner_1, null, ScaleTypes.FIT))
//        imageList.add(SlideModel(R.drawable.banner_2, null, ScaleTypes.FIT))
//        imageList.add(SlideModel(R.drawable.banner_3, null, ScaleTypes.FIT))
//
//        imageSlider.setImageList(imageList, ScaleTypes.FIT)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}