package com.example.assignment.network

import com.example.assignment.model.ApiResponse
import com.example.assignment.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.w3c.dom.Text
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.HashSet

interface ProductService {
    companion object {
        const val BASE_URL = "https://app.getswipe.in/api/public/"
    }

    @GET("get")
    suspend fun getProducts(): List<Product>

    @Multipart
    @POST("add")
    suspend fun postProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: Double,
        @Part("tax") tax: Double,
        @Part imageFile: MultipartBody.Part?
    ): ApiResponse
}





