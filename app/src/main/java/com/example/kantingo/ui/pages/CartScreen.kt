package com.example.kantingo.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.example.kantingo.ui.theme.KantinGoTheme
import com.example.kantingo.viewmodel.CartViewModel
import com.example.kantingo.viewmodel.GroupedCartItem
import java.text.NumberFormat
import java.util.*

@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val groupedItems by cartViewModel.groupedCartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF0F0F0)
    ) { paddingValues ->
        if (groupedItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Keranjang Anda masih kosong.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CartHeader(navController)
                }

                items(groupedItems, key = { it.vendor.id }) { groupedItem ->
                    VendorCartCard(
                        groupedItem = groupedItem,
                        onIncrease = { cartViewModel.increaseQuantity(it) },
                        onDecrease = { cartViewModel.decreaseQuantity(it) }
                    )
                }

                item {
                    TotalSummaryCard(totalPrice)
                }
            }
        }
    }
}

@Composable
private fun CartHeader(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Keranjang Saya",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VendorCartCard(
    groupedItem: GroupedCartItem,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
        }
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

@Composable
fun OutlinedIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    OutlinedButton(onClick = onClick, shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = modifier) {
        content()
    }
}


@Composable
fun TotalSummaryCard(totalPrice: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Rp ${NumberFormat.getNumberInstance(Locale.US).format(totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Arahkan ke Proses Pembayaran */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006970))
            ) {
                Text("Pesan & Bayar Sekarang", fontSize = 16.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    val previewViewModel = viewModel<CartViewModel>()
    previewViewModel.increaseQuantity(101)
    previewViewModel.increaseQuantity(101)
    previewViewModel.increaseQuantity(103)
    previewViewModel.increaseQuantity(201)

    KantinGoTheme {
        CartScreen(navController = rememberNavController(), cartViewModel = previewViewModel)
    }
}