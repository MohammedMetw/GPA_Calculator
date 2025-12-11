package com.example.gpa_calculatorversion1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpa_calculatorversion1.database.CourseEntity
import com.example.gpa_calculatorversion1.database.GPADatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = GPADatabase.getDatabase(application).courseDao()

    private val allCoursesRaw = dao.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Initialize logic: If empty, create Semester 1
    init {
        viewModelScope.launch {
            val realCoursesFromDb = dao.getAllCourses().first()
            if (realCoursesFromDb.isEmpty()) {
                addEmptySemester(1)
            }
        }
    }

    val totalCGPA: StateFlow<Double> = allCoursesRaw.map { courses ->
        calculateGPA(courses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val semestersList: StateFlow<List<SemesterGroup>> = allCoursesRaw.map { courses ->
        courses.groupBy { it.semester }
            .map { (semName, semCourses) ->
                SemesterGroup(
                    semesterName = semName,
                    semesterGPA = calculateGPA(semCourses),
                    courses = semCourses.sortedBy { it.id } // Keep order of insertion
                )
            }.sortedBy { extractSemesterNumber(it.semesterName) } // Sort by number (Sem 1, Sem 2...)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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

    fun addOneCourseToSemester(semesterName: String) {
        viewModelScope.launch {
            dao.insertCourse(
                CourseEntity(
                    courseName = "",
                    creditHours = 0,
                    gradeLetter = "",
                    semester = semesterName
                )
            )
        }
    }

    fun addNextSemester() {
        viewModelScope.launch {
            val groups = semestersList.value
            val maxSem = groups.maxOfOrNull { extractSemesterNumber(it.semesterName) } ?: 0
            addEmptySemester(maxSem + 1)
        }
    }

    fun deleteSemesterAndReorder(semesterToDelete: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deletedNumber = extractSemesterNumber(semesterToDelete)

                dao.deleteSemester(semesterToDelete)

                val currentList = allCoursesRaw.value

                // Filter courses that need renaming
                val coursesToRename = currentList.filter {
                    extractSemesterNumber(it.semester) > deletedNumber
                }

                coursesToRename.forEach { course ->
                    val oldNum = extractSemesterNumber(course.semester)
                    val newNum = oldNum - 1
                    val updatedCourse = course.copy(semester = "Semester $newNum")
                    dao.updateCourse(updatedCourse)
                }
            }
        }
    }


    private suspend fun addEmptySemester(number: Int) {
        val newCourses = List(4) {
            CourseEntity(
                courseName = "",
                creditHours = 0,
                gradeLetter = "",
                semester = "Semester $number"
            )
        }
        dao.insertAll(newCourses)
    }

    private fun extractSemesterNumber(name: String): Int {
        return name.replace("Semester", "").trim().toIntOrNull() ?: 999
    }

    private fun calculateGPA(courses: List<CourseEntity>): Double {
        // Filter out incomplete courses before calculating
        val validCourses = courses.filter { it.gradeLetter.isNotEmpty() && it.creditHours > 0 }

        if (validCourses.isEmpty()) return 0.0

        var totalPoints = 0.0
        var totalHours = 0

        for (course in validCourses) {
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
            else -> 0.0 // F or empty
        }
    }
}