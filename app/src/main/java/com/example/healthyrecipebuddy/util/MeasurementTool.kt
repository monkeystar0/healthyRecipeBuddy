package com.example.healthyrecipebuddy.util

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
                in 0.10f..0.13f -> "#FFFA3535"
                in 0.14f..0.20f -> "#FFEFFA63"
                in 0.21f..0.24f -> "#B8FF8B"
                in 0.25f..0.31f -> "#B8FF8B"
                in 0.32f..1.0f -> "#FFFA3535"
                else -> "Unknown"
            }
            "Male" -> when (bodyFatPercentageValue) {
                in 0.02f..0.05f -> "#FFFA3535"
                in 0.06f..0.13f -> "#FFEFFA63"
                in 0.14f..0.17f -> "#B8FF8B"
                in 0.18f..0.24f -> "#B8FF8B"
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
                in 0.10f..0.13f -> "Essential fat"
                in 0.14f..0.20f -> "Athletes"
                in 0.21f..0.24f -> "Fitness"
                in 0.25f..0.31f -> "Average"
                in 0.32f..1.0f -> "Obese"
                else -> "Unknown"
            }
            "Male" -> when (bodyFatPercentageValue) {
                in 0.02f..0.05f -> "Essential fat"
                in 0.06f..0.13f -> "Athletes"
                in 0.14f..0.17f -> "Fitness"
                in 0.18f..0.24f -> "Average"
                in 0.25f..1.0f -> "Obese"
                else -> "Unknown"
            }
            else -> throw IllegalArgumentException("Invalid gender provided.")
        }
    }

}