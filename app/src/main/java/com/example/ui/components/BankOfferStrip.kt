package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShopKartRepository
import com.example.model.BankOffer
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartBlue
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartGreenLight
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BankOfferStrip(
    onOfferClick: (BankOffer) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PureWhite
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalOffer,
                        contentDescription = "Offers",
                        tint = ShopKartOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bank & Payment Offers",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Instant 10% Off",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartGreen
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Horizontal Scroll of Bank Cards
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ShopKartRepository.bankOffers, key = { it.id }) { offer ->
                    BankOfferCard(
                        offer = offer,
                        onClick = { onOfferClick(offer) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BankOfferCard(
    offer: BankOffer,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp))
            .shadow(2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.04f))
            .clickable { onClick() }
            .testTag("bank_offer_${offer.code}"),
        color = PureWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShopKartOrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CreditCard,
                    contentDescription = offer.bankName,
                    tint = ShopKartOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = offer.bankName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ShopKartGreenLight)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = offer.code,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShopKartGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = offer.discountText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
