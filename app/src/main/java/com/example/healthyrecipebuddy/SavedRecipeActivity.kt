package com.example.healthyrecipebuddy

import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.healthyrecipebuddy.adapter.SavedRecipeAdapter
import com.example.healthyrecipebuddy.databinding.ActivitySavedRecipeBinding
import com.example.healthyrecipebuddy.db.SavedRecipeDatabase
import com.example.healthyrecipebuddy.entity.SavedRecipe
import com.example.healthyrecipebuddy.util.ButtonsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SavedRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedRecipeBinding
    private lateinit var adapter: SavedRecipeAdapter
    private lateinit var parentFragmentManager: FragmentManager
    private val buttonShowedState = ButtonsState.GONE
    private var swipeBack = false


    private val savedRecipeDatabase: SavedRecipeDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            SavedRecipeDatabase::class.java,
            "saved_recipe_database").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parentFragmentManager = supportFragmentManager
        adapter = SavedRecipeAdapter(parentFragmentManager){ savedRecipe ->
            showDeleteConfirmationDialog(savedRecipe) // Pass the callback to the adapter
        }
        binding.recipeList.layoutManager = LinearLayoutManager(this)
        binding.recipeList.adapter = adapter


        initializeUI()


    }

    private fun showDeleteConfirmationDialog(savedRecipe: SavedRecipe) {
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Do you want to delete this recipe?")
            .setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    savedRecipeDatabase.savedRecipeDao().deleteSavedRecipe(savedRecipe)
                    // Refresh the list on the main thread
                    withContext(Dispatchers.Main) {
                        savedRecipeDatabase.savedRecipeDao().getAllRecipes().observe(this@SavedRecipeActivity) { recipes ->
                            adapter.submitList(recipes.toList())
                        }
                    }
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Refresh the item to undo the swipe
                adapter.notifyItemChanged(adapter.currentList.indexOf(savedRecipe))
            }
            .show()
    }


    private fun initializeUI() {
        savedRecipeDatabase.savedRecipeDao().getAllRecipes().observe(this) { recipes ->
            adapter.submitList(recipes)
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }

}