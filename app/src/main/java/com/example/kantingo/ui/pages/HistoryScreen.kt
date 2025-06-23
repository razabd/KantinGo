package com.example.kantingo.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.ui.theme.KantinGoTheme

/**
 * Composable function for the History screen.
 * Displays a placeholder message and includes the bottom navigation bar for seamless navigation.
 *
 * @param navController The NavController to handle navigation actions.
 */
@Composable
fun HistoryScreen(navController: NavController) {
    // Set the selected item for the bottom navigation bar to "History" (index 1).
    var selectedItem by remember { mutableIntStateOf(1) }

    Scaffold(
        // Define the bottom navigation bar.
        bottomBar = {
            AppBottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    // Update the selected item state.
                    selectedItem = index
                    // Navigate based on the selected item index.
                    when (index) {
                        0 -> navController.navigate(AppRoutes.MENU_SCREEN) {
                            // Pop up to the Menu screen and make it inclusive,
                            // clearing the back stack up to and including MenuScreen.
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                        1 -> { /* Already on History Screen, do nothing or handle refresh */ }
                        2 -> navController.navigate(AppRoutes.PROFILE_SCREEN) {
                            // Pop up to the Menu screen and make it inclusive,
                            // clearing the back stack up to and including MenuScreen.
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF0F0F0) // Set a light grey background color.
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Make the column fill the entire available space.
                .padding(paddingValues) // Apply padding from the Scaffold (for bottom bar).
                .padding(16.dp), // Add general padding.
            verticalArrangement = Arrangement.Center, // Center content vertically.
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally.
        ) {
            Text(
                text = "History Screen Placeholder", // Placeholder text.
                style = MaterialTheme.typography.headlineMedium // Apply a medium headline style.
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    KantinGoTheme {
        // Provide a dummy NavController for preview purposes.
        HistoryScreen(rememberNavController())
    }
}