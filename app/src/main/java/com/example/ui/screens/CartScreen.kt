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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.model.CartItem
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
fun CartScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var couponCodeInput by remember { mutableStateOf("") }
    val totalMRP = viewModel.calculateTotalMRP(uiState)
    val totalDiscount = viewModel.calculateDiscount(uiState)
    val finalTotal = viewModel.calculateTotal(uiState)

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("cart_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "My Cart (${uiState.cartItems.sumOf { it.quantity }})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        if (uiState.cartItems.isEmpty()) {
            // Empty State
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
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(ShopKartOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "Empty Cart",
                            tint = ShopKartOrange,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Cart is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Explore trending deals and fill your bag with great products!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    Button(
                        onClick = { onNavigate(AppScreen.HOME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShopKartOrange,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start Shopping", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Delivery Address Strip
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Location",
                                    tint = ShopKartOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Deliver to: John Doe, ${uiState.pincodeInput}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Flat 402, Skyline Residency, Tech Hub Road",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, ShopKartOrange, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.checkPincode("560001") }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Change",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartOrange
                                )
                            }
                        }
                    }
                }

                // Cart Items
                items(uiState.cartItems, key = { "${it.product.id}_${it.selectedVariant}_${it.selectedColor}" }) { item ->
                    Spacer(modifier = Modifier.height(8.dp))
                    CartItemCard(
                        cartItem = item,
                        onIncrement = { viewModel.updateCartQuantity(item, 1) },
                        onDecrement = { viewModel.updateCartQuantity(item, -1) },
                        onRemove = { viewModel.removeCartItem(item) },
                        onMoveToWishlist = { viewModel.moveToWishlist(item) },
                        onProductClick = { viewModel.openProductDetail(item.product) }
                    )
                }

                // Coupon Code Box
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalOffer,
                                    contentDescription = "Coupons",
                                    tint = ShopKartOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Coupons & Bank Offers",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (uiState.appliedCoupon != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ShopKartGreenLight)
                                        .border(1.dp, ShopKartGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Applied",
                                            tint = ShopKartGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "'${uiState.appliedCoupon.code}' Applied",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ShopKartGreenDark
                                            )
                                            Text(
                                                text = uiState.appliedCoupon.discountText,
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.removeCoupon() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove coupon",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = couponCodeInput,
                                        onValueChange = { couponCodeInput = it },
                                        placeholder = { Text("Try HDFC10, ICICIFEST", fontSize = 12.sp) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("coupon_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ShopKartOrange,
                                            unfocusedBorderColor = BorderMedium,
                                            focusedContainerColor = SurfaceBg,
                                            unfocusedContainerColor = SurfaceBg
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.applyCoupon(couponCodeInput)
                                            couponCodeInput = ""
                                        },
                                        modifier = Modifier
                                            .height(46.dp)
                                            .testTag("apply_coupon_btn"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ShopKartOrange,
                                            contentColor = PureWhite
                                        )
                                    ) {
                                        Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Price Details (Bill Summary)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Price Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Total MRP
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Price (${uiState.cartItems.sumOf { it.quantity }} items)",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalMRP)}",
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Discount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Discount",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "- ₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalDiscount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Delivery Charges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Delivery Charges",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹99",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "FREE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Total Amount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Amount",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalTotal)}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Green Savings Strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ShopKartGreenLight)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "🎉 You will save ₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalDiscount)} on this order",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartGreenDark
                                )
                            }
                        }
                    }
                }

                // Security Note
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Safe",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Safe & Secure Payments • 100% Authentic Products",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Sticky Bottom Checkout Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, spotColor = Color.Black.copy(alpha = 0.1f)),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(finalTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "View price details",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShopKartOrange
                        )
                    }

                    Button(
                        onClick = { onNavigate(AppScreen.CHECKOUT) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("place_order_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShopKartOrange,
                            contentColor = PureWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Proceed to Checkout",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    onMoveToWishlist: () -> Unit,
    onProductClick: () -> Unit
) {
    val product = cartItem.product

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight),
        color = PureWhite
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceBg)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                        .clickable { onProductClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (product.drawableResId != null) {
                        Image(
                            painter = painterResource(id = product.drawableResId),
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrls.firstOrNull() ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = product.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Text(
                        text = "${cartItem.selectedColor} • ${cartItem.selectedVariant}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.price)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.originalPrice)}",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )

                        Text(
                            text = "${product.discountPercent}% off",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartGreen
                        )
                    }

                    if (product.oneDayDelivery) {
                        Text(
                            text = "⚡ Delivery by Tomorrow",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShopKartOrangeDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(10.dp))

            // Quantity Modifier + Remove + Save for Later
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Modifier Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, BorderMedium, RoundedCornerShape(6.dp))
                        .background(SurfaceBg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Decrease quantity",
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = "${cartItem.quantity}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onIncrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase quantity",
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Save for later (Wishlist) & Remove
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Save for later",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.clickable { onMoveToWishlist() }
                    )

                    Text(
                        text = "Remove",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartOrange,
                        modifier = Modifier.clickable { onRemove() }
                    )
                }
            }
        }
    }
}
