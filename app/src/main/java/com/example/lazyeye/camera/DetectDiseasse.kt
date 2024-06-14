package com.example.lazyeye.camera

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lazyeye.R
import com.example.lazyeye.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class DetectDiseasse : Fragment() {
    private lateinit var predBtn: Button
    private lateinit var imageCheck: ImageView
    private lateinit var result: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var labels: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detect_diseasse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        predBtn = view.findViewById(R.id.select_image)
        imageCheck = view.findViewById(R.id.detect_des)
        result = view.findViewById(R.id.result_view)

        // Load labels from file
        loadLabels()

        // Set button click listener to select an image
        predBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 100)
        }
    }

    private fun loadLabels() {
        try {
            labels = requireContext().assets.open("labels.txt").bufferedReader().useLines { it.toList() }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun processImageAndPredict() {
        val model = ModelUnquant.newInstance(requireContext())

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build()

        val processedImage = imageProcessor.process(tensorImage)

        // Creates inputs for reference
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedImage.buffer)

        // Runs model inference and gets result
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        // Find the index of the maximum value in the output
        val maxId = outputFeature0.indices.maxByOrNull { outputFeature0[it] } ?: -1
        val confidencePercentage = outputFeature0[maxId] * 100
        val conf:String = "%.2f%%".format(confidencePercentage)
        if (maxId != -1 && maxId < labels.size) {
            if(labels[maxId]=="0 Strabismus"){
                result.text = "يبدو أن هذا المريض يعاني من الحول بنسبة ${conf} وننصحكم بقراءة الارشادات ومراجعة الطبيب"
            }
            else{
                result.text ="لا يعاني هذا الشخص من أي حول"
            }

        } else {
            result.text = "Unknown"
        }

        model.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                imageCheck.setImageBitmap(bitmap)
                processImageAndPredict()
            }
        }
    }
}
