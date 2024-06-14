package com.example.lazyeye.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.lazyeye.R
import com.example.lazyeye.databinding.FragmentClassificationBinding
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Classification : Fragment() , DiseaseClassifierHelper.ClassifierListener {
    private val TAG = "AsmaaCamera"
    private var _fragmentCameraBinding: FragmentClassificationBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!
    private lateinit var diseaseClassifierHelper: DiseaseClassifierHelper
    private lateinit var bitmapBuffer: Bitmap

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentCameraBinding = null
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentCameraBinding = FragmentClassificationBinding.inflate(inflater , container , false)
        return fragmentCameraBinding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        diseaseClassifierHelper = DiseaseClassifierHelper(
            context = requireContext() ,
            diseaseClassifierListener = this
        )
        fragmentCameraBinding.viewFinder.post {
            setUpCamera()
            //updateControlsUi()

        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            } ,
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                    .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    // The analyzer can then be assigned to the instance
                    .also {
                        it.setAnalyzer(cameraExecutor) { image ->
                            if (!::bitmapBuffer.isInitialized) {
                                // The image rotation and RGB image buffer are initialized only once
                                // the analyzer has started running
                                bitmapBuffer = Bitmap.createBitmap(
                                    image.width ,
                                    image.height ,
                                    Bitmap.Config.ARGB_8888
                                )
                            }

                            classifyImage(image)
                        }
                    }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this , cameraSelector , preview , imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG , "Use case binding failed" , exc)
        }
    }

    private fun classifyImage(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        // Pass Bitmap and rotation to the image classifier helper for processing and classification
        diseaseClassifierHelper.classify(bitmapBuffer , getScreenOrientation())
    }

    private fun getScreenOrientation(): Int {
        val outMetrics = DisplayMetrics()

        val display: Display?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = requireActivity().display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        return display?.rotation ?: 0
    }

    override fun onInitialized() {
        diseaseClassifierHelper.setupImageClassifier()
        // Initialize background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Wait for views to be laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up camera and its use cases
            setUpCamera()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext() , error , Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResults(
        results: MutableList<Classifications>? ,
        inferenceTime: Long
    ) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                fragmentCameraBinding.result.text =
                    results.toString()               // Force redraw
            } else {
                Log.e(TAG , "Fragment view is not available.")
            }
        }
    }
}

