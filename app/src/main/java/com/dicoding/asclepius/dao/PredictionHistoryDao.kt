package com.dicoding.asclepius.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.asclepius.entity.PredictionHistory

@Dao
interface PredictionHistoryDao {
    @Insert
    suspend fun insertPrediction(history: PredictionHistory)

    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC")
    suspend fun getAllPredictions(): List<PredictionHistory>

    @Query("DELETE FROM prediction_history")
    suspend fun deleteAll()
}