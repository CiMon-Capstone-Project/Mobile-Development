package com.example.cimon_chilimonitoring.ui.tracking

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.data.local.room.tracking.TrackingDao
import com.example.cimon_chilimonitoring.databinding.FragmentTrackingBinding
import com.example.cimon_chilimonitoring.ui.tracking.create.CreateTrackingActivity
import kotlinx.coroutines.launch

class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackingAdapter: TrackingAdapter
    private lateinit var trackingViewModel: TrackingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)

        with(binding) {
            fabAddTracking.setOnClickListener {
                val intent = Intent(requireContext(), CreateTrackingActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity())
                startActivityForResult(intent, REQUEST_CODE_TRACKING, options.toBundle())
            }

            fabDelete.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Hapus Semua Data")
                builder.setMessage("Apakah Anda yakin ingin menghapus semua data?")
                builder.setPositiveButton("Ya") { _, _ ->
                    lifecycleScope.launch {
                        val trackingDao: TrackingDao = HistoryDatabase.getInstance(requireContext()).trackingDao()
                        trackingDao.deleteAllTracking()
                        Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        trackingViewModel.getAllReminders().observe(viewLifecycleOwner, Observer { trackingList ->
                            trackingAdapter.setData(trackingList)
                            trackingAdapter.notifyDataSetChanged()

                            if (trackingList.isEmpty()) {
                                binding.noTrackingText.visibility = View.VISIBLE
                            } else {
                                binding.noTrackingText.visibility = View.GONE
                            }
                        })
                    }
                }
                builder.setNegativeButton("Tidak") { _, _ -> }
                builder.show()

            }

        }

        val reminderRepository = TrackingRepo(HistoryDatabase.getInstance(requireContext()))
        val viewModelProviderFactory = TrackingViewModelFactory(requireActivity().application, reminderRepository)
        trackingViewModel = ViewModelProvider(this, viewModelProviderFactory).get(TrackingViewModel::class.java)

        trackingViewModel.getAllReminders().observe(viewLifecycleOwner, Observer { trackingList ->
            trackingAdapter.setData(trackingList)
            trackingAdapter.notifyDataSetChanged()

            if (trackingList.isEmpty()) {
                binding.noTrackingText.visibility = View.VISIBLE
            } else {
                binding.noTrackingText.visibility = View.GONE
            }
        })

        setupHomeRecycleView()

        return binding.root
    }

    private fun setupHomeRecycleView(){
        trackingAdapter = TrackingAdapter()
        binding.rvTracking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackingAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE_TRACKING || requestCode == REQUEST_UPDATE_TRACKING) && resultCode == Activity.RESULT_OK) {
            trackingViewModel.getAllReminders().observe(viewLifecycleOwner, Observer { trackingList ->
                trackingAdapter.setData(trackingList)
                trackingAdapter.notifyDataSetChanged()

                if (trackingList.isEmpty()) {
                    binding.noTrackingText.visibility = View.VISIBLE
                } else {
                    binding.noTrackingText.visibility = View.GONE
                }
            })
        }
    }

    companion object {
        const val REQUEST_CODE_TRACKING = 1003
        const val REQUEST_UPDATE_TRACKING = 1004
    }

}