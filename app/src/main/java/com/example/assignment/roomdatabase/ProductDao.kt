package com.example.assignment.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment.room.ProductEntity
@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
     fun getAllProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

//    @Query("SELECT COUNT(*) FROM products")
//    suspend fun getProductsCount(): Int
}
