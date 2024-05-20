package com.example.healthyrecipebuddy

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyrecipebuddy.databinding.ActivitySetTargetBinding
import com.example.healthyrecipebuddy.util.MeasurementTool

class SetTargetActivity: AppCompatActivity()  {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivitySetTargetBinding
    private lateinit var measurementTool: MeasurementTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivitySetTargetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        measurementTool = MeasurementTool()

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        binding.targetBMIText.text = "-"
        binding.targetBodyFatText.text = "- %"
        binding.saveButton.setOnClickListener {
            if(validateInput()){
                saveTarget()
                finish()
            }
        }

        binding.editTargetWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the user is currently typing
                if (count > 0) {
                    calculateTarget()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun calculateTarget() {
        if(binding.editTargetWeight.text.toString().isNotEmpty() && binding.editTargetWeight.text.toString().toFloat() > 0){
            val height = sharedPreferences.getFloat("height", 0f)
            val age = sharedPreferences.getInt("age", 0)
            val gender = sharedPreferences.getString("gender", "")?:"-"
            val targetWeight = binding.editTargetWeight.text.toString().toFloat()
            val targetBMI = measurementTool.getBMI(targetWeight, (height/100))
            val targetBodyFat = measurementTool.calculateBodyFatPercentageSI(targetBMI, age, gender)
            binding.targetBMIText.text = "%.2f".format(targetBMI)
            val targetBodyFatText = "%.2f".format(targetBodyFat)
            binding.targetBodyFatText.text = "$targetBodyFatText %"
        }
    }
    private fun validateInput(): Boolean {
        // Validate name (not empty)
        if (binding.editTargetWeight.text.toString().isEmpty()) {
            binding.editTargetWeight.error = "Please enter your target weight"
            return false
        }
        return true

    }

    private fun saveTarget(){
        val targetWeight = binding.editTargetWeight.text.toString().toFloat()
        sharedPreferences.edit().putFloat("target_weight", targetWeight).apply()

    }
}