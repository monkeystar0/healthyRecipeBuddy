package com.example.healthyrecipebuddy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.healthyrecipebuddy.entity.SavedRecipe
import com.example.healthyrecipebuddy.util.DateConverter

@Dao
interface SavedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipe: SavedRecipe)

    @Query("SELECT * FROM saved_recipe")
    fun getAllRecipes(): LiveData<List<SavedRecipe>>

    @Query("DELETE FROM saved_recipe")
    suspend fun deleteAllSavedRecipes()

}

@Database(entities = [SavedRecipe::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class SavedRecipeDatabase : RoomDatabase() {
    abstract fun savedRecipeDao(): SavedRecipeDao
}