package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShopKartRepository
import com.example.model.AppScreen
import com.example.ui.components.BankOfferStrip
import com.example.ui.components.CategoryStrip
import com.example.ui.components.DealTimerHeader
import com.example.ui.components.HeroBannerSlider
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopKartHeader
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel

@Composable
fun HomeScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val trendingProducts = ShopKartRepository.getTrendingProducts()
    val electronicsDeals = ShopKartRepository.getElectronicsDeals()
    val fashionTopPicks = ShopKartRepository.getFashionTopPicks()
    val allProducts = if (uiState.selectedCategory == "all") {
        ShopKartRepository.products
    } else {
        ShopKartRepository.products.filter { it.category == uiState.selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Sticky Header with ShopKart Logo, Pincode, Search bar, Notifications, Wishlist, Cart & Auth Profile
        ShopKartHeader(
            cartCount = uiState.cartItems.sumOf { it.quantity },
            wishlistCount = uiState.wishlistProductIds.size,
            unreadNotificationsCount = uiState.unreadNotificationsCount,
            pincode = uiState.pincodeInput,
            currentUser = uiState.currentUser,
            searchQuery = uiState.searchQuery,
            isSearchFocused = uiState.isSearchFocused,
            recentSearches = uiState.recentSearches,
            trendingSearches = uiState.trendingSearches,
            autocompleteSuggestions = uiState.autocompleteSuggestions,
            isVoiceSearching = uiState.isVoiceSearching,
            voiceTranscript = uiState.voiceTranscript,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onSearchFocusChange = { viewModel.setSearchFocused(it) },
            onPerformSearch = { viewModel.performSearch(it) },
            onDeleteSearchHistory = { viewModel.deleteSearchHistory(it) },
            onClearAllSearchHistory = { viewModel.clearAllSearchHistory() },
            onStartVoiceSearch = { viewModel.startVoiceSearch() },
            onStopVoiceSearch = { viewModel.stopVoiceSearch() },
            onSelectProductSuggestion = { prod ->
                viewModel.selectProduct(prod)
            },
            onNotificationClick = { onNavigate(AppScreen.NOTIFICATIONS) },
            onCartClick = { onNavigate(AppScreen.CART) },
            onWishlistClick = { onNavigate(AppScreen.WISHLIST) },
            onSellerClick = { onNavigate(AppScreen.BECOME_SELLER) },
            onLocationClick = { onNavigate(AppScreen.CART) },
            onAuthClick = { onNavigate(AppScreen.AUTH) },
            onOrdersClick = { onNavigate(AppScreen.ORDERS) },
            onAdminClick = { onNavigate(AppScreen.ADMIN_PANEL) },
            onSwitchRoleClick = {
                if (uiState.currentUser?.isAdmin == true) {
                    viewModel.switchUserRole("user")
                } else {
                    viewModel.switchUserRole("admin")
                }
            },
            onLogoutClick = { viewModel.logout() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_content"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Category Strip
            item {
                CategoryStrip(
                    selectedCategoryId = uiState.selectedCategory,
                    onCategorySelected = { catId ->
                        viewModel.selectCategory(catId)
                        if (catId != "all") {
                            onNavigate(AppScreen.SEARCH_FILTER)
                        }
                    }
                )
            }

            // Hero Banner Carousel
            item {
                HeroBannerSlider(
                    onSlideClick = { cat ->
                        viewModel.selectCategory(cat)
                        onNavigate(AppScreen.SEARCH_FILTER)
                    }
                )
            }

            // Bank Offers Strip
            item {
                BankOfferStrip(
                    onOfferClick = { offer ->
                        viewModel.applyCoupon(offer.code)
                        onNavigate(AppScreen.CART)
                    }
                )
            }

            // Flash Deals Section with Countdown Timer
            item {
                Spacer(modifier = Modifier.height(10.dp))
                DealTimerHeader(
                    title = "⚡ Deals of the Day",
                    subtitle = "Refreshes daily at 12:00 AM",
                    timerText = uiState.flashSaleTimeRemaining,
                    onViewAllClick = {
                        viewModel.setSearchSortBy("price_low")
                        onNavigate(AppScreen.SEARCH_FILTER)
                    }
                )
            }

            // Flash Deals Horizontal Carousel
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = uiState.wishlistProductIds.contains(product.id),
                            onProductClick = { viewModel.selectProduct(product) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onBuyNow = { viewModel.buyNow(product) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }

            // Best of Electronics
            item {
                Spacer(modifier = Modifier.height(14.dp))
                DealTimerHeader(
                    title = "💻 Best of Electronics",
                    subtitle = "Laptops, Audio & Flagships",
                    onViewAllClick = {
                        viewModel.selectCategory("electronics")
                        onNavigate(AppScreen.SEARCH_FILTER)
                    }
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(electronicsDeals, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = uiState.wishlistProductIds.contains(product.id),
                            onProductClick = { viewModel.selectProduct(product) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onBuyNow = { viewModel.buyNow(product) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }

            // Trending Fashion & Lifestyle
            item {
                Spacer(modifier = Modifier.height(14.dp))
                DealTimerHeader(
                    title = "👔 Trending Fashion & Lifestyle",
                    subtitle = "Premium brands & footwear",
                    onViewAllClick = {
                        viewModel.selectCategory("fashion")
                        onNavigate(AppScreen.SEARCH_FILTER)
                    }
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fashionTopPicks, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = uiState.wishlistProductIds.contains(product.id),
                            onProductClick = { viewModel.selectProduct(product) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onBuyNow = { viewModel.buyNow(product) },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }

            // Full Curated Grid - 2 per row (Responsive Flipkart-style layout)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PureWhite
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "✨ Top Recommendations For You",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Handpicked based on your taste & browsing",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Products Grid formatted in 2-item rows
            val productChunks = allProducts.chunked(2)
            items(productChunks) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (product in pair) {
                        ProductCard(
                            product = product,
                            isWishlisted = uiState.wishlistProductIds.contains(product.id),
                            onProductClick = { viewModel.selectProduct(product) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onBuyNow = { viewModel.buyNow(product) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Trust & Quality Strip
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Why Shop on ShopKart?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TrustBadge(
                                icon = Icons.Filled.ElectricBolt,
                                title = "1-Day Delivery",
                                subtitle = "Fastest to door"
                            )
                            TrustBadge(
                                icon = Icons.Filled.VerifiedUser,
                                title = "100% Genuine",
                                subtitle = "Direct from brand"
                            )
                            TrustBadge(
                                icon = Icons.Filled.Autorenew,
                                title = "7-Day Return",
                                subtitle = "No questions asked"
                            )
                            TrustBadge(
                                icon = Icons.Filled.HeadsetMic,
                                title = "24x7 Help",
                                subtitle = "Priority support"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun TrustBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ShopKartOrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ShopKartOrange,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
