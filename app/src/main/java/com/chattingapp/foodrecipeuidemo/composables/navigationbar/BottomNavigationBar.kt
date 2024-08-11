package com.chattingapp.foodrecipeuidemo.composables.navigationbar

import android.os.SystemClock
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chattingapp.foodrecipeuidemo.R

@Composable
fun AppNavigationBar(navController: NavController) {
    var lastClickTimeFeed by remember { mutableStateOf(0L) }
    var lastClickTimeProfile by remember { mutableStateOf(0L) }
    val clickInterval = 10000L // 10 seconds
    val ICON_SIZE = 28.dp
    val ICON_SIZE_FEED = 32.dp
    var currentRoot by remember { mutableStateOf("home") }

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = if (currentRoot == "home") R.drawable.homeselected else R.drawable.homenotselected),
                    contentDescription = "Home",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },
            selected = currentRoot == "home",
            onClick = {
                currentRoot = "home"

                lastClickTimeFeed = 0
                lastClickTimeProfile = 0
                navController.navigate("home")
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = if (currentRoot == "search") R.drawable.searchselected else R.drawable.searchnotselected),
                    contentDescription = "Search",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },
            selected = currentRoot == "search",
            onClick = {
                currentRoot = "search"

                lastClickTimeProfile = 0
                lastClickTimeFeed = 0
                navController.navigate("search")
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = if (currentRoot == "create recipe") R.drawable.createrecipeselected else R.drawable.createrecipenotselected),
                    contentDescription = "Create Recipe",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },
            selected = currentRoot == "create recipe",
            onClick = {
                currentRoot = "create recipe"

                lastClickTimeFeed = 0
                lastClickTimeProfile = 0
                navController.navigate("create recipe")
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = if (currentRoot == "feed") R.drawable.feedselected else R.drawable.feednotselected),
                    contentDescription = "Feed",
                    modifier = Modifier.size(ICON_SIZE_FEED)
                )
            },
            selected = currentRoot == "feed",
            onClick = {
                val currentTime = SystemClock.elapsedRealtime()
                currentRoot = "feed"
                if (currentTime - lastClickTimeFeed >= clickInterval) {
                    lastClickTimeFeed = currentTime
                    lastClickTimeProfile = 0
                    navController.navigate("feed")
                }
            }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = if (currentRoot == "profile") R.drawable.profileselected else R.drawable.profilenotselected),
                    contentDescription = "Profile",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },
            selected = currentRoot == "profile",
            onClick = {
                currentRoot = "profile"

                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastClickTimeProfile >= clickInterval) {
                    lastClickTimeProfile = currentTime
                    lastClickTimeFeed = 0
                    navController.navigate("profile")
                }
            }
        )
    }
}
