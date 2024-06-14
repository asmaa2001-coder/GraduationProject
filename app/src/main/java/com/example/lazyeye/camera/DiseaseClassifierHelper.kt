package com.example.lazyeye.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions.builder

class DiseaseClassifierHelper(
    var threshold: Float = 0.5f ,
    var numThreads: Int = 2 ,
    var maxResults: Int = 1 ,
    var currentDelegate: Int = 0 ,
    val context: Context ,
    val diseaseClassifierListener: ClassifierListener
) {
    private val TAG = "AsmaaHelper"

    val loadModel = "eye_disease_model.tflite"
    //val loadModel = "2.tflite"
    private var diseaseClassifier: ImageClassifier? = null

    init {
//         Initialize TfLiteVision with GPU support
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context , optionsBuilder.build())
        }.addOnSuccessListener {
            diseaseClassifierListener.onInitialized()
        }.addOnFailureListener {
            diseaseClassifierListener.onError("TfLiteVision failed to initialize: ${it.message}")
        }
        setupImageClassifier()
    }


    fun clearImageClassifier() {
        diseaseClassifier = null
    }

    // Initialize the object detector to use MobileNetV1 and GPU delegate
    fun setupImageClassifier() {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG , "setupObjectDetector: TfLiteVision is not initialized yet")
            return
        }

        val optionsBuilder =
            builder()
                    .setScoreThreshold(threshold)
                    .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }

            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    diseaseClassifierListener?.onError("GPU is not supported on this device")
                }
            }
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            diseaseClassifier = ImageClassifier.createFromFileAndOptions(
                context ,
                loadModel ,
                optionsBuilder.build()
            )
            Log.i(TAG , "Image classifier failed to initialize. See error logs for details")
        } catch (e: Exception) {
            diseaseClassifierListener.onError("Object detector failed to initialize. See error logs for details")
            Log.e(TAG , "TFLite failed to load model with error: ${e.message}")
        }


    }

    fun classify(image: Bitmap , imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG , "detect: TfLiteVision is not initialized yet")
            return
        }

        if (diseaseClassifier == null) {
            setupImageClassifier()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        // Preprocess the image and convert it into a TensorImage for detection
        val imageProcessor = ImageProcessor.Builder().build()
        Log.i(TAG , " Image is $imageProcessor")
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))
        Log.i(TAG , "tensor Image is $tensorImage")
        val imageProcessingOptions = ImageProcessingOptions.builder()
                .setOrientation(getOrientationFromRotation(1))
                .build()
        val results = diseaseClassifier?.classify(tensorImage , imageProcessingOptions)
        Log.i(TAG , "result Image is $results")

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        diseaseClassifierListener.onResults(
            results ,
            inferenceTime
        )
        Log.i(
            "Result" ,
            "result is ${results} and size of image ${tensorImage.height * tensorImage.width}"
        )
    }

    //
    // Receive the device rotation (Surface.x values range from 0->3) and return EXIF orientation
    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        when (rotation) {
            Surface.ROTATION_270 ->
                return ImageProcessingOptions.Orientation.BOTTOM_RIGHT

            Surface.ROTATION_180 ->
                return ImageProcessingOptions.Orientation.RIGHT_BOTTOM

            Surface.ROTATION_90 ->
                return ImageProcessingOptions.Orientation.TOP_LEFT

            else ->
                return ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    interface ClassifierListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
            results: MutableList<Classifications>? ,
            inferenceTime: Long
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
    }
}
