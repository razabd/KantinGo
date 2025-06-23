// file: app/src/main/java/com/example/kantingo/MenuScreen.kt

package com.example.kantingo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Import for Preview only
import com.example.kantingo.data.DummyData
import com.example.kantingo.data.FoodItem
import com.example.kantingo.data.MenuItem
import com.example.kantingo.navigation.AppRoutes // Import AppRoutes for navigation
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.ui.theme.KantinGoTheme

val AvatarBackgroundColor = Color(0xFFE0B2B2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) { // Added navController parameter
    // State to track the currently selected item in the bottom navigation bar.
    // Menu is at index 0.
    var selectedItem by remember { mutableIntStateOf(0) }
    // State to track quantities of food items in the cart.
    var cartQuantities by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

    // Calculate the total number of items in the cart for the notification badge.
    val totalCartItems = cartQuantities.values.sum()

    // Function to increase the quantity of a food item in the cart.
    val onIncrease = { foodId: Int ->
        val currentQuantity = cartQuantities[foodId] ?: 0
        cartQuantities = cartQuantities.toMutableMap().apply {
            this[foodId] = currentQuantity + 1
        }
    }

    // Function to decrease the quantity of a food item in the cart.
    val onDecrease = { foodId: Int ->
        val currentQuantity = cartQuantities[foodId] ?: 0
        if (currentQuantity > 0) {
            cartQuantities = cartQuantities.toMutableMap().apply {
                if (currentQuantity == 1) {
                    this.remove(foodId) // Remove item if quantity becomes 0.
                } else {
                    this[foodId] = currentQuantity - 1
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    // Update the local selected item state for visual feedback.
                    selectedItem = index
                    // Handle navigation based on the selected bottom bar item.
                    when (index) {
                        0 -> { /* Already on Menu Screen, do nothing or handle refresh */ }
                        1 -> navController.navigate(AppRoutes.HISTORY_SCREEN) {
                            // Pop up to the Menu screen and make it inclusive to clear back stack.
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                        2 -> navController.navigate(AppRoutes.PROFILE_SCREEN) {
                            // Pop up to the Menu screen and make it inclusive to clear back stack.
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF0F0F0) // Light grey background for the scaffold.
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues) // Apply padding to account for the bottom bar.
                .padding(horizontal = 16.dp) // Horizontal padding for content.
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Top spacer.
            Header("Davidson Edgar", totalCartItems = totalCartItems) // Header with user name and cart count.
            Spacer(modifier = Modifier.height(24.dp)) // Spacer.
            Text(
                text = "What would you like to do?",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp)) // Spacer.
            MenuList(
                cartQuantities = cartQuantities,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(name: String, totalCartItems: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Welcome Back", fontSize = 14.sp, color = Color.Gray)
            Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BadgedBox(
                badge = {
                    if (totalCartItems > 0) {
                        Badge { Text(text = "$totalCartItems") }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(AvatarBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuList(
    cartQuantities: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    var expandedCardId by remember { mutableStateOf<Int?>(null) }
    val menuItems = DummyData.menuItems // Assuming DummyData.menuItems is available.

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        menuItems.forEach { menuItem ->
            item(key = "card_${menuItem.id}") {
                MenuItemCard(
                    menuItem = menuItem,
                    onClick = {
                        // Toggle the expanded state of the card.
                        expandedCardId = if (expandedCardId == menuItem.id) null else menuItem.id
                    }
                )
            }
            item(key = "details_${menuItem.id}") {
                AnimatedVisibility(visible = expandedCardId == menuItem.id) {
                    // Show food item details only if the card is expanded.
                    FoodItemDetailsCard(
                        menuItem = menuItem,
                        cartQuantities = cartQuantities,
                        onIncrease = onIncrease,
                        onDecrease = onDecrease
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(menuItem: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() }, // Make the card clickable.
        shape = RoundedCornerShape(16.dp), // Apply rounded corners.
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Add shadow.
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = menuItem.imageResId), // Load image from resources.
                contentDescription = menuItem.title,
                contentScale = ContentScale.Crop, // Crop image to fill bounds.
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay for better text readability.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom // Align content to the bottom.
            ) {
                Text(text = menuItem.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = menuItem.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, lineHeight = 18.sp)
            }
        }
    }
}


@Composable
fun FoodItemDetailsCard(
    menuItem: MenuItem,
    cartQuantities: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp), // Padding around the card.
        shape = RoundedCornerShape(16.dp), // Rounded corners.
        colors = CardDefaults.cardColors(containerColor = Color.White), // White background.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Shadow.
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Daftar Menu:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            menuItem.foodItems.forEach { foodItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // Vertical padding for each food item row.
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) { // Takes available space.
                        Text(text = foodItem.name)
                        Text(
                            text = "Rp ${foodItem.price}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between buttons and quantity.
                    ) {
                        // Decrement button.
                        IconButton(
                            onClick = { onDecrease(foodItem.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurangi")
                        }

                        // Quantity display.
                        Text(
                            text = (cartQuantities[foodItem.id] ?: 0).toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        // Increment button.
                        IconButton(
                            onClick = { onIncrease(foodItem.id) }, // Corrected foodId.id to foodItem.id
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    KantinGoTheme {
        // Pass a dummy NavController for preview purposes.
        MenuScreen(rememberNavController())
    }
}
