package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.NotificationItem
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartViewModel

@Composable
fun NotificationsScreen(
    viewModel: ShopKartViewModel,
    uiState: com.example.viewmodel.ShopKartUiState,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val tabs = listOf("All", "Orders", "Offers", "System")
    val selectedTab = uiState.selectedNotificationTab

    val filteredNotifications = remember(uiState.notifications, selectedTab) {
        when (selectedTab.lowercase()) {
            "orders" -> uiState.notifications.filter { it.type == "order_update" }
            "offers" -> uiState.notifications.filter { it.type == "offer" }
            "system" -> uiState.notifications.filter { it.type == "system" || it.type == "low_stock" }
            else -> uiState.notifications
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SurfaceBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp),
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("back_btn_notifs")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Notifications",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (uiState.unreadNotificationsCount > 0) {
                                Text(
                                    text = "${uiState.unreadNotificationsCount} unread updates",
                                    fontSize = 11.sp,
                                    color = ShopKartOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (uiState.notifications.any { !it.isRead }) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ShopKartOrangeLight)
                                .clickable { viewModel.markAllNotificationsAsRead() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("mark_all_read_btn"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = "Mark all as read",
                                tint = ShopKartOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mark all read",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartOrange
                            )
                        }
                    }
                }
            }

            // Category Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = tab.equals(selectedTab, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ShopKartOrange else SurfaceBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ShopKartOrange else BorderMedium,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setNotificationTab(tab) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("notif_tab_$tab")
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PureWhite else TextSecondary
                        )
                    }
                }
            }

            // Notification List or Empty State
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(PureWhite)
                                .border(1.dp, BorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsNone,
                                contentDescription = "No notifications",
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notifications in '$selectedTab'",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "We'll notify you when new offers, price drops, or order dispatches occur.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationCard(
                            notification = notif,
                            onClick = { viewModel.onNotificationClicked(notif) },
                            onDelete = { viewModel.deleteNotification(notif.id) },
                            onMarkRead = { viewModel.markNotificationAsRead(notif.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onMarkRead: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = if (!notification.isRead) ShopKartOrange.copy(alpha = 0.3f) else BorderLight,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag("notif_card_${notification.id}"),
        color = if (!notification.isRead) PureWhite else PureWhite.copy(alpha = 0.95f),
        shadowElevation = if (!notification.isRead) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Type Icon or Product Thumbnail
            if (!notification.image.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceBg)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(notification.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Notification Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                val iconBg = when (notification.type) {
                    "order_update" -> ShopKartGreenLight
                    "offer" -> ShopKartOrangeLight
                    "low_stock" -> Color(0xFFFEF3C7)
                    else -> SurfaceBg
                }
                val iconTint = when (notification.type) {
                    "order_update" -> ShopKartGreen
                    "offer" -> ShopKartOrange
                    "low_stock" -> Color(0xFFD97706)
                    else -> TextPrimary
                }
                val iconVector = when (notification.type) {
                    "order_update" -> Icons.Filled.LocalShipping
                    "offer" -> Icons.Filled.LocalOffer
                    "low_stock" -> Icons.Filled.WarningAmber
                    else -> Icons.Filled.Notifications
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = notification.type,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ShopKartOrange)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Just now",
                        fontSize = 10.sp,
                        color = TextMuted
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!notification.isRead) {
                            Text(
                                text = "Mark Read",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartOrange,
                                modifier = Modifier.clickable { onMarkRead() }
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onDelete() }
                        )
                    }
                }
            }
        }
    }
}
