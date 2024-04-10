package com.example.financeback

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeback.screens.GoalScreen
import com.example.financeback.screens.Screen
import com.example.financeback.screens.HomeScreen
import com.example.financeback.screens.IncomeScreen
import com.example.financeback.screens.ReportScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContent {
            FinanceBackScreen()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceBackScreen(modifier: Modifier = Modifier){
    val navController = rememberNavController()

    val items = listOf(Screen.Home, Screen.Income, Screen.Report, Screen.Goal)
    var screenTitle by remember { mutableStateOf("Bem Vindo") }

    Scaffold(
        topBar = { TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.background,
            ),
            title = { Text(screenTitle) }
        )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = screen.description, tint = MaterialTheme.colorScheme.background) },
                    label = { Text(text = screen.route)},
                    selected = currentDestination?.hierarchy?.any {it.route == Screen.Home.route} == true,
                    onClick = { navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true

                        screenTitle = screen.title
                    } }
                )
            }
        }
        }
    ){ innerPadding ->
        val context = LocalContext.current
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(context = context) }
            composable(Screen.Report.route) { ReportScreen() }
            composable(Screen.Goal.route) { GoalScreen() }
            composable(Screen.Income.route) { IncomeScreen(context = context) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFinanceBackScreen(){
    FinanceBackScreen()
}

