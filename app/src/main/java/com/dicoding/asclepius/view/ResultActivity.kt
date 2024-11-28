package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.AppDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.entity.PredictionHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        val results = intent.getStringExtra("results")
        val confidenceScore = intent.getFloatExtra("confidenceScore", 0.0f)
        val imageUriString = intent.getStringExtra("imageUri")

        binding.resultText.text = results ?: getString(R.string.image_classifier_failed)

        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.resultImage.setImageURI(imageUri)
        }

        binding.saveButton.setOnClickListener {
            savePrediction(imageUriString, results, confidenceScore)
        }
        binding.backMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun savePrediction(imageUri: String?, result: String?, confidenceScore: Float) {
        if (imageUri != null && result != null) {
            val predictionHistory = PredictionHistory(
                imageUri = imageUri,
                predictionResult = result,
                confidenceScore = confidenceScore
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.predictionHistoryDao().insertPrediction(predictionHistory)
            }
            Toast.makeText(this, "Prediction saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to save prediction", Toast.LENGTH_SHORT).show()
        }
    }
}





