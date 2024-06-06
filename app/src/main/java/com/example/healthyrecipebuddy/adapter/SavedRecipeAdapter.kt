package com.example.healthyrecipebuddy.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthyrecipebuddy.databinding.SavedRecipeItemBinding
import com.example.healthyrecipebuddy.entity.FoodLog
import com.example.healthyrecipebuddy.entity.SavedRecipe
import com.example.healthyrecipebuddy.util.RecipeDetailsDialogFragment

// In your RecyclerView.Adapter (if using ListAdapter)
class SavedRecipeAdapter (private val fragmentManager: FragmentManager
) : ListAdapter<SavedRecipe, SavedRecipeAdapter.SavedRecipeViewHolder>(DIFF_CALLBACK) {
    // ... (Your existing code)
// 1. View Holder Creation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRecipeViewHolder {
        val binding = SavedRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedRecipeViewHolder(binding)
    }

    // 2. Data Binding
    override fun onBindViewHolder(holder: SavedRecipeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    // 3. DiffUtil Callback (Outside the Adapter Class)
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SavedRecipe>() {
            override fun areItemsTheSame(oldItem: SavedRecipe, newItem: SavedRecipe): Boolean {
                return oldItem.id == newItem.id // Or use a unique identifier for FoodLog
            }

            override fun areContentsTheSame(oldItem: SavedRecipe, newItem: SavedRecipe): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class SavedRecipeViewHolder(private val binding: SavedRecipeItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedRecipe = getItem(position)
                    val dialog = RecipeDetailsDialogFragment.newInstance(clickedRecipe.name, clickedRecipe.text)
                    dialog.show(fragmentManager, "recipeDetailsDialog") // Use the passed fragmentManager
                }
            }
        }

        fun bind(item: SavedRecipe) {
            val dateDisplayText = item.date.toString()
            binding.menuNameText.text = item.name
            binding.DateTimeText.text = dateDisplayText
        }
    }

}