package com.example.cimon_chilimonitoring.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.FragmentHomeBinding
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

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // image slider
        autoImageSlide()

        return root
    }

    private fun autoImageSlide() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        tabLayout.setupWithViewPager(viewPager, true)
        val stringList = ArrayList<Int>()
        stringList.add(R.drawable.banner_1)
        stringList.add(R.drawable.banner_2)
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