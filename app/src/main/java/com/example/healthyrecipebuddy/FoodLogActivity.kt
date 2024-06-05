package com.example.healthyrecipebuddy

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.healthyrecipebuddy.adapter.FoodLogAdapter
import com.example.healthyrecipebuddy.databinding.ActivityFoodLogBinding
import com.example.healthyrecipebuddy.db.FoodLogDatabase
import com.example.healthyrecipebuddy.entity.FoodLog
import com.example.healthyrecipebuddy.util.CustomDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime


class FoodLogActivity: AppCompatActivity(){
    private lateinit var binding: ActivityFoodLogBinding
    private lateinit var parentFragmentManager: FragmentManager
    private lateinit var adapter: FoodLogAdapter

    private val foodLogDatabase: FoodLogDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FoodLogDatabase::class.java,
            "food_log_database"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parentFragmentManager = supportFragmentManager
        adapter = FoodLogAdapter()
        binding.LogListRecycleView.layoutManager = LinearLayoutManager(this)
        binding.LogListRecycleView.adapter = adapter

        initializeUI()
        binding.saveBtn.setOnClickListener {
            if (validateInput()) {
                saveMenuLog()
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.historyLayout.setOnClickListener {
            val intent = Intent(this, FoodLogHistoryActivity::class.java)
            startActivity(intent)
        }
    }


    private fun initializeUI() {
        val currentDate = LocalDate.now()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val sqlDate = formatter.parse(currentDate.toString())?.let { Date(it.time) }

        val startTime = LocalTime.MIN
        val sqlStartTime = Time(startTime.toNanoOfDay())
        val endTime = LocalTime.MAX
        val sqlEndTime = Time(endTime.toNanoOfDay())
        binding.FoodTypeOptions.adapter = ArrayAdapter( this, android.R.layout.simple_spinner_item, listOf("Meal", "Dessert", "Drink"))
        if (sqlDate != null) {
            foodLogDatabase.foodLogDao().getFoodLogsBetweenTimes(sqlDate, sqlStartTime, sqlEndTime).observe(this) { foodLogs ->
                // Update the RecyclerView with the new data
                adapter.submitList(foodLogs)
                foodLogs.sumOf { item -> item.calories.toDouble() }.also { totalCalories -> binding.totalCalText.text = "$totalCalories cal" }
            }
        }

    }


    private fun validateInput(): Boolean {
        // Validate name (not empty)
        if (binding.editMenuNameText.text.toString().isEmpty()) {
            binding.editMenuNameText.error = "Please enter your menu's name"
            return false
        }
        return true
    }

    private fun saveMenuLog() {
        val menuName = binding.editMenuNameText.text.toString()
        val description = binding.editDescriptionText.text.toString()
        val calories = binding.editCaloriesText.text.toString().toFloat()
        val foodType = binding.FoodTypeOptions.selectedItem.toString()
        // Save the menu log to the database

        val currentDate = LocalDate.now()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayDate = formatter.parse(currentDate.toString())
        val sqlDate = Date(todayDate?.time ?: java.util.Date().time)
        val time = Time(System.currentTimeMillis())
        val foodLog = FoodLog(foodName = menuName, calories = calories, date = sqlDate, time = time, foodType = foodType, description = description)
        CoroutineScope(Dispatchers.IO).launch {

            foodLogDatabase.foodLogDao().insertFoodLog(foodLog)
            withContext(Dispatchers.Main) {
                clearInputFields()
                CustomDialogFragment().show(parentFragmentManager, "food_log_inserted_dialog")
            }
        }
    }

    private fun clearInputFields() {
        binding.editMenuNameText.text.clear()
        binding.editDescriptionText.text.clear()
        binding.editCaloriesText.text.clear()
    }


}