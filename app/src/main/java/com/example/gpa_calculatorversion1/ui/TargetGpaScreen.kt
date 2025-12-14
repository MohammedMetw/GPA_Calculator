package com.example.gpa_calculatorversion1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetGpaScreen(
    onBack: () -> Unit
) {
    var currentGpa by remember { mutableStateOf("") }
    var completedCredits by remember { mutableStateOf("") }
    var remainingCredits by remember { mutableStateOf("") }
    var targetGpa by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Target GPA", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = currentGpa,
                onValueChange = { currentGpa = it },
                label = { Text("Current GPA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = completedCredits,
                onValueChange = { completedCredits = it },
                label = { Text("Completed Credit Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = remainingCredits,
                onValueChange = { remainingCredits = it },
                label = { Text("Remaining Credit Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetGpa,
                onValueChange = { targetGpa = it },
                label = { Text("Target GPA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    error = null
                    result = null

                    val cGpa = currentGpa.toDoubleOrNull()
                    val cCredits = completedCredits.toDoubleOrNull()
                    val rCredits = remainingCredits.toDoubleOrNull()
                    val tGpa = targetGpa.toDoubleOrNull()

                    if (cGpa == null || cCredits == null || rCredits == null || tGpa == null) {
                        error = "Please enter valid numeric values"
                        return@Button
                    }

                    if (rCredits <= 0) {
                        result = "You have no remaining credit hours.\nYour GPA cannot change."
                        return@Button
                    }

                    val totalCredits = cCredits + rCredits
                    val requiredGpa =
                        ((tGpa * totalCredits) - (cGpa * cCredits)) / rCredits

                    result = when {
                        requiredGpa > 4.0 ->
                            "Target GPA is NOT achievable.\nYou would need GPA higher than 4.0."

                        requiredGpa < 0 ->
                            "You already exceeded your target GPA."

                        else -> {
                            val rounded = String.format("%.2f", requiredGpa)
                            "You need GPA $rounded in the remaining credit hours."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Calculate", fontSize = 18.sp)
            }

            error?.let {
                Text(it, color = Color.Red, fontWeight = FontWeight.Bold)
            }

            result?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
