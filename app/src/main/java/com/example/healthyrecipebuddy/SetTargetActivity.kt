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

        val targetWeight = sharedPreferences.getFloat("target_weight", 0f)
        val targetExercise = sharedPreferences.getInt("target_exercise", 1)

        when (targetExercise) {
            1 -> binding.littleExerciseRadioButton.isChecked = true
            2 -> binding.lightExerciseRadioButton.isChecked = true
            3 -> binding.moderateExerciseRadioButton.isChecked = true
            4 -> binding.activeExerciseRadioButton.isChecked = true
            5 -> binding.extraExerciseRadioButton.isChecked = true
            else ->  binding.littleExerciseRadioButton.isChecked = true // Handle the case where no option is selected
        }

        binding.editTargetWeight.setText(targetWeight.toString())
        if(targetWeight > 0){
            calculateTarget()
        }else{
            binding.targetBMIText.text = "-"
            binding.targetBodyFatText.text = "- %"
        }

        binding.saveButton.setOnClickListener {
            if(validateInput()){
                saveTarget()
                finish()
            }
        }
        binding.cancelButton.setOnClickListener {
            finish()
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
        val selectedExerciseId = binding.targetExerciseRadioGroup.checkedRadioButtonId
        val selectedExercise = when (selectedExerciseId) {
            R.id.littleExerciseRadioButton -> 1
            R.id.lightExerciseRadioButton -> 2
            R.id.moderateExerciseRadioButton -> 3
            R.id.activeExerciseRadioButton -> 4
            R.id.extraExerciseRadioButton -> 5
            else -> 1 // Handle the case where no option is selected
        }
        sharedPreferences.edit().putFloat("target_weight", targetWeight).apply()
        sharedPreferences.edit().putInt("target_exercise", selectedExercise).apply()

    }
}