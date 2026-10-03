package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Product
import com.example.model.SearchHistoryItem
import com.example.model.TrendingSearchItem
import com.example.model.User
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenDark
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShopKartHeader(
    cartCount: Int,
    wishlistCount: Int,
    unreadNotificationsCount: Int,
    pincode: String,
    currentUser: User?,
    searchQuery: String,
    isSearchFocused: Boolean,
    recentSearches: List<SearchHistoryItem>,
    trendingSearches: List<TrendingSearchItem>,
    autocompleteSuggestions: List<Product>,
    isVoiceSearching: Boolean,
    voiceTranscript: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onPerformSearch: (String) -> Unit,
    onDeleteSearchHistory: (String) -> Unit,
    onClearAllSearchHistory: () -> Unit,
    onStartVoiceSearch: () -> Unit,
    onStopVoiceSearch: () -> Unit,
    onSelectProductSuggestion: (Product) -> Unit,
    onNotificationClick: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onSellerClick: () -> Unit,
    onLocationClick: () -> Unit,
    onAuthClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUserMenu by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, spotColor = Color.Black.copy(alpha = 0.05f)),
        color = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Top Row: Brand Logo, Location Pill, Notification Bell, Wishlist, Cart & Auth Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ShopKart Brand Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Brand tap */ }
                        .testTag("brand_logo")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ShopKartOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = "ShopKart Bolt",
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Shop",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Kart",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = ShopKartOrange,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Text(
                            text = "PLUS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShopKartOrange,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(start = 1.dp)
                        )
                    }
                }

                // Delivery Location Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceBg)
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                        .clickable { onLocationClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("location_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = ShopKartOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$pincode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Change location",
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Header Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Notification Bell Icon with real-time unread badge
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("notifications_header_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = ShopKartOrange,
                                        contentColor = PureWhite
                                    ) {
                                        Text(
                                            text = if (unreadNotificationsCount > 9) "9+" else "$unreadNotificationsCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadNotificationsCount > 0) Icons.Filled.Notifications else Icons.Filled.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = if (unreadNotificationsCount > 0) ShopKartOrange else TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Wishlist Icon with badge
                    IconButton(
                        onClick = onWishlistClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("wishlist_header_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (wishlistCount > 0) {
                                    Badge(
                                        containerColor = ShopKartOrange,
                                        contentColor = PureWhite
                                    ) {
                                        Text(
                                            text = "$wishlistCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (wishlistCount > 0) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (wishlistCount > 0) ShopKartOrange else TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Cart Icon with badge
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("cart_header_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = ShopKartOrange,
                                        contentColor = PureWhite
                                    ) {
                                        Text(
                                            text = "$cartCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (cartCount > 0) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = "Cart",
                                tint = if (cartCount > 0) ShopKartOrange else TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // User Profile / Auth Avatar with Dropdown
                    Box {
                        if (currentUser != null) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (currentUser.isAdmin) ShopKartOrange else BorderMedium, CircleShape)
                                    .clickable { showUserMenu = true }
                                    .testTag("user_avatar_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!currentUser.avatar.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(currentUser.avatar)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "User",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showUserMenu,
                                onDismissRequest = { showUserMenu = false },
                                modifier = Modifier
                                    .background(PureWhite)
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                    Text(
                                        text = currentUser.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = currentUser.email,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (currentUser.isAdmin) ShopKartOrangeLight
                                                else if (currentUser.isSeller) ShopKartGreenLight
                                                else SurfaceBg
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Role: ${currentUser.role.uppercase()}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentUser.isAdmin) ShopKartOrange else if (currentUser.isSeller) ShopKartGreenDark else TextSecondary
                                        )
                                    }
                                }

                                HorizontalDivider(color = BorderLight)

                                if (currentUser.isAdmin) {
                                    DropdownMenuItem(
                                        text = { Text("⚡ Admin Dashboard", fontWeight = FontWeight.Bold, color = ShopKartOrange) },
                                        leadingIcon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ShopKartOrange) },
                                        onClick = {
                                            showUserMenu = false
                                            onAdminClick()
                                        }
                                    )
                                }

                                DropdownMenuItem(
                                    text = { Text("My Orders") },
                                    leadingIcon = { Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = TextSecondary) },
                                    onClick = {
                                        showUserMenu = false
                                        onOrdersClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Wishlist ($wishlistCount)") },
                                    leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = null, tint = ShopKartOrange) },
                                    onClick = {
                                        showUserMenu = false
                                        onWishlistClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Notifications ($unreadNotificationsCount)") },
                                    leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null, tint = TextSecondary) },
                                    onClick = {
                                        showUserMenu = false
                                        onNotificationClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Seller Hub") },
                                    leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = null, tint = TextSecondary) },
                                    onClick = {
                                        showUserMenu = false
                                        onSellerClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Switch Demo Role") },
                                    leadingIcon = { Icon(Icons.Filled.SwapHoriz, contentDescription = null, tint = TextSecondary) },
                                    onClick = {
                                        showUserMenu = false
                                        onSwitchRoleClick()
                                    }
                                )

                                HorizontalDivider(color = BorderLight)

                                DropdownMenuItem(
                                    text = { Text("Logout", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = { Icon(Icons.Filled.Logout, contentDescription = null, tint = Color(0xFFDC2626)) },
                                    onClick = {
                                        showUserMenu = false
                                        onLogoutClick()
                                    }
                                )
                            }
                        } else {
                            // Login button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ShopKartOrange)
                                    .clickable { onAuthClick() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("login_header_btn")
                            ) {
                                Text(
                                    text = "Login",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Flipkart-style Active Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSearchFocused) 1.5.dp else 1.dp,
                        color = if (isSearchFocused) ShopKartOrange else BorderMedium,
                        shape = RoundedCornerShape(12.dp)
                    ),
                color = SurfaceBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = if (isSearchFocused) ShopKartOrange else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            onSearchQueryChange(it)
                            if (!isSearchFocused) onSearchFocusChange(true)
                        },
                        placeholder = {
                            Text(
                                text = "Search iPhones, Sony, Jordan, Dyson...",
                                color = TextMuted,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = ShopKartOrange
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                onPerformSearch(searchQuery)
                            }
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Voice Search Mic Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ShopKartOrangeLight)
                            .clickable { onStartVoiceSearch() }
                            .testTag("voice_search_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Voice Search",
                            tint = ShopKartOrange,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Interactive Search Focus Dropdown Overlay
            AnimatedVisibility(
                visible = isSearchFocused,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .shadow(4.dp),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // 1. Autocomplete Live Suggestions
                        if (autocompleteSuggestions.isNotEmpty()) {
                            Text(
                                text = "SUGGESTIONS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            autocompleteSuggestions.forEach { prod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            focusManager.clearFocus()
                                            onSelectProductSuggestion(prod)
                                        }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SurfaceBg)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(prod.images.firstOrNull())
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = prod.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "in ${prod.category.replaceFirstChar { it.uppercase() }}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = "₹${prod.price}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartOrange
                                    )
                                }
                            }
                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        // 2. Recent Searches
                        if (recentSearches.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT SEARCHES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Clear all",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartOrange,
                                    modifier = Modifier.clickable { onClearAllSearchHistory() }
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            recentSearches.take(4).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            focusManager.clearFocus()
                                            onPerformSearch(item.query)
                                        }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AccessTime,
                                            contentDescription = "History",
                                            tint = TextMuted,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.query,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove",
                                        tint = TextMuted,
                                        modifier = Modifier
                                            .size(15.dp)
                                            .clickable { onDeleteSearchHistory(item.id) }
                                    )
                                }
                            }
                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))
                        }

                        // 3. Trending Searches 🔥
                        Text(
                            text = "TRENDING ON SHOPKART",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            trendingSearches.forEach { trend ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SurfaceBg)
                                        .border(1.dp, BorderMedium, RoundedCornerShape(20.dp))
                                        .clickable {
                                            focusManager.clearFocus()
                                            onPerformSearch(trend.query)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Whatshot,
                                        contentDescription = "Trending",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = trend.query,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Close overlay button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceBg)
                                .clickable { onSearchFocusChange(false) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Close Search Suggestions",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Voice Search Modal / Listening Dialog
    if (isVoiceSearching) {
        Dialog(onDismissRequest = onStopVoiceSearch) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                color = PureWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ShopKartOrangeLight)
                            .border(2.dp, ShopKartOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Listening",
                            tint = ShopKartOrange,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Listening to you...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = voiceTranscript,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ShopKartOrange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Say things like \"iPhone 16\", \"Sony Headphones\", \"Air Jordans\"",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceBg)
                            .clickable { onStopVoiceSearch() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
