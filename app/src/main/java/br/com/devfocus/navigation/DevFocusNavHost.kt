package br.com.devfocus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.devfocus.presentation.favorites.FavoritesScreen
import br.com.devfocus.presentation.history.HistoryScreen
import br.com.devfocus.presentation.home.HomeScreen
import br.com.devfocus.presentation.home.HomeViewModel

@Composable
fun DevFocusNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(viewModel = homeViewModel)
        }
        composable(AppDestination.Favorites.route) {
            FavoritesScreen()
        }
        composable(AppDestination.History.route) {
            HistoryScreen()
        }
    }
}
