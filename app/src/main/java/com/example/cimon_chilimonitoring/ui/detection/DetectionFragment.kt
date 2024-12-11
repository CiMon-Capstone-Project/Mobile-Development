package com.example.cimon_chilimonitoring.ui.detection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

class DetectionFragment : Fragment() {

    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var viewModel: DetectionViewModel
    private lateinit var historyDao: HistoryDao
    private lateinit var progressBar: ProgressBar

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
        progressBar = binding.progressIndicator

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                _binding?.let { binding ->
                    binding.ivPlaceholderDetection.apply {
                        setImageURI(it)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    binding.btnAnalyze.isEnabled = true
                }
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
                viewModel.currentImageUri.value?.let { uri ->
                    analyzeImage(uri)
                } ?: run {
                    showToast("Gambar tidak ditemukan")
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
            setCompressionQuality(90)
            setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f))
            withMaxResultSize(256, 256)
//            setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f), AspectRatio("3:4", 3f, 4f), AspectRatio("Original", 0f, 0f))
//            setFreeStyleCropEnabled(true)
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
                showImage()
            } else {
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
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
                        intent.putExtra(ResultActivity.EXTRA_CONFIDENCE,
                            results?.let {
                                val highestCategory = it[0].categories.maxByOrNull { it.score }
                                val displayResult = String.format("%.2f", highestCategory?.score)

                            displayResult
                        })
                        requireActivity().runOnUiThread {
                            startActivity(intent)
                            with(binding) {
                                btnAnalyze.isEnabled = true
                            }
                        }
                    }
                }
            }
        )

        // Wait until TfLite is initialized
        progressBar.visibility = View.VISIBLE
        Thread {
            while (!TfLiteVision.isInitialized()) {
                Thread.sleep(100)
            }
            requireActivity().runOnUiThread {
                progressBar.visibility = View.GONE
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