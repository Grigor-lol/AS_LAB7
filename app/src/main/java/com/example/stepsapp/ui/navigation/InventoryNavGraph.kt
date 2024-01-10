package com.example.stepsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stepsapp.ui.screens.HomeDestination
import com.example.stepsapp.ui.screens.HomeScreen
import com.example.stepsapp.ui.screens.NewRecordDestination
import com.example.stepsapp.ui.screens.NewRecordScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(navigateToRecordEntry = { navController.navigate(NewRecordDestination.route) })
        }
        composable(route = NewRecordDestination.route) {
            NewRecordScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
