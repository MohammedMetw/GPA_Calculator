package com.example.gpa_calculatorversion1.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CourseDaoInstrumentedTest {

    private lateinit var db: GPADatabase
    private lateinit var dao: CourseDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GPADatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.courseDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertCourse_thenReadAll() = runBlocking {
        dao.insertCourse(
            CourseEntity(courseName = "Math", creditHours = 3, gradeLetter = "A", semester = "Semester 1")
        )

        val all = dao.getAllCourses().first()
        assertEquals(1, all.size)
        assertEquals("Math", all[0].courseName)
    }

    @Test
    fun updateCourse_updatesStoredValues() = runBlocking {
        dao.insertCourse(
            CourseEntity(courseName = "Physics", creditHours = 3, gradeLetter = "A", semester = "Semester 1")
        )

        val inserted = dao.getAllCourses().first().single()
        dao.updateCourse(inserted.copy(gradeLetter = "B"))

        val updated = dao.getAllCourses().first().single()
        assertEquals("B", updated.gradeLetter)
    }

    @Test
    fun deleteCourse_removesIt() = runBlocking {
        dao.insertCourse(
            CourseEntity(courseName = "CS", creditHours = 4, gradeLetter = "A", semester = "Semester 1")
        )

        val inserted = dao.getAllCourses().first().single()
        dao.deleteCourse(inserted)

        val all = dao.getAllCourses().first()
        assertEquals(0, all.size)
    }

    @Test
    fun deleteSemester_deletesOnlyThatSemester() = runBlocking {
        dao.insertAll(
            listOf(
                CourseEntity(courseName = "A", creditHours = 3, gradeLetter = "A", semester = "Semester 1"),
                CourseEntity(courseName = "B", creditHours = 3, gradeLetter = "B", semester = "Semester 1"),
                CourseEntity(courseName = "C", creditHours = 3, gradeLetter = "C", semester = "Semester 2")
            )
        )

        dao.deleteSemester("Semester 1")

        val all = dao.getAllCourses().first()
        assertEquals(1, all.size)
        assertEquals("Semester 2", all[0].semester)
    }
}
