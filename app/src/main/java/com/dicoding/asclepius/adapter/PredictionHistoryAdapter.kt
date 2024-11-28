package com.dicoding.asclepius.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ItemCardBinding
import com.dicoding.asclepius.entity.PredictionHistory

class PredictionHistoryAdapter(
    private val predictionList: List<PredictionHistory>
) : RecyclerView.Adapter<PredictionHistoryAdapter.PredictionViewHolder>() {

    inner class PredictionViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val prediction = predictionList[position]


        holder.binding.itemTitle.text = prediction.predictionResult



        val imageUri = Uri.parse(prediction.imageUri)
        Log.d("PredictionHistoryAdapter", "Loading image URI: $imageUri")


        holder.binding.itemImage.load(imageUri) {
            placeholder(R.drawable.ic_place_holder)
            error(R.drawable.ic_place_holder)
        }
    }

    override fun getItemCount(): Int = predictionList.size
}