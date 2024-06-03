package com.example.healthyrecipebuddy.util

import androidx.room.TypeConverter
import java.sql.Date
import java.sql.Time

class DateConverter {

    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTimestampToTime(value: Long?): Time? {
        return if (value == null) null else Time(value)
    }

    @TypeConverter
    fun timeToTimestamp(time: Time?): Long? {
        return time?.time
    }
}