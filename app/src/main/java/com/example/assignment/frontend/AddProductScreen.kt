package com.example.assignment.frontend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.rememberImagePainter
import com.example.assignment.R
import com.example.assignment.ViewModel.ProductListViewModel
import com.example.assignment.model.Product
import com.example.assignment.repository.ProductRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.core.net.toFile
import java.util.Locale


class AddProductScreen {
    companion object {

        @RequiresApi(Build.VERSION_CODES.P)
        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        @OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
        @Composable
        fun BottomSheetDemo(
            bottomSheetVisible: Boolean,
            viewModel: ProductListViewModel,

            onDismissSheet: () -> Unit
        ) {

            val productTypesLoaded = remember { mutableStateOf(false) }
            val productTypes = remember { mutableStateOf(emptySet<String>()) }
            var productName by remember { mutableStateOf("") }
            var sellingPrice by remember { mutableStateOf("") }
            var taxRate by remember { mutableStateOf("") }
            var selectedProductType by remember { mutableStateOf("") }
            var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

            LaunchedEffect(Unit) {
                if (bottomSheetVisible && !productTypesLoaded.value) {
                    loadProductTypes(productTypes) {
                        productTypesLoaded.value = true
                    }
                }
            }


            Surface(
                color = Color.Yellow
            ) {
                val context = LocalContext.current
                val sheetState = rememberModalBottomSheetState()


                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            onDismissSheet()
                        }
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (productTypesLoaded.value) {
                                Text(
                                    text = "Add Products",
                                    style = TextStyle(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                val (selectedImageFile, setSelectedImageFile) = remember {
                                    mutableStateOf<File?>(
                                        null
                                    )
                                }

                                ImageSelection(
                                    onImageSelected = { uri ->

                                    },
                                    onImageFileReady = { file ->
                                        setSelectedImageFile(file)
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductNameList(productTypes.value) { selectedProductName ->
                                    selectedProductType = selectedProductName
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                ProductDetailsInput(
                                    onProductNameChange = { newName ->
                                        productName = newName
                                    },
                                    onSellingPriceChange = { newPrice ->
                                        sellingPrice = newPrice
                                    },
                                    onTaxRateChange = { newTaxRate ->
                                        taxRate = newTaxRate
                                    }
                                )


                                Spacer(modifier = Modifier.height(16.dp))
                                var loading by remember { mutableStateOf(false) }

                                SubmitButton(
                                    productName = productName,
                                    sellingPrice = sellingPrice,
                                    taxRate = taxRate,
                                    productTypeSelected = selectedProductType,
                                    imageFile = selectedImageFile,
                                    loading = loading,

                                    onSubmit = {
                                        loading = true
                                        viewModel.viewModelScope.launch {
                                            val productRepository = ProductRepository()

                                            try {

                                                val response = productRepository.postProduct(
                                                    productName,
                                                    selectedProductType,
                                                    sellingPrice.toDouble(),
                                                    taxRate.toDouble(),
                                                    selectedImageFile
                                                )
                                                if (response.success) {
                                                    onDismissSheet()
                                                    Toast.makeText(
                                                        context,
                                                        "Product added successfully \n Refresh the page",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to add product",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Error: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            finally {
                                                loading = false
                                            }

                                        }
                                    },
                                    onError = { errorMessage ->
                                        Toast.makeText(
                                            context,
                                            "Validation error: $errorMessage",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onSuccess = {

//                                        onDismissSheet()
                                    }
                                )


                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }




        @Composable
        fun ProductDetailsInput(
            onProductNameChange: (String) -> Unit,
            onSellingPriceChange: (String) -> Unit,
            onTaxRateChange: (String) -> Unit
        ) {
            var productName by remember { mutableStateOf("") }
            var sellingPrice by remember { mutableStateOf("") }
            var taxRate by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,


            ) {

                Text(
                    text = "Product name",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                    modifier = Modifier.padding(vertical = 5.dp)
                )


                TextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        onProductNameChange(it)

                    },
                    placeholder = { Text("Enter product name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Selling price",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(vertical = 5.dp)
                        )

                        TextField(
                            value = sellingPrice,
                            onValueChange = {
                                sellingPrice = it
                                onSellingPriceChange(it)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            placeholder = { Text("Enter selling price") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Tax rate",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(vertical = 5.dp)
                        )

                        TextField(
                            value = taxRate,
                            onValueChange = {
                                taxRate = it
                                onTaxRateChange(it)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            placeholder = { Text("Enter tax rate") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }

            }
        }

        @Composable
        fun ProductNameList(
            productNames: Set<String>,
            onProductNameSelected: (String) -> Unit
        ): String {
            var selectedProductName by remember { mutableStateOf("") }
            var expanded by remember { mutableStateOf(false) }
            var searchText by remember { mutableStateOf("Select product type") }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            )

            {
                Text(
                    text = "Product Type",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { expanded = true }
            ) {

                TextButton(
                    onClick = { expanded = true }
                ) {
                    Text(text = searchText)
                }

                IconButton(
                    onClick = {
                        expanded = true
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }

                if (expanded) {

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .height(200.dp)

                    ) {
                        productNames.forEach { productName ->
                            androidx.compose.material.DropdownMenuItem(
                                onClick = {
                                    selectedProductName = productName
                                    onProductNameSelected(productName)
                                    searchText = productName
                                    expanded = false
                                }
                            ) {
                                Text(text = productName)
                            }
                        }
                    }
                }
            }


            return selectedProductName
        }


        @Composable
        fun ImageSelection(
            onImageSelected: (Uri?) -> Unit,
            onImageFileReady: (File?) -> Unit
        ) {
            val context = LocalContext.current
            val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

            val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    onImageSelected(it)
                    selectedImageUri.value = it
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .aspectRatio(1f)
                        .border(4.dp, Color.White, RoundedCornerShape(26.dp))
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    val painter = if (selectedImageUri.value == null) {
                        painterResource(id = R.drawable.add_products_image)
                    } else {
                        rememberImagePainter(selectedImageUri.value)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(26.dp))

                    )

                    LaunchedEffect(selectedImageUri.value) {
                        selectedImageUri.value?.let { uri ->
                            val imageFile = resizeAndConvertUriToFile(uri, context)
                            onImageFileReady(imageFile)
                        }
                    }
                }
            }
        }

        fun resizeAndConvertUriToFile(uri: Uri, context: Context): File? {
            return try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(inputStream, null, options)

                    options.inSampleSize = calculateInSampleSize(options, 900, 900)
                    options.inJustDecodeBounds = false

                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options)

                        val outputFile = File(context.cacheDir, "resized_image.png")
                        FileOutputStream(outputFile).use { outputStream ->
                            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }
                        outputFile
                    }
                }
            } catch (e: Exception) {
                Log.e("ConvertUriToFile", "Error converting URI to file: ${e.message}")
                null
            }
        }


        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (width: Int, height: Int) = options.run { outWidth to outHeight }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        @Composable
        fun SubmitButton(
            productName: String,
            sellingPrice: String,
            taxRate: String,
            productTypeSelected: String,
            imageFile: File?,
            loading: Boolean,
            onSubmit: () -> Unit,
            onError: (String) -> Unit,
            onSuccess: () -> Unit
        ) {
            val context = LocalContext.current

            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(loading) {
                isLoading = loading
            }

            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    Button(
                        onClick = {
                            isLoading = true

                            val errorMessage = validateFields(
                                productName,
                                sellingPrice,
                                taxRate,
                                productTypeSelected
                            )

                            if (errorMessage == null) {
                                if (imageFile == null) {
                                    onSubmit()
                                    onSuccess()
                                } else {
                                    if (isValidImageFile(imageFile, context)) {
                                        onSubmit()
                                        onSuccess()
                                    } else {
                                        onError("Invalid image file")
                                    }
                                }
                            } else {
                                onError(errorMessage)
                            }

                            isLoading = false

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }


        fun isValidImageFile(file: File?, context: Context): Boolean {
            if (file != null && file.exists()) {
                val extension = file.extension.lowercase(Locale.ROOT)
                if (extension == "jpg" || extension == "jpeg" || extension == "png") {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    val width = bitmap?.width ?: 0
                    val height = bitmap?.height ?: 0
                    if (width == height) {
                        return true
                    } else  {

                        Toast.makeText(
                            context,
                            "Image must have a 1:1 aspect ratio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {

                    Toast.makeText(
                        context,
                        "Image must be in JPEG or PNG format",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                return false
            }
            return false
        }

        private fun validateFields(
            productName: String,
            sellingPrice: String,
            taxRate: String,
            productTypeSelected: String
        ): String? {
            // Log the value of productName
            Log.d("Validation", "Product Name: $productTypeSelected")

            if (productName.isBlank() || sellingPrice.isBlank() || taxRate.isBlank() || productTypeSelected.isBlank()) {
                return "Please fill all fields"
            }

            if (sellingPrice.toDoubleOrNull() == null) {
                return "Please enter a valid selling price (decimal number)"
            }

            if (taxRate.toDoubleOrNull() == null) {
                return "Please enter a valid tax rate (decimal number)"
            }

            return null
        }


        private suspend fun loadProductTypes(
            productTypes: MutableState<Set<String>>,
            onLoaded: () -> Unit
        ) {
            val errorHandler = CoroutineExceptionHandler { _, exception ->
            }

            val productRepository = ProductRepository()

            val loadedTypes = withContext(Dispatchers.IO + errorHandler) {
                val productTypesList: List<Product> = productRepository.fetchProducts()
                productTypesList.map { it.product_type }.toSet()
            }

            productTypes.value = loadedTypes
            onLoaded()
        }
    }
}

