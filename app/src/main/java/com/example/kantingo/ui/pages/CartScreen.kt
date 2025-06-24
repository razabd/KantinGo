package com.example.kantingo.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kantingo.data.FoodItem
import com.example.kantingo.navigation.AppRoutes
import com.example.kantingo.ui.theme.KantinGoTheme
import com.example.kantingo.viewmodel.CartViewModel
import com.example.kantingo.viewmodel.DeliveryOption
import com.example.kantingo.viewmodel.GroupedCartItem
import com.example.kantingo.viewmodel.PaymentOption
import java.text.NumberFormat
import java.util.*

@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val groupedItems by cartViewModel.groupedCartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val deliveryOption by cartViewModel.selectedDeliveryOption.collectAsState()
    val paymentOption by cartViewModel.selectedPaymentOption.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF0F0F0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CartHeader(navController)
            Spacer(modifier = Modifier.height(16.dp))

            if (groupedItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Your cart is empty.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        OrderDetailsCard(
                            groupedItems = groupedItems,
                            totalPrice = totalPrice,
                            deliveryOption = deliveryOption,
                            paymentOption = paymentOption,
                            onIncrease = cartViewModel::increaseQuantity,
                            onDecrease = cartViewModel::decreaseQuantity,
                            onDeliveryOptionSelected = cartViewModel::selectDeliveryOption,
                            onPaymentOptionSelected = cartViewModel::selectPaymentOption,
                            onOrderClick = {
                                cartViewModel.placeOrder()
                                navController.navigate(AppRoutes.HISTORY_SCREEN) {
                                    popUpTo(AppRoutes.MENU_SCREEN)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsCard(
    groupedItems: List<GroupedCartItem>,
    totalPrice: Int,
    deliveryOption: DeliveryOption,
    paymentOption: PaymentOption,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onDeliveryOptionSelected: (DeliveryOption) -> Unit,
    onPaymentOptionSelected: (PaymentOption) -> Unit,
    onOrderClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            groupedItems.forEach { groupedItem ->
                Text(
                    text = groupedItem.vendor.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                groupedItem.items.forEach { (foodItem, quantity) ->
                    CartItemRow(
                        foodItem = foodItem,
                        quantity = quantity,
                        onIncrease = { onIncrease(foodItem.id) },
                        onDecrease = { onDecrease(foodItem.id) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Divider(modifier = Modifier.padding(bottom = 16.dp))
            Text("Delivery Option", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(
                    label = "Pick Up",
                    isSelected = deliveryOption == DeliveryOption.PICK_UP,
                    onClick = { onDeliveryOptionSelected(DeliveryOption.PICK_UP) }
                )
                SelectableChip(
                    label = "Deliver to Table",
                    isSelected = deliveryOption == DeliveryOption.DELIVER_TO_TABLE,
                    onClick = { onDeliveryOptionSelected(DeliveryOption.DELIVER_TO_TABLE) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Divider(modifier = Modifier.padding(bottom = 16.dp))
            Text("Payment Option", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(
                    label = "Cash",
                    isSelected = paymentOption == PaymentOption.CASH,
                    onClick = { onPaymentOptionSelected(PaymentOption.CASH) }
                )
                SelectableChip(
                    label = "QRIS",
                    isSelected = paymentOption == PaymentOption.QRIS,
                    onClick = { onPaymentOptionSelected(PaymentOption.QRIS) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Payment", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Rp ${NumberFormat.getNumberInstance(Locale.US).format(totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOrderClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006970))
            ) {
                Text("Order Now", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CartHeader(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "My Cart",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CartItemRow(
    foodItem: FoodItem,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = foodItem.name, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Rp ${NumberFormat.getNumberInstance(Locale.US).format(foodItem.price)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedIconButton(onClick = onDecrease, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Kurangi", modifier = Modifier.size(20.dp))
            }
            Text(
                text = quantity.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(24.dp)
            )
            OutlinedIconButton(onClick = onIncrease, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (isSelected) {
            { Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected") }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFB2E0E0),
            selectedLabelColor = Color(0xFF003333),
            selectedLeadingIconColor = Color(0xFF003333)
        )
    )
}

@Composable
fun OutlinedIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    OutlinedButton(onClick = onClick, shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = modifier) {
        content()
    }
}

@Preview(showBackground = true, name = "Cart Screen With Items")
@Composable
fun CartScreenWithItemsPreview() {
    val previewViewModel = viewModel<CartViewModel>()
    previewViewModel.increaseQuantity(101)
    previewViewModel.increaseQuantity(103)
    previewViewModel.increaseQuantity(201)
    previewViewModel.selectDeliveryOption(DeliveryOption.DELIVER_TO_TABLE)
    previewViewModel.selectPaymentOption(PaymentOption.QRIS)

    KantinGoTheme {
        CartScreen(navController = rememberNavController(), cartViewModel = previewViewModel)
    }
}