package com.example.healthyrecipebuddy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.healthyrecipebuddy.adapter.FoodLogAdapter
import com.example.healthyrecipebuddy.databinding.ActivityFoodLogHistoryBinding
import com.example.healthyrecipebuddy.db.FoodLogDatabase
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

class FoodLogHistoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityFoodLogHistoryBinding
    private lateinit var adapter: FoodLogAdapter
    private lateinit var parentFragmentManager: FragmentManager

    private val foodLogDatabase: FoodLogDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FoodLogDatabase::class.java,
            "food_log_database"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodLogHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parentFragmentManager = supportFragmentManager
        adapter = FoodLogAdapter()
        binding.foodLogList.layoutManager = LinearLayoutManager(this)
        binding.foodLogList.adapter = adapter

        initializeUI()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initializeUI() {
        binding.progressBar2.visibility = View.VISIBLE
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -2) // Subtract 2 months from the current date
        binding.calendarView.minDate = calendar.timeInMillis
        val calendarMax = Calendar.getInstance() // Today for maxDate
        binding.calendarView.maxDate = calendarMax.timeInMillis
        binding.calendarView.date = calendarMax.timeInMillis

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.foodLogList.visibility = View.GONE
            val selectedCalendarDate = Calendar.getInstance().apply {set(year, month, dayOfMonth)
            }.time // Use Calendar for date manipulation
            val selectedDate = Date(selectedCalendarDate.time)
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val todayDate = formatter.parse(selectedDate.toString())
            val sqlDate = Date(todayDate?.time ?: java.util.Date().time)
            binding.progressBar2.visibility = View.VISIBLE

            foodLogDatabase.foodLogDao().getFoodLogsByDate(sqlDate).observe(this) { foodLogs ->
                adapter.submitList(foodLogs)
                if (foodLogs.isEmpty()) {
                    binding.foodLogList.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }else{
                    binding.foodLogList.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                }
                binding.progressBar2.visibility = View.GONE
            }
        }

        val currentDate = Date(binding.calendarView.date)
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayDate = formatter.parse(currentDate.toString())
        val sqlDate = Date(todayDate?.time ?: java.util.Date().time)
        foodLogDatabase.foodLogDao().getFoodLogsByDate(sqlDate).observe(this) { foodLogs ->
            adapter.submitList(foodLogs)
            binding.progressBar2.visibility = View.GONE
            if (foodLogs.isEmpty()) {
                binding.foodLogList.visibility = View.GONE
                binding.emptyText.visibility = View.VISIBLE
            }else{
                binding.foodLogList.visibility = View.VISIBLE
                binding.emptyText.visibility = View.GONE
            }
        }
    }
}