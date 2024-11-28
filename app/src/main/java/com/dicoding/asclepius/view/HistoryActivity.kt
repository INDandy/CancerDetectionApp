package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.PredictionHistoryAdapter
import com.dicoding.asclepius.database.AppDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: PredictionHistoryAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        loadHistory()
    }

    private fun loadHistory() {

        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val predictionList = db.predictionHistoryDao().getAllPredictions()

            runOnUiThread {
                if (predictionList.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter = PredictionHistoryAdapter(predictionList)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this@HistoryActivity)
                } else {
                    Toast.makeText(this@HistoryActivity, "Riwayat kosong", Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}