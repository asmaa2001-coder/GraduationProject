package com.example.lazyeye.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector.createFromFileAndOptions

class DiseaseDetectorHelper(
    var threshold: Float = 0.5f ,
    var numThreads: Int = 2 ,
    var maxResults: Int = 1 ,
    var currentDelegate: Int = 0 ,
    val context: Context ,
    val objectDetectorListener: DetectorListener
) {
    private val TAG = "AsmaaHelper"
    //val loadModel = "eye_disease_model.tflite"
    val loadModel = "2.tflite"
    private var objectDetector: ObjectDetector? = null

    init {
//         Initialize TfLiteVision with GPU support
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context , optionsBuilder.build())
        }.addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener {
            objectDetectorListener.onError("TfLiteVision failed to initialize: ${it.message}")
        }
        setupObjectDetector()
    }


    fun clearObjectDetector() {
        objectDetector = null
    }

    // Initialize the object detector to use MobileNetV1 and GPU delegate
    fun setupObjectDetector() {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG , "setupObjectDetector: TfLiteVision is not initialized yet")
            return
        }

        val optionsBuilder =
            ObjectDetectorOptions.builder()
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
                    objectDetectorListener?.onError("GPU is not supported on this device")
                }
            }
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = createFromFileAndOptions(context , loadModel , optionsBuilder.build())
            Log.i(TAG , "TFLite success to load model without error:$objectDetector")
        } catch (e: Exception) {
            objectDetectorListener.onError("Object detector failed to initialize. See error logs for details")
            Log.e(TAG , "TFLite failed to load model with error: ${e.message}")
        }


    }

    fun detect(image: Bitmap , imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG , "detect: TfLiteVision is not initialized yet")
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        // Preprocess the image and convert it into a TensorImage for detection
        val imageProcessor =
            ImageProcessor.Builder()
                    .add(Rot90Op(-imageRotation / 90))
                    .build()
        Log.i(TAG , " Image is $imageProcessor")

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))
        Log.i(TAG , "tensor Image is $tensorImage")
        val results = objectDetector?.detect(tensorImage)
        Log.i(TAG , "result Image is $results")

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener.onResults(
            results ,
            inferenceTime ,
            tensorImage.height ,
            tensorImage.width
        )
        Log.i(
            "Result" ,
            "result is ${results} and size of image ${tensorImage.height * tensorImage.width}"
        )
    }



    interface DetectorListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>? ,
            inferenceTime: Long ,
            imageHeight: Int ,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
    }
}
