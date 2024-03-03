package com.example.assignment.frontend

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.assignment.R
import com.example.assignment.ViewModel.ProductListViewModel
import com.example.assignment.model.Product
import com.example.assignment.frontend.AddProductScreen.Companion.BottomSheetDemo


class ProductListScreen {
    companion object {
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
        @Composable
         fun ProductListScreenShow(
            viewModel: ProductListViewModel,
            navigateToAddProduct: () -> Unit
        ) {
            LaunchedEffect(Unit) {
                viewModel.fetchProductData()
            }
            var rotationState by remember { mutableStateOf(0f) }
            val productList = viewModel.productList
            val isLoading = viewModel.isLoading
            val isOffline = viewModel.isOffline
            val backgroundColor = colorResource(id = R.color.black)
            var searchText by remember { mutableStateOf("") }
            var isSearching by remember { mutableStateOf(false) }
            var filteredProductList = productList

//            if (isSearching && searchText.isNotEmpty()) {
//                filteredProductList = productList.filter {
//                    it.product_name?.contains(searchText, ignoreCase = true) == true
//                }.sortedBy { it.product_name }
//            }


            val pullrefreshstate = rememberPullRefreshState(
                refreshing = false, onRefresh = {
                    viewModel.fetchProductData()
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .pullRefresh(pullrefreshstate)

            ) {


                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    if (isSearching) {
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            label = { Text("Search") }
                        )
                    }
                    if (isOffline) {
                        var offlineProductList by remember { mutableStateOf<List<Product>>(emptyList()) }

                        LaunchedEffect(Unit) {
                            val offlineList = viewModel.getOfflineProductList()
                            offlineProductList = offlineList
                        }

                        Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Displaying Cache Items",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    }
                        if (isSearching && searchText.isNotEmpty()) {
                            filteredProductList = offlineProductList.filter {
                                it.product_name?.contains(searchText, ignoreCase = true) == true
                            }.sortedBy { it.product_name }
                        }

                        ProductList(
                            productList = filteredProductList,
                            onItemClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    } else {

                        if (isSearching && searchText.isNotEmpty()) {
                            filteredProductList = filteredProductList.filter {
                                it.product_name?.contains(searchText, ignoreCase = true) == true
                            }.sortedBy { it.product_name }
                        }
                        PullRefreshIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            refreshing = true, state = pullrefreshstate
                        )
                        ProductList(
                            productList = filteredProductList,
                            onItemClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }

                }
            }


            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .padding(10.dp)
            ) {
                IconButton(
                    onClick = {
                        isSearching = !isSearching
                        searchText = ""
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White, CircleShape)
                ) {
                    if (isSearching) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Search"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }

            var bottomSheetVisible by remember { mutableStateOf(false) }
            val context = LocalContext.current
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .padding(14.dp)

            ) {

                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                val isConnected = networkInfo != null && networkInfo.isConnected

                if (isConnected ) {
                    IconButton(
                        onClick = {
                            bottomSheetVisible = !bottomSheetVisible
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (bottomSheetVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                    if (bottomSheetVisible) {
                        BottomSheetDemo(bottomSheetVisible, viewModel) {
                            bottomSheetVisible = false
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = "No internet connection Try Refreshing.",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    }                }

            }
        }



        @Composable
        fun ProductList(
            productList: List<Product>,
            onItemClick: (Product) -> Unit,
            modifier: Modifier = Modifier
        ) {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(productList) { product ->
                    ProductListItem(product = product, onItemClick = onItemClick)
                }
            }
        }


        @Composable
        fun ProductListItem(product: Product, onItemClick: (Product) -> Unit) {
            var isExpanded by remember { mutableStateOf(false) }

            if (isExpanded) {
                ExpandedProductDetails(
                    product = product,
                    onClose = { isExpanded = false }
                )
            } else {


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp)
                        .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                        .clickable { isExpanded = true }


                ) {
                    val backgroundColorBox = colorResource(id = R.color.Box)


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(1.dp)
                            .background(backgroundColorBox)

                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = product.product_name ?: "Unknown Product Name",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),

                                )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {
                                val painter = rememberImagePainter(
                                    data = product.image ?: "",
                                    builder = {
                                        crossfade(true)
                                    }
                                )

                                val imagePainter = if (product.image.isNullOrEmpty()) {
                                    painterResource(R.drawable.product_image)
                                } else {
                                    painter
                                }

                                Image(
                                    painter = imagePainter,
                                    contentDescription = "Product Image",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(text = "Type: ${product.product_type ?: "Unknown Type"}")
                                    Text(text = "Price: ${product.price ?: "Unknown Price"}")
                                    Text(text = "Tax: ${product.tax ?: "Unknown Tax"}")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))

            }

        }


        @Composable
        fun ExpandedProductDetails(
            product: Product,
            onClose: () -> Unit
        ) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.border(4.dp, Color.White, RoundedCornerShape(16.dp))


            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clickable { onClose()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally


                ) {
                    Text(
                        text = product.product_name ?: "Unknown Product Name",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val painter = rememberImagePainter(
                        data = product.image ?: "",
                        builder = {
                            crossfade(true)
                        }
                    )
                    val imagePainter = if (product.image.isNullOrEmpty()) {
                        painterResource(R.drawable.product_image)
                    } else {
                        painter
                    }


                    Image(
                        painter = imagePainter,
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(shape = RoundedCornerShape(16.dp)),

                    )

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { onClose() }
                        ,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Type: ${product.product_type ?: "Unknown Type"}",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Price: ${product.price ?: "Unknown Price"}",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Tax: ${product.tax ?: "Unknown Tax"}",
                            color = Color.White,
                            fontSize = 18.sp
                        )

                    }


                }

            }
            Spacer(modifier = Modifier.height(64.dp))

        }

    }
}
