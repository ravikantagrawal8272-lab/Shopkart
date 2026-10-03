package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.model.Product
import com.example.model.SellerHubTab
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenDark
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeDark
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BecomeSellerScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val seller = uiState.sellerProfile
    val isApprovedSeller = seller != null && seller.status == "approved"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("seller_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = ShopKartOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isApprovedSeller) "${seller?.shopName} (Seller Central)" else "Sell on ShopKart",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        if (isApprovedSeller) {
                            Text(
                                text = "Commission: 10% • GST: ${seller?.gstNumber}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                if (!isApprovedSeller && seller?.status == "pending") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Pending Review",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }
        }

        if (isApprovedSeller) {
            // Full Amazon-Style Seller Central Dashboard
            SellerCentralView(viewModel = viewModel, uiState = uiState)
        } else {
            // Seller Registration Onboarding View
            SellerOnboardingView(viewModel = viewModel, uiState = uiState)
        }
    }
}

@Composable
fun SellerCentralView(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    val selectedTab = uiState.currentSellerTab
    val tabs = listOf(
        SellerHubTab.DASHBOARD to "Dashboard",
        SellerHubTab.MY_PRODUCTS to "My Products",
        SellerHubTab.ORDERS to "Orders",
        SellerHubTab.EARNINGS to "Earnings & Payouts"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhite)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { (tab, label) ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ShopKartOrange else SurfaceBg)
                        .border(1.dp, if (isSelected) ShopKartOrange else BorderMedium, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setSellerTab(tab) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("seller_tab_$label")
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PureWhite else TextSecondary
                    )
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                SellerHubTab.DASHBOARD -> SellerDashboardTab(viewModel, uiState)
                SellerHubTab.MY_PRODUCTS -> SellerProductsTab(viewModel, uiState)
                SellerHubTab.ORDERS -> SellerOrdersTab(viewModel, uiState)
                SellerHubTab.EARNINGS -> SellerEarningsTab(viewModel, uiState)
                SellerHubTab.SETTINGS -> SellerDashboardTab(viewModel, uiState)
            }
        }
    }

    // Add Product Modal Form Dialog
    if (uiState.showAddSellerProductDialog) {
        SellerAddProductDialog(
            onDismiss = { viewModel.closeAddSellerProductDialog() },
            onSubmit = { title, desc, price, origPrice, cat, brand, stock, images ->
                viewModel.addSellerProduct(title, desc, price, origPrice, cat, brand, stock, images)
            }
        )
    }

    // Request Payout Modal Form Dialog
    if (uiState.showPayoutDialog) {
        SellerPayoutDialog(
            pendingAmount = uiState.sellerStats?.pendingPayout ?: 0,
            onDismiss = { viewModel.closePayoutDialog() },
            onSubmit = { amount, method, details ->
                viewModel.requestSellerPayout(amount, method, details)
            }
        )
    }
}

@Composable
fun SellerDashboardTab(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    val stats = uiState.sellerStats

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 4 Big Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SellerStatCard(
                    title = "Total Gross Sales",
                    value = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(stats?.totalSales ?: 0)}",
                    icon = Icons.Filled.MonetizationOn,
                    color = ShopKartOrange,
                    modifier = Modifier.weight(1f)
                )
                SellerStatCard(
                    title = "Net Earnings (90%)",
                    value = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(stats?.netEarnings ?: 0)}",
                    icon = Icons.Filled.Payments,
                    color = ShopKartGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SellerStatCard(
                    title = "Total Orders",
                    value = "${stats?.totalOrders ?: 0}",
                    icon = Icons.Filled.ShoppingBag,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                SellerStatCard(
                    title = "Live Catalog Items",
                    value = "${stats?.totalProducts ?: 0}",
                    icon = Icons.Filled.Inventory2,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Payout Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, ShopKartGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available Pending Payout",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(stats?.pendingPayout ?: 0)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartGreenDark
                        )
                        Text(
                            text = "10% platform fee deducted automatically",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { viewModel.openPayoutDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = (stats?.pendingPayout ?: 0) > 0
                    ) {
                        Text("Withdraw Payout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 7-Day Sales Trend
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 Sales Last 7 Days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        stats?.sales7Days?.forEach { day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                val barHeight = if (stats.totalSales > 0) ((day.revenue.toFloat() / stats.totalSales) * 200).coerceIn(12f, 80f) else 15f
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(barHeight.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ShopKartOrange)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day.day,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerProductsTab(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    val products = uiState.sellerProducts

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Catalog (${products.size} Products)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Button(
                    onClick = { viewModel.openAddSellerProductDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No products in your catalog yet.", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Tap 'Add Product' to list items on ShopKart.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                SellerProductCard(
                    product = product,
                    onDelete = { viewModel.deleteSellerProduct(product.id) }
                )
            }
        }
    }
}

@Composable
fun SellerProductCard(
    product: Product,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
        color = PureWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceBg)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.images.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.price)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = ShopKartOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stock: ${product.stock}",
                        fontSize = 11.sp,
                        color = if (product.stock < 5) Color(0xFFEF4444) else TextMuted,
                        fontWeight = if (product.stock < 5) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Status Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.isApproved) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ShopKartGreenLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Approved & Live ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ShopKartGreenDark)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Pending Review ⏳", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
            }
        }
    }
}

@Composable
fun SellerOrdersTab(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    val orders = uiState.sellerOrders

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Orders for Your Products (${orders.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No orders received yet.", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order #${order.id}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (order.status.lowercase()) {
                                            "delivered" -> ShopKartGreenLight
                                            "shipped" -> Color(0xFFEDE9FE)
                                            else -> ShopKartOrangeLight
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = order.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (order.status.lowercase()) {
                                        "delivered" -> ShopKartGreenDark
                                        "shipped" -> Color(0xFF6D28D9)
                                        else -> ShopKartOrange
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Ship to: ${order.address.fullName}, ${order.address.city} (${order.address.pincode})",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total: ₹${order.totalAmount} • Earnings: ₹${order.sellerEarnings}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            if (order.status == "Ordered") {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, "Packed") },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Mark Packed", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (order.status == "Packed") {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, "Shipped") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Mark Shipped", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerEarningsTab(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    val earnings = uiState.sellerEarnings

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Earnings Ledger",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Button(
                    onClick = { viewModel.openPayoutDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Request Payout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (earnings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No earnings records yet.", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        } else {
            items(earnings, key = { it.id }) { record ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
                    color = PureWhite
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${record.orderId}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Gross: ₹${record.amount} • Fee (10%): -₹${record.commission}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            if (record.payoutMethod != null) {
                                Text(
                                    text = "Disbursed to: ${record.payoutMethod}",
                                    fontSize = 10.sp,
                                    color = ShopKartGreenDark
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${record.netEarnings}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = ShopKartGreenDark
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (record.status == "paid") ShopKartGreenLight else Color(0xFFFEF3C7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = record.status.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.status == "paid") ShopKartGreenDark else Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SellerStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
        color = PureWhite
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun SellerOnboardingView(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState
) {
    var shopName by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var shopDescription by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }

    val isPending = uiState.sellerProfile?.status == "pending"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isPending) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, Color(0xFFD97706), RoundedCornerShape(14.dp)),
                    color = Color(0xFFFFFBEB)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Application Under Review", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF92400E))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your store '${uiState.sellerProfile?.shopName}' (GST: ${uiState.sellerProfile?.gstNumber}) is currently being verified by ShopKart administration. Approval takes approximately 2 hours.",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.approveSeller(uiState.sellerProfile?.id ?: "") },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Quick Demo: Auto-Approve Store ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Hero Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🚀 Grow Your Business on ShopKart Marketplace",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Join India's fastest-growing multi-vendor retail platform. Sell directly to 10M+ verified buyers.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SellerBenefit(icon = Icons.Filled.TrendingUp, title = "10M+ Reach", desc = "Nationwide traffic")
                        SellerBenefit(icon = Icons.Filled.ElectricBolt, title = "Fast Payouts", desc = "Direct to UPI/Bank")
                        SellerBenefit(icon = Icons.Filled.Security, title = "Flat 10% Fee", desc = "Zero hidden costs")
                    }
                }
            }
        }

        // Registration Form
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register Your Merchant Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("Shop / Business Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = gstNumber,
                        onValueChange = { gstNumber = it.uppercase() },
                        label = { Text("GSTIN (e.g. 29ABCDE1234F1Z5)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = shopDescription,
                        onValueChange = { shopDescription = it },
                        label = { Text("Business & Category Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = { pickupAddress = it },
                        label = { Text("Warehouse Pickup Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (shopName.isNotBlank() && gstNumber.isNotBlank()) {
                                viewModel.becomeSeller(shopName, gstNumber, shopDescription, address = pickupAddress)
                            } else {
                                viewModel.showSnackbar("Please fill in Shop Name and GST number.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit Merchant Application", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerBenefit(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ShopKartOrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ShopKartOrange, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(desc, fontSize = 9.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun SellerAddProductDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Int, Int, String, String, Int, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var origPriceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("electronics") }
    var brand by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Product to Your Store", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = origPriceStr,
                        onValueChange = { origPriceStr = it },
                        label = { Text("MRP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("Available Stock Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toIntOrNull() ?: 0
                    val op = origPriceStr.toIntOrNull() ?: (p + 500)
                    val s = stockStr.toIntOrNull() ?: 10
                    if (title.isNotBlank() && p > 0) {
                        onSubmit(title, desc, p, op, category, brand.ifBlank { "Generic" }, s, emptyList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
            ) {
                Text("Submit for Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SellerPayoutDialog(
    pendingAmount: Int,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("$pendingAmount") }
    var method by remember { mutableStateOf("UPI") }
    var details by remember { mutableStateOf("seller@okhdfcbank") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Earnings Payout", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Available Pending Balance: ₹$pendingAmount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartGreenDark
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Withdrawal Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("UPI", "Bank Transfer").forEach { m ->
                        val isSel = method == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ShopKartGreenLight else SurfaceBg)
                                .border(1.dp, if (isSel) ShopKartGreen else BorderMedium, RoundedCornerShape(8.dp))
                                .clickable { method = m }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(m, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) ShopKartGreenDark else TextPrimary)
                        }
                    }
                }

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(if (method == "UPI") "UPI ID" else "Bank Account & IFSC") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toIntOrNull() ?: 0
                    if (amt in 1..pendingAmount && details.isNotBlank()) {
                        onSubmit(amt, method, details)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartGreen)
            ) {
                Text("Confirm Withdrawal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
