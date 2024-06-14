package com.example.lazyeye.camera

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lazyeye.databinding.FragmentCamera2Binding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.tensorflow.lite.task.vision.detector.Detection



class CameraFragment : Fragment() , DiseaseDetectorHelper.DetectorListener {

    private val TAG = "AsmaaCamera"
    private var _fragmentCameraBinding: FragmentCamera2Binding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!
    private lateinit var objectDetectorHelper: DiseaseDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

    }



    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()

    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCamera2Binding.inflate(inflater , container , false)
        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        objectDetectorHelper = DiseaseDetectorHelper(
            context = requireContext() ,
            objectDetectorListener = this
        )
        // Initialize the camera and its use cases
        fragmentCameraBinding.viewFinder.post {
          setUpCamera()
            //updateControlsUi()

        }
    }
    private fun updateControlsUi() {

        objectDetectorHelper.clearObjectDetector()
        fragmentCameraBinding.overlay.clear()
    }

    // Initialize CameraX and prepare to bind the camera use cases
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

    // Bind preview, capture, and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - assumes only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview - only using the 4:3 ratio
        preview = _fragmentCameraBinding?.viewFinder?.display?.let {
            Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(it.rotation)
                    .build()
        }

        // ImageAnalysis - using RGBA 8888 to match model requirements
        imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // Initialize image rotation and RGB image buffer once analyzer starts running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width ,
                                image.height ,
                                Bitmap.Config.ARGB_8888
                            )
                        }
                        detectObjects(image)
                    }
                }

        // Unbind use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // Bind use-cases to camera
            camera = cameraProvider.bindToLifecycle(this , cameraSelector , preview , imageAnalyzer)

            // Attach viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG , "Use case binding failed" , exc)
        }
    }

    // Perform object detection on captured image
    private fun detectObjects(image: ImageProxy) {
        // Copy RGB bits to shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(it.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to ObjectDetectorHelper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer , imageRotation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after objects have been detected. Extract original image height/width
    // to scale and place bounding boxes properly via OverlayView
    override fun onResults(
        results: MutableList<Detection>? ,
        inferenceTime: Long ,
        imageHeight: Int ,
        imageWidth: Int
    ) {

        activity?.runOnUiThread {
            // Ensure that fragmentCameraBinding is not null
            if (_fragmentCameraBinding != null) {
                fragmentCameraBinding.overlay.setResults(
                    results ?: LinkedList(),
                    imageHeight ,
                    imageWidth
                )

                // Force redraw
                fragmentCameraBinding.overlay.invalidate()
            } else {
                Log.e(TAG, "Fragment view is not available.")
            }
        }
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext() , error , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInitialized() {
        objectDetectorHelper.setupObjectDetector()
        // Initialize background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Wait for views to be laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up camera and its use cases
            setUpCamera()
        }

    }
}