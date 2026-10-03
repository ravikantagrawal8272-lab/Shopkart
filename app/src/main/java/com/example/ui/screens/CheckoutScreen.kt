package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ConvexBackendService
import com.example.data.ShopKartRepository
import com.example.model.AddressRecord
import com.example.model.AppScreen
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
fun CheckoutScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedPaymentMethod by remember { mutableStateOf("razorpay") } // "razorpay" | "cod" | "upi"
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var showRazorpayGatewayModal by remember { mutableStateOf(false) }

    val userAddresses = uiState.currentUser?.addresses?.ifEmpty {
        listOf(ShopKartRepository.defaultAddress)
    } ?: listOf(ShopKartRepository.defaultAddress)

    var selectedAddressId by remember { mutableStateOf(userAddresses.firstOrNull()?.id ?: "addr_001") }
    val selectedAddress = userAddresses.find { it.id == selectedAddressId } ?: userAddresses.first()

    val totalMRP = viewModel.calculateTotalMRP(uiState)
    val totalDiscount = viewModel.calculateDiscount(uiState)
    val baseTotal = viewModel.calculateTotal(uiState)
    val codExtraFee = if (selectedPaymentMethod == "cod") 40 else 0
    val finalPayable = baseTotal + codExtraFee

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Sticky Header
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Secure Checkout",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Secure",
                        tint = ShopKartGreenDark,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Delivery Address Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(ShopKartOrangeLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("1", color = ShopKartOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delivery Address", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            TextButton(
                                onClick = { showAddAddressDialog = true },
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, tint = ShopKartOrange, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Add New", color = ShopKartOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        userAddresses.forEach { addr ->
                            val isSelected = addr.id == selectedAddressId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ShopKartOrangeLight.copy(alpha = 0.3f) else PureWhite)
                                    .border(1.dp, if (isSelected) ShopKartOrange else BorderLight, RoundedCornerShape(8.dp))
                                    .clickable { selectedAddressId = addr.id }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAddressId = addr.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = ShopKartOrange),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(addr.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        if (addr.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(ShopKartGreenLight)
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text("HOME", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ShopKartGreenDark)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${addr.street}, ${addr.city}, ${addr.state} - ${addr.pincode}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // 2. Order Summary Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp)),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ShopKartOrangeLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("2", color = ShopKartOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Order Items (${uiState.cartItems.sumOf { it.quantity }})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.product.drawableResId != null) {
                                        Image(
                                            painter = painterResource(item.product.drawableResId),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else if (item.product.images.isNotEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(item.product.images.first())
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, color = TextPrimary)
                                    Text("Qty: ${item.quantity} • ${item.selectedVariant}", fontSize = 10.sp, color = TextMuted)
                                }

                                Text(
                                    text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(item.product.price * item.quantity)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Select Payment Method Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp)),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ShopKartOrangeLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("3", color = ShopKartOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Payment Options", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Option A: Razorpay
                        PaymentOptionCard(
                            title = "Razorpay Gateway (UPI / Cards / NetBanking)",
                            subtitle = "Instant 10% discount on HDFC & Axis Cards",
                            badgeText = "Fastest & Recommended",
                            icon = Icons.Filled.CreditCard,
                            isSelected = selectedPaymentMethod == "razorpay",
                            onClick = { selectedPaymentMethod = "razorpay" }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option B: UPI Direct
                        PaymentOptionCard(
                            title = "UPI Direct (Google Pay, PhonePe, Paytm)",
                            subtitle = "Zero convenience fee • Instant confirmation",
                            badgeText = "Instant",
                            icon = Icons.Filled.QrCode2,
                            isSelected = selectedPaymentMethod == "upi",
                            onClick = { selectedPaymentMethod = "upi" }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option C: Cash on Delivery
                        PaymentOptionCard(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash or UPI at delivery doorstep (+₹40 charge)",
                            badgeText = "+₹40 handling",
                            icon = Icons.Filled.CurrencyRupee,
                            isSelected = selectedPaymentMethod == "cod",
                            onClick = { selectedPaymentMethod = "cod" }
                        )
                    }
                }
            }

            // 4. Price Breakdown & Bill Details
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp)),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Price Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        HorizontalDivider(color = BorderLight)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total MRP", fontSize = 12.sp, color = TextSecondary)
                            Text("₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalMRP)}", fontSize = 12.sp, color = TextPrimary)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount on MRP", fontSize = 12.sp, color = ShopKartGreenDark)
                            Text("-₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalDiscount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ShopKartGreenDark)
                        }

                        if (uiState.appliedCoupon != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bank Offer (${uiState.appliedCoupon.code})", fontSize = 12.sp, color = ShopKartGreenDark)
                                Text("-₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(uiState.appliedCoupon.maxDiscount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ShopKartGreenDark)
                            }
                        }

                        if (selectedPaymentMethod == "cod") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("COD Handling Fee", fontSize = 12.sp, color = Color(0xFFD97706))
                                Text("+₹40", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Charges", fontSize = 12.sp, color = TextSecondary)
                            Text("FREE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShopKartGreenDark)
                        }

                        HorizontalDivider(color = BorderLight)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text(
                                "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalPayable)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ShopKartOrange
                            )
                        }
                    }
                }
            }

            // Trust Security Badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = ShopKartGreenDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Safe & Secure Payments • Verified by Convex Security",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }
        }

        // Bottom Sticky Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalPayable)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = if (selectedPaymentMethod == "cod") "Pay on delivery" else "Pay securely online",
                        fontSize = 10.sp,
                        color = ShopKartGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (selectedPaymentMethod == "razorpay") {
                            showRazorpayGatewayModal = true
                        } else {
                            viewModel.processPaymentAndPlaceOrder(
                                paymentMethod = selectedPaymentMethod,
                                totalAmount = finalPayable,
                                address = selectedAddress
                            )
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("pay_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = PureWhite, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedPaymentMethod == "cod") "Place COD Order" else "Pay & Place Order",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Razorpay Simulation Gateway Modal
    if (showRazorpayGatewayModal) {
        AlertDialog(
            onDismissRequest = { showRazorpayGatewayModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C2340)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razorpay Trusted Gateway", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Amount to Pay: ₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalPayable)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Merchant: ShopKart Official Hub • Encrypted 256-Bit SSL",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    HorizontalDivider(color = BorderLight)
                    Text("Select payment option:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    listOf("Google Pay / PhonePe UPI", "HDFC / ICICI / SBI Cards", "NetBanking", "Paytm Wallet").forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceBg)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ShopKartGreenDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRazorpayGatewayModal = false
                        viewModel.processPaymentAndPlaceOrder(
                            paymentMethod = "razorpay",
                            totalAmount = finalPayable,
                            address = selectedAddress,
                            razorpayPaymentId = "pay_rzp_${System.currentTimeMillis()}"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Text("Authorize & Pay ₹$finalPayable", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRazorpayGatewayModal = false }) {
                    Text("Cancel Payment")
                }
            }
        )
    }

    // Add New Address Modal Dialog
    if (showAddAddressDialog) {
        var fullName by remember { mutableStateOf("") }
        var street by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("Bengaluru") }
        var state by remember { mutableStateOf("Karnataka") }
        var pincode by remember { mutableStateOf("560001") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Receiver Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("House / Flat / Street Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("PIN Code") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullName.isNotBlank() && street.isNotBlank()) {
                            val newAddr = AddressRecord(
                                id = "addr_${System.currentTimeMillis()}",
                                fullName = fullName,
                                street = street,
                                city = city,
                                state = state,
                                pincode = pincode,
                                isDefault = true
                            )
                            viewModel.addNewAddress(newAddr)
                            selectedAddressId = newAddr.id
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Text("Save Address", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, if (isSelected) ShopKartOrange else BorderLight, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) ShopKartOrangeLight.copy(alpha = 0.25f) else PureWhite
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = ShopKartOrange),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) ShopKartOrange else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text(subtitle, fontSize = 10.sp, color = TextSecondary)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) ShopKartOrange else TextMuted
                )
            }
        }
    }
}
