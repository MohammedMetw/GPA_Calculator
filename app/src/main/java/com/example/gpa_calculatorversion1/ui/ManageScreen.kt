package com.example.gpa_calculatorversion1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gpa_calculatorversion1.database.CourseEntity
import com.example.gpa_calculatorversion1.viewmodel.MainViewModel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.clickable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    viewModel: MainViewModel,
    onBackClicked: () -> Unit
) {
    val semesters by viewModel.semestersList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Courses") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(semesters) { semesterGroup ->
                SemesterSection(
                    semesterName = semesterGroup.semesterName,
                    courses = semesterGroup.courses,
                    onDeleteSemester = { viewModel.deleteSemesterAndReorder(semesterGroup.semesterName) },
                    onAddCourse = { viewModel.addOneCourseToSemester(semesterGroup.semesterName) },
                    onUpdateCourse = { viewModel.updateCourse(it) },
                    onDeleteCourse = { viewModel.deleteCourse(it) }
                )
            }

            item {
                Button(
                    onClick = { viewModel.addNextSemester() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Semester")
                }
                Spacer(modifier = Modifier.height(32.dp)) // Extra space at bottom
            }
        }
    }
}

@Composable
fun SemesterSection(
    semesterName: String,
    courses: List<CourseEntity>,
    onDeleteSemester: () -> Unit,
    onAddCourse: () -> Unit,
    onUpdateCourse: (CourseEntity) -> Unit,
    onDeleteCourse: (CourseEntity) -> Unit
) {

    var isExpanded by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = semesterName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDeleteSemester) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Semester", tint = Color.Gray)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                    Text("Course", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.3f).padding(start = 4.dp))
                    Text("Hrs", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(0.5f))
                    Text("Grade", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.2f))
                    Spacer(modifier = Modifier.width(32.dp))
                }

                courses.forEach { course ->
                    InlineCourseRow(
                        course = course,
                        onUpdate = onUpdateCourse,
                        onDelete = { onDeleteCourse(course) }
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                }

                TextButton(
                    onClick = onAddCourse,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Course")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineCourseRow(
    course: CourseEntity,
    onUpdate: (CourseEntity) -> Unit,
    onDelete: () -> Unit
) {
    val gradeOptions = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = course.courseName,
            onValueChange = { onUpdate(course.copy(courseName = it)) },
            placeholder = { Text("Course Name", fontSize = 10.sp) },
            modifier = Modifier.weight(1.3f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = if (course.creditHours == 0) "" else course.creditHours.toString(),
            onValueChange = {
                val newHours = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                onUpdate(course.copy(creditHours = newHours))
            },
            placeholder = { Text("Hrs", fontSize = 10.sp) },
            modifier = Modifier.weight(0.5f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1.2f)
        ) {
            OutlinedTextField(
                value = course.gradeLetter,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Grade", fontSize = 10.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                gradeOptions.forEach { grade ->
                    DropdownMenuItem(
                        text = { Text(text = grade) },
                        onClick = {
                            onUpdate(course.copy(gradeLetter = grade))
                            expanded = false
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove Course",
                tint = Color.Red.copy(alpha = 0.6f)
            )
        }
    }
}