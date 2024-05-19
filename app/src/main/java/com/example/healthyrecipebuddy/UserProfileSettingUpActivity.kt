package com.example.healthyrecipebuddy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyrecipebuddy.databinding.ActivityUserProfileSettingUpBinding

class UserProfileSettingUpActivity : AppCompatActivity(){

    private lateinit var binding: ActivityUserProfileSettingUpBinding // View Binding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileSettingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("profile_complete", false)) {
            navigateToMainScreen()
            return
        }

        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                saveUserData()
                navigateToMainScreen()
            }
        }
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
            putString("age", binding.editAgeText.text.toString())
            putString("height", binding.editHeightText.text.toString())
            putString("weight", binding.editWeightText.text.toString())

            // ... save other values (height, weight, etc.) similarly
            putBoolean("profile_complete", true)
            apply()
        }
        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}