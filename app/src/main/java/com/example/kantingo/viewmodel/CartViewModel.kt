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

// Data class untuk menampung item yang sudah dikelompokkan
data class GroupedCartItem(
    val vendor: MenuItem,
    val items: List<Pair<FoodItem, Int>>, // List of (FoodItem, Quantity)
    val subtotal: Int
)

class CartViewModel : ViewModel() {

    private val _cart = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val cart = _cart.asStateFlow()

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

    // State baru untuk data yang sudah dikelompokkan per vendor <-- INI YANG BARU
    val groupedCartItems = cart.map { cartItems ->
        cartItems
            .mapNotNull { (foodId, quantity) ->
                val foodItem = allFoodItemsById[foodId]
                val vendor = vendorByFoodId[foodId]
                if (foodItem != null && vendor != null) {
                    Triple(vendor, foodItem, quantity)
                } else {
                    null
                }
            }
            .groupBy { it.first } // Group by vendor (MenuItem)
            .map { (vendor, triples) ->
                val items = triples.map { Pair(it.second, it.third) } // List of (FoodItem, Quantity)
                val subtotal = items.sumOf { (foodItem, quantity) -> foodItem.price * quantity }
                GroupedCartItem(vendor, items, subtotal)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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