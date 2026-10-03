package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ConvexBackendService
import com.example.data.ShopKartRepository
import com.example.model.AddressRecord
import com.example.model.AdminStats
import com.example.model.AdminTab
import com.example.model.AppScreen
import com.example.model.BankOffer
import com.example.model.CartItem
import com.example.model.CartItemEntity
import com.example.model.FrequentlyBoughtBundle
import com.example.model.NotificationItem
import com.example.model.Order
import com.example.model.PosCartItem
import com.example.model.PosInvoice
import com.example.model.Product
import com.example.model.RecentlyViewedItem
import com.example.model.SearchHistoryItem
import com.example.model.Seller
import com.example.model.SellerEarningRecord
import com.example.model.SellerHubTab
import com.example.model.SellerStats
import com.example.model.TrendingSearchItem
import com.example.model.User
import com.example.model.WishlistEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class ShopKartUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val screenStack: List<AppScreen> = listOf(AppScreen.HOME),
    val selectedCategory: String = "all",
    val searchQuery: String = "",
    val selectedProduct: Product? = null,
    val selectedProductVariant: String = "",
    val selectedProductColor: String = "",
    val selectedImageIndex: Int = 0,
    val cartItems: List<CartItem> = emptyList(),
    val wishlistProductIds: Set<String> = emptySet(),
    val appliedCoupon: BankOffer? = null,
    val couponInput: String = "",
    val pincodeInput: String = "560001",
    val pincodeStatus: String? = "⚡ Delivery by tomorrow, 11:00 AM | FREE",
    val isPincodeChecking: Boolean = false,
    val sortBy: String = "popularity",
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val flashSaleTimeRemaining: String = "03:42:19",
    val orders: List<Order> = emptyList(),
    val sellerProfile: Seller? = null,
    val showOrderSuccessDialog: Boolean = false,
    val lastPlacedOrderId: String? = null,
    val lastPlacedTrackingId: String? = null,
    val snackbarMessage: String? = null,
    val activeBannerIndex: Int = 0,
    val isConvexSyncing: Boolean = false,

    // Auth & User State
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val showOtpDialog: Boolean = false,
    val pendingOtpPhone: String = "",
    val showForgotPasswordDialog: Boolean = false,

    // Admin Panel State
    val currentAdminTab: AdminTab = AdminTab.DASHBOARD,
    val adminSearchQuery: String = "",
    val adminStats: AdminStats? = null,
    val allUsers: List<User> = emptyList(),
    val allSellers: List<Seller> = emptyList(),
    val showAddProductDialog: Boolean = false,
    val editingProduct: Product? = null,
    val selectedAdminOrder: Order? = null,

    // Checkout & POS State
    val selectedAddress: AddressRecord? = null,
    val isPlacingOrder: Boolean = false,
    val posCart: List<PosCartItem> = emptyList(),
    val posDiscountPercent: Int = 0,
    val posCustomerName: String = "",
    val posCustomerPhone: String = "",
    val lastGeneratedInvoice: PosInvoice? = null,

    // Notification System State
    val notifications: List<NotificationItem> = emptyList(),
    val unreadNotificationsCount: Int = 0,
    val selectedNotificationTab: String = "All",

    // Search Engine State
    val recentSearches: List<SearchHistoryItem> = emptyList(),
    val trendingSearches: List<TrendingSearchItem> = emptyList(),
    val autocompleteSuggestions: List<Product> = emptyList(),
    val isSearchFocused: Boolean = false,
    val isVoiceSearching: Boolean = false,
    val voiceTranscript: String = "",
    val searchCategoryFilter: String = "All",
    val searchBrandFilter: String = "All",
    val searchInStockOnly: Boolean = false,
    val searchMinRating: Float = 0f,
    val searchMinPrice: Int = 0,
    val searchMaxPrice: Int = 200000,
    val searchSortBy: String = "relevance",

    // Multi-Vendor Marketplace State
    val currentSellerTab: SellerHubTab = SellerHubTab.DASHBOARD,
    val sellerStats: SellerStats? = null,
    val sellerProducts: List<Product> = emptyList(),
    val sellerOrders: List<Order> = emptyList(),
    val sellerEarnings: List<SellerEarningRecord> = emptyList(),
    val showAddSellerProductDialog: Boolean = false,
    val showPayoutDialog: Boolean = false,

    // AI Recommended & Personalization State
    val recentlyViewedProducts: List<Product> = emptyList(),
    val aiRecommendedProducts: List<Product> = emptyList(),
    val frequentlyBoughtBundle: FrequentlyBoughtBundle? = null
)

class ShopKartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ShopKartUiState())
    val uiState: StateFlow<ShopKartUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        observeConvexTables()
        startFlashSaleTimer()
    }

    /**
     * Observes real-time changes in all Convex tables
     */
    private fun observeConvexTables() {
        viewModelScope.launch {
            combine(
                ConvexBackendService.productsTable,
                ConvexBackendService.cartTable,
                ConvexBackendService.wishlistTable,
                ConvexBackendService.ordersTable,
                ConvexBackendService.currentUser,
                ConvexBackendService.notificationsTable,
                ConvexBackendService.searchHistoryTable,
                ConvexBackendService.trendingSearchesTable,
                ConvexBackendService.sellersTable,
                ConvexBackendService.sellerEarningsTable,
                ConvexBackendService.recentlyViewedTable
            ) { args: Array<Any?> ->
                @Suppress("UNCHECKED_CAST")
                val products = args[0] as List<Product>
                @Suppress("UNCHECKED_CAST")
                val cartEntries = args[1] as List<CartItemEntity>
                @Suppress("UNCHECKED_CAST")
                val wishlistEntries = args[2] as List<WishlistEntity>
                @Suppress("UNCHECKED_CAST")
                val orders = args[3] as List<Order>
                val currentUser = args[4] as? User
                @Suppress("UNCHECKED_CAST")
                val notifications = args[5] as List<NotificationItem>
                @Suppress("UNCHECKED_CAST")
                val searchHistory = args[6] as List<SearchHistoryItem>
                @Suppress("UNCHECKED_CAST")
                val trendingSearches = args[7] as List<TrendingSearchItem>
                @Suppress("UNCHECKED_CAST")
                val sellers = args[8] as List<Seller>
                @Suppress("UNCHECKED_CAST")
                val earnings = args[9] as List<SellerEarningRecord>
                @Suppress("UNCHECKED_CAST")
                val recentlyViewed = args[10] as List<RecentlyViewedItem>

                // 1. Map cart entries
                val populatedCart: List<CartItem> = cartEntries.mapNotNull { entry ->
                    val product = products.find { it.id == entry.productId }
                    if (product != null) {
                        CartItem(
                            cartId = entry.id,
                            product = product,
                            quantity = entry.qty,
                            selectedVariant = entry.selectedVariant,
                            selectedColor = entry.selectedColor
                        )
                    } else null
                }

                // 2. Map wishlist IDs
                val wishSet: Set<String> = wishlistEntries.map { it.productId }.toSet()

                // 3. Current user seller profile & seller stats
                val currentUid = currentUser?.id ?: ConvexBackendService.CURRENT_USER_ID
                val seller = sellers.find { it.userId == currentUid }

                val mySellerProducts = if (seller != null) products.filter { it.sellerId == seller.id } else emptyList()
                val mySellerOrders = if (seller != null) orders.filter { o -> o.sellerId == seller.id || o.products.any { it.sellerId == seller.id } } else emptyList()
                val mySellerEarnings = if (seller != null) earnings.filter { it.sellerId == seller.id } else emptyList()
                val sStats = if (seller != null) ConvexBackendService.getSellerStats(seller.id) else null

                // 4. Admin stats
                val stats = if (currentUser?.isAdmin == true) ConvexBackendService.getAdminStats() else null

                // 5. User notifications
                val isAdmin = currentUser?.isAdmin == true
                val userNotifs = notifications.filter {
                    it.userId == currentUid || (it.userId == null && isAdmin)
                }.sortedByDescending { it.createdAt }
                val unreadCount = userNotifs.count { !it.isRead }

                val userHistory = searchHistory.filter { it.userId == currentUid }.sortedByDescending { it.createdAt }.take(8)

                // 6. AI Personalization
                val recentProds = recentlyViewed.filter { it.userId == currentUid }.sortedByDescending { it.createdAt }.mapNotNull { rv -> products.find { it.id == rv.productId } }
                val aiRecs = ConvexBackendService.getAIRecommendedProducts(currentUid, limit = 8)

                val updatedSelectedProduct = _uiState.value.selectedProduct?.let { sel ->
                    products.find { it.id == sel.id } ?: sel
                }

                val bundle = updatedSelectedProduct?.let { ConvexBackendService.getFrequentlyBoughtTogether(it.id) }

                _uiState.update { current ->
                    current.copy(
                        cartItems = populatedCart,
                        wishlistProductIds = wishSet,
                        orders = if (currentUser?.isAdmin == true) orders else orders.filter { it.userId == currentUid },
                        sellerProfile = seller,
                        sellerStats = sStats,
                        sellerProducts = mySellerProducts,
                        sellerOrders = mySellerOrders,
                        sellerEarnings = mySellerEarnings,
                        selectedProduct = updatedSelectedProduct,
                        frequentlyBoughtBundle = bundle,
                        currentUser = currentUser,
                        isLoggedIn = currentUser != null,
                        allUsers = ConvexBackendService.usersTable.value,
                        allSellers = sellers,
                        adminStats = stats,
                        selectedAddress = current.selectedAddress ?: currentUser?.addresses?.firstOrNull { it.isDefault } ?: currentUser?.addresses?.firstOrNull(),
                        notifications = userNotifs,
                        unreadNotificationsCount = unreadCount,
                        recentSearches = userHistory,
                        trendingSearches = trendingSearches.sortedByDescending { it.count }.take(8),
                        recentlyViewedProducts = recentProds,
                        aiRecommendedProducts = aiRecs
                    )
                }
            }.collect {}
        }
    }

    private fun startFlashSaleTimer() {
        viewModelScope.launch {
            var totalSeconds = 3 * 3600 + 42 * 60 + 19
            while (true) {
                delay(1000)
                totalSeconds--
                if (totalSeconds < 0) totalSeconds = 24 * 3600
                val h = totalSeconds / 3600
                val m = (totalSeconds % 3600) / 60
                val s = totalSeconds % 60
                _uiState.update {
                    it.copy(flashSaleTimeRemaining = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s))
                }
            }
        }
    }

    // ==========================================
    // MULTI-VENDOR MARKETPLACE ACTIONS
    // ==========================================

    fun setSellerTab(tab: SellerHubTab) {
        _uiState.update { it.copy(currentSellerTab = tab) }
    }

    fun becomeSeller(
        shopName: String,
        gstNumber: String,
        shopDescription: String,
        shopLogo: String? = null,
        address: String? = null
    ) {
        val uid = _uiState.value.currentUser?.id ?: ConvexBackendService.CURRENT_USER_ID
        val res = ConvexBackendService.becomeSeller(uid, shopName, gstNumber, shopDescription, shopLogo, address)
        res.onSuccess {
            showSnackbar("Merchant application submitted! Awaiting admin review. ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Failed to submit application.")
        }
    }

    fun approveSeller(sellerId: String) {
        val res = ConvexBackendService.approveSeller(sellerId)
        res.onSuccess {
            showSnackbar("Seller approved and active on marketplace! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Error approving seller.")
        }
    }

    fun rejectSeller(sellerId: String) {
        val res = ConvexBackendService.rejectSeller(sellerId)
        res.onSuccess {
            showSnackbar("Seller application rejected.")
        }.onFailure {
            showSnackbar(it.message ?: "Error rejecting seller.")
        }
    }

    fun approveProduct(productId: String) {
        val res = ConvexBackendService.approveProduct(productId)
        res.onSuccess {
            showSnackbar("Product approved & live in catalog! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Error approving product.")
        }
    }

    fun rejectProduct(productId: String) {
        val res = ConvexBackendService.rejectProduct(productId)
        res.onSuccess {
            showSnackbar("Product listing disapproved.")
        }.onFailure {
            showSnackbar(it.message ?: "Error disapproving product.")
        }
    }

    fun addSellerProduct(
        title: String,
        description: String,
        price: Int,
        originalPrice: Int,
        category: String,
        brand: String,
        stock: Int,
        images: List<String>
    ) {
        val seller = _uiState.value.sellerProfile
        if (seller == null) {
            showSnackbar("You must be an approved seller to add products.")
            return
        }

        val res = ConvexBackendService.addSellerProduct(
            sellerId = seller.id,
            title = title,
            description = description,
            price = price,
            originalPrice = originalPrice,
            category = category,
            brand = brand,
            stock = stock,
            images = images
        )

        res.onSuccess {
            _uiState.update { it.copy(showAddSellerProductDialog = false) }
            showSnackbar("Product submitted for review! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Failed to submit product.")
        }
    }

    fun updateSellerProduct(
        productId: String,
        title: String? = null,
        description: String? = null,
        price: Int? = null,
        originalPrice: Int? = null,
        stock: Int? = null,
        category: String? = null,
        brand: String? = null
    ) {
        val seller = _uiState.value.sellerProfile
        val sellerId = seller?.id ?: ""
        val res = ConvexBackendService.updateSellerProduct(sellerId, productId, title, description, price, originalPrice, stock, category, brand)
        res.onSuccess {
            showSnackbar("Product details updated! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Error updating product.")
        }
    }

    fun deleteSellerProduct(productId: String) {
        val seller = _uiState.value.sellerProfile
        val sellerId = seller?.id ?: ""
        val res = ConvexBackendService.deleteSellerProduct(sellerId, productId)
        res.onSuccess {
            showSnackbar("Product removed from your inventory.")
        }.onFailure {
            showSnackbar(it.message ?: "Error removing product.")
        }
    }

    fun requestSellerPayout(amount: Int, method: String, details: String) {
        val seller = _uiState.value.sellerProfile
        if (seller == null) return

        val res = ConvexBackendService.requestPayout(seller.id, amount, method, details)
        res.onSuccess {
            _uiState.update { it.copy(showPayoutDialog = false) }
            showSnackbar("Payout of ₹$amount initiated successfully! 💰")
        }.onFailure {
            showSnackbar(it.message ?: "Payout request failed.")
        }
    }

    fun openAddSellerProductDialog() {
        _uiState.update { it.copy(showAddSellerProductDialog = true) }
    }

    fun closeAddSellerProductDialog() {
        _uiState.update { it.copy(showAddSellerProductDialog = false) }
    }

    fun openPayoutDialog() {
        _uiState.update { it.copy(showPayoutDialog = true) }
    }

    fun closePayoutDialog() {
        _uiState.update { it.copy(showPayoutDialog = false) }
    }

    // ==========================================
    // AI RECOMMENDATIONS & BUNDLE ACTIONS
    // ==========================================

    fun addBundleToCart(bundle: FrequentlyBoughtBundle) {
        addToCart(bundle.mainProduct)
        addToCart(bundle.bundleProduct)
        showSnackbar("Frequently Bought Together bundle added! (Saved extra ₹${bundle.savings}) 🎉")
    }

    // ==========================================
    // NOTIFICATION SYSTEM ACTIONS
    // ==========================================

    fun setNotificationTab(tab: String) {
        _uiState.update { it.copy(selectedNotificationTab = tab) }
    }

    fun markNotificationAsRead(id: String) {
        ConvexBackendService.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        ConvexBackendService.markAllNotificationsAsRead()
        showSnackbar("All notifications marked as read ✓")
    }

    fun deleteNotification(id: String) {
        ConvexBackendService.deleteNotification(id)
        showSnackbar("Notification removed")
    }

    fun onNotificationClicked(notification: NotificationItem) {
        markNotificationAsRead(notification.id)
        val link = notification.link
        if (!link.isNullOrBlank()) {
            if (link.startsWith("ord_") || link.contains("order")) {
                navigateTo(AppScreen.ORDERS)
            } else if (link.startsWith("prod_")) {
                val prod = ConvexBackendService.getProductById(link)
                if (prod != null) {
                    selectProduct(prod)
                }
            } else if (link == "seller_dashboard") {
                navigateTo(AppScreen.BECOME_SELLER)
            } else if (link == "festive_sale" || link == "welcome_offer") {
                setSearchCategory("electronics")
                navigateTo(AppScreen.SEARCH_FILTER)
            }
        }
    }

    // ==========================================
    // ADVANCED SEARCH ENGINE ACTIONS
    // ==========================================

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchDebounceJob?.cancel()
        if (query.trim().isEmpty()) {
            _uiState.update { it.copy(autocompleteSuggestions = emptyList()) }
        } else {
            searchDebounceJob = viewModelScope.launch {
                delay(200)
                val suggestions = ConvexBackendService.getAutocompleteSuggestions(query, limit = 5)
                _uiState.update { it.copy(autocompleteSuggestions = suggestions) }
            }
        }
    }

    fun setSearchFocused(focused: Boolean) {
        _uiState.update { it.copy(isSearchFocused = focused) }
    }

    fun performSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            ConvexBackendService.addSearchQuery(trimmed)
            _uiState.update {
                it.copy(
                    searchQuery = trimmed,
                    isSearchFocused = false,
                    autocompleteSuggestions = emptyList()
                )
            }
            navigateTo(AppScreen.SEARCH_FILTER)
        }
    }

    fun deleteSearchHistory(id: String) {
        ConvexBackendService.deleteSearchHistoryItem(id)
    }

    fun clearAllSearchHistory() {
        ConvexBackendService.clearAllSearchHistory()
        showSnackbar("Search history cleared")
    }

    fun setSearchCategory(category: String) {
        _uiState.update { it.copy(searchCategoryFilter = category) }
    }

    fun setSearchBrand(brand: String) {
        _uiState.update { it.copy(searchBrandFilter = brand) }
    }

    fun setSearchPriceRange(min: Int, max: Int) {
        _uiState.update { it.copy(searchMinPrice = min, searchMaxPrice = max) }
    }

    fun setSearchMinRating(rating: Float) {
        _uiState.update { it.copy(searchMinRating = rating) }
    }

    fun setSearchInStockOnly(inStock: Boolean) {
        _uiState.update { it.copy(searchInStockOnly = inStock) }
    }

    fun setSearchSortBy(sortBy: String) {
        _uiState.update { it.copy(searchSortBy = sortBy, sortBy = sortBy) }
    }

    fun setSortBy(sortBy: String) {
        setSearchSortBy(sortBy)
    }

    fun resetSearchFilters() {
        _uiState.update {
            it.copy(
                searchCategoryFilter = "All",
                searchBrandFilter = "All",
                searchMinPrice = 0,
                searchMaxPrice = 200000,
                searchMinRating = 0f,
                searchInStockOnly = false,
                searchSortBy = "relevance"
            )
        }
    }

    fun startVoiceSearch() {
        _uiState.update { it.copy(isVoiceSearching = true, voiceTranscript = "Listening...") }
        viewModelScope.launch {
            delay(1200)
            val demoTranscripts = listOf("iPhone 16 Pro Max", "Sony noise cancelling headphones", "Nike Air Jordan shoes", "MacBook Air M3")
            val chosen = demoTranscripts.random()
            _uiState.update { it.copy(voiceTranscript = "“$chosen”") }
            delay(1000)
            _uiState.update { it.copy(isVoiceSearching = false, searchQuery = chosen) }
            performSearch(chosen)
        }
    }

    fun stopVoiceSearch() {
        _uiState.update { it.copy(isVoiceSearching = false) }
    }

    // ==========================================
    // NAVIGATION & CORE STOREFRONT
    // ==========================================

    fun navigateTo(screen: AppScreen) {
        _uiState.update { state ->
            if (state.currentScreen == screen) return@update state
            state.copy(
                currentScreen = screen,
                screenStack = state.screenStack + screen,
                isSearchFocused = false
            )
        }
    }

    fun navigateBack(): Boolean {
        var handled = false
        _uiState.update { state ->
            if (state.isSearchFocused) {
                handled = true
                return@update state.copy(isSearchFocused = false)
            }
            if (state.screenStack.size > 1) {
                handled = true
                val newStack = state.screenStack.dropLast(1)
                state.copy(
                    currentScreen = newStack.last(),
                    screenStack = newStack
                )
            } else {
                state
            }
        }
        return handled
    }

    fun selectCategory(categoryId: String) {
        _uiState.update {
            it.copy(
                selectedCategory = categoryId,
                searchCategoryFilter = if (categoryId == "all") "All" else categoryId
            )
        }
        navigateTo(AppScreen.SEARCH_FILTER)
    }

    fun selectProduct(product: Product) {
        val uid = _uiState.value.currentUser?.id ?: ConvexBackendService.CURRENT_USER_ID
        ConvexBackendService.logUserInteraction(uid, product.id, "view", product.category)

        _uiState.update {
            it.copy(
                selectedProduct = product,
                selectedProductVariant = product.variants.firstOrNull() ?: "Standard",
                selectedProductColor = product.colors.firstOrNull() ?: "Default",
                selectedImageIndex = 0
            )
        }
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun openProductDetail(product: Product) {
        selectProduct(product)
    }

    fun setSelectedVariant(variant: String) {
        _uiState.update { it.copy(selectedProductVariant = variant) }
    }

    fun setSelectedColor(color: String) {
        _uiState.update { it.copy(selectedProductColor = color) }
    }

    fun setSelectedImageIndex(index: Int) {
        _uiState.update { it.copy(selectedImageIndex = index) }
    }

    fun setActiveBannerIndex(index: Int) {
        _uiState.update { it.copy(activeBannerIndex = index) }
    }

    // Cart Operations
    fun addToCart(product: Product, variant: String? = null, color: String? = null) {
        if (product.stock <= 0) {
            showSnackbar("Sorry, '${product.title}' is currently Out of Stock!")
            return
        }
        val v = variant ?: _uiState.value.selectedProductVariant.ifEmpty { product.variants.firstOrNull() ?: "Standard" }
        val c = color ?: _uiState.value.selectedProductColor.ifEmpty { product.colors.firstOrNull() ?: "Default" }
        val success = ConvexBackendService.addToCart(product, v, c)
        if (success) {
            showSnackbar("Added '${product.title.take(20)}...' to Cart! ✓")
        } else {
            showSnackbar("Maximum available stock (${product.stock}) reached for this item.")
        }
    }

    fun buyNow(product: Product, variant: String? = null, color: String? = null) {
        if (product.stock <= 0) {
            showSnackbar("Sorry, '${product.title}' is currently Out of Stock!")
            return
        }
        addToCart(product, variant, color)
        navigateTo(AppScreen.CART)
    }

    fun updateCartQuantity(cartId: String, newQty: Int) {
        ConvexBackendService.updateCartQty(cartId, newQty)
    }

    fun updateCartQuantity(item: CartItem, delta: Int) {
        val newQty = item.quantity + delta
        if (newQty <= 0) {
            removeCartItem(item.cartId)
        } else {
            updateCartQuantity(item.cartId, newQty)
        }
    }

    fun removeCartItem(cartId: String) {
        ConvexBackendService.removeFromCart(cartId)
        showSnackbar("Item removed from Cart")
    }

    fun removeCartItem(item: CartItem) {
        removeCartItem(item.cartId)
    }

    fun moveToWishlist(cartItem: CartItem) {
        ConvexBackendService.removeFromCart(cartItem.cartId)
        ConvexBackendService.toggleWishlist(cartItem.product.id)
        showSnackbar("Moved to Wishlist")
    }

    fun toggleWishlist(productId: String) {
        val isAdded = ConvexBackendService.toggleWishlist(productId)
        if (isAdded) {
            showSnackbar("Saved to Wishlist! ❤️")
        } else {
            showSnackbar("Removed from Wishlist")
        }
    }

    fun isProductInWishlist(productId: String): Boolean {
        return _uiState.value.wishlistProductIds.contains(productId)
    }

    // Coupon & Pincode
    fun setCouponInput(input: String) {
        _uiState.update { it.copy(couponInput = input.uppercase()) }
    }

    fun applyCoupon(code: String) {
        val offer = ShopKartRepository.bankOffers.find { it.code.equals(code.trim(), ignoreCase = true) }
        if (offer != null) {
            val total = calculateTotalAmount()
            if (total >= offer.minSpend) {
                _uiState.update { it.copy(appliedCoupon = offer) }
                showSnackbar("Coupon '${offer.code}' applied! You saved extra ₹${calculateDiscountAmount()} ✓")
            } else {
                showSnackbar("Minimum order value of ₹${offer.minSpend} required for this coupon.")
            }
        } else {
            showSnackbar("Invalid coupon code. Try HDFC1500 or FESTIVE2000.")
        }
    }

    fun removeCoupon() {
        _uiState.update { it.copy(appliedCoupon = null, couponInput = "") }
        showSnackbar("Coupon removed")
    }

    fun setPincodeInput(input: String) {
        val digits = input.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(pincodeInput = digits) }
    }

    fun checkPincode(pin: String? = null) {
        if (pin != null) {
            setPincodeInput(pin)
        }
        val targetPin = pin ?: _uiState.value.pincodeInput
        if (targetPin.length != 6) {
            showSnackbar("Please enter a valid 6-digit PIN code.")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isPincodeChecking = true) }
            delay(500)
            _uiState.update {
                it.copy(
                    isPincodeChecking = false,
                    pincodeStatus = "⚡ Delivery by tomorrow, 11:00 AM | FREE"
                )
            }
            showSnackbar("Delivery available for $targetPin! Express shipping enabled. ✓")
        }
    }

    // Calculation Helpers
    fun calculateTotalMRP(state: ShopKartUiState? = null): Int = calculateOriginalTotal()
    fun calculateDiscount(state: ShopKartUiState? = null): Int = calculateDiscountAmount()
    fun calculateTotal(state: ShopKartUiState? = null): Int = calculateFinalPayable()

    fun calculateOriginalTotal(): Int {
        return _uiState.value.cartItems.sumOf { it.product.originalPrice * it.quantity }
    }

    fun calculateTotalAmount(): Int {
        return _uiState.value.cartItems.sumOf { it.product.price * it.quantity }
    }

    fun calculateDiscountAmount(): Int {
        val originalTotal = calculateOriginalTotal()
        val currentTotal = calculateTotalAmount()
        var totalDiscount = originalTotal - currentTotal

        _uiState.value.appliedCoupon?.let { coupon ->
            val extraDiscount = (currentTotal * 0.10).toInt().coerceAtMost(coupon.maxDiscount)
            totalDiscount += extraDiscount
        }
        return totalDiscount
    }

    fun calculateFinalPayable(): Int {
        val currentTotal = calculateTotalAmount()
        var finalAmount = currentTotal
        _uiState.value.appliedCoupon?.let { coupon ->
            val extraDiscount = (currentTotal * 0.10).toInt().coerceAtMost(coupon.maxDiscount)
            finalAmount -= extraDiscount
        }
        return maxOf(0, finalAmount)
    }

    // ==========================================
    // CHECKOUT & PAYMENT ACTIONS
    // ==========================================

    fun selectAddress(address: AddressRecord) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    fun addNewAddress(address: AddressRecord) {
        _uiState.update { state ->
            val updatedUser = state.currentUser?.let { u ->
                u.copy(addresses = u.addresses + address)
            }
            state.copy(
                currentUser = updatedUser,
                selectedAddress = address
            )
        }
        showSnackbar("Delivery address added ✓")
    }

    fun processPaymentAndPlaceOrder(
        paymentMethod: String,
        totalAmount: Int? = null,
        address: AddressRecord? = null,
        razorpayPaymentId: String? = null
    ) {
        val state = _uiState.value
        val addr = address ?: state.selectedAddress ?: state.currentUser?.addresses?.firstOrNull() ?: AddressRecord(
            "addr_default", "Ritu Agrawal", "402, Silicon Valley", "Bengaluru", "Karnataka", "560001", true
        )
        val finalAmount = totalAmount ?: calculateFinalPayable()

        if (state.cartItems.isEmpty()) {
            showSnackbar("Your cart is empty!")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true) }
            delay(600)

            val res = ConvexBackendService.verifyAndCreateOrder(
                cartItems = state.cartItems,
                totalAmount = finalAmount,
                paymentMethod = paymentMethod,
                address = addr,
                razorpayPaymentId = razorpayPaymentId
            )

            res.onSuccess { order ->
                _uiState.update {
                    it.copy(
                        isPlacingOrder = false,
                        showOrderSuccessDialog = true,
                        lastPlacedOrderId = order.id,
                        lastPlacedTrackingId = order.trackingId,
                        appliedCoupon = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isPlacingOrder = false) }
                showSnackbar(err.message ?: "Failed to place order.")
            }
        }
    }

    fun placeOrder(paymentMethod: String) {
        processPaymentAndPlaceOrder(paymentMethod)
    }

    fun dismissOrderSuccessDialog() {
        _uiState.update { it.copy(showOrderSuccessDialog = false) }
        navigateTo(AppScreen.ORDERS)
    }

    // ==========================================
    // AUTHENTICATION ACTIONS
    // ==========================================

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            delay(500)
            val res = ConvexBackendService.login(email, pass)
            res.onSuccess {
                _uiState.update { it.copy(isAuthLoading = false) }
                showSnackbar("Welcome back, ${it.name}! 🎉")
                navigateBack()
            }.onFailure {
                _uiState.update { s -> s.copy(isAuthLoading = false, authErrorMessage = it.message) }
            }
        }
    }

    fun loginWithGoogle() {
        googleLogin()
    }

    fun googleLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            delay(600)
            val res = ConvexBackendService.googleLogin()
            res.onSuccess {
                _uiState.update { it.copy(isAuthLoading = false) }
                showSnackbar("Signed in with Google as ${it.name}! ✓")
                navigateBack()
            }
        }
    }

    fun triggerOtpFlow(phone: String) {
        _uiState.update { it.copy(pendingOtpPhone = phone, showOtpDialog = true) }
        ConvexBackendService.sendOtp(phone)
        showSnackbar("OTP sent to $phone. Use code 123456 to verify.")
    }

    fun verifyOtp(otp: String): Boolean {
        val ok = ConvexBackendService.verifyOtp(_uiState.value.pendingOtpPhone, otp).getOrDefault(true)
        if (ok) {
            _uiState.update { it.copy(showOtpDialog = false) }
            showSnackbar("Phone verified successfully! ✓")
        } else {
            showSnackbar("Invalid OTP code. Try 123456.")
        }
        return ok
    }

    fun resetPassword(email: String, newPass: String = "") {
        ConvexBackendService.resetPassword(email)
        showSnackbar("Password reset link and instructions sent to $email ✓")
    }

    fun signup(name: String, email: String, pass: String, phone: String, role: String? = "user") {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            delay(500)
            val res = ConvexBackendService.signup(name, email, pass, phone)
            res.onSuccess {
                if (!role.isNullOrBlank() && role != "user") {
                    ConvexBackendService.updateUserRole(it.id, role)
                }
                _uiState.update { it.copy(isAuthLoading = false) }
                showSnackbar("Account created! Welcome to ShopKart, ${it.name}! 🎉")
                navigateBack()
            }.onFailure {
                _uiState.update { s -> s.copy(isAuthLoading = false, authErrorMessage = it.message) }
            }
        }
    }

    fun logout() {
        ConvexBackendService.logout()
        showSnackbar("Logged out successfully.")
        navigateTo(AppScreen.HOME)
    }

    fun switchUserRole(role: String) {
        ConvexBackendService.switchUserRole(role)
        showSnackbar("Switched active role to '$role'")
    }

    fun switchToAdminRole() {
        switchUserRole("admin")
    }

    fun switchToUserRole() {
        switchUserRole("user")
    }

    fun switchToSellerRole() {
        switchUserRole("seller")
    }

    // ==========================================
    // ADMIN PANEL ACTIONS
    // ==========================================

    fun setAdminTab(tab: AdminTab) {
        _uiState.update { it.copy(currentAdminTab = tab) }
    }

    fun setAdminSearchQuery(query: String) {
        _uiState.update { it.copy(adminSearchQuery = query) }
    }

    fun updateUserRole(userId: String, newRole: String) {
        val res = ConvexBackendService.updateUserRole(userId, newRole)
        res.onSuccess {
            showSnackbar("User role changed to $newRole")
        }.onFailure {
            showSnackbar(it.message ?: "Error updating role")
        }
    }

    fun deleteUser(userId: String) {
        val res = ConvexBackendService.deleteUser(userId)
        res.onSuccess {
            showSnackbar("User removed successfully")
        }.onFailure {
            showSnackbar(it.message ?: "Error deleting user")
        }
    }

    fun createAdminProduct(
        title: String,
        description: String,
        price: Int,
        originalPrice: Int,
        category: String,
        brand: String,
        stock: Int,
        images: List<String>
    ) {
        val res = ConvexBackendService.createAdminProduct(
            title, description, price, originalPrice, category, brand, stock, images
        )
        res.onSuccess {
            _uiState.update { it.copy(showAddProductDialog = false) }
            showSnackbar("Product '${title.take(20)}' created successfully! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Error creating product")
        }
    }

    fun updateAdminProduct(
        productId: String,
        title: String? = null,
        description: String? = null,
        price: Int? = null,
        originalPrice: Int? = null,
        stock: Int? = null,
        category: String? = null,
        brand: String? = null
    ) {
        val res = ConvexBackendService.updateAdminProduct(
            productId, title, description, price, originalPrice, stock, category, brand
        )
        res.onSuccess {
            _uiState.update { it.copy(editingProduct = null) }
            showSnackbar("Product updated! ✓")
        }.onFailure {
            showSnackbar(it.message ?: "Error updating product")
        }
    }

    fun deleteAdminProduct(productId: String) {
        val res = ConvexBackendService.deleteAdminProduct(productId)
        res.onSuccess {
            showSnackbar("Product deleted from catalog")
        }.onFailure {
            showSnackbar(it.message ?: "Error deleting product")
        }
    }

    fun quickRestock(productId: String, addStock: Int = 10) {
        val prod = ConvexBackendService.getProductById(productId)
        if (prod != null) {
            val newStock = prod.stock + addStock
            ConvexBackendService.updateAdminProduct(productId, stock = newStock)
            showSnackbar("Restocked '${prod.title.take(18)}' (+${addStock} units)")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val res = ConvexBackendService.updateOrderStatus(orderId, newStatus)
        res.onSuccess {
            showSnackbar("Order $orderId marked as '$newStatus'")
        }.onFailure {
            showSnackbar(it.message ?: "Error updating order")
        }
    }

    fun removeSeller(sellerId: String) {
        val res = ConvexBackendService.removeSeller(sellerId)
        res.onSuccess {
            showSnackbar("Seller removed")
        }.onFailure {
            showSnackbar(it.message ?: "Error removing seller")
        }
    }

    fun openAddProductDialog() {
        _uiState.update { it.copy(showAddProductDialog = true, editingProduct = null) }
    }

    fun openEditProductDialog(product: Product) {
        _uiState.update { it.copy(showAddProductDialog = false, editingProduct = product) }
    }

    fun closeProductDialogs() {
        _uiState.update { it.copy(showAddProductDialog = false, editingProduct = null) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun dismissSnackbar() {
        clearSnackbar()
    }
}
