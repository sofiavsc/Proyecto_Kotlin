package com.example.proyectomov.back.api

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String? = null,
    val rating: RatingDto? = null,
)

data class RatingDto(
    val rate: Double,
    val count: Int,
)

data class ProductUpsertDto(
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String,
)

data class CartDto(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductDto>,
)

data class CartProductDto(
    val productId: Int,
    val quantity: Int,
)

data class CartUpsertDto(
    val userId: Int,
    val date: String,
    val products: List<CartProductDto>,
)
