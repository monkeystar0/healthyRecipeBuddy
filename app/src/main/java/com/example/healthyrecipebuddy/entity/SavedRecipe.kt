package com.example.healthyrecipebuddy.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "saved_recipe")
data class SavedRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val name: String,
    val text: String
    )