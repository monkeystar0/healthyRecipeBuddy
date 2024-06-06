package com.example.healthyrecipebuddy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.healthyrecipebuddy.adapter.SavedRecipeAdapter
import com.example.healthyrecipebuddy.databinding.ActivitySavedRecipeBinding
import com.example.healthyrecipebuddy.db.SavedRecipeDatabase

class SavedRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedRecipeBinding
    private lateinit var adapter: SavedRecipeAdapter
    private lateinit var parentFragmentManager: FragmentManager

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
        adapter = SavedRecipeAdapter(parentFragmentManager)
        binding.recipeList.layoutManager = LinearLayoutManager(this)
        binding.recipeList.adapter = adapter
        initializeUI()

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