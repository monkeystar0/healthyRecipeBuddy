package com.example.healthyrecipebuddy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.healthyrecipebuddy.entity.FoodLog
import java.sql.Time
import java.util.Date

// Define the FoodLogDao interface
@Dao
interface FoodLogDao {
    @Insert
    suspend fun insertFoodLog(foodLog: FoodLog)

    @Query("SELECT * FROM food_log")
    fun getAllFoodLogs(): LiveData<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE date = :date")
    fun getFoodLogsByDate(date: Date): LiveData<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE time = :time")
    fun getFoodLogsByTime(time: Time): LiveData<List<FoodLog>>
}

// Define the FoodLogDatabase class
@Database(entities = [FoodLog::class], version = 1)
abstract class FoodLogDatabase : RoomDatabase() {
    abstract fun foodLogDao(): FoodLogDao
}
