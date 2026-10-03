package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

sealed class NavItem(
    val screen: AppScreen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavItem(AppScreen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Categories : NavItem(AppScreen.CATEGORIES, "Categories", Icons.Filled.Category, Icons.Outlined.Category)
    object Search : NavItem(AppScreen.SEARCH_FILTER, "Explore", Icons.Filled.Search, Icons.Outlined.Search)
    object Wishlist : NavItem(AppScreen.WISHLIST, "Wishlist", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Cart : NavItem(AppScreen.CART, "Cart", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag)
}

@Composable
fun BottomNavBar(
    currentScreen: AppScreen,
    cartCount: Int,
    wishlistCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem.Home,
        NavItem.Categories,
        NavItem.Search,
        NavItem.Wishlist,
        NavItem.Cart
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, spotColor = Color.Black.copy(alpha = 0.08f)),
        color = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderLight)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) ShopKartOrange else TextSecondary,
                        animationSpec = spring(),
                        label = "iconColor"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigate(item.screen) }
                            .padding(vertical = 4.dp)
                            .testTag("nav_item_${item.title.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (item == NavItem.Cart) {
                                BadgedBox(
                                    badge = {
                                        if (cartCount > 0) {
                                            Badge(
                                                containerColor = ShopKartOrange,
                                                contentColor = PureWhite
                                            ) {
                                                Text(
                                                    text = "$cartCount",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = iconColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else if (item == NavItem.Wishlist) {
                                BadgedBox(
                                    badge = {
                                        if (wishlistCount > 0) {
                                            Badge(
                                                containerColor = ShopKartOrange,
                                                contentColor = PureWhite
                                            ) {
                                                Text(
                                                    text = "$wishlistCount",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = iconColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = iconColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
