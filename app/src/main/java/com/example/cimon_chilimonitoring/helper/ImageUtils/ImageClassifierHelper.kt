package com.example.cimon_chilimonitoring.helper.ImageUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.ml.CancerClassification
import com.example.cimon_chilimonitoring.ml.FinalModelMetadata
import com.example.cimon_chilimonitoring.ml.FinalModelMetadataV2
import com.example.cimon_chilimonitoring.ml.Model
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import org.tensorflow.lite.task.gms.vision.classifier.ImageClassifier
import java.io.IOException

class ImageClassifierHelper(
    var threshold: Float = 0f,
    var maxResults: Int = 5,
    val modelName: String = "final_model_metadata_v2.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null


    init {
        initializeTfLiteVision()
    }

    private fun initializeTfLiteVision() {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupImageClassifier()
//            (context as? MainActivity)?.isLoading?.postValue(false)
        }
            .addOnFailureListener {
                classifierListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
//                (context as? MainActivity)?.isLoading?.postValue(false)
            }
    }

    private fun setupImageClassifier() {
        Log.d(TAG, "Setting up ImageClassifier with threshold: $threshold and maxResults: $maxResults")
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(maxResults)

        // menggunakan GPU pada ImageClassifier
        if (CompatibilityList().isDelegateSupportedOnThisDevice){
            baseOptionsBuilder.useGpu()  // menggunakan GPU
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            baseOptionsBuilder.useNnapi()
        } else {
            // Menggunakan CPU
            baseOptionsBuilder.setNumThreads(4)
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        Log.d("testinga", "Setting up image classifier")
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            classifierListener?.onError(errorMessage)
            return
        }

        // Ensure imageClassifier is set up
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        try {
            val model = FinalModelMetadataV2.newInstance(context)

            // convert ke bitmap
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }.copy(Bitmap.Config.ARGB_8888, true)
            Log.d(TAG, "Bitmap dimensions: ${bitmap.width} x ${bitmap.height}")
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0.0f, 0.257f))
                .add(CastOp(DataType.FLOAT32))
                .build()

            val image = bitmap?.let { TensorImage.fromBitmap(it)}
            val processedImage = imageProcessor.process(image)

            Log.d(TAG, "Image tensor buffer: ${image?.tensorBuffer?.floatArray?.contentToString()}")

            val results = imageClassifier?.classify(processedImage)

            Log.d(TAG, "Classification results: ${results?.joinToString { it.categories.joinToString { category -> "${category.label}: ${category.score}" } }}")
            classifierListener?.onResults(results, System.currentTimeMillis())
            results?.forEach { classification ->
                classification.categories.forEach { category ->
                    Log.d(TAG, "Number of categories: ${classification.categories.size}")
                    Log.d(TAG, "Category: ${category.label}, Score: ${category.score}")
                }
            }

            model.close()
        } catch (e: IOException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }

    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}