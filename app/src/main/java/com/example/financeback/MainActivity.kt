package com.example.financeback

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeback.classes.MenuItem
import com.example.financeback.classes.User
import com.example.financeback.classes.UserInfo
import com.example.financeback.screens.compose.Screen
import com.example.financeback.screens.compose.HomeScreen
import com.example.financeback.screens.compose.IncomeScreen
import com.example.financeback.screens.compose.ReportScreen
import com.example.financeback.ui.theme.Negative
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val user = User()
        val userID = intent.getIntExtra("UserID", 0)
        val userInfo = user.getUserByID(this, userID)

        setContent {
            FinanceBackScreen(userInfo = userInfo)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceBackScreen(modifier: Modifier = Modifier, userInfo: UserInfo) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val items = listOf(Screen.Home, Screen.Income, Screen.Report)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var screenTitle by remember { mutableStateOf(Screen.Home.title) }

    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = { ModalDrawerSheet {
            DrawerHeader(modifier, userInfo)
            HorizontalDivider()
            DrawerItems(modifier = modifier,
                items = listOf(
                    MenuItem(
                        id = "profile",
                        title = "Perfil",
                        contentDescription = "Ir para perfil",
                        icon = Icons.Filled.Person,
                        sensitiveItem = false
                    ),
                    MenuItem(
                        id = "settings",
                        title = "Configurações",
                        contentDescription = "Ir para configurações",
                        icon = Icons.Filled.Build,
                        sensitiveItem = false
                    ),
                    MenuItem(
                        id = "help",
                        title = "Ajuda",
                        contentDescription = "Ir para documentação",
                        icon = Icons.Filled.Info,
                        sensitiveItem = false
                    ),
                    MenuItem(
                        id = "logout",
                        title = "Deslogar",
                        contentDescription = "Deslogar",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        sensitiveItem = false
                    ),
                    MenuItem(
                        id = "restart",
                        title = "Restaurar App",
                        contentDescription = "Restaurar app",
                        icon = Icons.Filled.Warning,
                        sensitiveItem = true
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
            val context = LocalContext.current
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(context = context,
                        navigateTo = {
                            navController.navigate(Screen.Income.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                screenTitle = Screen.Income.title
                            }
                        })
                }
                composable(Screen.Report.route) {
                    ReportScreen(context = context,
                        navigateToEdit = {
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
                composable(Screen.Income.route) { IncomeScreen(context = context,) }
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
                .clickable { /*TODO*/ },
                verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    tint = if (item.sensitiveItem) Negative else Color.Black)
                Spacer(modifier = modifier.width(10.dp))
                Text(text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.sensitiveItem) Negative else Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFinanceBackScreen(){
    FinanceBackScreen(userInfo = UserInfo())
}

