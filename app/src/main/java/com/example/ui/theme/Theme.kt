package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Strict White Mode Only Theme
private val ShopKartColorScheme = lightColorScheme(
    primary = ShopKartOrange,
    onPrimary = PureWhite,
    primaryContainer = ShopKartOrangeLight,
    onPrimaryContainer = ShopKartOrangeDark,
    secondary = ShopKartGreen,
    onSecondary = PureWhite,
    secondaryContainer = ShopKartGreenLight,
    onSecondaryContainer = ShopKartGreenDark,
    background = SurfaceBg,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondary,
    outline = BorderMedium,
    outlineVariant = BorderLight
)

@Composable
fun MyApplicationTheme(
    // Ignore system dark mode to strictly honor "White mode only, NO dark mode" requirement
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ShopKartColorScheme,
        typography = Typography,
        content = content
    )
}
