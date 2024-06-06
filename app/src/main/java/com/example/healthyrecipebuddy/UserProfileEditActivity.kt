package com.example.healthyrecipebuddy

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.healthyrecipebuddy.databinding.ActivityUserProfileUpdateBinding
import com.example.healthyrecipebuddy.db.FoodLogDatabase
import com.example.healthyrecipebuddy.db.SavedRecipeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileEditActivity: AppCompatActivity()  {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityUserProfileUpdateBinding
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
        binding = ActivityUserProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        initializeUI()
        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                saveUserData()
                finish()
            }
        }
        binding.cancelButton.setOnClickListener {
            finish()
        }
        binding.resetButton.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Reset Confirmation")
            .setMessage("Do you confirm to reset all user information?")
            .setPositiveButton("OK") { _, _ ->
                resetUserInformation()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetUserInformation() {
        lifecycleScope.launch(Dispatchers.IO) {
            foodLogDatabase.foodLogDao().deleteAllFoodLogs()
            savedRecipeDatabase.savedRecipeDao().deleteAllSavedRecipes()
        }
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "User information has been reset!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initializeUI() {
        val name = sharedPreferences.getString("name", "")
        val gender = sharedPreferences.getString("gender", "")
        val age = sharedPreferences.getInt("age", 0)
        val height = sharedPreferences.getFloat("height", 0f)
        val weight = sharedPreferences.getFloat("weight", 0f)
        binding.editNameText.setText(name)
        binding.editAgeText.setText(age.toString())
        binding.editHeightText.setText(height.toString())
        binding.editWeightText.setText(weight.toString())
    }

    private fun validateInput(): Boolean {
        // Validate name (not empty)
        if (binding.editNameText.text.toString().isEmpty()) {
            binding.editNameText.error = "Please enter your name"
            return false
        }

        if (binding.editAgeText.text.toString().isEmpty() || binding.editAgeText.text.toString() == "0") {
            binding.editAgeText.error = "Please enter your age"
            return false
        }

        if (binding.editHeightText.text.toString().isEmpty() || binding.editHeightText.text.toString() == "0") {
            binding.editHeightText.error = "Please enter your height"
            return false
        }

        if (binding.editWeightText.text.toString().isEmpty() || binding.editWeightText.text.toString() == "0") {
            binding.editWeightText.error = "Please enter your weight"
            return false
        }

        // Validate height, weight, fat% (positive numbers)
        // You'll need to add similar validation for other numeric fields

        return true // All inputs are valid
    }

    private fun saveUserData() {
        val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
        val selectedGender = when (selectedGenderId) {
            R.id.maleRadioButton -> "Male"
            R.id.femaleRadioButton -> "Female"
            else -> "" // Handle the case where no option is selected
        }

        with(sharedPreferences.edit()) {
            putString("gender", selectedGender)
            putString("name", binding.editNameText.text.toString())
            putInt("age", binding.editAgeText.text.toString().toInt())
            putFloat("height", binding.editHeightText.text.toString().toFloat())
            putFloat("weight", binding.editWeightText.text.toString().toFloat())

            // ... save other values (height, weight, etc.) similarly
            putBoolean("profile_complete", true)
            apply()
        }
        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
    }
}