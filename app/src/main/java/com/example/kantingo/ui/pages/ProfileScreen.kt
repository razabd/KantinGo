// file: app/src/main/java/com/example/kantingo/ProfileScreen.kt

package com.example.kantingo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.ui.theme.KantinGoTheme
import com.example.kantingo.ui.theme.AvatarBackgroundColor // Imported AvatarBackgroundColor

/**
 * Composable function for the Profile screen.
 * Displays Davidson Edgar's profile details and includes the bottom navigation bar.
 *
 * @param navController The NavController to handle navigation actions.
 */
@Composable
fun ProfileScreen(navController: NavController) {
    // Set the selected item for the bottom navigation bar to "Profile" (index 2).
    var selectedItem by remember { mutableIntStateOf(2) }

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
                        1 -> navController.navigate(AppRoutes.HISTORY_SCREEN) {
                            // Pop up to the Menu screen and make it inclusive,
                            // clearing the back stack up to and including MenuScreen.
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                        2 -> { /* Already on Profile Screen, do nothing or handle refresh */ }
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
            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally.
            verticalArrangement = Arrangement.Center // Center content vertically (primary axis for column).
        ) {
            Spacer(modifier = Modifier.height(32.dp)) // Spacer for top margin.

            // Avatar for Davidson Edgar.
            Box(
                modifier = Modifier
                    .size(120.dp) // Set size for the avatar.
                    .clip(CircleShape) // Clip to a circle shape.
                    .background(AvatarBackgroundColor), // Apply a background color.
                contentAlignment = Alignment.Center // Center content inside the box.
            ) {
                Text(
                    text = "DE", // Initials.
                    color = Color.White, // White text color.
                    fontWeight = FontWeight.Bold, // Bold font weight.
                    fontSize = 48.sp // Large font size.
                )
            }
            Spacer(modifier = Modifier.height(24.dp)) // Spacer below the avatar.

            // Card displaying profile details.
            Card(
                modifier = Modifier.fillMaxWidth(0.8f), // Make card fill 80% of width.
                shape = RoundedCornerShape(16.dp), // Rounded corners.
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Add elevation.
                colors = CardDefaults.cardColors(containerColor = Color.White) // White background for the card.
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Make column fill card width.
                        .padding(24.dp), // Padding inside the card.
                    horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally.
                ) {
                    Text(
                        text = "Davidson Edgar", // Name.
                        style = MaterialTheme.typography.headlineSmall, // Apply headline small style.
                        fontWeight = FontWeight.Bold, // Bold font weight.
                        color = Color.Black // Black text color.
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Spacer.
                    Text(
                        text = "davidson.edgar@example.com", // Email.
                        style = MaterialTheme.typography.bodyLarge, // Body large style.
                        color = Color.Gray // Gray text color.
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Spacer.
                    Text(
                        text = "+62 812-3456-7890", // Phone number.
                        style = MaterialTheme.typography.bodyLarge, // Body large style.
                        color = Color.Gray // Gray text color.
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Spacer.
                    Text(
                        text = "Student at University of X", // Occupation/status.
                        style = MaterialTheme.typography.bodyLarge, // Body large style.
                        color = Color.Gray // Gray text color.
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    KantinGoTheme {
        // Provide a dummy NavController for preview purposes.
        ProfileScreen(rememberNavController())
    }
}
