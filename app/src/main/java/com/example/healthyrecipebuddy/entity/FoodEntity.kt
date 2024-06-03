package com.example.healthyrecipebuddy.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.sql.Date

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val time: Time,
    val foodType: String,
    val foodName: String,
    val description: String,
    val calories: Float,

    )