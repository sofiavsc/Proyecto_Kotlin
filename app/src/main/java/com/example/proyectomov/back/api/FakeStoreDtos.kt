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

data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    val password: String,
    val name: UserNameDto? = null,
    val address: UserAddressDto? = null,
    val phone: String? = null,
)

data class UserNameDto(
    val firstname: String,
    val lastname: String,
)

data class UserAddressDto(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String,
)

data class UserUpsertDto(
    val email: String,
    val username: String,
    val password: String,
    val name: UserNameDto,
    val address: UserAddressDto,
    val phone: String,
)

data class CreateUserRequestDto(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
)

/** Respuesta de POST /users; campos opcionales por si Gson o el API omiten algunos valores. */
data class CreateUserResponseDto(
    val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
)

data class LoginRequestDto(
    val username: String,
    val password: String,
)

data class LoginResponseDto(
    val token: String,
)
