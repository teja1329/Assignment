package com.example.assignment.ViewModel

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.frontend.ProductListScreen
import com.example.assignment.model.Product
import kotlinx.coroutines.launch
import com.example.assignment.repository.ProductRepository
import com.example.assignment.room.ProductEntity
import com.example.assignment.roomdatabase.RoomViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProductListViewModel(private val roomViewModel: RoomViewModel) : ViewModel() {
    var productList: List<Product> by mutableStateOf(emptyList())
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var isOffline: Boolean by mutableStateOf(false)
        private set

    private val repository = ProductRepository()

    fun fetchProductData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val products: List<Product> = repository.fetchProducts()
                productList = products
                isOffline = false
                val productEntities = roomViewModel.getAllProducts()
                if (productEntities.isEmpty()) {
                    val products: List<Product> = repository.fetchProducts()

                    products.forEach { product ->
                        val productEntity = ProductEntity(
                            productName = product.product_name ?: "",
                            productType = product.product_type ?: "",
                            price = product.price ?: 0.0,
                            tax = product.tax ?: 0.0,
                            imageUrl = product.image ?: ""
                        )
                        roomViewModel.insertProduct(productEntity)
                    }
                }

            } catch (e: Exception) {
                isOffline = true

                val productEntities = roomViewModel.getAllProducts()
                productList = productEntities.map {
                    Product(
                        product_name = it.productName,
                        product_type = it.productType,
                        price = it.price,
                        tax = it.tax,
                        image = it.imageUrl
                    )
                }
            } finally {
                isLoading = false
            }
        }
    }


    suspend fun getOfflineProductList(): List<Product> {
        return withContext(Dispatchers.IO) {
            val productEntities = roomViewModel.getAllProducts()
            productEntities.map {
                Product(
                    product_name = it.productName,
                    product_type = it.productType,
                    price = it.price,
                    tax = it.tax,
                    image = it.imageUrl
                )
            }
        }
    }

}




@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun App(viewModel: ProductListViewModel, navigateToAddProduct: () -> Unit) {

    ProductListScreen.ProductListScreenShow(viewModel = viewModel, navigateToAddProduct = navigateToAddProduct)

}
