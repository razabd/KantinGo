package com.example.kantingo.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.viewmodel.CartViewModel
import com.example.kantingo.viewmodel.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController, cartViewModel: CartViewModel) {
    var selectedItem by remember { mutableIntStateOf(1) }
    val orderHistory by cartViewModel.orderHistory.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                    when (index) {
                        0 -> navController.navigate(AppRoutes.MENU_SCREEN) {
                            popUpTo(AppRoutes.MENU_SCREEN) { inclusive = true }
                        }
                        1 -> { /* Already on History Screen */ }
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Order History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (orderHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You haven't made any orders yet.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(orderHistory.sortedByDescending { it.timestamp }) { order ->
                        OrderHistoryCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id.substring(0, 8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rp ${NumberFormat.getNumberInstance(Locale.US).format(order.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(order.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Delivery: ${order.deliveryOption.name.replace('_', ' ').lowercase().capitalize()}")
            Text("Payment: ${order.paymentOption.name}")
        }
    }
}