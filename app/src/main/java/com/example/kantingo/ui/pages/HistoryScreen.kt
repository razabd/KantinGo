package com.example.kantingo.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.components.AppBottomNavigationBar
import com.example.kantingo.ui.theme.KantinGoTheme
import com.example.kantingo.viewmodel.CartViewModel
import com.example.kantingo.viewmodel.DeliveryOption
import com.example.kantingo.viewmodel.Order
import com.example.kantingo.viewmodel.PaymentOption
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController, cartViewModel: CartViewModel) {
    var selectedItem by remember { mutableStateOf(1) }
    val orderHistory by cartViewModel.orderHistory.collectAsState()
    var expandedOrderId by remember { mutableStateOf<String?>(null) }

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
                    items(orderHistory.sortedByDescending { it.timestamp }, key = { it.id }) { order ->
                        OrderHistoryCard(
                            order = order,
                            cartViewModel = cartViewModel,
                            isExpanded = expandedOrderId == order.id,
                            onClick = {
                                expandedOrderId = if (expandedOrderId == order.id) null else order.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    cartViewModel: CartViewModel,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
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
                    color = Color.Black //
                )
            }
            Text(
                text = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date(order.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Delivery", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = order.deliveryOption.name.replace('_', ' ').lowercase(Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Payment", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = order.paymentOption.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }


            // Details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onClick)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Hide Details" else "Show Details",
                        tint = Color.Black
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded) {
                PurchasedItemsList(order = order, cartViewModel = cartViewModel)
            }
        }
    }
}

@Composable
fun PurchasedItemsList(order: Order, cartViewModel: CartViewModel) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Divider(modifier = Modifier.padding(bottom = 8.dp))

        order.items.forEach { (foodId, quantity) ->
            val foodItem = cartViewModel.getFoodItemById(foodId)
            if (foodItem != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${foodItem.name} x$quantity",
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Rp ${NumberFormat.getNumberInstance(Locale.US).format(foodItem.price * quantity)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Interactive History Card")
@Composable
fun OrderHistoryCardPreview() {
    val dummyOrder = Order(
        items = mapOf(101 to 2, 103 to 1, 201 to 1, 202 to 3),
        totalPrice = 88000,
        deliveryOption = DeliveryOption.DELIVER_TO_TABLE,
        paymentOption = PaymentOption.QRIS
    )
    var isExpanded by remember { mutableStateOf(false) }

    KantinGoTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            OrderHistoryCard(
                order = dummyOrder,
                cartViewModel = viewModel(),
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded }
            )
        }
    }
}