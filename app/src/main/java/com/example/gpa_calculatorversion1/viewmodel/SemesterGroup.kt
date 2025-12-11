package com.example.gpa_calculatorversion1.viewmodel

import com.example.gpa_calculatorversion1.database.CourseEntity

data class SemesterGroup(
    val semesterName: String,
    val semesterGPA: Double,
    val courses: List<CourseEntity>
)