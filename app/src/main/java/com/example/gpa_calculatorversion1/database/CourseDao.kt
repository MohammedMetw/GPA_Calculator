package com.example.gpa_calculatorversion1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface CourseDao {
    @Insert
    suspend fun insertCourse(course: CourseEntity)
    //Insert multiple courses at once
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)
    @Update
    suspend fun updateCourse(course: CourseEntity)
    @Delete
    suspend fun deleteCourse(course: CourseEntity)
    //Delete all courses in a specific semester
    @Query("DELETE FROM courses_table WHERE semester = :semesterName")
    suspend fun deleteSemester(semesterName: String)
    @Query("SELECT * FROM courses_table")
    fun getAllCourses(): Flow<List<CourseEntity>>
}