package com.example.gpa_calculatorversion1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.example.gpa_calculatorversion1.viewmodel.MainViewModel
                                // for testing
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    viewModel: MainViewModel,
    onBackClicked: () -> Unit
) {

    var showAddDialog by remember { mutableStateOf(false) }


    var courseName by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("A") }
    var selectedSemester by remember { mutableStateOf("Semester 1") }


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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (semesters.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No courses added. Click + to start.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(semesters) { semesterGroup ->

                        Text(
                            text = semesterGroup.semesterName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        semesterGroup.courses.forEach { course ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(course.courseName, fontWeight = FontWeight.Bold)
                                        Text("${course.creditHours} Hours | Grade: ${course.gradeLetter}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { viewModel.deleteCourse(course) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }


        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Course") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = courseName,
                            onValueChange = { courseName = it },
                            label = { Text("Course Name") }
                        )
                        OutlinedTextField(
                            value = creditHours,
                            onValueChange = { creditHours = it },
                            label = { Text("Credit Hours (e.g. 3)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = selectedGrade,
                            onValueChange = { selectedGrade = it },
                            label = { Text("Grade (A, B, C...)") }
                        )
                        OutlinedTextField(
                            value = selectedSemester,
                            onValueChange = { selectedSemester = it },
                            label = { Text("Semester (e.g. Sem 1)") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (courseName.isNotEmpty() && creditHours.isNotEmpty()) {
                            viewModel.addCourse(courseName, creditHours.toIntOrNull() ?: 0, selectedGrade, selectedSemester)
                            showAddDialog = false
                            courseName = ""
                            creditHours = ""
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}