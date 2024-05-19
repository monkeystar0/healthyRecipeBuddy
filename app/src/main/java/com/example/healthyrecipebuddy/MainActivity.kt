package com.example.healthyrecipebuddy

import android.content.SharedPreferences
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
        val weight = sharedPreferences.getString("weight", "0")?.toFloat() ?: 0f
        val height = sharedPreferences.getString("height", "0")?.toFloat() ?: 0f
        val age = sharedPreferences.getString("age", "0")?.toInt() ?: 0
        val bmiValue = measurementTool.getBMI(weight, height)
        val bmiText = "%.2f".format(bmiValue)
        binding.bmiValueText.text = bmiText

    }

}