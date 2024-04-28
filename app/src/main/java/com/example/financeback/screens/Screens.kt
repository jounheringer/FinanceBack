package com.example.financeback.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val description: String, val icon: ImageVector, val title: String) {
    object Home : Screen("Home", "Home Screen", Icons.Filled.Home, "Bem Vindo")
    object Income : Screen("Income", "Add Income", Icons.Filled.Add, "Adicionar Nota Fiscal")
    object Report : Screen("Report", "Report Screen", Icons.AutoMirrored.Filled.List, "Gerenciar Notas Fiscais")
//    object Goal : Screen("Goal", "Goals Screen", Icons.Filled.Person, "Gerenciar Metas")
}