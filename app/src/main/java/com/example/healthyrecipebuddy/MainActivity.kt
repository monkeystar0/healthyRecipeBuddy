package com.example.healthyrecipebuddy

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.futured.donut.DonutSection
import com.example.healthyrecipebuddy.databinding.ActivityMainBinding
import com.example.healthyrecipebuddy.model.UiState
import com.example.healthyrecipebuddy.util.MeasurementTool
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var measurementTool: MeasurementTool
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uiState: StateFlow<UiState>
    private var initialSetup = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel = MainViewModel(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        measurementTool = MeasurementTool()
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        uiState = mainViewModel.uiState
        if (savedInstanceState == null) {
            // Call the API here
            initialSetup = true
        }
        setupVisualisation()
        setupMenuList()

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
                        binding.buddyRecommendTxt.text = uiState.response
                        hideLoadingIndicator()
                    }
                    is UiState.Error -> {
                        // Show an error message
                        Toast.makeText(this@MainActivity, uiState.error, Toast.LENGTH_SHORT).show()
                        hideLoadingIndicator()
                    }
                }
            }
        }

    }

    private fun setupMenuList() {
        binding.targetButton.setOnClickListener {
            val intent = Intent(this, SetTargetActivity::class.java)
            startActivity(intent)
        }
        binding.profileButton.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoadingIndicator() {
        binding.loadingLayout.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.loadingLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
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
        val activityLevel = sharedPreferences.getInt("target_exercise", 1)
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
            val targetBodyFatValue = measurementTool.calculateBodyFatPercentageSI(targetBmiValue, age, gender)
            val targetBodyFatText = "%.2f".format(targetBodyFatValue)
            val targetBodyFatColor = measurementTool.getBodyFatCategoryColor(targetBodyFatValue, gender)
            binding.bodyFatTargetValueText.text = "$targetBodyFatText %"
            binding.bdTargetCardView.setCardBackgroundColor(Color.parseColor(targetBodyFatColor))
        }
        //body fat setup
        val bodyFatValue = measurementTool.calculateBodyFatPercentageSI(bmiValue, age, gender)
        val bodyFatText = "%.2f".format(bodyFatValue)
        binding.bodyFatValueText.text = "$bodyFatText %"
        val bodyFatCategory = measurementTool.getBodyFatCategoryText(bodyFatValue, gender)
        binding.bodyFatCatagoryText.text = bodyFatCategory
        val bodyFatColor = measurementTool.getBodyFatCategoryColor(bodyFatValue, gender)
        binding.bdFatCardView.setCardBackgroundColor(Color.parseColor(bodyFatColor))
        //calories setup
        val caloriesNeeded = measurementTool.calculateCaloriesNeeded(weight, height, age, gender == "Male", activityLevel)
        val caloriesNeededText = "%.2f".format(caloriesNeeded)
        binding.caloriesNeedText.text = "Calories needed: $caloriesNeededText cal"

        val prompt = "provide the friendly greeting message with emoticons and a briefly recommendation for healthy eating based on the BMI and body fat percentage of the user's health from the following information: gender=$gender, age= $age, bmi= $bmiValue, bodyFat= $bodyFatValue"
        if(initialSetup){
            mainViewModel.sendPrompt(prompt)
            initialSetup = false
        }

        val section1 = DonutSection(
            name = "section_1",
            color = Color.parseColor("#FB1D32"),
            amount = 25f
        )

//        val section2 = DonutSection(
//            name = "section_2",
//            color = Color.parseColor("#FFB98E"),
//            amount = 28f
//        )

        binding.donutView.cap = 100f
        binding.donutView.submitData(listOf(section1))
    }

}