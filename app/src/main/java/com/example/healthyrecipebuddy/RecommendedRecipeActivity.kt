package com.example.healthyrecipebuddy

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.healthyrecipebuddy.databinding.ActivityRecommendedRecipeBinding
import com.example.healthyrecipebuddy.db.FoodLogDatabase
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime

class RecommendedRecipeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRecommendedRecipeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uiState: StateFlow<UiState>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var measurementTool: MeasurementTool

    private val foodLogDatabase: FoodLogDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FoodLogDatabase::class.java,
            "food_log_database"
        ).build()
    }

    private val savedRecipeDatabase: SavedRecipeDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            SavedRecipeDatabase::class.java,
            "saved_recipe_database").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendedRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = MainViewModel(this)
        uiState = mainViewModel.uiState
        measurementTool = MeasurementTool()
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

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
                        binding.recipeText.text = uiState.response
                        hideLoadingIndicator()
                    }
                    is UiState.Error -> {
                        // Show an error message
                        Toast.makeText(this@RecommendedRecipeActivity, uiState.error, Toast.LENGTH_SHORT).show()
                        hideLoadingIndicator()
                    }
                }
            }
        }

        setupUI()
    }

    private fun showLoadingIndicator() {
        binding.loadingProgress.visibility = View.VISIBLE
        binding.recipeText.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingProgress.visibility = View.GONE
        binding.recipeText.visibility = View.VISIBLE
    }

    private fun setupUI() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        val weight = sharedPreferences.getFloat("weight", 0f)
        val height = sharedPreferences.getFloat("height", 0f)
        val age = sharedPreferences.getInt("age", 0)
        val gender = sharedPreferences.getString("gender", "")?:"-"
        val activityLevel = sharedPreferences.getInt("target_exercise", 1)

        val bmiValue = measurementTool.getBMI(weight, (height/100))
        val bodyFatValue = measurementTool.calculateBodyFatPercentageSI(bmiValue, age, gender)

        val currentDate = LocalDate.now()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val sqlDate = formatter.parse(currentDate.toString())?.let { java.sql.Date(it.time) }

        val startTime = LocalTime.MIN
        val sqlStartTime = java.sql.Time(startTime.toNanoOfDay())
        val endTime = LocalTime.MAX
        val sqlEndTime = java.sql.Time(endTime.toNanoOfDay())

        val caloriesNeeded = measurementTool.calculateCaloriesNeeded(weight, height, age, gender == "Male", activityLevel)
        val caloriesNeededText = "%.2f".format(caloriesNeeded)
        var prompt = ""
        if (sqlDate != null) {
            foodLogDatabase.foodLogDao().getFoodLogsBetweenTimes(sqlDate, sqlStartTime, sqlEndTime).observe(this) { foodLogs ->
                val eatenFoods = foodLogs.joinToString(", ") { it.foodName }
                val  totalCalories = foodLogs.sumOf { item -> item.calories.toDouble() }

                prompt = "provide the healthy recipe for meal based on  the user's information, the BMI and body fat percentage from the following information: gender=$gender, age= $age, bmi= $bmiValue, bodyFat= $bodyFatValue . And the recommended recipe is based on the food consumption as the following: total intake calories today: $totalCalories cal, target calories: $caloriesNeededText cal, menu of eaten food: $eatenFoods. Please return in format: (briefly explain why recommend this recipe)<new line> Menu name: (name of recipe)<new line> ingredients: (list of ingredients)<new line> steps: (list of steps)<new line> Tips: (list of tips), and using emoticons for friendly messages."
                mainViewModel.sendPrompt(prompt)
            }
        }

        binding.regenBtn.setOnClickListener {
            mainViewModel.sendPrompt(prompt)
        }

        binding.saveBtn.setOnClickListener {
            val dialog= RecipeNameDialogFragment { recipeName ->
                // This lambda function is called when the user presses "OK" on the dialog
                lifecycleScope.launch(Dispatchers.IO) {
                    // Save the recipe to the database
                    val newRecipe = SavedRecipe(name = recipeName, date = Date(System.currentTimeMillis()), text = binding.recipeText.text.toString())
                    savedRecipeDatabase.savedRecipeDao().insertRecipe(newRecipe)
                    // Show the toast on the main thread
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RecommendedRecipeActivity, "Saved recipe successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.show(supportFragmentManager, "recipeNameDialog")
        }


    }

}