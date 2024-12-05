package com.example.cimon_chilimonitoring.ui.forum.updatePost

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cimon_chilimonitoring.MainActivity
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.pref.TokenManager
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItem
import com.example.cimon_chilimonitoring.databinding.ActivityUpdatePostBinding
import com.example.cimon_chilimonitoring.helper.ImageUtils.getImageUri
import com.example.cimon_chilimonitoring.helper.ImageUtils.uriToFile
import com.example.cimon_chilimonitoring.helper.ViewModelFactory
import com.example.cimon_chilimonitoring.ui.forum.ForumFragment
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UpdatePostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUpdatePostBinding
    private lateinit var viewModel: UpdatePostViewModel
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(UpdatePostViewModel::class.java)
        val story = intent.getSerializableExtra("STORY_OBJECT") as ResultsItem
//        currentImageUri = Uri.parse(story.imageUrl)
        Log.d("UpdatePostActivity", "Story: ${story.id}, currentImageUri: $currentImageUri")

        with(binding){
            galleryButton.setOnClickListener { startGallery() }

            cameraButton.setOnClickListener { startCamera() }

            uploadButton.setOnClickListener {
                story.id?.let { it1 -> updateData(it1) }
            }

            // data
            TextAreaDescriptionTitle.setText(story.title)
            TextAreaDescription.setText(story.description)
            viewModel.setCurrentImageUri(Uri.parse(story.imageUrl))

            Glide.with(this@UpdatePostActivity)
                .load(viewModel.currentImageUri.value)
                .into(binding.previewImageView)
        }


    }

    private fun updateData(id : Int) {
        if (currentImageUri == null) {
            showToast("Mohon perbarui gambar terlebih dahulu")
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
                val response = apiService?.updateArticle(id,title, description, body)
                showToast(response?.message ?: "Upload successful")
                showLoading(false)
                val intent = Intent(this@UpdatePostActivity, MainActivity::class.java).apply {
                    putExtra("navigateTo", R.id.navigation_forum)
                }
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@UpdatePostActivity).toBundle())
            } catch (e: HttpException) {
                showToast("Upload failed: ${e.message}")
            } finally {
                showLoading(false)
            }
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