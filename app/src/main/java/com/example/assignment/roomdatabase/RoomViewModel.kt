package com.example.assignment.roomdatabase

import com.example.assignment.room.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomViewModel(private val productDao: ProductDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getAllProducts(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            productDao.getAllProducts()
        }
    }
    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertAll(listOf(product))
    }



}

