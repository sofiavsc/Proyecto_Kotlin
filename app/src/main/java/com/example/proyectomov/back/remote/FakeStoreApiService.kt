package com.example.proyectomov.back.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FakeStoreApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductDto

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ProductDto>

    @POST("products")
    suspend fun createProduct(@Body body: ProductUpsertDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body body: ProductUpsertDto): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): ProductDto

    @GET("carts")
    suspend fun getCarts(): List<CartDto>

    @GET("carts/{id}")
    suspend fun getCart(@Path("id") id: Int): CartDto

    @GET("carts/user/{userId}")
    suspend fun getCartsByUser(@Path("userId") userId: Int): List<CartDto>

    @POST("carts")
    suspend fun createCart(@Body body: CartUpsertDto): CartDto

    @PUT("carts/{id}")
    suspend fun updateCart(@Path("id") id: Int, @Body body: CartUpsertDto): CartDto

    @DELETE("carts/{id}")
    suspend fun deleteCart(@Path("id") id: Int): CartDto

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserDto

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequestDto): CreateUserResponseDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body body: UserUpsertDto): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): UserDto

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto
}
