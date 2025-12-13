package com.example.gpa_calculatorversion1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gpa_calculatorversion1.ui.HomeScreen
import com.example.gpa_calculatorversion1.ui.InfoScreen
import com.example.gpa_calculatorversion1.ui.ManageScreen
import com.example.gpa_calculatorversion1.utils.SettingsManager
import com.example.gpa_calculatorversion1.ui.PinInputDialog
import com.example.gpa_calculatorversion1.viewmodel.MainViewModel


enum class CurrentScreen {
    HOME,
    MANAGE,
    INFO
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val settingsManager = SettingsManager(this)

        setContent {
            // متغيرات الحالة (State)
            var currentScreen by remember { mutableStateOf(CurrentScreen.HOME) }
            var isAppLocked by remember { mutableStateOf(settingsManager.isBiometricEnabled()) }
            var showUnlockPinDialog by remember { mutableStateOf(false) }


            fun authenticate() {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            isAppLocked = false
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            showUnlockPinDialog = true
                        }
                    })

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Login")
                    .setSubtitle("Log in to view your GPA")
                    .setNegativeButtonText("Use PIN")
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }

            LaunchedEffect(Unit) {
                if (isAppLocked) {
                    authenticate()
                }
            }


            if (isAppLocked) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { authenticate() }) {
                        Text("Unlock App")
                    }

                    if (showUnlockPinDialog) {
                        PinInputDialog(
                            title = "Enter PIN to Unlock",
                            onPinSubmit = { enteredPin ->
                                if (enteredPin == settingsManager.getPin()) {
                                    isAppLocked = false
                                    showUnlockPinDialog = false
                                } else {
                                    Toast.makeText(this@MainActivity, "Wrong PIN", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDismiss = {
                                //showUnlockPinDialog = TRUE
                            }
                        )
                    }
                }
            } else {
                when (currentScreen) {
                    CurrentScreen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            settingsManager = settingsManager,
                            onManageClicked = {
                                currentScreen = CurrentScreen.MANAGE
                            },
                            onTargetGPAClicked = {
                                val packageName = "com.example.gpa_calculatorversion1.targetgpa"
                                val intent = packageManager.getLaunchIntentForPackage(packageName)
                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@MainActivity, "Target GPA module not found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onInfoClicked = {
                                currentScreen = CurrentScreen.INFO
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
                    CurrentScreen.INFO -> {
                        InfoScreen(
                            onBackClicked = {
                                currentScreen = CurrentScreen.HOME
                            }
                        )
                    }
                }
            }
        }
    }
}