package com.example.gpa_calculatorversion1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpa_calculatorversion1.database.CourseEntity
import com.example.gpa_calculatorversion1.database.GPADatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val dao = GPADatabase.getDatabase(application).courseDao()


    private val allCoursesRaw = dao.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCGPA: StateFlow<Double> = allCoursesRaw.map { courses ->
        calculateGPA(courses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    val semestersList: StateFlow<List<SemesterGroup>> = allCoursesRaw.map { courses ->

        courses.groupBy { it.semester }
            .map { (semName, semCourses) ->

                SemesterGroup(
                    semesterName = semName,
                    semesterGPA = calculateGPA(semCourses),
                    courses = semCourses
                )
            }.sortedBy { it.semesterName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addCourse(name: String, hours: Int, grade: String, semester: String) {
        viewModelScope.launch {
            dao.insertCourse(
                CourseEntity(
                    courseName = name,
                    creditHours = hours,
                    gradeLetter = grade,
                    semester = semester
                )
            )
        }
    }

    fun updateCourse(course: CourseEntity) {
        viewModelScope.launch {
            dao.updateCourse(course)
        }
    }

    fun deleteCourse(course: CourseEntity) {
        viewModelScope.launch {
            dao.deleteCourse(course)
        }
    }


    //  (GPA Calculator)
    private fun calculateGPA(courses: List<CourseEntity>): Double {
        if (courses.isEmpty()) return 0.0

        var totalPoints = 0.0
        var totalHours = 0

        for (course in courses) {
            val points = getPointsFromGrade(course.gradeLetter)
            totalPoints += points * course.creditHours
            totalHours += course.creditHours
        }

        return if (totalHours > 0) totalPoints / totalHours else 0.0
    }

    private fun getPointsFromGrade(grade: String): Double {
        return when (grade.uppercase()) {
            "A+", "A" -> 4.0
            "A-" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "B-" -> 2.7
            "C+" -> 2.3
            "C" -> 2.0
            "C-" -> 1.7
            "D+", "D" -> 1.0
            else -> 0.0 // F
        }
    }
}