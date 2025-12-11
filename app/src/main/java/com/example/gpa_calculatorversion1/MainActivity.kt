package com.example.gpa_calculatorversion1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.gpa_calculatorversion1.ui.HomeScreen
import com.example.gpa_calculatorversion1.ui.ManageScreen
import com.example.gpa_calculatorversion1.viewmodel.MainViewModel


enum class CurrentScreen {
    HOME,
    MANAGE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {

            var currentScreen by remember { mutableStateOf(CurrentScreen.HOME) }

            when (currentScreen) {
                CurrentScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onManageClicked = {
                            currentScreen = CurrentScreen.MANAGE
                        },
                        onTargetGPAClicked = {
                            val packageName = ".targetgpa"
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            if (intent != null) startActivity(intent)
                        }
                    )
                }
                CurrentScreen.MANAGE -> {
                    ManageScreen(
                        viewModel = viewModel,
                        onBackClicked = {
                            currentScreen = CurrentScreen.HOME
                        }
                    )
                }
            }
        }
    }
}