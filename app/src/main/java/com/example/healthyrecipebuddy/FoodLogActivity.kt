package com.example.healthyrecipebuddy

import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.healthyrecipebuddy.db.FoodLogDatabase

class FoodLogActivity: AppCompatActivity(){
    private val foodLogDatabase: FoodLogDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FoodLogDatabase::class.java,
            "food_log_database"
        ).build()
    }
}