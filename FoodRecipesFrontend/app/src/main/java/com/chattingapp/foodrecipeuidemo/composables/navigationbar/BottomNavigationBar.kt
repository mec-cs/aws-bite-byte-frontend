package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            selected = currentRoute(navController) == "home",
            onClick = { navController.navigate("home") }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            selected = currentRoute(navController) == "search",
            onClick = { navController.navigate("search") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create Recipe") },
            selected = currentRoute(navController) == "create recipe",
            onClick = { navController.navigate("create recipe") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Feed") },
            selected = currentRoute(navController) == "feed",
            onClick = { navController.navigate("feed") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            selected = currentRoute(navController) == "profile",
            onClick = { navController.navigate("profile") }
        )
        // Add more BottomNavigationItem for additional destinations
    }
}

@Composable
private fun currentRoute(navController: NavController): String? {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    return currentRoute
}
