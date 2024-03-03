package com.example.assignment.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.assignment.model.ApiResponse
import com.example.assignment.network.ProductService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.assignment.model.Product
import com.example.assignment.network.ProductService.Companion.BASE_URL
import com.example.assignment.room.ProductEntity
import com.example.assignment.roomdatabase.ProductDao
import com.example.assignment.roomdatabase.RoomViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Text
import java.io.File

class ProductRepository {
//    companion object {
        private val productService: ProductService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductService::class.java)
        }


        suspend fun fetchProducts(): List<Product> {
            return productService.getProducts()

        }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun postProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: File?
    ): ApiResponse {
        try {
            val productNameRequestBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
            val productTypeRequestBody = productType.toRequestBody("text/plain".toMediaTypeOrNull())
            val imageRequestBody = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = imageRequestBody?.let { MultipartBody.Part.createFormData("files[]", imageFile.name, it) }
            return productService.postProduct(productNameRequestBody, productTypeRequestBody, price, tax, imagePart)
        } catch (e: HttpException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

}