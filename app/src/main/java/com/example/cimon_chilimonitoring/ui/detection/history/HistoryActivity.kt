package com.example.cimon_chilimonitoring.ui.detection.history

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.databinding.ActivityHistoryBinding
import com.example.cimon_chilimonitoring.helper.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val historyViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[HistoryViewModel::class.java]

        val recyclerView: RecyclerView = binding.rvHistory
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = HistoryAdapter()
        recyclerView.adapter = adapter

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        historyViewModel.isLoading.observe(this) { isLoading ->
            // Show or hide loading indicator
            binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        historyViewModel.favoriteEventsEntity.observe(this){ events ->
            // Update the UI with the list of favorite events
            if (events != null) {
                adapter.submitList(events)
                if (events.isEmpty()){
                    binding.noEventText.visibility = View.VISIBLE
                    binding.eventHeader.visibility = View.GONE
                } else {
                    binding.noEventText.visibility = View.GONE
                    binding.eventHeader.visibility = View.VISIBLE
                }
            }
        }

        historyViewModel.isLoading.observe(this){ isLoading ->
            // Show or hide loading indicator
            binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}