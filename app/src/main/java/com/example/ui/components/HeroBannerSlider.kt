package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeDark
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class HeroSlideData(
    val title: String,
    val subtitle: String,
    val badge: String,
    val discount: String,
    val drawableRes: Int,
    val targetCategory: String
)

@Composable
fun HeroBannerSlider(
    onSlideClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = listOf(
        HeroSlideData(
            title = "Next-Gen Tech",
            subtitle = "Flagships & Pro Audio Deals",
            badge = "🔥 BIGGEST SALE OF 2026",
            discount = "UP TO 50% OFF",
            drawableRes = R.drawable.img_hero_festive_sale,
            targetCategory = "electronics"
        ),
        HeroSlideData(
            title = "Luxury Fashion Drop",
            subtitle = "Sneakers, Watches & Apparel",
            badge = "✨ TRENDING CURATIONS",
            discount = "EXTRA ₹1,500 OFF",
            drawableRes = R.drawable.img_hero_fashion_trend,
            targetCategory = "fashion"
        ),
        HeroSlideData(
            title = "Smart Living & Home",
            subtitle = "Espresso Machines & Vacuums",
            badge = "⚡ 1-DAY DELIVERY",
            discount = "FLAT 40% OFF",
            drawableRes = R.drawable.img_hero_home_appliances,
            targetCategory = "appliances"
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto slide banner with loop
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 14.dp),
            pageSpacing = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("hero_banner_pager")
        ) { page ->
            val slide = slides[page]
            HeroBannerCard(
                slide = slide,
                onClick = { onSlideClick(slide.targetCategory) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pager indicator dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slides.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width = if (isSelected) 22.dp else 6.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(if (isSelected) ShopKartOrange else BorderLight)
                )
            }
        }
    }
}

@Composable
private fun HeroBannerCard(
    slide: HeroSlideData,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp))
            .shadow(4.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.06f))
            .clickable { onClick() }
            .testTag("hero_card_${slide.title}"),
        color = PureWhite
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Hero Image
            Image(
                painter = painterResource(id = slide.drawableRes),
                contentDescription = slide.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Ultra-Clean 2D Clean Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PureWhite.copy(alpha = 0.96f),
                                PureWhite.copy(alpha = 0.82f),
                                PureWhite.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 850f
                        )
                    )
            )

            // Content Left Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ShopKartOrangeLight)
                        .border(1.dp, ShopKartOrange.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = slide.badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShopKartOrangeDark,
                        letterSpacing = 0.5.sp
                    )
                }

                // Middle Titles
                Column {
                    Text(
                        text = slide.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        lineHeight = 22.sp,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = slide.subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }

                // CTA Button & Discount Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ShopKartOrange)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shop Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = PureWhite,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Text(
                        text = slide.discount,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShopKartGreen
                    )
                }
            }
        }
    }
}
