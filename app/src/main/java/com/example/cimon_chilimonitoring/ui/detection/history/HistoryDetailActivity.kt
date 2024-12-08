package com.example.cimon_chilimonitoring.ui.detection.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemBlog
import com.example.cimon_chilimonitoring.databinding.ActivityHistoryDetailBinding
import com.example.cimon_chilimonitoring.ui.blog.detail.BlogDetailActivity.Companion.EXTRA_BLOG
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        updateUI()
    }

    private fun updateUI(){
        val history = intent.getParcelableExtra<HistoryEntity>(EXTRA_HISTORY)
        history?.let {
            with(binding){
                tvResultText.text = when (it.result) {
                    "cercospora" -> "Cercospora"
                    "nutritional" -> "Nutritional Deficiency / Kekurangan Nutrisi"
                    "mites_and_trips" -> "Mites and Trips"
                    "powdery_mildew" -> "Powdery Mildew / Jamur Tepung"
                    else -> "Healthy"
                }
                tvSymptom.text = it.symptom
                tvPrevention.text = it.prevention
                tvTreatment.text = it.treatment
                tvDate.text = it.analyzeTime
                Glide.with(this@HistoryDetailActivity)
                    .load(it.uriImage)
                    .into(ivResultImage)
            }
        }
    }

    companion object {
        const val EXTRA_HISTORY = "extra_history"
    }
}