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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.financeback.classes.MenuItem
import com.example.financeback.classes.User
import com.example.financeback.classes.UserInfo
import com.example.financeback.screens.LoginScreen
import com.example.financeback.screens.Screen
import com.example.financeback.screens.compose.ConfigurationsCompose
import com.example.financeback.screens.compose.EditCompose
import com.example.financeback.screens.compose.HomeCompose
import com.example.financeback.screens.compose.IncomeCompose
import com.example.financeback.screens.compose.ProfileCompose
import com.example.financeback.screens.compose.ReportCompose
import com.example.financeback.screens.compose.StockCompose
import com.example.financeback.ui.theme.AppTheme
import com.example.financeback.ui.theme.negativeLight
import com.example.financeback.utils.Utils
import kotlinx.coroutines.launch

object Globals{
    private var userID: Int = 0
    private var isStockEnabled: Boolean = true

    fun setUser(userID: Int) {
        this.userID = userID
    }

    fun getUser(): Int {
        return this.userID
    }

    fun setStock(isStockEnabled: Boolean) {
        this.isStockEnabled = isStockEnabled
    }

    fun getStock(): Boolean {
        return this.isStockEnabled
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        Globals.setUser(intent.getIntExtra("UserID", -1))
        setContent {
            AppTheme(false) {
                FinanceBackScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceBackScreen(modifier: Modifier = Modifier) {
    val user = User()
    val context = LocalContext.current
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
        userInfo = user.getUserByID(context, Globals.getUser())
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
                        id = Screen.Stock.route,
                        title = Screen.Stock.title,
                        contentDescription = Screen.Stock.description,
                        icon = Screen.Stock.icon,
                        sensitiveItem = false,
                        onClick = {navController.navigate(Screen.Stock.route) {
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
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
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
                        HomeCompose(context).HomeScreen(navController = navController)
                    }
                    composable(Screen.Report.route) {
                        ReportCompose(context).ReportScreen(navigateToEdit = {
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
                    composable(Screen.Income.route) { IncomeCompose(context).IncomeScreen() }

                    composable("${Screen.Edit.route}/{incomeID}",
                        listOf(navArgument(name = "incomeID"){
                            type = NavType.IntType
                        })) { bacStackEntry ->
                        bacStackEntry.arguments?.let { EditCompose(context).EditScreen(incomeInfo = Income(context, Globals.getUser()).getIncomeByID(it.getInt("incomeID")), navController = navController) }
                        screenTitle = Screen.Edit.title}

                    composable(Screen.Profile.route) {
                        ProfileCompose(context).ProfileScreen(
                            userInfo = userInfo,
                            updatedUser = { updatedUser = it })
                    }
                    composable(Screen.Settings.route) {
                        ConfigurationsCompose().ConfigurationsScreen()
                    }
                    composable(Screen.Stock.route) {
                        StockCompose(context).StockScreen()
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
            Image(painter = painterResource(R.drawable.baseline_person_48), contentDescription = null)
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
