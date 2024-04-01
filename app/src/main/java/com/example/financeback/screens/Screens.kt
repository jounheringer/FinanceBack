package com.example.financeback.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val description: String, val icon: ImageVector) {
    object Home : Screen("Home", "Home Screen", Icons.Filled.Home)
    object Income : Screen("Income", "Add Income", Icons.Filled.Add)
    object Report : Screen("Report", "Report Screen", Icons.Filled.Star)
    object Goal : Screen("Goal", "Goals Screen", Icons.Filled.Person)
}