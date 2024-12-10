package com.example.cimon_chilimonitoring.ui.forum.addPost

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.databinding.ActivityAddPostBinding
import com.example.cimon_chilimonitoring.helper.ImageUtils.getImageUri
import com.example.cimon_chilimonitoring.helper.ImageUtils.reduceFileImage
import com.example.cimon_chilimonitoring.helper.ImageUtils.uriToFile
import com.example.cimon_chilimonitoring.helper.ViewModelFactory
import com.example.cimon_chilimonitoring.ui.detection.DetectionViewModel
import com.example.cimon_chilimonitoring.ui.forum.ForumFragment
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private var currentImageUri: Uri? = null
    private lateinit var viewModel: AddPostViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(AddPostViewModel::class.java)

//        if (!allPermissionsGranted()) {
//            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
//        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        with(binding){
            galleryButton.setOnClickListener { startGallery() }

            cameraButton.setOnClickListener { startCamera() }

            uploadButton.setOnClickListener {
                uploadImage()
            }
        }
    }

    // create uploadImage function
    private fun uploadImage() {
        if (currentImageUri == null) {
            showToast("Please select an image first")
            return
        }

        val file = uriToFile(currentImageUri!!, this)
        val requestFile = file.asRequestBody("image/jpeg".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val title = binding.TextAreaDescriptionTitle.text.toString().toRequestBody("text/plain".toMediaType())
        val description = binding.TextAreaDescription.text.toString().toRequestBody("text/plain".toMediaType())

        lifecycleScope.launch {
            showLoading(true)
            try {
                val token = TokenManager.idToken
                val apiService = token?.let { ApiConfig.getApiService(it) }
                val response = apiService?.postArticle(title, description, body)
                showToast(response?.message ?: "Upload successful")
                showLoading(false)
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } catch (e: HttpException) {
                showToast("Upload failed: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri =
            Uri.fromFile(File(this.cacheDir, "croppedImage_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f), AspectRatio("3:4", 3f, 4f), AspectRatio("Original", 0f, 0f))
            setFreeStyleCropEnabled(true)
        }
        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .start(this)
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


    private fun handleCropResult(data: Intent?) {
        val resultUri = UCrop.getOutput(data!!)
        if (resultUri != null) {
            currentImageUri = resultUri
            showImage()
        } else {
            showToast("Crop failed")
        }
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

    private fun startCamera() {
        currentImageUri = getImageUri(this)
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
            binding.previewImageView.apply {
                setImageURI(it)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}