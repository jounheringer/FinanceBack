package com.example.financeback

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financeback.classes.Income
import com.example.financeback.ui.theme.AppTheme
import com.example.financeback.classes.MenuItem
import com.example.financeback.classes.User
import com.example.financeback.classes.UserInfo
import com.example.financeback.screens.LoginScreen
import com.example.financeback.screens.Screen
import com.example.financeback.screens.compose.ConfigurationsScreen
import com.example.financeback.screens.compose.EditScreen
import com.example.financeback.screens.compose.HomeScreen
import com.example.financeback.screens.compose.IncomeScreen
import com.example.financeback.screens.compose.ProfileScreen
import com.example.financeback.screens.compose.ReportScreen
import com.example.financeback.ui.theme.negativeLight
import com.example.financeback.utils.Utils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val userID = intent.getIntExtra("UserID", -1)

        setContent {
            AppTheme(false) {
                FinanceBackScreen(userID = userID, context = this)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceBackScreen(modifier: Modifier = Modifier, userID: Int, context: Context) {
    val user = User()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val items = listOf(Screen.Home, Screen.Income, Screen.Report)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var userInfo by remember { mutableStateOf(UserInfo()) }
    var updatedUser by remember { mutableStateOf(true) }
    var restartClicked by remember { mutableStateOf(false) }
    var screenTitle by remember { mutableStateOf(Screen.Home.title) }
    var helpClicked by remember { mutableStateOf(false) }

    if (updatedUser){
        userInfo = user.getUserByID(context, userID)
        updatedUser = false
    }
    
    if(helpClicked) {
        AlertDialog(onDismissRequest = { helpClicked = false },
            text = { Text(text = "Deseja baixar o guia de utilização do aplicativo?")},
            confirmButton = { Button(onClick = { helpClicked = false }) {
                Text(text = "Baixar")
            } },
            dismissButton = { Button(onClick = { helpClicked = false }) {
                Text(text = "Cancelar")
            } })
    }

    if(restartClicked) {
        AlertDialog(onDismissRequest = { restartClicked = false }, 
            text = { Text(text = "Ao restaurar o aplicativo sera reiniciado todos os usuarios e dados do aplicativo")},
            confirmButton = { Button(onClick = { restartClicked = false }) {
                Text(text = "Restaurar")
            } },
            dismissButton = { Button(onClick = { restartClicked = false }) {
                Text(text = "Cancelar")
            } })
    }

    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = { ModalDrawerSheet {
            DrawerHeader(modifier, userInfo)
            HorizontalDivider()
            DrawerItems(modifier = modifier,
                items = listOf(
                    MenuItem(
                        id = Screen.Profile.route,
                        title = Screen.Profile.title,
                        contentDescription = Screen.Profile.description,
                        icon = Screen.Profile.icon,
                        sensitiveItem = false,
                        onClick = {navController.navigate(Screen.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                }
                            scope.launch{drawerState.close() }
                            screenTitle = Screen.Profile.title}
                    ),
                    MenuItem(
                        id = Screen.Settings.route,
                        title = Screen.Settings.title,
                        contentDescription = Screen.Settings.description,
                        icon = Screen.Settings.icon,
                        sensitiveItem = false,
                        onClick = {navController.navigate(Screen.Settings.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                }
                        scope.launch{drawerState.close() }
                            screenTitle = Screen.Settings.title}
                    ),
                    MenuItem(
                        id = Screen.Help.route,
                        title = Screen.Help.title,
                        contentDescription = Screen.Help.description,
                        icon = Screen.Help.icon,
                        sensitiveItem = false,
                        onClick = {helpClicked = true
                                scope.launch{drawerState.close() }
                        }
                    ),
                    MenuItem(
                        id = Screen.LogOut.route,
                        title = Screen.LogOut.title,
                        contentDescription = Screen.LogOut.description,
                        icon = Screen.LogOut.icon,
                        sensitiveItem = false,
                        onClick = { Utils().logout(context, LoginScreen::class.java, "LogOut") }
                    ),
                    MenuItem(
                        id = Screen.Restart.route,
                        title = Screen.Restart.title,
                        contentDescription = Screen.Restart.description,
                        icon = Screen.Restart.icon,
                        sensitiveItem = true,
                        onClick = {restartClicked = true
                                scope.launch{drawerState.close() }
                        }
                    )
                ))
        } })
    {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.background,
                    ),
                    title = { Text(screenTitle) },
                    navigationIcon = {
                        IconButton(onClick = {scope.launch{ drawerState.open() }}) {
                            Image(
                                painterResource(R.drawable.user), "Perfil",
                                modifier.padding(10.dp, 0.dp)
                            )
                        }
                    }
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
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = screen.description,
                                    tint = MaterialTheme.colorScheme.background
                                )
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true

                                    screenTitle = screen.title
                                }
                            }
                        )
                    }
                }
            }
        )
        { innerPadding ->
            Surface(color = MaterialTheme.colorScheme.primary) {
                NavHost(
                    navController,
                    startDestination = Screen.Home.route,
                    Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(Screen.Report.route) {
                        ReportScreen(navigateToEdit = {
                            navController.navigate(Screen.Income.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                screenTitle = Screen.Income.title
                            }
                        }
                        )
                    }
                    composable(Screen.Income.route) { IncomeScreen(userID = userID) }

                    composable("${Screen.Edit.route}/{incomeID}",
                        listOf(navArgument(name = "incomeID"){
                            type = NavType.IntType
                        })) { bacStackEntry ->
                        bacStackEntry.arguments?.let { EditScreen(income = Income().getIncomeByID(context, it.getInt("incomeID")), userID = userID, navController = navController) }
                        screenTitle = Screen.Edit.title}

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            userInfo = userInfo,
                            updatedUser = { updatedUser = it })
                    }
                    composable(Screen.Settings.route) {
                        ConfigurationsScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(modifier: Modifier, userInfo: UserInfo) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(24.dp),
        contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(userInfo.iconImage), contentDescription = null)
            Spacer(modifier.height(10.dp))
            Text(text = userInfo.fullName, fontSize = 18.sp)
            Text(text = userInfo.userName, fontSize = 12.sp)
        }
    }
}

@Composable
fun DrawerItems(modifier: Modifier, items: List<MenuItem>) {
    Column {
        items.forEach { item ->
            Row(modifier = modifier
                .padding(16.dp, 8.dp)
                .fillMaxWidth()
                .height(32.dp)
                .clickable(onClick = item.onClick),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    tint = if (item.sensitiveItem) negativeLight else Color.Black)
                Spacer(modifier = modifier.width(10.dp))
                Text(text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.sensitiveItem) negativeLight else Color.Black)
            }
        }
    }
}
