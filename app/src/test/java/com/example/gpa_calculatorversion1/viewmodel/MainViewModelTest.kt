package com.example.gpa_calculatorversion1.viewmodel

import android.app.Application
import com.example.gpa_calculatorversion1.database.CourseDao
import com.example.gpa_calculatorversion1.database.CourseEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var dao: CourseDao

    private fun course(
        id: Int,
        name: String,
        hours: Int,
        grade: String,
        semester: String
    ) = CourseEntity(
        id = id,
        courseName = name,
        creditHours = hours,
        gradeLetter = grade,
        semester = semester
    )

    @Test
    fun totalCGPA_calculatesWeightedGpa_correctly() = runTest {
        val flow = MutableStateFlow(
            listOf(
                course(1, "Math", 3, "A", "Semester 1"),   // 4.0 * 3 = 12
                course(2, "Physics", 3, "B", "Semester 1") // 3.0 * 3 = 9  => 21/6 = 3.5
            )
        )
        Mockito.`when`(dao.getAllCourses()).thenReturn(flow)

        val viewModel = MainViewModel(Application(), dao)
        val job = launch { viewModel.totalCGPA.collect { } }
        advanceUntilIdle()

        assertEquals(3.5, viewModel.totalCGPA.value, 0.0001)

        job.cancel()

        // Mockito verify (Lab-style)
        Mockito.verify(dao, Mockito.atLeastOnce()).getAllCourses()
    }

    @Test
    fun totalCGPA_ignoresIncompleteCourses() = runTest {
        val flow = MutableStateFlow(
            listOf(
                course(1, "Complete", 3, "A", "Semester 1"),
                course(2, "NoGrade", 3, "", "Semester 1"),      // ignored
                course(3, "NoHours", 0, "A", "Semester 1")      // ignored
            )
        )
        Mockito.`when`(dao.getAllCourses()).thenReturn(flow)

        val viewModel = MainViewModel(Application(), dao)
        val job = launch { viewModel.totalCGPA.collect { } }

        advanceUntilIdle()

        // Only first course counts => 4.0
        assertEquals(4.0, viewModel.totalCGPA.value, 0.0001)


        job.cancel()
    }

    @Test
    fun semestersList_groupsBySemester_andSortsByNumber() = runTest {
        val flow = MutableStateFlow(
            listOf(
                course(10, "Later", 3, "B", "Semester 2"),
                course(1, "Earlier", 3, "A", "Semester 1")
            )
        )
        Mockito.`when`(dao.getAllCourses()).thenReturn(flow)

        val viewModel = MainViewModel(Application(), dao)

        val job = launch { viewModel.semestersList.collect { } }  // ✅ collect first
        advanceUntilIdle()

        val semesters = viewModel.semestersList.value
        assertEquals(2, semesters.size)

        assertEquals("Semester 1", semesters[0].semesterName)
        assertEquals(4.0, semesters[0].semesterGPA, 0.0001)

        assertEquals("Semester 2", semesters[1].semesterName)
        assertEquals(3.0, semesters[1].semesterGPA, 0.0001)

        job.cancel()
    }

}
