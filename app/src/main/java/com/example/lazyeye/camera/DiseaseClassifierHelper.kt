package com.example.lazyeye.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions.builder

class DiseaseClassifierHelper(
    var threshold: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 1,
    var currentDelegate: Int = DELEGATE_CPU,
    val context: Context,
    val diseaseClassifierListener: ClassifierListener
) {
    private val TAG = "DiseaseClassifierHelper"

    private val loadModel = "eye_disease_model.tflite"
    private var diseaseClassifier: ImageClassifier? = null

    init {
        initializeTfLiteVision()
    }

    private fun initializeTfLiteVision() {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            diseaseClassifierListener.onInitialized()
        }.addOnFailureListener {
            diseaseClassifierListener.onError("TfLiteVision failed to initialize: ${it.message}")
        }
    }

    fun clearImageClassifier() {
        diseaseClassifier = null
    }

    fun setupImageClassifier() {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "setupImageClassifier: TfLiteVision is not initialized yet")
            return
        }

        val optionsBuilder = builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default: No action needed
            }
            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    diseaseClassifierListener.onError("GPU is not supported on this device")
                }
            }
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            diseaseClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                loadModel,
                optionsBuilder.build()
            )
            Log.i(TAG, "Image classifier initialized successfully.")
        } catch (e: Exception) {
            diseaseClassifierListener.onError("Image classifier failed to initialize: ${e.message}")
            Log.e(TAG, "TfLite failed to load model with error: ${e.message}")
        }
    }


    fun classify(image: Bitmap, imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "classify: TfLiteVision is not initialized yet")
            return
        }

        if (diseaseClassifier == null) {
            setupImageClassifier()
        }

        val inferenceTime = SystemClock.uptimeMillis()

        // Preprocess the image and convert it into a TensorImage for classification
        val imageProcessor = ImageProcessor.Builder()
                .add(NormalizeOp(0.0f, 1.0f))  // Normalization options
                .build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))
        val imageProcessingOptions = ImageProcessingOptions.builder()
                .setOrientation(getOrientationFromRotation(imageRotation))
                .build()
        val results = diseaseClassifier?.classify(tensorImage, imageProcessingOptions)

        val totalInferenceTime = SystemClock.uptimeMillis() - inferenceTime
        diseaseClassifierListener.onResults(results, totalInferenceTime)
    }
    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    interface ClassifierListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
            results: MutableList<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
    }
}
