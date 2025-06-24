package com.example.kantingo.ui.pages

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kantingo.R
import com.example.kantingo.data.DummyData
import com.example.kantingo.data.FoodItem
import com.example.kantingo.data.MenuItem
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.ui.theme.KantinGoTheme
import com.example.kantingo.viewmodel.CartViewModel

val AvatarBackgroundColor = Color(0xFFE0B2B2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var expandedCardId by remember { mutableStateOf<Int?>(null) }

    val cartQuantities by cartViewModel.cart.collectAsState()
    val totalCartItems by cartViewModel.totalCartItems.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                    when (index) {
                        0 -> { /* Already on Menu Screen */ }
                        1 -> navController.navigate(AppRoutes.HISTORY_SCREEN) {
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                        2 -> navController.navigate(AppRoutes.PROFILE_SCREEN) {
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF0F0F0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Header(
                name = "Davidson Edgar",
                totalCartItems = totalCartItems,
                onCartClick = { navController.navigate(AppRoutes.CART_SCREEN) },
                onAvatarClick = { navController.navigate(AppRoutes.PROFILE_SCREEN) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "What would you like to do?",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuList(
                expandedCardId = expandedCardId,
                onCardClick = { id ->
                    expandedCardId = if (expandedCardId == id) null else id
                },
                cartQuantities = cartQuantities,
                onIncrease = cartViewModel::increaseQuantity,
                onDecrease = cartViewModel::decreaseQuantity
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    name: String,
    totalCartItems: Int,
    onCartClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
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
                },
                modifier = Modifier.clickable(onClick = onCartClick)
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AvatarBackgroundColor)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuList(
    expandedCardId: Int?,
    onCardClick: (Int) -> Unit,
    cartQuantities: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    val menuItems = DummyData.menuItems

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        menuItems.forEach { menuItem ->
            item(key = "card_${menuItem.id}") {
                MenuItemCard(
                    menuItem = menuItem,
                    onClick = { onCardClick(menuItem.id) }
                )
            }
            item(key = "details_${menuItem.id}") {
                AnimatedVisibility(visible = expandedCardId == menuItem.id) {
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = menuItem.imageResId),
                contentDescription = menuItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
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
                verticalArrangement = Arrangement.Bottom
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
            .padding(top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = foodItem.name)
                        Text(
                            text = "Rp ${foodItem.price}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onDecrease(foodItem.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurangi")
                        }
                        Text(
                            text = (cartQuantities[foodItem.id] ?: 0).toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = { onIncrease(foodItem.id) },
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
        MenuScreen(
            navController = rememberNavController(),
            cartViewModel = viewModel()
        )
    }
}