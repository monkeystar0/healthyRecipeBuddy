package com.example.healthyrecipebuddy.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val date: Date,
    val time: Time,
    val foodType: Int,
    val foodName: String,
    val calories: Float,

    )