package com.example.healthyrecipebuddy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthyrecipebuddy.R
import com.example.healthyrecipebuddy.databinding.FoodLogItemBinding
import com.example.healthyrecipebuddy.entity.FoodLog

// In your RecyclerView.Adapter (if using ListAdapter)
class FoodLogAdapter : ListAdapter<FoodLog, FoodLogAdapter.FoodLogViewHolder>(DIFF_CALLBACK) {
    // ... (Your existing code)
// 1. View Holder Creation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodLogViewHolder {
        val binding = FoodLogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodLogViewHolder(binding)
    }

    // 2. Data Binding
    override fun onBindViewHolder(holder: FoodLogViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    // 3. DiffUtil Callback (Outside the Adapter Class)
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FoodLog>() {
            override fun areItemsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
                return oldItem.id == newItem.id // Or use a unique identifier for FoodLog
            }

            override fun areContentsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
                return oldItem == newItem
            }
        }
    }

    class FoodLogViewHolder(private val binding: FoodLogItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(foodLog: FoodLog) {
            val dateDisplayText = foodLog.date.toString()
            val timeDisplayText = foodLog.time.toString()
            val dateTimeText = "$dateDisplayText $timeDisplayText"
            binding.menuNameText.text = foodLog.foodName
            binding.descriptionText.text = foodLog.description
            binding.caloriesText.text = foodLog.calories.toString() + " cal"
            binding.menuTypeText.text = foodLog.foodType
            binding.DateTimeText.text = dateTimeText
        }
    }

}