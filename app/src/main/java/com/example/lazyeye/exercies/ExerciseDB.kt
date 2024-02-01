package com.example.traningforproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Data::class] , version = 1 , exportSchema = false)
abstract class ExerciseDB() : RoomDatabase() {

    abstract fun DataDao(): DataDao
    companion object {
        private  var  INSTANCE: ExerciseDB? = null
        public lateinit var exercise_type:String

        fun getInstance(context: Context): ExerciseDB? {
            if (INSTANCE == null) {
                synchronized(ExerciseDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ExerciseDB::class.java, "exercise_data")
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE
        }


        fun destroyInstance() {
            INSTANCE = null
        }
    }
}