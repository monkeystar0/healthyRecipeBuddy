package com.example.healthyrecipebuddy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthyrecipebuddy.entity.FoodLog
import com.example.healthyrecipebuddy.util.DateConverter
import java.io.Serializable
import java.sql.Date
import java.sql.Time


// Define the FoodLogDao interface
@Dao
interface FoodLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodLog(foodLog: FoodLog)

    @Query("SELECT * FROM food_log")
    fun getAllFoodLogs(): LiveData<List<FoodLog>>

//    @Query("SELECT * FROM food_log WHERE date = :date")
//    fun getFoodLogsByDate(date: Date): LiveData<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE date = :timestamp")
    fun getFoodLogsByDate(timestamp: Date): LiveData<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE time = :time")
    fun getFoodLogsByTime(time: Time): LiveData<List<FoodLog>>

    @Query("SELECT * FROM food_log WHERE date >= :date AND time BETWEEN :startTime AND :endTime")
    fun getFoodLogsBetweenTimes(date: Date, startTime: Time, endTime: Time): LiveData<List<FoodLog>>
}

// Define the FoodLogDatabase class
@Database(entities = [FoodLog::class], version = 3)
@TypeConverters(DateConverter::class)
abstract class FoodLogDatabase : RoomDatabase() {
    abstract fun foodLogDao(): FoodLogDao
}
