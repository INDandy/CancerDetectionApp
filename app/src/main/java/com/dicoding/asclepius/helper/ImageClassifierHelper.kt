package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(   var threshold: Float = 0.1f,
                               var maxResults: Int = 3,
                               val modelName: String = "cancer_classification.tflite",
                               val context: Context,
                               val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
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

    fun classifyStaticImage(uri: Uri) {
        val imageBitmap = getBitmapFromUri(uri)

        val model = CancerClassification.newInstance(context)
        val image = TensorImage.fromBitmap(imageBitmap)

        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList

        model.close()

        val bestResult = probability.maxByOrNull { it.score }

        if (bestResult != null && bestResult.score >= threshold) {
            var inferenceTime = SystemClock.uptimeMillis()
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            val resultText = "${bestResult.label}: ${String.format("%.2f", bestResult.score * 100)}%"

            classifierListener?.onResults(listOf(resultText), inferenceTime)
        } else {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
        }
    }



    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val contentResolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            val decodedBitmap = ImageDecoder.decodeBitmap(source)
            decodedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
    }



    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<String>,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}


