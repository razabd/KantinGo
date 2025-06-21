package com.example.kantingo.data

import androidx.annotation.DrawableRes
import com.example.kantingo.R

data class FoodItem(
    val id: Int,
    val name: String,
    val price: Int
)

data class MenuItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    @DrawableRes val imageResId: Int,
    val foodItems: List<FoodItem>
)

object DummyData {
    val menuItems = listOf(
        MenuItem(
            id = 1,
            title = "Bubur Bandung AA",
            subtitle = "The best porridge in town",
            imageResId = R.drawable.gambar_bubur,
            foodItems = listOf(
                FoodItem(id = 101, name = "Bubur Ayam Spesial", 15000),
                FoodItem(id = 102, name = "Bubur Polos", 8000),
                FoodItem(id = 103, name = "Sate Ati Ampela", 4000)
            )
        ),
        MenuItem(
            id = 2,
            title = "Soto Ayam Lamongan",
            subtitle = "Soto legendaris dengan koya gurih",
            imageResId = R.drawable.gambar_soto,
            foodItems = listOf(
                FoodItem(id = 201, name = "Soto Ayam Biasa", 12000),
                FoodItem(id = 202, name = "Soto Ayam + Ceker", 15000),
                FoodItem(id = 203, name = "Nasi Putih", 3000)
            )
        ),
        MenuItem(
            id = 3,
            title = "Nasi Goreng Kebon Sirih",
            subtitle = "Nasi goreng kambing khas",
            imageResId = R.drawable.gambar_nasgor,
            foodItems = listOf(
                FoodItem(id = 301, name = "Nasi Goreng Kambing", 25000),
                FoodItem(id = 302, name = "Nasi Goreng Ayam", 20000)
            )
        )
    )
}