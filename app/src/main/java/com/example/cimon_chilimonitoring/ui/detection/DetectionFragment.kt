package com.example.cimon_chilimonitoring.ui.detection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDao
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.databinding.FragmentDetectionBinding
import com.example.cimon_chilimonitoring.helper.ImageUtils.ImageClassifierHelper
import com.example.cimon_chilimonitoring.helper.ImageUtils.getImageUri
import com.example.cimon_chilimonitoring.ui.result.ResultActivity
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class DetectionFragment : Fragment() {

    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var viewModel: DetectionViewModel
    private lateinit var historyDao: HistoryDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        }

        viewModel = ViewModelProvider(this).get(DetectionViewModel::class.java)

        viewModel.currentImageUri.observe(requireActivity()) { uri ->
            uri?.let {
                binding.ivPlaceholderDetection.apply {
                    setImageURI(it)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                binding.btnAnalyze.isEnabled = true
            }
        }

        // database
        val db = HistoryDatabase.getInstance(requireContext())
        historyDao = db.historyDao()

        with(binding) {
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnOpenCamera.setOnClickListener {
                startCamera()
            }
            btnAnalyze.setOnClickListener {
                currentImageUri?.let { uri ->
                    analyzeImage(uri)
                }
            }
        }

        return root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(uri)
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri =
            Uri.fromFile(File(requireContext().cacheDir, "croppedImage_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f), AspectRatio("3:4", 3f, 4f), AspectRatio("Original", 0f, 0f))
            setFreeStyleCropEnabled(true)
        }
        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                viewModel.setCurrentImageUri(resultUri)
                Log.d("DetectionFragment", "Image URI: $currentImageUri")
                showImage()
            } else {
                Log.d("DetectionFragment", "Crop failed, resultUri is null")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("UCrop", "Crop error: ${cropError?.message}")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            startCrop(currentImageUri!!)
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        viewModel.currentImageUri.value?.let {
            binding.ivPlaceholderDetection.apply {
                setImageURI(it)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            binding.btnAnalyze.isEnabled = true
        }
    }

    private fun analyzeImage(uri: Uri) {
        val imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    requireActivity().runOnUiThread {
                        showToast(error)
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    requireActivity().runOnUiThread {
                        val intent = Intent(requireContext(), ResultActivity::class.java)
                        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
                        intent.putExtra(
                            ResultActivity.EXTRA_RESULT,
                            results?.let { it[0].categories[0].label })
                        intent.putExtra(ResultActivity.EXTRA_CONFIDENCE, results?.let {
                            val sortedCategories =
                                it[0].categories.sortedByDescending { it?.score }
                            val displayResult =
                                sortedCategories.joinToString("\n") {
                                    "${it.label} " + NumberFormat.getPercentInstance()
                                        .format(it.score).trim()
                                }
                            displayResult
                        })

                        // Save result to database
//                        results?.let {
//                            val historyEntity = HistoryEntity(
//                                id = 0,
//                                uriImage = uri.toString(),
//                                result = it[0].categories[0].label,
//                                detail = it[0].categories.maxByOrNull { it.score }
//                                    ?.let { category ->
//                                        NumberFormat.getPercentInstance().format(category.score)
//                                            .trim()
//                                    } ?: ""
//                            )
//                            HistoryRepo.getInstance(historyDao)
//                                .saveHistoryToDatabase(listOf(historyEntity))
//                        }

                        requireActivity().runOnUiThread {
                            startActivity(intent)
                            with(binding) {
                                btnAnalyze.isEnabled = true
//                                btnAnalyze.text = getString(R.string.analyze)
                            }
                        }
                    }
                }
            }
        )

        // Wait until TfLite is initialized
        Thread {
            while (!TfLiteVision.isInitialized()) {
                Thread.sleep(100)
            }
            requireActivity().runOnUiThread {
                imageClassifierHelper.classifyStaticImage(uri)
            }
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE = 1001
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}