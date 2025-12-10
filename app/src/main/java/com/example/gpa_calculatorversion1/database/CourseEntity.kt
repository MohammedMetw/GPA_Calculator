package com.example.gpa_calculatorversion1.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses_table")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseName: String,
    val creditHours: Int,
    val gradeLetter: String,
    val semester: String
)