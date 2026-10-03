package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShopKartRepository
import com.example.model.AppScreen
import com.example.model.CategoryItem
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

@Composable
fun CategoriesScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var activeCatId by remember {
        mutableStateOf(if (uiState.selectedCategory != "all") uiState.selectedCategory else "mobiles")
    }

    val selectedCategoryItem = ShopKartRepository.categories.find { it.id == activeCatId }
        ?: ShopKartRepository.categories[1]

    val categoryProducts = ShopKartRepository.products.filter { it.category == activeCatId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "All Categories",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = { onNavigate(AppScreen.SEARCH_FILTER) }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextPrimary
                    )
                }
            }
        }

        // Left Category Rail + Right Content Pane
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Category Navigation Rail
            LazyColumn(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .background(PureWhite)
                    .border(width = 1.dp, color = BorderLight),
                contentPadding = PaddingValues(bottom = 70.dp)
            ) {
                items(ShopKartRepository.categories.filter { it.id != "all" }, key = { it.id }) { cat ->
                    val isSelected = cat.id == activeCatId
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeCatId = cat.id }
                            .background(if (isSelected) ShopKartOrangeLight.copy(alpha = 0.5f) else PureWhite)
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                                .border(
                                    1.5.dp,
                                    if (isSelected) ShopKartOrange else BorderLight,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.iconEmoji,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.name,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ShopKartOrange else TextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    HorizontalDivider(color = BorderLight)
                }
            }

            // Right Pane: Subcategories & Featured Picks
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                contentPadding = PaddingValues(bottom = 70.dp)
            ) {
                // Category Banner Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShopKartOrangeLight)
                            .border(1.dp, ShopKartOrange.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.selectCategory(activeCatId)
                                onNavigate(AppScreen.SEARCH_FILTER)
                            }
                            .padding(14.dp),
                        color = ShopKartOrangeLight
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${selectedCategoryItem.name} Store",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Up to 50% Off • Verified Genuine",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ShopKartOrange
                                )
                            }
                            Text(
                                text = selectedCategoryItem.iconEmoji,
                                fontSize = 32.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Subcategories Title
                item {
                    Text(
                        text = "Explore Sub-Categories",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Subcategories Grid
                val subcategoryChunks = selectedCategoryItem.subcategories.chunked(2)
                items(subcategoryChunks) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (sub in pair) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.selectCategory(activeCatId)
                                        viewModel.setSearchQuery(sub)
                                        onNavigate(AppScreen.SEARCH_FILTER)
                                    }
                                    .padding(10.dp),
                                color = PureWhite
                            ) {
                                Column {
                                    Text(
                                        text = sub,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "⚡ View Deals >",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ShopKartOrange,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Category Featured Products
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular in ${selectedCategoryItem.name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(categoryProducts) { product ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .clickable { viewModel.openProductDetail(product) }
                            .padding(10.dp),
                        color = PureWhite
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = selectedCategoryItem.iconEmoji, fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "₹${product.price} • ${product.discountPercent}% OFF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
