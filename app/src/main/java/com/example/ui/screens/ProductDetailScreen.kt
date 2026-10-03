package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ShopKartRepository
import com.example.model.AppScreen
import com.example.model.Product
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.HeartRed
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartBlue
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenDark
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeDark
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.StarYellow
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = uiState.selectedProduct ?: return

    BackHandler {
        viewModel.navigateBack()
    }

    var isZoomed by remember { mutableStateOf(false) }
    val zoomScale by animateFloatAsState(
        targetValue = if (isZoomed) 1.25f else 1.0f,
        animationSpec = spring(),
        label = "zoomScale"
    )

    var pincodeInputText by remember { mutableStateOf(uiState.pincodeInput) }
    val isWishlisted = uiState.wishlistProductIds.contains(product.id)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
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
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { /* Share action */ },
                            modifier = Modifier.testTag("share_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = TextSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.testTag("detail_wishlist_btn")
                        ) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) HeartRed else TextSecondary
                            )
                        }

                        IconButton(
                            onClick = { onNavigate(AppScreen.CART) },
                            modifier = Modifier.testTag("detail_cart_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = "Cart",
                                tint = ShopKartOrange
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Image Gallery (Main Image + Zoom toggle + 3 Thumbnails)
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Main Image Frame
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.2f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceBg)
                                    .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp))
                                    .clickable { isZoomed = !isZoomed },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.selectedImageIndex == 0 && product.drawableResId != null) {
                                    Image(
                                        painter = painterResource(id = product.drawableResId),
                                        contentDescription = product.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .scale(zoomScale)
                                    )
                                } else {
                                    val imageUrl = product.imageUrls.getOrNull(uiState.selectedImageIndex)
                                        ?: product.imageUrls.firstOrNull() ?: ""
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = product.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .scale(zoomScale)
                                    )
                                }

                                // Zoom tip badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PureWhite.copy(alpha = 0.9f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isZoomed) "Tap to normal" else "🔍 Tap to zoom",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3-4 Thumbnails Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val imagesCount = maxOf(product.imageUrls.size, 1)
                                for (i in 0 until minOf(imagesCount, 4)) {
                                    val isSelected = uiState.selectedImageIndex == i
                                    val borderC = if (isSelected) ShopKartOrange else BorderLight
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceBg)
                                            .border(2.dp, borderC, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setSelectedImageIndex(i) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (i == 0 && product.drawableResId != null) {
                                            Image(
                                                painter = painterResource(id = product.drawableResId),
                                                contentDescription = "Thumb 1",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            val thumbUrl = product.imageUrls.getOrNull(i) ?: product.imageUrls.firstOrNull() ?: ""
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(thumbUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Thumb ${i + 1}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Title, Brand & Ratings Summary
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Brand Tag & Assured Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = product.brand.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ShopKartOrange,
                                    letterSpacing = 1.sp
                                )

                                if (product.isShopKartAssured) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ShopKartOrangeLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ElectricBolt,
                                            contentDescription = "Assured",
                                            tint = ShopKartOrange,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "ShopKart Assured",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShopKartOrangeDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Product Title
                            Text(
                                text = product.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Rating Pill + Reviews Count
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ShopKartGreen)
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", product.rating),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Rating",
                                        tint = PureWhite,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale.US).format(product.ratingCount)} Ratings & ${NumberFormat.getNumberInstance(Locale.US).format(product.reviewCount)} Reviews",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Pricing & EMI info
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.price)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(product.originalPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextMuted,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Text(
                                    text = "${product.discountPercent}% OFF",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ShopKartGreen
                                )
                            }

                            Text(
                                text = "EMI starting from ₹${(product.price / 12)}/month. No Cost EMI available.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Offers Box with Green Dotted/Dashed Border (As requested)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Available Bank Offers & Discounts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dotted Green Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val stroke = Stroke(
                                            width = 3f,
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                        )
                                        drawRoundRect(
                                            color = Color(0xFF008A00),
                                            style = stroke,
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
                                        )
                                    }
                                    .background(ShopKartGreenLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    product.bankOffers.forEach { offerText ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.LocalOffer,
                                                contentDescription = "Offer",
                                                tint = ShopKartGreen,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Text(
                                                text = offerText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Delivery Pincode Checker
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Delivery Options & Pincode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = pincodeInputText,
                                    onValueChange = { if (it.length <= 6) pincodeInputText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("pincode_input"),
                                    placeholder = { Text("Enter 6-digit PIN", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = "Pincode",
                                            tint = ShopKartOrange,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        viewModel.checkPincode(pincodeInputText)
                                    }),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ShopKartOrange,
                                        unfocusedBorderColor = BorderMedium,
                                        focusedContainerColor = SurfaceBg,
                                        unfocusedContainerColor = SurfaceBg
                                    )
                                )

                                Button(
                                    onClick = { viewModel.checkPincode(pincodeInputText) },
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("check_pincode_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ShopKartOrange,
                                        contentColor = PureWhite
                                    )
                                ) {
                                    if (uiState.isPincodeChecking) {
                                        CircularProgressIndicator(
                                            color = PureWhite,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Check", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (uiState.pincodeStatus != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Status",
                                        tint = ShopKartGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = uiState.pincodeStatus,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ShopKartGreenDark
                                    )
                                }
                            }
                        }
                    }
                }

                // Variants Selector (Colors & Sizes/Storage)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Colors
                            Text(
                                text = "Select Color: ${uiState.selectedProductColor}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                product.colors.forEach { colorName ->
                                    val isSelected = uiState.selectedProductColor == colorName
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) ShopKartOrange else BorderMedium,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.setSelectedColor(colorName) }
                                            .padding(horizontal = 12.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = colorName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) ShopKartOrange else TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Storage / Size Variants
                            Text(
                                text = "Select Variant: ${uiState.selectedProductVariant}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                product.variants.forEach { variantName ->
                                    val isSelected = uiState.selectedProductVariant == variantName
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) ShopKartOrange else BorderMedium,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.setSelectedVariant(variantName) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = variantName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) ShopKartOrange else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Highlights Section
                if (product.highlights.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = PureWhite
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Product Highlights",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    product.highlights.forEach { highlight ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 6.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(ShopKartOrange)
                                            )
                                            Text(
                                                text = highlight,
                                                fontSize = 12.sp,
                                                color = TextPrimary,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Detailed Specifications Table
                if (product.specifications.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = PureWhite
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Specifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    product.specifications.forEach { (key, value) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = key,
                                                fontSize = 12.sp,
                                                color = TextSecondary,
                                                modifier = Modifier.weight(0.4f)
                                            )
                                            Text(
                                                text = value,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary,
                                                modifier = Modifier.weight(0.6f)
                                            )
                                        }
                                        HorizontalDivider(color = BorderLight)
                                    }
                                }
                            }
                        }
                    }
                }

                // Seller Information & ShopKart Guarantee
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Sold by",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = product.sellerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "★ ${product.sellerRating} Seller Rating | 100% On-Time Dispatch",
                                    fontSize = 11.sp,
                                    color = ShopKartGreen
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceBg)
                                    .border(1.dp, BorderMedium, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "View Store",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartOrange
                                )
                            }
                        }
                    }
                }

                // Reviews Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PureWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Customer Ratings & Reviews",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Rate Product",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartOrange
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            ShopKartRepository.sampleReviews.forEach { review ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ShopKartGreen)
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${review.rating}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PureWhite
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = "Star",
                                                tint = PureWhite,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }

                                        Text(
                                            text = review.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = review.comment,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${review.userName} • ${review.date}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                        if (review.verifiedPurchase) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.Verified,
                                                    contentDescription = "Verified",
                                                    tint = ShopKartGreen,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = " Verified Buyer",
                                                    fontSize = 10.sp,
                                                    color = ShopKartGreen,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = BorderLight)
                            }
                        }
                    }
                }
            }

            // Sticky Bottom Bar with "Add to Cart" and "Buy Now"
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
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add to Cart Button (White with Orange Border)
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(
                                product,
                                uiState.selectedProductVariant,
                                uiState.selectedProductColor
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_cart_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PureWhite,
                            contentColor = ShopKartOrange
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ShopKartOrange)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "Cart",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add to Cart",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Buy Now Button (Vibrant Orange Filled)
                    Button(
                        onClick = {
                            viewModel.buyNow(
                                product,
                                uiState.selectedProductVariant,
                                uiState.selectedProductColor
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShopKartOrange,
                            contentColor = PureWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = "Buy",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buy Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
