package com.example.lazyeye.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.lazyeye.R
import org.tensorflow.lite.task.vision.classifier.Classifications

class OverlayViewClassification(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Classifications>? = null
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results?.let {
            for (classification in it) {
                val categories = classification.categories
                if (categories.isNotEmpty()) {
                    val drawableText = "${categories[0].label} ${String.format("%.2f", categories[0].score)}"

                    // Draw rect behind display text
                    textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                    val textWidth = bounds.width()
                    val textHeight = bounds.height()
                    val xPos = (width / 2) - (textWidth / 2)
                    val yPos = (height / 2) - (textHeight / 2)

                    canvas.drawRect(
                        xPos.toFloat(),
                        yPos.toFloat(),
                        (xPos + textWidth + BOUNDING_RECT_TEXT_PADDING).toFloat() ,
                        (yPos + textHeight + BOUNDING_RECT_TEXT_PADDING).toFloat() ,
                        textBackgroundPaint
                    )

                    // Draw text for classified object
                    canvas.drawText(drawableText, xPos.toFloat(), yPos + bounds.height().toFloat(), textPaint)
                }
            }
        }
    }

    fun setResults(classificationResults: List<Classifications>) {
        results = classificationResults
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}
