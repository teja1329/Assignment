package com.example.assignment.model




data class ApiResponse(
    val message: String,
    val productDetails: Product?,
    val productId: Int,
    val success: Boolean
)


data class Product(

val image: String,
val price: Double,
val product_name: String,
val product_type: String,
val tax: Double
)


