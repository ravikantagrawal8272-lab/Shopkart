package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppScreen
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BecomeSellerScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.DevTestsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrderSuccessDialog
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchFilterScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceBg
import com.example.viewmodel.ShopKartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ShopKartApp()
            }
        }
    }
}

@Composable
fun ShopKartApp(
    viewModel: ShopKartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }

    val showBottomBar = uiState.currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.CATEGORIES,
        AppScreen.SEARCH_FILTER,
        AppScreen.WISHLIST,
        AppScreen.CART
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentScreen = uiState.currentScreen,
                    cartCount = uiState.cartItems.sumOf { it.quantity },
                    wishlistCount = uiState.wishlistProductIds.size,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.CATEGORIES -> CategoriesScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.CART -> CartScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.CHECKOUT -> CheckoutScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.SEARCH_FILTER -> SearchFilterScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.WISHLIST -> WishlistScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.ORDERS -> OrdersScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.BECOME_SELLER -> BecomeSellerScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.AUTH -> AuthScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.ADMIN_PANEL -> AdminScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.NOTIFICATIONS -> NotificationsScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onBack = { viewModel.navigateBack() }
                    )
                    AppScreen.DEV_TESTS -> DevTestsScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }

            // Order Placed Celebration Modal
            if (uiState.showOrderSuccessDialog && uiState.lastPlacedOrderId != null) {
                OrderSuccessDialog(
                    orderId = uiState.lastPlacedOrderId!!,
                    onDismiss = { viewModel.dismissOrderSuccessDialog() },
                    onTrackOrder = {
                        viewModel.dismissOrderSuccessDialog()
                        viewModel.navigateTo(AppScreen.ORDERS)
                    },
                    onContinueShopping = {
                        viewModel.dismissOrderSuccessDialog()
                        viewModel.navigateTo(AppScreen.HOME)
                    }
                )
            }
        }
    }
}
