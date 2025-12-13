package com.example.gpa_calculatorversion1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gpa_calculatorversion1.utils.SettingsManager
import com.example.gpa_calculatorversion1.viewmodel.MainViewModel
import com.example.gpa_calculatorversion1.viewmodel.SemesterGroup
import androidx.compose.material.icons.filled.LockOpen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    settingsManager: SettingsManager,
    onManageClicked: () -> Unit,
    onTargetGPAClicked: () -> Unit,
    onInfoClicked: () -> Unit
) {
    val cgpa by viewModel.totalCGPA.collectAsState()
    val semesters by viewModel.semestersList.collectAsState()
    var showSetupDialog by remember { mutableStateOf(false) }
    var showDisableDialog by remember { mutableStateOf(false) }
    var isBiometricActive by remember { mutableStateOf(settingsManager.isBiometricEnabled()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My GPA Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onInfoClicked) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Grading Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        if (isBiometricActive) {
                            showDisableDialog = true
                        } else {
                            showSetupDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isBiometricActive) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Security Settings",
                            tint = if (isBiometricActive) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CGPACard(cgpa = cgpa)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Semesters Summary",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(semesters) { semester ->
                        SemesterSummaryCard(semester)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onManageClicked,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Text(
                            "Add / Edit\nSemester",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Button(
                        onClick = onTargetGPAClicked,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                    ) {
                        Text(
                            "Target\nGPA",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }


            if (showSetupDialog) {
                PinInputDialog(
                    title = "Set New PIN to Enable Security",
                    onPinSubmit = { newPin ->
                        settingsManager.setPin(newPin)
                        settingsManager.setBiometricEnabled(true)
                        isBiometricActive = true
                        showSetupDialog = false
                    },
                    onDismiss = { showSetupDialog = false }
                )
            }


            if (showDisableDialog) {
                AlertDialog(
                    onDismissRequest = { showDisableDialog = false },
                    title = { Text("Disable Security?") },
                    text = { Text("This will remove the fingerprint lock protection.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                settingsManager.setBiometricEnabled(false)
                                isBiometricActive = false
                                showDisableDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Disable")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDisableDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SemesterSummaryCard(semester: SemesterGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(semester.semesterName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = String.format("%.2f", semester.semesterGPA),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CGPACard(cgpa: Double) {
    val cardColor = when {
        cgpa >= 3.0 -> Color(0xFF4CAF50)
        cgpa >= 2.0 -> Color(0xFFFFC107)
        else -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total CGPA",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.2f", cgpa),
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}