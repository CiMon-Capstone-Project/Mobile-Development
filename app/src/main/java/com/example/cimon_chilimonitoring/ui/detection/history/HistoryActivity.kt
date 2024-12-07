package com.example.cimon_chilimonitoring.ui.detection.history

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
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
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemHistory
import com.example.cimon_chilimonitoring.databinding.ActivityHistoryBinding
import com.example.cimon_chilimonitoring.helper.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel

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

        historyViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[HistoryViewModel::class.java]

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

        lifecycleScope.launch {
            val token = TokenManager.idToken
            if (token != null) {
                historyViewModel.detectPages(token)
                historyViewModel.getHistory(token)
                Log.e("HistoryACtivity", "Token is $token")
                historyViewModel.listStory.observe(this@HistoryActivity) { stories ->
//                    adapter.submitList(stories)
                    stories?.let { saveToDatabase(it) }
                }
            } else {
                Log.e("HistoryACtivity", "Token is null")
            }
        }

        with(binding){
            fabRefresh.setOnClickListener {
                val alertDialog = android.app.AlertDialog.Builder(this@HistoryActivity)
                    .setTitle("Hapus History?")
                    .setMessage("Yakin ingin menghapus lokal history? hanya lokal yang akan terhapus")
                    .setPositiveButton("Ya") { _, _ ->
                        lifecycleScope.launch {
                            val historyRepo = HistoryRepo.getInstance(HistoryDatabase.getInstance(this@HistoryActivity).historyDao())
                            historyRepo.deleteHistory()
                            Toast.makeText(this@HistoryActivity, "History dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Tidak", null)
                    .create()
                alertDialog.show()
            }
        }
    }

    private fun saveToDatabase(stories: List<ResultsItemHistory>) {
        val blogRepo = HistoryRepo.getInstance(HistoryDatabase.getInstance(this@HistoryActivity).historyDao())
        val historyEntities = stories.map { story ->
            HistoryEntity(
                id = story.id ?: 0,
                uriImage = story.imageUrl ?: "",
                result = story.disease ?: "",
                detail = story.confidence ?: "",
                symptom = story.symptom ?: "",
                prevention = story.prevention ?: "",
                treatment = story.treatment ?: "",
                analyzeTime = story.createdAt ?: "",
            )
        }
        blogRepo.saveHistoryToDatabase(historyEntities)
    }
}