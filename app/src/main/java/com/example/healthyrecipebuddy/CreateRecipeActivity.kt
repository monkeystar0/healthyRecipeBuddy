package com.example.healthyrecipebuddy

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.healthyrecipebuddy.databinding.ActivityCreateRecipeBinding
import com.example.healthyrecipebuddy.db.SavedRecipeDatabase
import com.example.healthyrecipebuddy.entity.SavedRecipe
import com.example.healthyrecipebuddy.model.UiState
import com.example.healthyrecipebuddy.util.MeasurementTool
import com.example.healthyrecipebuddy.util.RecipeNameDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date

class CreateRecipeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCreateRecipeBinding
    private var ingredients = mutableListOf<String>()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uiState: StateFlow<UiState>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var measurementTool: MeasurementTool

    private val savedRecipeDatabase: SavedRecipeDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            SavedRecipeDatabase::class.java,
            "saved_recipe_database").build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = MainViewModel(this)
        uiState = mainViewModel.uiState
        measurementTool = MeasurementTool()
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        // Set up the UI and logic here
        initializeUI()
        lifecycleScope.launch {
            // Observe the uiState variable
            mainViewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        showLoadingIndicator()
                    }
                    is UiState.Initial -> {
                        // Do nothing
                    }
                    is UiState.Success -> {
                        // Display the generated text
                        binding.createRecipeText.text = uiState.response
                        hideLoadingIndicator()
                        showRecipeText()
                    }
                    is UiState.Error -> {
                        // Show an error message
                        Toast.makeText(this@CreateRecipeActivity, uiState.error, Toast.LENGTH_SHORT).show()
                        hideLoadingIndicator()
                    }
                }
            }
        }
    }

    private fun showLoadingIndicator() {
        // Show loading indicator
        binding.loadingLayout.visibility = View.VISIBLE
        binding.recipeLayout.visibility = View.GONE
        binding.SaveButtonLayout.visibility = View.GONE
        binding.addPanelLayout.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        // Hide loading indicator
        binding.loadingLayout.visibility = View.GONE
    }

    private fun showRecipeText(){
        // Show the recipe text
        binding.recipeLayout.visibility = View.VISIBLE
        binding.SaveButtonLayout.visibility = View.VISIBLE
        binding.addPanelLayout.visibility = View.GONE
    }

    private fun hideRecipeText(){
        // Hide the recipe text
        binding.recipeLayout.visibility = View.GONE
        binding.SaveButtonLayout.visibility = View.GONE
        binding.addPanelLayout.visibility = View.VISIBLE
    }

    private fun initializeUI() {
        // Set up the UI elements and logic here
        binding.FoodTypeOptions.adapter = ArrayAdapter( this, android.R.layout.simple_spinner_item, listOf("Meal", "Dessert", "Drink"))
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.addButton.setOnClickListener {
            // Add the new ingredient to the list
            addIngredient()
        }
        binding.clearButton.setOnClickListener {
            ingredients.clear()
            binding.ingredientBasketText.text = "-"
            binding.createButton.isEnabled = ingredients.isNotEmpty()
            binding.editIngredientName.text.clear()
            binding.editQtyText.text.clear()
            binding.editUnitText.text.clear()
        }
        binding.createButton.setOnClickListener {
            createRecipe()
        }
        binding.saveButton.setOnClickListener {
            saveRecipe()
        }
        binding.newRecipeButton.setOnClickListener {
            hideRecipeText()
            ingredients.clear()
            binding.ingredientBasketText.text = "-"
            binding.createButton.isEnabled = ingredients.isNotEmpty()
        }
        binding.createButton.isEnabled = ingredients.isNotEmpty()
    }

    private fun saveRecipe() {
        // Save the recipe to the database
        val dialog= RecipeNameDialogFragment { recipeName ->
            // This lambda function is called when the user presses "OK" on the dialog
            lifecycleScope.launch(Dispatchers.IO) {
                // Save the recipe to the database
                val newRecipe = SavedRecipe(name = recipeName, date = Date(System.currentTimeMillis()), text = binding.createRecipeText.text.toString())
                savedRecipeDatabase.savedRecipeDao().insertRecipe(newRecipe)
                // Show the toast on the main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateRecipeActivity, "Saved recipe successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(supportFragmentManager, "recipeNameDialog")
    }

    private fun createRecipe() {
        val foodType = binding.FoodTypeOptions.selectedItem.toString()
        val gender = sharedPreferences.getString("gender", "") ?: "-";
        val age = sharedPreferences.getInt("age", 0)
        val bmiValue = measurementTool.getBMI(
            sharedPreferences.getFloat("weight", 0f),
            (sharedPreferences.getFloat("height", 0f) / 100)
        )
        val bodyFatValue = measurementTool.calculateBodyFatPercentageSI(bmiValue, age, gender)
        // Create the prompt string
        val ingredientsString = ingredients.joinToString(", ")
        val prompt =
            "provide the healthy recipe of $foodType based on the following ingredients: $ingredientsString, the BMI and body fat percentage from the following information: gender=$gender, age= $age, bmi= $bmiValue, bodyFat= $bodyFatValue. Please return in format: (briefly explain why recommend this recipe)<new line> Menu name: (name of recipe)<new line> ingredients: (list of ingredients)<new line> steps: (list of steps)<new line> Tips: (list of tips), using emoticons for friendly messages"
        // Send the prompt to the API
        mainViewModel.sendPrompt(prompt)
    }

    private fun addIngredient() {
        // Add the new ingredient to the list
        val ingredientName = binding.editIngredientName.text.toString()
        val quantity = binding.editQtyText.text.toString()
        val unit = binding.editUnitText.text.toString()
        // Add the ingredient to the list
        if (ingredientName.isNotEmpty()){
            if(quantity.isNotEmpty()){
                if(unit.isNotEmpty()){
                    ingredients.add("- $quantity $unit of $ingredientName")
                }else{
                    binding.editUnitText.error = "Please enter a unit"
                    return
                }
            }else{
                ingredients.add("- $ingredientName")
            }
            binding.ingredientBasketText.text = ingredients.joinToString("\n")
            binding.createButton.isEnabled = ingredients.isNotEmpty()
            // Clear the input fields
            binding.editIngredientName.text.clear()
            binding.editQtyText.text.clear()
            binding.editUnitText.text.clear()
        }else{
            binding.editIngredientName.error = "Please enter an ingredient name"
            return
        }

    }
}