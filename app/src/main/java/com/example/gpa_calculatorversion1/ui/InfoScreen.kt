package com.example.gpa_calculatorversion1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GradeInfo(val letter: String, val points: String, val range: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onBackClicked: () -> Unit
) {

    val gradingScale = listOf(
        GradeInfo("A+", "4.0", "97 - 100%"),
        GradeInfo("A", "4.0", "93 - 97%"),
        GradeInfo("A-", "3.7", "89 - 93%"),
        GradeInfo("B+", "3.3", "84 - 89%"),
        GradeInfo("B", "3.0", "80 - 84%"),
        GradeInfo("B-", "2.7", "76 - 80%"),
        GradeInfo("C+", "2.3", "73 - 76%"),
        GradeInfo("C", "2.0", "70 - 73%"),
        GradeInfo("C-", "1.7", "67 - 70%"),
        GradeInfo("D+", "1.3", "64 - 67%"),
        GradeInfo("D", "1.0", "60 - 64%"),
        GradeInfo("F", "0.0", "Below 60%")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grading Scale Info") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Grade", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Points", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Percentage", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
            }

            LazyColumn {
                items(gradingScale) { item ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.letter, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            Text(item.points, modifier = Modifier.weight(1f))
                            Text(item.range, modifier = Modifier.weight(1.5f), color = Color.Gray)
                        }
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}