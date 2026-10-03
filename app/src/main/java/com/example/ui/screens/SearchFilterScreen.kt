package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConvexBackendService
import com.example.model.AppScreen
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchFilterScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val focusManager = LocalFocusManager.current
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Real-time dynamic search engine query
    val searchResults = remember(
        uiState.searchQuery,
        uiState.searchCategoryFilter,
        uiState.searchBrandFilter,
        uiState.searchMinPrice,
        uiState.searchMaxPrice,
        uiState.searchMinRating,
        uiState.searchInStockOnly,
        uiState.searchSortBy
    ) {
        ConvexBackendService.searchProductsAdvanced(
            query = uiState.searchQuery,
            category = uiState.searchCategoryFilter,
            brand = uiState.searchBrandFilter,
            minPrice = uiState.searchMinPrice,
            maxPrice = uiState.searchMaxPrice,
            minRating = uiState.searchMinRating,
            inStockOnly = uiState.searchInStockOnly,
            sortBy = uiState.searchSortBy
        )
    }

    val categories = listOf("All", "mobiles", "electronics", "fashion", "appliances", "home", "grocery")
    val brands = listOf("All", "Apple", "Sony", "Samsung", "Nike", "Dyson", "Instant Pot", "Fastrack")

    val sortOptions = listOf(
        "relevance" to "Relevance (Popular)",
        "price_low" to "Price: Low to High",
        "price_high" to "Price: High to Low",
        "rating" to "Customer Rating (4★+)",
        "newest" to "Newest Arrivals"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SurfaceBg
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PureWhite,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("search_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search products, brands and more...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("search_text_input_screen"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = ShopKartOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.setSearchQuery("") },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Clear",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.startVoiceSearch() },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Mic,
                                            contentDescription = "Voice",
                                            tint = ShopKartOrange,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium,
                                focusedContainerColor = SurfaceBg,
                                unfocusedContainerColor = SurfaceBg,
                                cursorColor = ShopKartOrange
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    viewModel.performSearch(uiState.searchQuery)
                                }
                            )
                        )
                    }

                    // Sort & Filter Quick Action Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sort Dropdown Trigger
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceBg)
                                    .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                    .clickable { showSortMenu = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("sort_btn"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Sort,
                                    contentDescription = "Sort",
                                    tint = ShopKartOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sort: ${sortOptions.find { it.first == uiState.searchSortBy }?.second?.take(15) ?: "Relevance"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier
                                    .background(PureWhite)
                                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            ) {
                                sortOptions.forEach { (key, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (uiState.searchSortBy == key) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (uiState.searchSortBy == key) ShopKartOrange else TextPrimary
                                                )
                                                if (uiState.searchSortBy == key) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = "Selected",
                                                        tint = ShopKartOrange,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSearchSortBy(key)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Filter Button Trigger
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ShopKartOrangeLight)
                                .border(1.dp, ShopKartOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { showFilterSheet = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("filter_btn"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Filters",
                                tint = ShopKartOrange,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Filters",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartOrange
                            )
                        }
                    }

                    // Category Pill Strip
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat.equals(uiState.searchCategoryFilter, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) ShopKartOrange else SurfaceBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) ShopKartOrange else BorderLight,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setSearchCategory(cat) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = cat.replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PureWhite else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Results Counter & Breadcrumb
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${searchResults.size} Products found",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                if (uiState.searchInStockOnly || uiState.searchMinRating > 0f || uiState.searchBrandFilter != "All") {
                    Text(
                        text = "Clear filters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartOrange,
                        modifier = Modifier.clickable { viewModel.resetSearchFilters() }
                    )
                }
            }

            // Products Grid / List or Empty State
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(PureWhite)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SearchOff,
                                contentDescription = "No products found",
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No results found for \"${uiState.searchQuery}\"",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching with different keywords, relaxing your price filters, or browsing top categories below.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetSearchFilters(); viewModel.setSearchQuery("") },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("View All Products", fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chunk in 2 columns for a clean Flipkart card grid
                    val chunked = searchResults.chunked(2)
                    items(chunked) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (product in rowItems) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(
                                        product = product,
                                        isWishlisted = uiState.wishlistProductIds.contains(product.id),
                                        onProductClick = { viewModel.selectProduct(product) },
                                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                                        onAddToCart = { viewModel.addToCart(product) },
                                        onBuyNow = { viewModel.buyNow(product) }
                                    )
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet for Advanced Filters
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Products",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Reset All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartOrange,
                        modifier = Modifier.clickable { viewModel.resetSearchFilters() }
                    )
                }

                HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 12.dp))

                // Brand Filter
                Text(
                    text = "Brand",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(brands) { b ->
                        val isSelected = b.equals(uiState.searchBrandFilter, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ShopKartOrange else SurfaceBg)
                                .border(1.dp, if (isSelected) ShopKartOrange else BorderMedium, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSearchBrand(b) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = b,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PureWhite else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Filter (4★ & Above)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customer Rating (4★ & above)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Only show highly-rated verified items",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = uiState.searchMinRating >= 4.0f,
                        onCheckedChange = { isChecked ->
                            viewModel.setSearchMinRating(if (isChecked) 4.0f else 0f)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = ShopKartOrange
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // In-Stock Availability Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "In Stock Only",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Hide items that are out of stock",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = uiState.searchInStockOnly,
                        onCheckedChange = { isChecked ->
                            viewModel.setSearchInStockOnly(isChecked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = ShopKartOrange
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Apply Filters (${searchResults.size} items)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}
