package com.example.cimon_chilimonitoring.ui.result

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDao
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.databinding.ActivityResultBinding
import com.example.cimon_chilimonitoring.ui.forum.ForumViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private var imageUri: Uri? = null
    private var result: String? = null
    private var confidant: String? = null
    private val viewModel: ResultViewModel by viewModels()

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

        lifecycleScope.launch {
            val token = TokenManager.idToken
            val idTreatments = when (result?.trim()?.lowercase()) {
                "cercospora" -> 0
                "nutritional" -> 3
                "mites_and_trips" -> 2
                "powdery_mildew" -> 4
                else -> 1
            }

            Log.d("ResultActivity", "onCreate: $result and id : $idTreatments")
            if (token != null) {
                viewModel.getTreatments(token, idTreatments)
                viewModel.listTreatments.observe(this@ResultActivity) { treatments ->
                    treatments?.let {
                        with(binding){
                            tvSymptom.text = it[0].symptom
                            tvPrevention.text = it[0].prevention
                            tvTreatment.text = it[0].treatment
                        }
                    }
                }
            } else {
                // Handle the case where the token is null
            }
        }

        with(binding){
            tvResultText.text = confidant
//            when (result) {
//                "cercospora" -> tvResultConclusion.text = getText(R.string.analyze_cercospora)
//                "nutritional" -> tvResultConclusion.text = getText(R.string.analyze_nutritional)
//                "mites_and_trips" -> tvResultConclusion.text = getText(R.string.analyze_mites_and_trips)
//                "powdery_mildew" -> tvResultConclusion.text = getText(R.string.analyze_powdery_mildew)
//                else -> tvResultConclusion.text = getText(R.string.analyze_healthy)
//            }

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
                    showToast("Hasil telah disimpan ke riwayat")
                } else {
                    showToast("Terjadi kesalahan ketika menyimpan")
                }
            }

//            btnUploadDetection.setOnClickListener {
//                lifecycleScope.launch {
//                    val token = TokenManager.idToken
//                    if (token != null && imageUri != null && result != null && confidant != null) {
//                        val file = File(imageUri!!.path!!)
//                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//                        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//                        val disease = result!!.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val confidence = confidant!!.toRequestBody("text/plain".toMediaTypeOrNull())
//
//                        try {
//                            val response = ApiConfig.getApiService(token).postDetection(disease, confidence, body)
//                            showToast("Detection uploaded successfully")
//                        } catch (e: Exception) {
//                            showToast("Failed to upload detection")
//                        }
//                    } else {
//                        showToast("Missing required data for upload")
//                    }
//                }
//            }

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