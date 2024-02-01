package com.example.traningforproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Int ,
    @ColumnInfo(name = "Description_of_exercise") val des: String ,
    @ColumnInfo(name = "Image_of_exercise") val image: String
)
