package br.com.devfocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.devfocus.navigation.AppDestination
import br.com.devfocus.navigation.DevFocusNavHost
import br.com.devfocus.presentation.ViewModelFactory
import br.com.devfocus.presentation.home.HomeViewModel
import br.com.devfocus.ui.theme.DevFocusTheme
import br.com.devfocus.ui.theme.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val factory = ViewModelFactory(this)
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            val favoritesViewModel: br.com.devfocus.presentation.favorites.FavoritesViewModel = viewModel(factory = factory)

            DevFocusTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        NavigationBar(
                            containerColor = Surface,
                            contentColor = Color.White
                        ) {
                            AppDestination.bottomNavItems.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = br.com.devfocus.ui.theme.Primary,
                                        selectedTextColor = br.com.devfocus.ui.theme.Primary,
                                        unselectedIconColor = br.com.devfocus.ui.theme.TextSecondary,
                                        unselectedTextColor = br.com.devfocus.ui.theme.TextSecondary,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    DevFocusNavHost(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        favoritesViewModel = favoritesViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
