package com.example.traningforproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun addAllExercise(exercise: List<Data>)

    @Delete
    fun delete(exercise: List<Data>)

    @Query("SELECT * FROM Data")
    fun getAllExercise(): List<Data>
}




