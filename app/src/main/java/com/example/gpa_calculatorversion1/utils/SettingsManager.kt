package com.example.gpa_calculatorversion1.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("biometric_enabled", false)
    }
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }
    fun getPin(): String? {
        return prefs.getString("user_pin", null)
    }
    fun setPin(pin: String) {
        prefs.edit().putString("user_pin", pin).apply()
    }
}