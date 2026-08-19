package br.com.devfocus.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : AppDestination("home", "Início", Icons.Default.Home)
    object Favorites : AppDestination("favorites", "Favoritos", Icons.Default.Favorite)
    object History : AppDestination("history", "Histórico", Icons.AutoMirrored.Filled.List)

    companion object {
        val bottomNavItems = listOf(Home, Favorites, History)
    }
}
