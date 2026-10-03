package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ConvexBackendService
import com.example.model.AdminRevenueDay
import com.example.model.AdminStats
import com.example.model.AdminTab
import com.example.model.AppScreen
import com.example.model.Order
import com.example.model.PosCartItem
import com.example.model.PosInvoice
import com.example.model.Product
import com.example.model.Seller
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
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    // Role Enforcement: Check if logged in user is admin
    if (uiState.currentUser?.isAdmin != true) {
        Admin403AccessDenied(
            onBackToShop = { viewModel.navigateTo(AppScreen.HOME) },
            onSwitchToAdmin = { viewModel.switchToAdminRole() }
        )
        return
    }

    val stats = uiState.adminStats ?: ConvexBackendService.getAdminStats()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Admin Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ShopKartOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ShopKart",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = " ADMIN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ShopKartOrange
                                )
                            }
                            Text(
                                text = uiState.currentUser.name,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(AppScreen.DEV_TESTS) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("⚡ Tests", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ShopKartOrange)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.HOME) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Storefront", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Admin Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminTabItem(
                        title = "Dashboard",
                        icon = Icons.Filled.Dashboard,
                        isSelected = uiState.currentAdminTab == AdminTab.DASHBOARD,
                        onClick = { viewModel.setAdminTab(AdminTab.DASHBOARD) }
                    )
                    AdminTabItem(
                        title = "POS Counter",
                        icon = Icons.Filled.PointOfSale,
                        isSelected = uiState.currentAdminTab == AdminTab.POS,
                        onClick = { viewModel.setAdminTab(AdminTab.POS) }
                    )
                    AdminTabItem(
                        title = "Products (${stats.totalProducts})",
                        icon = Icons.Filled.Inventory2,
                        isSelected = uiState.currentAdminTab == AdminTab.PRODUCTS,
                        onClick = { viewModel.setAdminTab(AdminTab.PRODUCTS) }
                    )
                    AdminTabItem(
                        title = "Orders (${stats.totalOrders})",
                        icon = Icons.Filled.ShoppingBag,
                        isSelected = uiState.currentAdminTab == AdminTab.ORDERS,
                        onClick = { viewModel.setAdminTab(AdminTab.ORDERS) }
                    )
                    AdminTabItem(
                        title = "Users (${stats.totalUsers})",
                        icon = Icons.Filled.People,
                        isSelected = uiState.currentAdminTab == AdminTab.USERS,
                        onClick = { viewModel.setAdminTab(AdminTab.USERS) }
                    )
                    AdminTabItem(
                        title = "Sellers (${stats.totalSellers})",
                        icon = Icons.Filled.Storefront,
                        isSelected = uiState.currentAdminTab == AdminTab.SELLERS,
                        onClick = { viewModel.setAdminTab(AdminTab.SELLERS) }
                    )
                    AdminTabItem(
                        title = "Analytics",
                        icon = Icons.Filled.Analytics,
                        isSelected = uiState.currentAdminTab == AdminTab.ANALYTICS,
                        onClick = { viewModel.setAdminTab(AdminTab.ANALYTICS) }
                    )
                }
            }
        }

        // Body Content by Selected Tab
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState.currentAdminTab) {
                AdminTab.DASHBOARD -> AdminDashboardTab(stats, viewModel)
                AdminTab.POS -> AdminPosCashCounterTab(viewModel)
                AdminTab.PRODUCTS -> AdminProductsTab(viewModel, uiState)
                AdminTab.ORDERS -> AdminOrdersTab(viewModel, uiState)
                AdminTab.USERS -> AdminUsersTab(viewModel, uiState)
                AdminTab.SELLERS -> AdminSellersTab(viewModel, uiState)
                AdminTab.ANALYTICS -> AdminAnalyticsTab(stats)
                AdminTab.NOTIFICATIONS -> AdminDashboardTab(stats, viewModel)
            }
        }
    }

    // Add Product Modal Form Dialog
    if (uiState.showAddProductDialog) {
        AdminAddProductDialog(
            onDismiss = { viewModel.closeProductDialogs() },
            onSubmit = { title, desc, price, origPrice, cat, brand, stock, imgs ->
                viewModel.createAdminProduct(title, desc, price, origPrice, cat, brand, stock, imgs)
            }
        )
    }

    // Edit Product Modal Dialog
    if (uiState.editingProduct != null) {
        AdminEditProductDialog(
            product = uiState.editingProduct,
            onDismiss = { viewModel.closeProductDialogs() },
            onSubmit = { title, desc, price, origPrice, stock, cat, brand ->
                viewModel.updateAdminProduct(
                    productId = uiState.editingProduct.id,
                    title = title,
                    description = desc,
                    price = price,
                    originalPrice = origPrice,
                    stock = stock,
                    category = cat,
                    brand = brand
                )
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: DASHBOARD
// -------------------------------------------------------------
@Composable
private fun AdminDashboardTab(stats: AdminStats, viewModel: ShopKartViewModel) {
    val products = ConvexBackendService.productsTable.value
    val lowStockList = products.filter { it.stock < 5 }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 4 Key Stat Metric Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = "Total Revenue",
                        value = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(stats.totalRevenue)}",
                        trend = "+18.4%",
                        isPositive = true,
                        icon = Icons.Filled.Analytics,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Total Orders",
                        value = "${stats.totalOrders}",
                        trend = "+12.1%",
                        isPositive = true,
                        icon = Icons.Filled.ShoppingBag,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = "Today Cash POS",
                        value = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(stats.todayCashCollection)}",
                        trend = "Cash Counter Live",
                        isPositive = true,
                        icon = Icons.Filled.PointOfSale,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Active Products",
                        value = "${stats.totalProducts}",
                        trend = "${stats.lowStockCount} Low Stock",
                        isPositive = stats.lowStockCount == 0,
                        icon = Icons.Filled.Inventory2,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 7-Day Revenue Graph
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("7-Day Revenue Trend", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Real-time revenue performance", fontSize = 10.sp, color = TextMuted)
                        }
                        Text("Live", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartGreenDark)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxRev = stats.revenue7Days.maxOfOrNull { it.revenue } ?: 1
                        stats.revenue7Days.forEach { dayData ->
                            val heightFraction = (dayData.revenue.toFloat() / maxRev).coerceIn(0.2f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "₹${dayData.revenue / 1000}k",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(ShopKartOrange)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dayData.day,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Low Stock Alerts
        if (lowStockList.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp)),
                    color = Color(0xFFFFFBEB)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Low Stock Alerts (${lowStockList.size} Items)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        lowStockList.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, color = TextPrimary)
                                    Text(
                                        text = if (p.stock == 0) "Out of Stock (0 remaining)" else "Only ${p.stock} left in stock",
                                        fontSize = 10.sp,
                                        color = if (p.stock == 0) Color(0xFFDC2626) else Color(0xFFD97706),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { viewModel.quickRestock(p.id, 10) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Restock +10", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: POS CASH COUNTER TERMINAL
// -------------------------------------------------------------
@Composable
private fun AdminPosCashCounterTab(viewModel: ShopKartViewModel) {
    val products = ConvexBackendService.productsTable.value
    var searchQuery by remember { mutableStateOf("") }
    val posCart = remember { mutableStateListOf<PosCartItem>() }

    var customerName by remember { mutableStateOf("Walk-in Customer") }
    var customerPhone by remember { mutableStateOf("+91 9876543210") }
    var discountInput by remember { mutableStateOf("0") }
    var selectedPosPayment by remember { mutableStateOf("cash") } // "cash" | "upi" | "card"
    var activeInvoice by remember { mutableStateOf<PosInvoice?>(null) }

    val filteredProducts = if (searchQuery.isBlank()) products else products.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true)
    }

    val subtotal = posCart.sumOf { it.product.price * it.qty }
    val discountAmt = discountInput.toIntOrNull() ?: 0
    val gstAmt = ((subtotal - discountAmt) * 0.18).toInt().coerceAtLeast(0) // 18% GST calculation
    val finalTotal = (subtotal - discountAmt + gstAmt).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // POS Header & Live Cash Stat
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("⚡ Offline Store Cash Counter", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Instant In-Store Billing & 18% GST Invoice", fontSize = 10.sp, color = TextSecondary)
                    }

                    val (cash, upi) = ConvexBackendService.getTodayCashCollection()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ShopKartGreenLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Today's Cash: ₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(cash)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartGreenDark)
                    }
                }
            }
        }

        // Search Bar & Quick Add Products
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search catalog / scan barcode...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShopKartOrange,
                    unfocusedBorderColor = BorderMedium
                )
            )
        }

        // Product Catalog Grid (Compact 1-tap add)
        item {
            Text("Quick Catalog Add (${filteredProducts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                filteredProducts.take(4).forEach { p ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(8.dp)),
                        color = PureWhite
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, color = TextPrimary)
                                Text("₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(p.price)} • Stock: ${p.stock}", fontSize = 10.sp, color = if (p.stock > 0) ShopKartGreenDark else Color(0xFFDC2626))
                            }

                            Button(
                                onClick = {
                                    val existing = posCart.indexOfFirst { it.product.id == p.id }
                                    if (existing >= 0) {
                                        posCart[existing] = posCart[existing].copy(qty = posCart[existing].qty + 1)
                                    } else {
                                        posCart.add(PosCartItem(p, 1))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp),
                                enabled = p.stock > 0,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("+ Add", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // POS Billing Cart Terminal
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, if (posCart.isNotEmpty()) ShopKartOrange else BorderLight, RoundedCornerShape(14.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current In-Store Bill (${posCart.sumOf { it.qty }} items)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        if (posCart.isNotEmpty()) {
                            TextButton(onClick = { posCart.clear() }, contentPadding = PaddingValues(0.dp)) {
                                Text("Clear", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (posCart.isEmpty()) {
                        Text("No items added to counter bill. Tap '+ Add' above.", fontSize = 11.sp, color = TextMuted)
                    } else {
                        posCart.forEachIndexed { idx, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.title, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                    Text("₹${item.product.price} each", fontSize = 10.sp, color = TextMuted)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            if (item.qty > 1) posCart[idx] = item.copy(qty = item.qty - 1)
                                            else posCart.removeAt(idx)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("−", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text("${item.qty}", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                    IconButton(
                                        onClick = {
                                            if (item.qty < item.product.stock) posCart[idx] = item.copy(qty = item.qty + 1)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(item.product.price * item.qty)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Customer Details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("Customer Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Phone") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bill Math: Subtotal, Discount, 18% GST
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 11.sp, color = TextSecondary)
                            Text("₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(subtotal)}", fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST (18% Tax)", fontSize = 11.sp, color = TextSecondary)
                            Text("₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(gstAmt)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount Amount (₹)", fontSize = 11.sp, color = TextSecondary)
                            OutlinedTextField(
                                value = discountInput,
                                onValueChange = { discountInput = it },
                                singleLine = true,
                                modifier = Modifier.width(80.dp).height(44.dp)
                            )
                        }

                        HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Net In-Store Total", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalTotal)}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = ShopKartOrange)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment Methods for POS
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("cash" to "Cash", "upi" to "UPI QR", "card" to "Card POS").forEach { (method, label) ->
                                val isSelected = selectedPosPayment == method
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                                        .border(1.5.dp, if (isSelected) ShopKartOrange else BorderMedium, RoundedCornerShape(8.dp))
                                        .clickable { selectedPosPayment = method }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) ShopKartOrange else TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val result = ConvexBackendService.createPosOrder(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    items = posCart.toList(),
                                    subtotal = subtotal,
                                    discount = discountAmt,
                                    gst = gstAmt,
                                    totalAmount = finalTotal,
                                    paymentMethod = selectedPosPayment
                                )
                                result.onSuccess { inv ->
                                    activeInvoice = inv
                                    posCart.clear()
                                    viewModel.showSnackbar("Sale complete! Invoice ${inv.invoiceNumber} created.")
                                }.onFailure { err ->
                                    viewModel.showSnackbar("Error: ${err.message}")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Print, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Complete Sale & Print Bill", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // POS Clean Tax Invoice Dialog
    activeInvoice?.let { inv ->
        AlertDialog(
            onDismissRequest = { activeInvoice = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ShopKartGreenDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("TAX INVOICE", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("SHOPKART RETAIL HUB", fontSize = 14.sp, fontWeight = FontWeight.Black)
                    Text("GSTIN: 29AABCS1429B1ZX • Indiranagar, Bengaluru", fontSize = 10.sp, color = TextSecondary)
                    Text("Invoice No: ${inv.invoiceNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShopKartOrange)
                    Text("Customer: ${inv.customerName} (${inv.customerPhone})", fontSize = 10.sp, color = TextMuted)
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                    inv.items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.qty}x ${item.product.title}", fontSize = 10.sp, maxLines = 1, modifier = Modifier.weight(1f))
                            Text("₹${item.product.price * item.qty}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 10.sp)
                        Text("₹${inv.subtotal}", fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("CGST (9%) + SGST (9%)", fontSize = 10.sp)
                        Text("₹${inv.gst}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Paid (${inv.paymentMethod})", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        Text("₹${inv.totalAmount}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = ShopKartOrange)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showSnackbar("Invoice sent to ${inv.customerPhone} ✓")
                        activeInvoice = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Text("Print / Share Bill", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeInvoice = null }) { Text("Close") }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 3: PRODUCTS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminProductsTab(viewModel: ShopKartViewModel, uiState: ShopKartUiState) {
    val products = ConvexBackendService.productsTable.value
    var search by remember { mutableStateOf("") }
    val filtered = if (search.isBlank()) products else products.filter {
        it.title.contains(search, ignoreCase = true) || it.brand.contains(search, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Products Catalog (${filtered.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Button(
                    onClick = { viewModel.openAddProductDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search products by title or brand...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShopKartOrange,
                    unfocusedBorderColor = BorderMedium
                )
            )
        }

        items(filtered, key = { it.id }) { product ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (product.drawableResId != null) {
                            Image(
                                painter = painterResource(product.drawableResId),
                                contentDescription = product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (product.images.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(product.images.first())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.price)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "• ${product.category.uppercase()}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        if (product.stock <= 0) {
                            Text(
                                text = "OUT OF STOCK (0)",
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        } else if (product.stock < 5) {
                            Text(
                                text = "Low Stock: ${product.stock} units",
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        } else {
                            Text(
                                text = "Stock: ${product.stock} units",
                                color = ShopKartGreenDark,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = { viewModel.openEditProductDialog(product) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { viewModel.deleteAdminProduct(product.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: ORDERS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminOrdersTab(viewModel: ShopKartViewModel, uiState: ShopKartUiState) {
    val orders = ConvexBackendService.ordersTable.value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "All Store Orders (${orders.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(orders, key = { it.id }) { order ->
            var expandedMenu by remember { mutableStateOf(false) }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ORDER #${order.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Tracking: ${order.trackingId}", fontSize = 9.sp, color = TextMuted)
                        }

                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (order.status) {
                                            "Delivered" -> ShopKartGreenLight
                                            "Shipped" -> Color(0xFFEDE9FE)
                                            "Packed" -> Color(0xFFFEF3C7)
                                            else -> Color(0xFFDBEAFE)
                                        }
                                    )
                                    .clickable { expandedMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${order.status} ▾",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (order.status) {
                                        "Delivered" -> ShopKartGreenDark
                                        "Shipped" -> Color(0xFF6D28D9)
                                        "Packed" -> Color(0xFFB45309)
                                        else -> Color(0xFF1D4ED8)
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                listOf("Ordered", "Packed", "Shipped", "Delivered").forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st, fontWeight = if (st == order.status) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = {
                                            expandedMenu = false
                                            viewModel.updateOrderStatus(order.id, st)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    order.products.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${p.qty}x ${p.productTitle}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(p.price * p.qty)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer: ${order.address.fullName} • Method: ${order.paymentMethod.uppercase()}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Total: ₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(order.totalAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartOrange
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: USERS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminUsersTab(viewModel: ShopKartViewModel, uiState: ShopKartUiState) {
    val users = ConvexBackendService.usersTable.value
    var search by remember { mutableStateOf("") }
    val filtered = if (search.isBlank()) users else users.filter {
        it.name.contains(search, ignoreCase = true) || it.email.contains(search, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Registered Users (${filtered.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search users by name or email...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ShopKartOrange,
                    unfocusedBorderColor = BorderMedium
                )
            )
        }

        items(filtered, key = { it.id }) { user ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ShopKartOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartOrange
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(user.email, fontSize = 11.sp, color = TextSecondary)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (user.isAdmin) ShopKartOrangeLight
                                        else if (user.isSeller) ShopKartGreenLight
                                        else SurfaceBg
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = user.role.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isAdmin) ShopKartOrange else if (user.isSeller) ShopKartGreenDark else TextMuted
                                )
                            }
                            Text(user.phone, fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    var showRoleMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showRoleMenu = true },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Role ▾", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            listOf("user", "seller", "admin").forEach { r ->
                                DropdownMenuItem(
                                    text = { Text("Set as ${r.uppercase()}") },
                                    onClick = {
                                        showRoleMenu = false
                                        viewModel.updateUserRole(user.id, r)
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete User", color = Color(0xFFDC2626)) },
                                onClick = {
                                    showRoleMenu = false
                                    viewModel.deleteUser(user.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: SELLERS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminSellersTab(viewModel: ShopKartViewModel, uiState: ShopKartUiState) {
    val sellers = ConvexBackendService.sellersTable.value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Registered Merchants & Sellers (${sellers.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(sellers, key = { it.id }) { seller ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(seller.shopName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("GST: ${seller.gst}", fontSize = 10.sp, color = TextSecondary)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (seller.isApproved) ShopKartGreenLight else Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (seller.isApproved) "Active / Approved" else "Pending Approval",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (seller.isApproved) ShopKartGreenDark else Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sales: ${seller.totalSales} units • Rating: ★ ${seller.rating}", fontSize = 11.sp, color = TextMuted)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (!seller.isApproved) {
                                Button(
                                    onClick = { viewModel.approveSeller(seller.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.removeSeller(seller.id) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Remove", fontSize = 10.sp, color = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 7: ANALYTICS
// -------------------------------------------------------------
@Composable
private fun AdminAnalyticsTab(stats: AdminStats) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Business Analytics & Growth", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Top Category Distribution", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "Electronics & Mobiles" to 0.65f,
                        "Fashion & Lifestyle" to 0.20f,
                        "Home & Appliances" to 0.10f,
                        "Grocery & Pantry" to 0.05f
                    ).forEach { (cat, pct) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(cat, fontSize = 11.sp, color = TextSecondary)
                                Text("${(pct * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SurfaceBg)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(pct)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ShopKartOrange)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 403 ACCESS DENIED SCREEN
// -------------------------------------------------------------
@Composable
private fun Admin403AccessDenied(
    onBackToShop: () -> Unit,
    onSwitchToAdmin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp)),
            color = PureWhite
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Access Denied",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "403 Access Denied",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                Text(
                    text = "Administrative privileges are required to view the ShopKart Admin Console. You are currently logged in with a standard customer account.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Button(
                    onClick = onSwitchToAdmin,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("⚡ Quick Switch to Admin Profile", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBackToShop,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Return to ShopKart Storefront", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER UI COMPONENTS & DIALOGS
// -------------------------------------------------------------
@Composable
private fun AdminTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) ShopKartOrange else SurfaceBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PureWhite else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PureWhite else TextPrimary
            )
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    trend: String,
    isPositive: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
        color = PureWhite
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = ShopKartOrange, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trend,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) ShopKartGreenDark else Color(0xFFDC2626)
            )
        }
    }
}

@Composable
private fun AdminAddProductDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, price: Int, origPrice: Int, cat: String, brand: String, stock: Int, imgs: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var origPriceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("electronics") }
    var brand by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("15") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = origPriceText,
                        onValueChange = { origPriceText = it },
                        label = { Text("MRP (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (mobiles/electronics/fashion/home)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toIntOrNull() ?: 999
                    val origPrice = origPriceText.toIntOrNull() ?: (price * 12 / 10)
                    val stock = stockText.toIntOrNull() ?: 10
                    if (title.isNotBlank() && brand.isNotBlank()) {
                        onSubmit(title, desc.ifBlank { title }, price, origPrice, category, brand, stock, listOf(imageUrl))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
            ) {
                Text("Create Product", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AdminEditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, price: Int, origPrice: Int, stock: Int, cat: String, brand: String) -> Unit
) {
    var title by remember { mutableStateOf(product.title) }
    var desc by remember { mutableStateOf(product.description) }
    var priceText by remember { mutableStateOf(product.price.toString()) }
    var origPriceText by remember { mutableStateOf(product.originalPrice.toString()) }
    var stockText by remember { mutableStateOf(product.stock.toString()) }
    var category by remember { mutableStateOf(product.category) }
    var brand by remember { mutableStateOf(product.brand) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toIntOrNull() ?: product.price
                    val origPrice = origPriceText.toIntOrNull() ?: product.originalPrice
                    val stock = stockText.toIntOrNull() ?: product.stock
                    onSubmit(title, desc, price, origPrice, stock, category, brand)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
