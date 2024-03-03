package com.example.assignment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment.ViewModel.App
import com.example.assignment.ViewModel.ProductListViewModel
import com.example.assignment.roomdatabase.ProductDao
import com.example.assignment.roomdatabase.ProductDatabase
import com.example.assignment.roomdatabase.RoomViewModel
import com.example.assignment.ui.theme.AssignmentTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ProductListViewModel // Declare viewModel as a property of MainActivity

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val productDao = ProductDatabase.getInstance(context).productDao()
                    val roomViewModel = RoomViewModel(productDao)

                    val viewModel = remember {
                        ProductListViewModel(roomViewModel)
                    }


                    App(viewModel = viewModel, navigateToAddProduct = {})
                }
            }
        }
    }
}