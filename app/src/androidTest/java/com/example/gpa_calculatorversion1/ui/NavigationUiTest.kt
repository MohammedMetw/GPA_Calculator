package com.example.gpa_calculatorversion1.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gpa_calculatorversion1.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun home_to_manage_and_back_works() {
        // Home screen visible
        composeRule.onNodeWithText("My GPA Dashboard").assertIsDisplayed()

        // Click button (text contains a newline in your UI, so use substring)
        composeRule.onNodeWithText("Add / Edit", substring = true).performClick()

        // Manage screen visible
        composeRule.onNodeWithText("Manage Courses").assertIsDisplayed()

        // Tap back icon (you set contentDescription="Back" in ManageScreen)
        composeRule.onNodeWithContentDescription("Back").performClick()

        // Back on home
        composeRule.onNodeWithText("My GPA Dashboard").assertIsDisplayed()
    }

    @Test
    fun home_shows_cgpa_card_label() {
        // Simple UI assertion that's easy to explain/demo
        composeRule.onNodeWithText("Total CGPA").assertIsDisplayed()
    }
}
