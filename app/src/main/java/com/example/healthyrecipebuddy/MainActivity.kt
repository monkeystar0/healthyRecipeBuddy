package com.example.healthyrecipebuddy

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthyrecipebuddy.databinding.ActivityMainBinding
import com.example.healthyrecipebuddy.databinding.ActivityUserProfileSettingUpBinding
import com.example.healthyrecipebuddy.util.MeasurementTool

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var measurementTool: MeasurementTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        measurementTool = MeasurementTool()
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupVisualisation()
    }
    private fun setupVisualisation() {
        // Set up the visualisation here
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val weight = sharedPreferences.getFloat("weight", 0f)
        val targetWeight = sharedPreferences.getFloat("target_weight", 0f)
        val height = sharedPreferences.getFloat("height", 0f)
        val age = sharedPreferences.getInt("age", 0)
        val gender = sharedPreferences.getString("gender", "")?:"-"
        //BMI setup
        val bmiValue = measurementTool.getBMI(weight, (height/100))
        val bmiText = "%.2f".format(bmiValue)
        binding.bmiValueText.text = bmiText
        val bmiColor = measurementTool.getBMIClassificationColor(bmiValue.toDouble())
        binding.bmiCardView.setCardBackgroundColor(Color.parseColor(bmiColor))
        if(targetWeight == 0f){
            binding.bmiTargetValueText.text = "-"
            binding.bodyFatTargetValueText.text = "-"
        }else{
            val targetBmiValue = measurementTool.getBMI(targetWeight, (height/100))
            val targetBmiText = "%.2f".format(targetBmiValue)
            binding.bmiTargetValueText.text = targetBmiText
        }
        //body fat setup
        val bodyFatValue = measurementTool.calculateBodyFatPercentageSI(bmiValue, age, gender)
        val bodyFatText = "%.2f".format(bodyFatValue)
        binding.bodyFatValueText.text = "$bodyFatText %"
        val bodyFatCategory = measurementTool.getBodyFatCategoryText(bodyFatValue, gender)
        binding.bodyFatCatagoryText.text = bodyFatCategory
        val bodyFatColor = measurementTool.getBodyFatCategoryColor(bodyFatValue, gender)
        binding.bdFatCardView.setCardBackgroundColor(Color.parseColor(bodyFatColor))
    }

}