package com.example.kantingo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kantingo.data.DummyData
import com.example.kantingo.data.FoodItem
import com.example.kantingo.data.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

enum class DeliveryOption { PICK_UP, DELIVER_TO_TABLE }

enum class PaymentOption { CASH, QRIS }

// Data class untuk menampung item yang sudah dikelompokkan
data class GroupedCartItem(
    val vendor: MenuItem,
    val items: List<Pair<FoodItem, Int>>, // List of (FoodItem, Quantity)
    val subtotal: Int
)

// Data class untuk menyimpan pesanan yang sudah selesai
data class Order(
    val id: String = UUID.randomUUID().toString(),
    val items: Map<Int, Int>,
    val totalPrice: Int,
    val deliveryOption: DeliveryOption,
    val paymentOption: PaymentOption,
    val timestamp: Long = System.currentTimeMillis()
)

class CartViewModel : ViewModel() {

    private val _cart = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val cart = _cart.asStateFlow()

    private val _selectedDeliveryOption = MutableStateFlow(DeliveryOption.PICK_UP)
    val selectedDeliveryOption = _selectedDeliveryOption.asStateFlow()

    private val _selectedPaymentOption = MutableStateFlow(PaymentOption.CASH)
    val selectedPaymentOption = _selectedPaymentOption.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory = _orderHistory.asStateFlow()


    private val allFoodItemsById: Map<Int, FoodItem> =
        DummyData.menuItems.flatMap { it.foodItems }.associateBy { it.id }

    private val vendorByFoodId: Map<Int, MenuItem> =
        DummyData.menuItems.flatMap { vendor ->
            vendor.foodItems.map { foodItem -> foodItem.id to vendor }
        }.toMap()

    val totalCartItems = cart.map { it.values.sum() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPrice = cart.map { cartItems ->
        cartItems.map { (foodId, quantity) ->
            (allFoodItemsById[foodId]?.price ?: 0) * quantity
        }.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val groupedCartItems = cart.map { cartItems ->
        cartItems
            .mapNotNull { (foodId, quantity) ->
                allFoodItemsById[foodId]?.let { foodItem ->
                    vendorByFoodId[foodId]?.let { vendor ->
                        Triple(vendor, foodItem, quantity)
                    }
                }
            }
            .groupBy { it.first }
            .map { (vendor, triples) ->
                val items = triples.map { Pair(it.second, it.third) }
                val subtotal = items.sumOf { (foodItem, quantity) -> foodItem.price * quantity }
                GroupedCartItem(vendor, items, subtotal)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDeliveryOption(option: DeliveryOption) {
        _selectedDeliveryOption.value = option
    }

    fun selectPaymentOption(option: PaymentOption) {
        _selectedPaymentOption.value = option
    }

    fun placeOrder() {
        if (_cart.value.isEmpty()) return // Jangan proses jika keranjang kosong

        val newOrder = Order(
            items = _cart.value,
            totalPrice = totalPrice.value,
            deliveryOption = _selectedDeliveryOption.value,
            paymentOption = _selectedPaymentOption.value
        )

        _orderHistory.value += newOrder // Tambahkan ke riwayat
        _cart.value = emptyMap() // Kosongkan keranjang
    }

    fun increaseQuantity(foodId: Int) {
        val currentCart = _cart.value.toMutableMap()
        currentCart[foodId] = (currentCart[foodId] ?: 0) + 1
        _cart.value = currentCart
    }

    fun decreaseQuantity(foodId: Int) {
        val currentCart = _cart.value.toMutableMap()
        val currentQuantity = currentCart[foodId] ?: 0
        if (currentQuantity > 1) {
            currentCart[foodId] = currentQuantity - 1
        } else {
            currentCart.remove(foodId)
        }
        _cart.value = currentCart
    }

    fun getFoodItemById(foodId: Int): FoodItem? {
        return allFoodItemsById[foodId]
    }
}