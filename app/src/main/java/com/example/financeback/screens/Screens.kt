package com.example.financeback.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import com.example.financeback.R

sealed class Screen(val route: String, val description: String, val icon: ImageVector, val title: String) {
    object Home : Screen("Home", "Home Screen", Icons.Filled.Home, "Bem Vindo")
    object Income : Screen("Income", "Add Income", Icons.Filled.Add, "Adicionar Nota Fiscal")
    object Edit : Screen("Edit", "Edit income", Icons.Filled.Create, "Editar Nota")
    object Report : Screen("Report", "Report Screen", Icons.AutoMirrored.Filled.List, "Gerenciar Notas Fiscais")
    object Profile : Screen("Profile", "Profile Screen", Icons.Filled.Person, "Perfil")
    object Settings : Screen("Settings", "Settings Screen", Icons.Filled.Build, "Configurações")
    object Help : Screen("Help", "Help Screen", Icons.Filled.Info, "Ajuda")
    object LogOut : Screen("LogOut", "LogOut Screen", Icons.AutoMirrored.Filled.ExitToApp, "Deslogar")
    object Restart : Screen("Restart", "Restart Screen", Icons.Filled.Warning, "Restaurar App")
    object Stock : Screen("Stock", "Stock Screen", Icons.Filled.ShoppingCart, "Estoque")
//    object Goal : Screen("Goal", "Goals Screen", Icons.Filled.Person, "Gerenciar Metas")
}