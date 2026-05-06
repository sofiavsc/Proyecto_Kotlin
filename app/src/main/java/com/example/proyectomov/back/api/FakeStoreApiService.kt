package com.example.proyectomov.back.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FakeStoreApiService {
    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int? = null): List<ProductDto>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ProductDto>

    @POST("products")
    suspend fun createProduct(@Body body: ProductUpsertDto): ProductDto

    @POST("carts")
    suspend fun createCart(@Body body: CartUpsertDto): CartDto

    @PUT("carts/{id}")
    suspend fun updateCart(@Path("id") id: Int, @Body body: CartUpsertDto): CartDto
}
