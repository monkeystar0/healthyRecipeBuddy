package com.example.healthyrecipebuddy.util

class MeasurementTool {

    val SEVERELY_THIN = 1
    val MODERATELY_THIN = 2
    val MILDLY_THIN = 3
    val NORMAL = 4
    val OVERWEIGHT = 5
    val OBESE_I = 6
    val OBESE_II = 7
    val OBESE_III = 8

    val SEDENTARY = 1
    val LIGHTLY_ACTIVE = 2
    val MODERATELY_ACTIVE = 3
    val VERY_ACTIVE = 4
    val EXTRA_ACTIVE = 5

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

    fun getBMIClassificationColor(bmi: Double): String {
        return when {
            bmi < 16 -> "#FFFA3535"
            bmi in 16.0..17.0 -> "#FFF6A169"
            bmi in 17.0..18.5 -> "#FFEFFA63"
            bmi in 18.5..25.0 -> "#B8FF8B"
            bmi in 25.0..30.0 -> "#FFEFFA63"
            bmi in 30.0..35.0 -> "#FFF6A169"
            bmi in 35.0..40.0 -> "#FFFA3535"
            else -> "#FFFA3535"
        }
    }

    fun calculateBodyFatPercentageSI(bmi: Float, age: Int, gender: String): Float {

        val bodyFatPercentage: Float

        if (gender == "Male") {
            bodyFatPercentage = (1.20f * bmi + 0.23f * age - 16.2f)
        } else if (gender =="Female") {
            bodyFatPercentage = (1.20f * bmi + 0.23f * age - 5.4f)
        } else {
            throw IllegalArgumentException("Invalid gender provided.")
        }

        return bodyFatPercentage
    }

    fun getBodyFatCategoryColor(bodyFatPercentage: Float, gender: String): String {
        val bodyFatPercentageValue = bodyFatPercentage / 100
        return when (gender) {
            "Female" -> when (bodyFatPercentageValue) {
                in 0.10f..0.1399f -> "#FFFA3535"
                in 0.14f..0.2099f -> "#FFEFFA63"
                in 0.21f..0.2499f -> "#B8FF8B35"
                in 0.25f..0.3199f -> "#86fa63"
                in 0.32f..1.0f -> "#FFFA3535"
                else -> "Unknown"
            }
            "Male" -> when (bodyFatPercentageValue) {
                in 0.02f..0.0599f -> "#FFFA3535"
                in 0.06f..0.1399f -> "#FFEFFA63"
                in 0.14f..0.1799f -> "#B8FF8B35"
                in 0.18f..0.2499f -> "#86fa63"
                in 0.25f..1.0f -> "#FFFA3535"
                else -> "Unknown"
            }
            else -> throw IllegalArgumentException("Invalid gender provided.")
        }
    }

    fun getBodyFatCategoryText(bodyFatPercentage: Float, gender: String): String {
        val bodyFatPercentageValue = bodyFatPercentage / 100
        return when (gender) {
            "Female" -> when (bodyFatPercentageValue) {
                in 0.10f..0.1399f -> "Essential fat"
                in 0.14f..0.2099f -> "Athletes"
                in 0.21f..0.2499f -> "Fitness"
                in 0.25f..0.3199f -> "Average"
                in 0.32f..1.0f -> "Obese"
                else -> "Unknown"
            }
            "Male" -> when (bodyFatPercentageValue) {
                in 0.02f..0.0599f -> "Essential fat"
                in 0.06f..0.1399f -> "Athletes"
                in 0.14f..0.1799f -> "Fitness"
                in 0.18f..0.2499f -> "Average"
                in 0.25f..1.0f -> "Obese"
                else -> "Unknown"
            }
            else -> throw IllegalArgumentException("Invalid gender provided.")
        }
    }

    fun calculateCaloriesNeeded(weightKg: Float, heightCm: Float, ageYears: Int, isMale: Boolean, activityLevel: Int): Double {
        val bmr: Double = if (isMale) {
            66.47 + (13.75 * weightKg) + (5.003 * heightCm) - (6.755 * ageYears)
        } else {
            655.1 + (9.563 * weightKg) + (1.85 * heightCm) - (4.676 * ageYears)
        }

        return when (activityLevel) {
            SEDENTARY -> bmr * 1.2
            LIGHTLY_ACTIVE -> bmr * 1.375
            MODERATELY_ACTIVE -> bmr * 1.55
            VERY_ACTIVE -> bmr * 1.725
            EXTRA_ACTIVE -> bmr * 1.9
            else -> {
                throw IllegalArgumentException("Invalid activity level provided.")
            }
        }
    }


}