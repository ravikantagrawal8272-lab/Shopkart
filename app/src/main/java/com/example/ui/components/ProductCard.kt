package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.model.Product
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.HeartRed
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeDark
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isOutOfStock = product.stock <= 0

    // Smooth spring lift and zoom animation
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 1.dp,
        animationSpec = spring(),
        label = "elevation"
    )
    val imageScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1.0f,
        animationSpec = spring(),
        label = "imageScale"
    )
    val cardOffsetY by animateDpAsState(
        targetValue = if (isPressed) (-4).dp else 0.dp,
        animationSpec = spring(),
        label = "offsetY"
    )

    Surface(
        modifier = modifier
            .offset(y = cardOffsetY)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, BorderLight, RoundedCornerShape(16.dp))
            .shadow(elevation, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onProductClick() }
            .testTag("product_card_${product.id}"),
        color = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Product Image Container (4:3 ratio) + Wishlist Icon + Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceBg),
                contentAlignment = Alignment.Center
            ) {
                // Image with fallback to drawableResId or Coil remote URL
                if (product.drawableResId != null) {
                    Image(
                        painter = painterResource(id = product.drawableResId),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(imageScale)
                    )
                } else if (product.images.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.images.first())
                            .crossfade(true)
                            .build(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(imageScale)
                    )
                }

                // Tag or Out of Stock Badge top-left
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(HeartRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OUT OF STOCK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PureWhite
                        )
                    }
                } else if (product.tag != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ShopKartOrange)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                // Wishlist Heart Button top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PureWhite.copy(alpha = 0.92f))
                        .clickable { onWishlistToggle() }
                        .testTag("wishlist_btn_${product.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) HeartRed else TextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Badge (Green Pill with Star) & ShopKart Assured
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Green Rating Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ShopKartGreen)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f", product.rating),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating Star",
                        tint = PureWhite,
                        modifier = Modifier.size(10.dp)
                    )
                }

                // Stock status indicator
                if (isOutOfStock) {
                    Text(
                        text = "Out of Stock",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeartRed
                    )
                } else if (product.isShopKartAssured) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ShopKartOrangeLight)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = "Assured",
                            tint = ShopKartOrange,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Assured",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShopKartOrangeDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Brand & Product Title (2 lines max)
            Text(
                text = product.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                modifier = Modifier.height(34.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price Row: Bold Price, Strikethrough, Green Discount %
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    fontWeight = FontWeight.Normal,
                    color = TextMuted,
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = "${product.discount}% OFF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartGreen
                )
            }

            // 1-Day Delivery Tag or Stock warning
            if (isOutOfStock) {
                Text(
                    text = "🚫 Currently Unavailable",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (product.oneDayDelivery) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ Delivery by Tomorrow",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ShopKartOrangeDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: "Add to Cart" + "Buy Now" (or Notify Me if out of stock)
            if (isOutOfStock) {
                OutlinedButton(
                    onClick = onProductClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("out_of_stock_${product.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceBg,
                        contentColor = TextSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(BorderMedium)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notify",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Notify Me",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Add to Cart Button (White bordered)
                    OutlinedButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("add_to_cart_${product.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PureWhite,
                            contentColor = ShopKartOrange
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ShopKartOrange)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Add to Cart",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // Buy Now Button (Orange Filled)
                    Button(
                        onClick = onBuyNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("buy_now_${product.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShopKartOrange,
                            contentColor = PureWhite
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Buy Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
