package com.dicoding.asclepius.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val predictionResult: String,
    val confidenceScore: Float,
    val timestamp: Long = System.currentTimeMillis()
)

