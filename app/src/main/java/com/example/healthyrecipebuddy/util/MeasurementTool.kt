package com.example.healthyrecipebuddy.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

class MeasurementTool {

    public val SEVERELY_THIN = 1
    public val MODERATELY_THIN = 2
    public val MILDLY_THIN = 3
    public val NORMAL = 4
    public val OVERWEIGHT = 5
    public val OBESE_I = 6
    public val OBESE_II = 7
    public val OBESE_III = 8

    fun getBMI(weight: Float, height: Float): Float{
        val bmi = weight / (height * height)
        return bmi
    }

    fun getBMIClassification(bmi: Double): Int {
        return when {
            bmi < 16 -> SEVERELY_THIN
            bmi in 16.0..17.0 -> MODERATELY_THIN
            bmi in 17.0..18.5 -> MILDLY_THIN
            bmi in 18.5..25.0 -> NORMAL
            bmi in 25.0..30.0 -> OVERWEIGHT
            bmi in 30.0..35.0 -> OBESE_I
            bmi in 35.0..40.0 -> OBESE_II
            else -> OBESE_III
        }
    }
}