package com.example.cimon_chilimonitoring.ui.result

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDao
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private var imageUri: Uri? = null
    private var result: String? = null
    private var confidant: String? = null

    private lateinit var historyDao: HistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)?.let { Uri.parse(it) }
        result = intent.getStringExtra(EXTRA_RESULT)
        confidant = intent.getStringExtra(EXTRA_CONFIDENCE)

        historyDao = HistoryDatabase.getInstance(this).historyDao()

        imageUri?.let {
            binding.ivResultImage.setImageURI(it)
        }
        with(binding){
            tvResultText.text = confidant
            when (result) {
                "cercospora" -> tvResultConclusion.text = getText(R.string.analyze_cercospora)
                "nutritional" -> tvResultConclusion.text = getText(R.string.analyze_nutritional)
                "mites_and_trips" -> tvResultConclusion.text = getText(R.string.analyze_mites_and_trips)
                "powdery_mildew" -> tvResultConclusion.text = getText(R.string.analyze_powdery_mildew)
                else -> tvResultConclusion.text = getText(R.string.analyze_healthy)
            }

            btnSaveAnalysis.setOnClickListener {
                val uri = intent.getStringExtra(EXTRA_IMAGE_URI)
                val result = intent.getStringExtra(EXTRA_RESULT)
                val confidence = intent.getStringExtra(EXTRA_CONFIDENCE)

                if (uri != null && result != null && confidence != null) {
                    val historyEntity = HistoryEntity(
                        id = 0,
                        uriImage = uri,
                        result = result,
                        detail = confidence
                    )
                    HistoryRepo.getInstance(historyDao)
                        .saveHistoryToDatabase(listOf(historyEntity))
                    showToast("Result saved to history")
                } else {
                    showToast("Failed to save result")
                }
            }

            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE = "extra_confidence"
    }
}