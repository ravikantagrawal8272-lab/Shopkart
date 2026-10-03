package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeDark
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OrderSuccessDialog(
    orderId: String,
    onDismiss: () -> Unit,
    onTrackOrder: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, BorderLight, RoundedCornerShape(20.dp))
                .testTag("order_success_dialog"),
            color = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Animated Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ShopKartGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = ShopKartGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Order Placed Successfully! 🎉",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Thank you for shopping on ShopKart. Your order has been confirmed.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Order ID Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceBg)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ORDER REFERENCE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = orderId,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartOrange
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ElectricBolt,
                                contentDescription = "Express",
                                tint = ShopKartOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Guaranteed 1-Day Delivery by Tomorrow 11 AM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartOrangeDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onTrackOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("track_order_modal_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShopKartOrange,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Track Order Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onContinueShopping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("continue_shopping_modal_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Continue Shopping",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
