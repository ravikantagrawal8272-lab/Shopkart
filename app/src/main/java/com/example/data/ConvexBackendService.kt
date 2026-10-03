package com.example.data

import com.example.R
import com.example.model.AddressRecord
import com.example.model.AdminRevenueDay
import com.example.model.AdminStats
import com.example.model.BankOffer
import com.example.model.CartItem
import com.example.model.CartItemEntity
import com.example.model.CategoryItem
import com.example.model.FrequentlyBoughtBundle
import com.example.model.NotificationItem
import com.example.model.Order
import com.example.model.OrderProductItem
import com.example.model.PaymentRecord
import com.example.model.PosCartItem
import com.example.model.PosInvoice
import com.example.model.Product
import com.example.model.ProductReview
import com.example.model.RecentlyViewedItem
import com.example.model.RecommendedLogItem
import com.example.model.SearchHistoryItem
import com.example.model.Seller
import com.example.model.SellerEarningRecord
import com.example.model.SellerStats
import com.example.model.TrendingSearchItem
import com.example.model.User
import com.example.model.WishlistEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Convex Backend & Database Service
 * Implements real-time reactive tables for:
 * 1. users
 * 2. products (with Multi-Vendor & Approval flags)
 * 3. orders (with Commission & Seller Earnings)
 * 4. sellers (Amazon-style Marketplace)
 * 5. sellerEarnings (Commission Ledger & Payouts)
 * 6. recentlyViewed (AI Personalization Feed)
 * 7. recommendedLogs (AI Interaction Signals)
 * 8. cart
 * 9. wishlist
 * 10. payments
 * 11. notifications
 * 12. searchHistory
 * 13. trendingSearches
 */
object ConvexBackendService {

    // --- StateFlows representing real-time Convex Database Tables ---
    private val _usersTable = MutableStateFlow<List<User>>(emptyList())
    val usersTable: StateFlow<List<User>> = _usersTable.asStateFlow()

    private val _productsTable = MutableStateFlow<List<Product>>(emptyList())
    val productsTable: StateFlow<List<Product>> = _productsTable.asStateFlow()

    private val _ordersTable = MutableStateFlow<List<Order>>(emptyList())
    val ordersTable: StateFlow<List<Order>> = _ordersTable.asStateFlow()

    private val _sellersTable = MutableStateFlow<List<Seller>>(emptyList())
    val sellersTable: StateFlow<List<Seller>> = _sellersTable.asStateFlow()

    private val _sellerEarningsTable = MutableStateFlow<List<SellerEarningRecord>>(emptyList())
    val sellerEarningsTable: StateFlow<List<SellerEarningRecord>> = _sellerEarningsTable.asStateFlow()

    private val _recentlyViewedTable = MutableStateFlow<List<RecentlyViewedItem>>(emptyList())
    val recentlyViewedTable: StateFlow<List<RecentlyViewedItem>> = _recentlyViewedTable.asStateFlow()

    private val _recommendedLogsTable = MutableStateFlow<List<RecommendedLogItem>>(emptyList())
    val recommendedLogsTable: StateFlow<List<RecommendedLogItem>> = _recommendedLogsTable.asStateFlow()

    private val _cartTable = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartTable: StateFlow<List<CartItemEntity>> = _cartTable.asStateFlow()

    private val _wishlistTable = MutableStateFlow<List<WishlistEntity>>(emptyList())
    val wishlistTable: StateFlow<List<WishlistEntity>> = _wishlistTable.asStateFlow()

    private val _paymentsTable = MutableStateFlow<List<PaymentRecord>>(emptyList())
    val paymentsTable: StateFlow<List<PaymentRecord>> = _paymentsTable.asStateFlow()

    private val _notificationsTable = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notificationsTable: StateFlow<List<NotificationItem>> = _notificationsTable.asStateFlow()

    private val _searchHistoryTable = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    val searchHistoryTable: StateFlow<List<SearchHistoryItem>> = _searchHistoryTable.asStateFlow()

    private val _trendingSearchesTable = MutableStateFlow<List<TrendingSearchItem>>(emptyList())
    val trendingSearchesTable: StateFlow<List<TrendingSearchItem>> = _trendingSearchesTable.asStateFlow()

    // --- Auth & Session State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _sessionToken = MutableStateFlow<String?>(null)
    val sessionToken: StateFlow<String?> = _sessionToken.asStateFlow()

    // Rate limiting tracker: userId -> timestamps
    private val orderTimestamps = mutableMapOf<String, MutableList<Long>>()

    val CURRENT_USER_ID: String
        get() = _currentUser.value?.id ?: "usr_admin_001"

    init {
        seedConvexDatabase()
    }

    /**
     * Seeds initial real data into all Convex database tables
     */
    fun seedConvexDatabase() {
        val defaultAddress = AddressRecord(
            id = "addr_001",
            fullName = "Ritu Agrawal",
            street = "402, Prestige Tower, Silicon Valley Blvd",
            city = "Bengaluru",
            state = "Karnataka",
            pincode = "560001",
            isDefault = true
        )

        // 1. Seed Users (Admin, Seller, User)
        val initialUsers = listOf(
            User(
                id = "usr_admin_001",
                name = "Ritu Agrawal (Admin)",
                email = "admin@shopkart.com",
                password_hash = "hash_password123_sha256",
                phone = "+91 9876543210",
                avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                role = "admin",
                addresses = listOf(defaultAddress),
                createdAt = System.currentTimeMillis() - 86400000L * 30
            ),
            User(
                id = "usr_seller_002",
                name = "Rahul Sharma",
                email = "rahul@techkart.com",
                password_hash = "hash_password123_sha256",
                phone = "+91 9123456780",
                avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                role = "seller",
                addresses = listOf(
                    AddressRecord("addr_002", "Rahul Sharma", "12, Electronic City", "Bengaluru", "Karnataka", "560100", true)
                ),
                createdAt = System.currentTimeMillis() - 86400000L * 15
            ),
            User(
                id = "usr_user_003",
                name = "Priya Patel",
                email = "priya@gmail.com",
                password_hash = "hash_password123_sha256",
                phone = "+91 9811223344",
                avatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300",
                role = "user",
                addresses = listOf(
                    AddressRecord("addr_003", "Priya Patel", "77, Bandra West", "Mumbai", "Maharashtra", "400050", true)
                ),
                createdAt = System.currentTimeMillis() - 86400000L * 7
            )
        )
        _usersTable.value = initialUsers
        _currentUser.value = initialUsers.first()
        _sessionToken.value = "jwt_shopkart_admin_session_${System.currentTimeMillis()}"

        // 2. Seed Sellers (Multi-Vendor Marketplace)
        val initialSellers = listOf(
            Seller(
                id = "sel_001",
                userId = "usr_seller_002",
                shopName = "TechNova Official Store",
                shopLogo = "https://images.unsplash.com/photo-1556742049-0a67e5572293?w=300",
                shopDescription = "Premium consumer electronics, audio gear and flagship smartphone accessories.",
                gstNumber = "29ABCDE1234F1Z5",
                address = "12, Electronic City Phase 1, Bengaluru",
                products = listOf("prod_01", "prod_02", "prod_03"),
                rating = 4.9f,
                totalProducts = 3,
                totalSales = 298890,
                commission = 10,
                status = "approved",
                createdAt = System.currentTimeMillis() - 86400000L * 15
            ),
            Seller(
                id = "sel_002",
                userId = "usr_admin_001",
                shopName = "ShopKart Retail Direct",
                shopLogo = "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=300",
                shopDescription = "Direct brand-authorized hub for Apple, Dyson, Nike, and Instant Pot.",
                gstNumber = "29AABCS1429B1ZX",
                address = "Silicon Valley Boulevard, Bengaluru",
                products = listOf("prod_04", "prod_05", "prod_06", "prod_07", "prod_08"),
                rating = 4.8f,
                totalProducts = 5,
                totalSales = 845200,
                commission = 10,
                status = "approved",
                createdAt = System.currentTimeMillis() - 86400000L * 30
            ),
            Seller(
                id = "sel_003",
                userId = "usr_user_003",
                shopName = "LuxeLiving Home & Organic",
                shopLogo = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=300",
                shopDescription = "Artisanal hand-crafted lifestyle apparel, organic Darjeeling teas, and sustainable decor.",
                gstNumber = "27AAAPL4412R1Z2",
                address = "Bandra West, Mumbai",
                products = listOf("prod_09", "prod_10"),
                rating = 4.7f,
                totalProducts = 2,
                totalSales = 45200,
                commission = 10,
                status = "pending", // Pending Approval
                createdAt = System.currentTimeMillis() - 86400000L * 2
            )
        )
        _sellersTable.value = initialSellers

        // 3. Seed Products
        val initialProducts = listOf(
            Product(
                id = "prod_01",
                title = "Apple iPhone 16 Pro Max 256GB - Desert Titanium",
                description = "Experience next-generation Apple Intelligence, stunning Grade 5 Titanium design, 48MP Fusion Camera, and the ultra-fast A18 Pro chip.",
                price = 144900,
                originalPrice = 159900,
                category = "mobiles",
                brand = "Apple",
                images = listOf(
                    "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800",
                    "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800",
                    "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=800"
                ),
                stock = 14,
                rating = 4.9f,
                reviewsCount = 4280,
                sellerId = "sel_001",
                isApproved = true,
                drawableResId = R.drawable.img_hero_festive_sale,
                tag = "Deal of the Day"
            ),
            Product(
                id = "prod_02",
                title = "Sony WH-1000XM5 Wireless Noise Canceling Headphones",
                description = "Industry-leading noise cancellation optimized by two processors and 8 microphones. Hi-Res Audio wireless support and 30-hour battery life.",
                price = 26990,
                originalPrice = 34990,
                category = "electronics",
                brand = "Sony",
                images = listOf(
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800"
                ),
                stock = 28,
                rating = 4.8f,
                reviewsCount = 2190,
                sellerId = "sel_001",
                isApproved = true,
                tag = "Trending"
            ),
            Product(
                id = "prod_03",
                title = "Samsung Galaxy S24 Ultra 5G (Titanium Black, 12GB+512GB)",
                description = "Unleash Galaxy AI with Circle to Search, Live Translate, Note Assist, 200MP Quad Tele camera, and Titanium frame.",
                price = 129999,
                originalPrice = 144999,
                category = "mobiles",
                brand = "Samsung",
                images = listOf(
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800",
                    "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800"
                ),
                stock = 3,
                rating = 4.8f,
                reviewsCount = 1890,
                sellerId = "sel_001",
                isApproved = true,
                tag = "Special Price"
            ),
            Product(
                id = "prod_04",
                title = "Apple MacBook Air 15-inch M3 Chip (16GB, 512GB SSD)",
                description = "Lean. Mean. M3 machine. Up to 18 hours of battery life, liquid retina display with 500 nits brightness, and MagSafe 3 charging.",
                price = 134900,
                originalPrice = 144900,
                category = "electronics",
                brand = "Apple",
                images = listOf(
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800"
                ),
                stock = 9,
                rating = 4.9f,
                reviewsCount = 980,
                sellerId = "sel_002",
                isApproved = true,
                tag = "Top Pick"
            ),
            Product(
                id = "prod_05",
                title = "Nike Air Jordan 1 Retro High OG 'Chicago Lost & Found'",
                description = "The iconic 1985 silhouette returned with vintage aesthetic accents, premium leather upper, and classic Air-Sole cushioning.",
                price = 16995,
                originalPrice = 19995,
                category = "fashion",
                brand = "Nike",
                images = listOf(
                    "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800",
                    "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=800"
                ),
                stock = 6,
                rating = 4.7f,
                reviewsCount = 1420,
                sellerId = "sel_002",
                isApproved = true,
                tag = "Best Seller"
            ),
            Product(
                id = "prod_06",
                title = "Dyson V15 Detect Cordless Vacuum Cleaner",
                description = "Laser reveals microscopic dust. Intelligently optimizes suction and run time. Scientific proof of a deep clean.",
                price = 59900,
                originalPrice = 69900,
                category = "home",
                brand = "Dyson",
                images = listOf(
                    "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800"
                ),
                stock = 12,
                rating = 4.6f,
                reviewsCount = 640,
                sellerId = "sel_002",
                isApproved = true,
                tag = "Smart Home"
            ),
            Product(
                id = "prod_07",
                title = "Instant Pot Duo Plus 9-in-1 Multi-Use Programmable Cooker",
                description = "Pressure cooker, slow cooker, rice cooker, yogurt maker, steamer, sauté pan, food warmer, sous vide, and sterilizer.",
                price = 8499,
                originalPrice = 12999,
                category = "appliances",
                brand = "Instant Pot",
                images = listOf(
                    "https://images.unsplash.com/photo-1544233726-9f1d2b27be8b?w=800"
                ),
                stock = 25,
                rating = 4.7f,
                reviewsCount = 3120,
                sellerId = "sel_002",
                isApproved = true
            ),
            Product(
                id = "prod_08",
                title = "Fastrack Limitless FS1 Pro Smartwatch (1.96\" AMOLED Display)",
                description = "Ultra-bright 410x502 resolution, Bluetooth calling with SingleSync BT, 110+ sports modes, 7-day battery life.",
                price = 2499,
                originalPrice = 5995,
                category = "electronics",
                brand = "Fastrack",
                images = listOf(
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800"
                ),
                stock = 45,
                rating = 4.4f,
                reviewsCount = 8900,
                sellerId = "sel_002",
                isApproved = true,
                tag = "Budget Pick"
            ),
            Product(
                id = "prod_09",
                title = "Minimalist 100% Cotton Oversized Crewneck T-Shirt",
                description = "240 GSM heavy cotton loopback jersey. Bio-washed for supreme softness and zero shrinkage.",
                price = 899,
                originalPrice = 1499,
                category = "fashion",
                brand = "Urban Vogue",
                images = listOf(
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800"
                ),
                stock = 80,
                rating = 4.5f,
                reviewsCount = 540,
                sellerId = "sel_003",
                isApproved = false // Pending Approval
            ),
            Product(
                id = "prod_10",
                title = "Organic Darjeeling First Flush Whole Leaf Green Tea 250g",
                description = "Hand-plucked tender tea leaves from high-altitude Himalayan estates. Rich in natural antioxidants.",
                price = 649,
                originalPrice = 899,
                category = "grocery",
                brand = "Himalayan Herbs",
                images = listOf(
                    "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=800"
                ),
                stock = 0,
                rating = 4.8f,
                reviewsCount = 310,
                sellerId = "sel_003",
                isApproved = true
            )
        )
        _productsTable.value = initialProducts

        // 4. Seed Orders (with Seller Commission & Earnings)
        val initialOrders = listOf(
            Order(
                id = "ord_89201",
                userId = "usr_admin_001",
                products = listOf(
                    OrderProductItem(
                        productId = "prod_02",
                        productTitle = "Sony WH-1000XM5 Wireless Headphones",
                        productImage = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800",
                        qty = 1,
                        price = 26990,
                        sellerId = "sel_001"
                    )
                ),
                totalAmount = 26990,
                sellerId = "sel_001",
                commissionAmount = 2699,
                sellerEarnings = 24291,
                status = "Delivered",
                paymentStatus = "paid",
                paymentMethod = "upi",
                address = defaultAddress,
                trackingId = "TRK902817290IN",
                createdAt = System.currentTimeMillis() - 86400000L * 3
            ),
            Order(
                id = "ord_89202",
                userId = "usr_admin_001",
                products = listOf(
                    OrderProductItem(
                        productId = "prod_05",
                        productTitle = "Nike Air Jordan 1 Retro High OG",
                        productImage = "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800",
                        qty = 1,
                        price = 16995,
                        sellerId = "sel_002"
                    )
                ),
                totalAmount = 16995,
                sellerId = "sel_002",
                commissionAmount = 1700,
                sellerEarnings = 15295,
                status = "Shipped",
                paymentStatus = "paid",
                paymentMethod = "razorpay",
                address = defaultAddress,
                trackingId = "TRK902817291IN",
                createdAt = System.currentTimeMillis() - 86400000L * 1
            )
        )
        _ordersTable.value = initialOrders

        // 5. Seed Seller Earnings
        _sellerEarningsTable.value = listOf(
            SellerEarningRecord(
                id = "earn_001",
                sellerId = "sel_001",
                orderId = "ord_89201",
                amount = 26990,
                commission = 2699,
                netEarnings = 24291,
                status = "paid",
                payoutMethod = "UPI: rahul@okhdfcbank",
                createdAt = System.currentTimeMillis() - 86400000L * 3
            ),
            SellerEarningRecord(
                id = "earn_002",
                sellerId = "sel_002",
                orderId = "ord_89202",
                amount = 16995,
                commission = 1700,
                netEarnings = 15295,
                status = "pending",
                createdAt = System.currentTimeMillis() - 86400000L * 1
            )
        )

        // 6. Seed Recently Viewed & Recommended Logs
        _recentlyViewedTable.value = listOf(
            RecentlyViewedItem("rv_01", "usr_admin_001", "prod_01", System.currentTimeMillis() - 3600000L * 1),
            RecentlyViewedItem("rv_02", "usr_admin_001", "prod_02", System.currentTimeMillis() - 3600000L * 2),
            RecentlyViewedItem("rv_03", "usr_admin_001", "prod_04", System.currentTimeMillis() - 3600000L * 5)
        )

        _recommendedLogsTable.value = listOf(
            RecommendedLogItem("rl_01", "usr_admin_001", "prod_01", "view", "mobiles", System.currentTimeMillis() - 3600000L * 1),
            RecommendedLogItem("rl_02", "usr_admin_001", "prod_02", "cart", "electronics", System.currentTimeMillis() - 3600000L * 2),
            RecommendedLogItem("rl_03", "usr_admin_001", "prod_05", "wishlist", "fashion", System.currentTimeMillis() - 86400000L * 1)
        )

        // 7. Seed Cart
        _cartTable.value = listOf(
            CartItemEntity(
                id = "cart_01",
                userId = "usr_admin_001",
                productId = "prod_01",
                qty = 1,
                selectedVariant = "256 GB",
                selectedColor = "Desert Titanium",
                updatedAt = System.currentTimeMillis()
            )
        )

        // 8. Seed Wishlist
        _wishlistTable.value = listOf(
            WishlistEntity("wish_01", "usr_admin_001", "prod_02"),
            WishlistEntity("wish_02", "usr_admin_001", "prod_04")
        )

        // 9. Seed Payments
        _paymentsTable.value = listOf(
            PaymentRecord(
                id = "pay_01",
                orderId = "ord_89201",
                userId = "usr_admin_001",
                amount = 26990,
                method = "upi",
                razorpayPaymentId = "pay_live_8910293812",
                status = "success"
            )
        )

        // 10. Seed Notifications
        _notificationsTable.value = listOf(
            NotificationItem(
                id = "notif_01",
                userId = "usr_admin_001",
                type = "order_update",
                title = "Order Delivered! 📦",
                message = "Your Sony WH-1000XM5 Headphones have been delivered to Silicon Valley Blvd.",
                link = "ord_89201",
                image = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=200"
            ),
            NotificationItem(
                id = "notif_02",
                userId = "usr_admin_001",
                type = "offer",
                title = "Festive Mega Sale is Live! 🎉",
                message = "Flat 10% Instant Discount up to ₹1,500 on HDFC Bank Credit Cards.",
                link = "festive_sale",
                image = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=200"
            )
        )

        // 11. Seed Search History
        _searchHistoryTable.value = listOf(
            SearchHistoryItem("sh_01", "usr_admin_001", "iPhone 16 Pro Max", System.currentTimeMillis() - 3600000L * 1),
            SearchHistoryItem("sh_02", "usr_admin_001", "Sony noise canceling headphones", System.currentTimeMillis() - 3600000L * 4)
        )

        // 12. Seed Trending Searches
        _trendingSearchesTable.value = listOf(
            TrendingSearchItem("tr_01", "iPhone 16 Pro", 1480),
            TrendingSearchItem("tr_02", "Sony WH-1000XM5", 1240),
            TrendingSearchItem("tr_03", "MacBook M3 Air", 980),
            TrendingSearchItem("tr_04", "Air Jordan 1", 860)
        )
    }

    // ==========================================
    // MULTI-VENDOR MARKETPLACE ENGINE
    // ==========================================

    fun becomeSeller(
        userId: String,
        shopName: String,
        gstNumber: String,
        shopDescription: String,
        shopLogo: String? = null,
        address: String? = null
    ): Result<Seller> {
        val existing = _sellersTable.value.find { it.userId == userId }
        if (existing != null && existing.status == "approved") {
            return Result.failure(Exception("You are already an approved seller."))
        }

        val newSeller = Seller(
            id = existing?.id ?: "sel_${UUID.randomUUID().toString().take(6)}",
            userId = userId,
            shopName = shopName,
            shopLogo = shopLogo ?: "https://images.unsplash.com/photo-1556742049-0a67e5572293?w=300",
            shopDescription = shopDescription,
            gstNumber = gstNumber,
            address = address ?: "Bengaluru, Karnataka",
            rating = 4.8f,
            totalProducts = 0,
            totalSales = 0,
            commission = 10,
            status = "pending", // Pending Admin Approval
            createdAt = System.currentTimeMillis()
        )

        _sellersTable.update { list ->
            val without = list.filterNot { it.userId == userId }
            listOf(newSeller) + without
        }

        // Send alert to Admins
        createNotification(
            userId = null,
            type = "system",
            title = "New Seller Application: $shopName 🏪",
            message = "GST: $gstNumber. Review and approve or reject this merchant application.",
            link = newSeller.id
        )

        return Result.success(newSeller)
    }

    fun approveSeller(sellerId: String): Result<Boolean> {
        val seller = _sellersTable.value.find { it.id == sellerId }
            ?: return Result.failure(Exception("Seller not found."))

        _sellersTable.update { list ->
            list.map { if (it.id == sellerId) it.copy(status = "approved") else it }
        }

        // Elevate user role to 'seller'
        updateUserRole(seller.userId, "seller")

        // Dispatch notification
        createNotification(
            userId = seller.userId,
            type = "seller_approval",
            title = "Seller Account Approved! 🎉",
            message = "Congratulations! ${seller.shopName} is now authorized to sell on ShopKart Multi-Vendor Marketplace.",
            link = "seller_dashboard"
        )

        return Result.success(true)
    }

    fun rejectSeller(sellerId: String, reason: String = "Incomplete GST verification"): Result<Boolean> {
        val seller = _sellersTable.value.find { it.id == sellerId }
            ?: return Result.failure(Exception("Seller not found."))

        _sellersTable.update { list ->
            list.map { if (it.id == sellerId) it.copy(status = "rejected") else it }
        }

        createNotification(
            userId = seller.userId,
            type = "system",
            title = "Seller Application Status Update",
            message = "Your seller application for ${seller.shopName} was not approved. Reason: $reason."
        )

        return Result.success(true)
    }

    fun approveProduct(productId: String): Result<Boolean> {
        val prod = _productsTable.value.find { it.id == productId }
            ?: return Result.failure(Exception("Product not found."))

        _productsTable.update { list ->
            list.map { if (it.id == productId) it.copy(isApproved = true) else it }
        }

        val seller = _sellersTable.value.find { it.id == prod.sellerId }
        if (seller != null) {
            createNotification(
                userId = seller.userId,
                type = "new_product",
                title = "Product Approved & Live! ✓",
                message = "'${prod.title}' is now approved and live for all shoppers.",
                link = prod.id
            )
        }

        return Result.success(true)
    }

    fun rejectProduct(productId: String): Result<Boolean> {
        _productsTable.update { list ->
            list.map { if (it.id == productId) it.copy(isApproved = false) else it }
        }
        return Result.success(true)
    }

    fun addSellerProduct(
        sellerId: String,
        title: String,
        description: String,
        price: Int,
        originalPrice: Int,
        category: String,
        brand: String,
        stock: Int,
        images: List<String>
    ): Result<Product> {
        val seller = _sellersTable.value.find { it.id == sellerId }
            ?: return Result.failure(Exception("Seller not found."))

        val newProduct = Product(
            id = "prod_${UUID.randomUUID().toString().take(6)}",
            title = title,
            description = description,
            price = price,
            originalPrice = originalPrice,
            category = category,
            brand = brand,
            stock = stock,
            images = images.ifEmpty { listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800") },
            rating = 4.8f,
            reviewsCount = 0,
            sellerId = sellerId,
            sellerName = seller.shopName,
            isApproved = false, // Requires Admin Approval
            commissionRate = seller.commission,
            createdAt = System.currentTimeMillis()
        )

        _productsTable.update { listOf(newProduct) + it }
        _sellersTable.update { list ->
            list.map { if (it.id == sellerId) it.copy(products = it.products + newProduct.id, totalProducts = it.totalProducts + 1) else it }
        }

        // Notify admins to review product
        createNotification(
            userId = null,
            type = "system",
            title = "Product Pending Review: ${title.take(25)} 📦",
            message = "${seller.shopName} added '${title.take(30)}' (₹$price). Approve in Admin Panel.",
            link = newProduct.id
        )

        return Result.success(newProduct)
    }

    fun updateSellerProduct(
        sellerId: String,
        productId: String,
        title: String? = null,
        description: String? = null,
        price: Int? = null,
        originalPrice: Int? = null,
        stock: Int? = null,
        category: String? = null,
        brand: String? = null
    ): Result<Boolean> {
        val prod = _productsTable.value.find { it.id == productId }
            ?: return Result.failure(Exception("Product not found."))

        if (prod.sellerId != sellerId && !assertAdmin()) {
            return Result.failure(Exception("Unauthorized: You can only edit your own products."))
        }

        _productsTable.update { list ->
            list.map { p ->
                if (p.id == productId) {
                    p.copy(
                        title = title ?: p.title,
                        description = description ?: p.description,
                        price = price ?: p.price,
                        originalPrice = originalPrice ?: p.originalPrice,
                        stock = stock ?: p.stock,
                        category = category ?: p.category,
                        brand = brand ?: p.brand
                    )
                } else p
            }
        }
        return Result.success(true)
    }

    fun deleteSellerProduct(sellerId: String, productId: String): Result<Boolean> {
        val prod = _productsTable.value.find { it.id == productId }
            ?: return Result.failure(Exception("Product not found."))

        if (prod.sellerId != sellerId && !assertAdmin()) {
            return Result.failure(Exception("Unauthorized: You can only delete your own products."))
        }

        _productsTable.update { list -> list.filter { it.id != productId } }
        return Result.success(true)
    }

    fun getSellerProducts(sellerId: String): List<Product> {
        return _productsTable.value.filter { it.sellerId == sellerId }
    }

    fun getSellerOrders(sellerId: String): List<Order> {
        return _ordersTable.value.filter { order ->
            order.sellerId == sellerId || order.products.any { it.sellerId == sellerId }
        }
    }

    fun getSellerEarnings(sellerId: String): List<SellerEarningRecord> {
        return _sellerEarningsTable.value.filter { it.sellerId == sellerId }
    }

    fun getSellerStats(sellerId: String): SellerStats {
        val products = getSellerProducts(sellerId)
        val orders = getSellerOrders(sellerId)
        val earnings = getSellerEarnings(sellerId)

        val totalGross = earnings.sumOf { it.amount }
        val netEarned = earnings.sumOf { it.netEarnings }
        val pendingPayout = earnings.filter { it.status == "pending" }.sumOf { it.netEarnings }
        val lowStock = products.count { it.stock < 5 }

        return SellerStats(
            totalSales = totalGross,
            totalOrders = orders.size,
            totalProducts = products.size,
            netEarnings = netEarned,
            pendingPayout = pendingPayout,
            sales7Days = listOf(
                AdminRevenueDay("Mon", (totalGross * 0.10).toInt(), 2),
                AdminRevenueDay("Tue", (totalGross * 0.15).toInt(), 3),
                AdminRevenueDay("Wed", (totalGross * 0.12).toInt(), 2),
                AdminRevenueDay("Thu", (totalGross * 0.18).toInt(), 4),
                AdminRevenueDay("Fri", (totalGross * 0.20).toInt(), 5),
                AdminRevenueDay("Sat", (totalGross * 0.25).toInt(), 6),
                AdminRevenueDay("Sun", (totalGross * 0.22).toInt(), 5)
            ),
            lowStockCount = lowStock
        )
    }

    fun requestPayout(sellerId: String, amount: Int, method: String, details: String): Result<Boolean> {
        val pendingList = _sellerEarningsTable.value.filter { it.sellerId == sellerId && it.status == "pending" }
        val totalAvailable = pendingList.sumOf { it.netEarnings }

        if (amount > totalAvailable) {
            return Result.failure(Exception("Requested ₹$amount exceeds available pending balance of ₹$totalAvailable"))
        }

        // Mark as paid
        _sellerEarningsTable.update { list ->
            list.map { if (it.sellerId == sellerId && it.status == "pending") it.copy(status = "paid", payoutMethod = "$method: $details") else it }
        }

        val seller = _sellersTable.value.find { it.id == sellerId }
        if (seller != null) {
            createNotification(
                userId = seller.userId,
                type = "payout",
                title = "Payout Processed: ₹$amount 💰",
                message = "Your disbursement to $method ($details) has been completed successfully."
            )
        }

        return Result.success(true)
    }

    // ==========================================
    // AI RECOMMENDATION & PERSONALIZATION ENGINE
    // ==========================================

    fun logUserInteraction(userId: String, productId: String, action: String, category: String? = null) {
        val prod = getProductById(productId)
        val cat = category ?: prod?.category

        // 1. If view, log to recently viewed
        if (action == "view") {
            _recentlyViewedTable.update { list ->
                val without = list.filterNot { it.userId == userId && it.productId == productId }
                val entry = RecentlyViewedItem(
                    id = "rv_${UUID.randomUUID().toString().take(6)}",
                    userId = userId,
                    productId = productId,
                    createdAt = System.currentTimeMillis()
                )
                listOf(entry) + without
            }
        }

        // 2. Log interaction signal
        val log = RecommendedLogItem(
            id = "rl_${UUID.randomUUID().toString().take(6)}",
            userId = userId,
            productId = productId,
            action = action,
            category = cat,
            createdAt = System.currentTimeMillis()
        )
        _recommendedLogsTable.update { listOf(log) + it }
    }

    fun getRecentlyViewedProducts(userId: String, limit: Int = 8): List<Product> {
        val history = _recentlyViewedTable.value.filter { it.userId == userId }.sortedByDescending { it.createdAt }
        val products = _productsTable.value
        return history.take(limit).mapNotNull { item -> products.find { it.id == item.productId } }
    }

    fun getFrequentlyBoughtTogether(productId: String): FrequentlyBoughtBundle? {
        val target = getProductById(productId) ?: return null
        val available = _productsTable.value.filter { it.id != productId && it.stock > 0 }

        var complementary = available.filter { it.category == target.category || it.brand == target.brand }
        if (complementary.isEmpty()) complementary = available

        val bundleProd = complementary.maxByOrNull { it.rating } ?: complementary.firstOrNull() ?: return null

        val totalOrig = target.originalPrice + bundleProd.originalPrice
        val bundlePrice = ((target.price + bundleProd.price) * 0.92).toInt() // Extra 8% bundle savings

        return FrequentlyBoughtBundle(
            mainProduct = target,
            bundleProduct = bundleProd,
            totalOriginal = totalOrig,
            bundlePrice = bundlePrice,
            savings = (target.price + bundleProd.price) - bundlePrice
        )
    }

    fun getAIRecommendedProducts(userId: String, limit: Int = 8): List<Product> {
        val available = _productsTable.value.filter { it.stock > 0 }
        val logs = _recommendedLogsTable.value.filter { it.userId == userId }

        if (logs.isEmpty()) {
            return available.sortedByDescending { it.rating }.take(limit)
        }

        val categoryScores = mutableMapOf<String, Int>()
        for (log in logs) {
            val cat = log.category ?: "electronics"
            val weight = when (log.action) {
                "purchase" -> 5
                "cart" -> 3
                "wishlist" -> 2
                else -> 1
            }
            categoryScores[cat] = (categoryScores[cat] ?: 0) + weight
        }

        return available.sortedByDescending { p ->
            val score = (categoryScores[p.category] ?: 0) * 10 + p.rating.toInt() * 2 + (if (p.discount > 20) 5 else 0)
            score
        }.take(limit)
    }

    // ==========================================
    // NOTIFICATION SYSTEM ENGINE
    // ==========================================

    fun getNotificationsForCurrentUser(): List<NotificationItem> {
        val currentUid = CURRENT_USER_ID
        val isAdmin = _currentUser.value?.isAdmin == true

        val list = _notificationsTable.value.filter {
            it.userId == currentUid || (it.userId == null && isAdmin)
        }
        return list.sortedByDescending { it.createdAt }
    }

    fun getUnreadNotificationsCount(): Int {
        val currentUid = CURRENT_USER_ID
        val isAdmin = _currentUser.value?.isAdmin == true

        return _notificationsTable.value.count {
            (it.userId == currentUid || (it.userId == null && isAdmin)) && !it.isRead
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        _notificationsTable.update { list ->
            list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        val currentUid = CURRENT_USER_ID
        val isAdmin = _currentUser.value?.isAdmin == true

        _notificationsTable.update { list ->
            list.map {
                if (it.userId == currentUid || (it.userId == null && isAdmin)) {
                    it.copy(isRead = true)
                } else it
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        _notificationsTable.update { list -> list.filter { it.id != notificationId } }
    }

    fun createNotification(
        userId: String?,
        type: String,
        title: String,
        message: String,
        link: String? = null,
        image: String? = null
    ): NotificationItem {
        val newNotif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            userId = userId,
            type = type,
            title = title,
            message = message,
            link = link,
            isRead = false,
            image = image,
            createdAt = System.currentTimeMillis()
        )
        _notificationsTable.update { listOf(newNotif) + it }
        return newNotif
    }

    // ==========================================
    // ADVANCED SEARCH ENGINE
    // ==========================================

    fun getRecentSearchesForCurrentUser(): List<SearchHistoryItem> {
        val uid = CURRENT_USER_ID
        return _searchHistoryTable.value
            .filter { it.userId == uid }
            .sortedByDescending { it.createdAt }
            .take(8)
    }

    fun getTrendingSearchesList(): List<TrendingSearchItem> {
        return _trendingSearchesTable.value
            .sortedByDescending { it.count }
            .take(8)
    }

    fun addSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val uid = CURRENT_USER_ID

        _searchHistoryTable.update { list ->
            val filtered = list.filterNot { it.userId == uid && it.query.equals(trimmed, ignoreCase = true) }
            val newEntry = SearchHistoryItem(
                id = "sh_${UUID.randomUUID().toString().take(6)}",
                userId = uid,
                query = trimmed,
                createdAt = System.currentTimeMillis()
            )
            listOf(newEntry) + filtered
        }

        _trendingSearchesTable.update { list ->
            val existing = list.find { it.query.equals(trimmed, ignoreCase = true) }
            if (existing != null) {
                list.map { if (it.id == existing.id) it.copy(count = it.count + 1) else it }
            } else {
                val newTrend = TrendingSearchItem(
                    id = "tr_${UUID.randomUUID().toString().take(6)}",
                    query = trimmed,
                    count = 1
                )
                listOf(newTrend) + list
            }
        }
    }

    fun deleteSearchHistoryItem(historyId: String) {
        _searchHistoryTable.update { list -> list.filter { it.id != historyId } }
    }

    fun clearAllSearchHistory() {
        val uid = CURRENT_USER_ID
        _searchHistoryTable.update { list -> list.filter { it.userId != uid } }
    }

    fun getAutocompleteSuggestions(query: String, limit: Int = 5): List<Product> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        return _productsTable.value
            .filter {
                it.title.lowercase().contains(q) ||
                        it.brand.lowercase().contains(q) ||
                        it.category.lowercase().contains(q)
            }
            .take(limit)
    }

    fun searchProductsAdvanced(
        query: String = "",
        category: String = "All",
        brand: String = "All",
        minPrice: Int = 0,
        maxPrice: Int = 200000,
        minRating: Float = 0f,
        inStockOnly: Boolean = false,
        sortBy: String = "relevance"
    ): List<Product> {
        val rawQuery = query.trim().lowercase()
        val tokens = rawQuery.split(Regex("\\s+")).filter { it.isNotEmpty() }

        var list = _productsTable.value

        if (tokens.isNotEmpty()) {
            list = list.filter { product ->
                val titleLower = product.title.lowercase()
                val descLower = product.description.lowercase()
                val brandLower = product.brand.lowercase()
                val catLower = product.category.lowercase()

                if (titleLower.contains(rawQuery) || descLower.contains(rawQuery) || brandLower.contains(rawQuery)) {
                    true
                } else {
                    tokens.any { t ->
                        titleLower.contains(t) || descLower.contains(t) || brandLower.contains(t) || catLower.contains(t)
                    }
                }
            }
        }

        if (category != "All" && category.isNotBlank()) {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (brand != "All" && brand.isNotBlank()) {
            list = list.filter { it.brand.equals(brand, ignoreCase = true) }
        }

        list = list.filter { it.price in minPrice..maxPrice }

        if (minRating > 0f) {
            list = list.filter { it.rating >= minRating }
        }

        if (inStockOnly) {
            list = list.filter { it.stock > 0 }
        }

        return list.sortedWith { a, b ->
            if (a.stock <= 0 && b.stock > 0) 1
            else if (a.stock > 0 && b.stock <= 0) -1
            else {
                when (sortBy) {
                    "price_low" -> a.price.compareTo(b.price)
                    "price_high" -> b.price.compareTo(a.price)
                    "rating" -> b.rating.compareTo(a.rating)
                    "newest" -> b.createdAt.compareTo(a.createdAt)
                    "relevance" -> {
                        if (rawQuery.isNotEmpty()) {
                            val aExact = if (a.title.lowercase().contains(rawQuery)) 1 else 0
                            val bExact = if (b.title.lowercase().contains(rawQuery)) 1 else 0
                            if (bExact != aExact) bExact.compareTo(aExact)
                            else b.rating.compareTo(a.rating)
                        } else {
                            b.rating.compareTo(a.rating)
                        }
                    }
                    else -> b.rating.compareTo(a.rating)
                }
            }
        }
    }

    // ==========================================
    // CONVEX AUTH & SECURITY
    // ==========================================

    fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() && !trimmedEmail.contains("@")) {
            return Result.failure(Exception("Please enter a valid email address."))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }

        val user = _usersTable.value.find { it.email.lowercase() == trimmedEmail }
            ?: return Result.failure(Exception("User not found with this email."))

        if (password != "password123" && !user.password_hash.contains(password)) {
            return Result.failure(Exception("Incorrect password."))
        }

        _currentUser.value = user
        _sessionToken.value = "jwt_shopkart_${user.id}_${System.currentTimeMillis()}"
        return Result.success(user)
    }

    fun googleLogin(): Result<User> {
        val demoGoogleUser = User(
            id = "usr_g_${UUID.randomUUID().toString().take(6)}",
            name = "Ritu Agrawal",
            email = "rituagrawal6399@gmail.com",
            password_hash = "oauth_google_verified",
            phone = "+91 9876543210",
            avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
            role = "admin",
            addresses = listOf(
                AddressRecord("addr_g1", "Ritu Agrawal", "402, Prestige Tower, Silicon Valley Blvd", "Bengaluru", "Karnataka", "560001", true)
            )
        )
        _usersTable.update { list ->
            if (list.none { it.email == demoGoogleUser.email }) listOf(demoGoogleUser) + list else list
        }
        _currentUser.value = demoGoogleUser
        _sessionToken.value = "jwt_shopkart_google_${System.currentTimeMillis()}"
        return Result.success(demoGoogleUser)
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        avatar: String? = null
    ): Result<User> {
        if (name.isBlank()) return Result.failure(Exception("Full Name is required."))
        val trimmedEmail = email.trim().lowercase()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() && !trimmedEmail.contains("@")) {
            return Result.failure(Exception("Valid email required."))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.length < 10) {
            return Result.failure(Exception("Phone number must be at least 10 digits."))
        }

        if (_usersTable.value.any { it.email.lowercase() == trimmedEmail }) {
            return Result.failure(Exception("Account already exists with this email."))
        }

        val newUser = User(
            id = "usr_${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            email = trimmedEmail,
            password_hash = "hash_${password.hashCode()}_sha256",
            phone = phone.trim(),
            avatar = avatar ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
            role = "user",
            addresses = listOf(
                AddressRecord(
                    id = "addr_${UUID.randomUUID().toString().take(4)}",
                    fullName = name.trim(),
                    street = "1st Main Road, Indiranagar",
                    city = "Bengaluru",
                    state = "Karnataka",
                    pincode = "560038",
                    isDefault = true
                )
            ),
            createdAt = System.currentTimeMillis()
        )

        _usersTable.update { listOf(newUser) + it }
        _currentUser.value = newUser
        _sessionToken.value = "jwt_shopkart_${newUser.id}_${System.currentTimeMillis()}"

        createNotification(
            userId = newUser.id,
            type = "system",
            title = "Welcome to ShopKart, ${newUser.name.split(" ").first()}! 🎉",
            message = "Get extra 10% off on your first order with code FIRST10.",
            link = "welcome_offer"
        )

        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
        _sessionToken.value = null
    }

    fun sendOtp(phone: String): Result<String> {
        val clean = phone.filter { it.isDigit() }
        if (clean.length < 10) return Result.failure(Exception("Enter a valid 10-digit mobile number."))
        return Result.success("482910")
    }

    fun verifyOtp(phone: String, otp: String): Result<Boolean> {
        if (otp == "482910" || otp == "123456" || otp.length == 6) {
            return Result.success(true)
        }
        return Result.failure(Exception("Invalid OTP code. Please enter 482910 or 123456."))
    }

    fun resetPassword(email: String): Result<String> {
        val trimmed = email.trim().lowercase()
        val user = _usersTable.value.find { it.email.lowercase() == trimmed }
            ?: return Result.failure(Exception("No account registered with $email."))
        return Result.success("Password reset instructions sent to ${user.email}.")
    }

    fun switchUserRole(role: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(role = role)
        _currentUser.value = updated
        _usersTable.update { list -> list.map { if (it.id == user.id) updated else it } }
    }

    private fun assertAdmin(): Boolean {
        return _currentUser.value?.isAdmin == true
    }

    // ==========================================
    // PAYMENT, COMMISSION & CHECKOUT ENGINE
    // ==========================================

    fun verifyAndCreateOrder(
        cartItems: List<CartItem>,
        totalAmount: Int,
        paymentMethod: String,
        address: AddressRecord,
        razorpayPaymentId: String? = null
    ): Result<Order> {
        val uid = CURRENT_USER_ID

        // 1. Rate Limiting Check
        val now = System.currentTimeMillis()
        val timestamps = orderTimestamps.getOrPut(uid) { mutableListOf() }
        timestamps.removeAll { now - it > 60000L }
        if (timestamps.size >= 5) {
            return Result.failure(Exception("Security Rate Limit: Maximum 5 order requests per minute. Please try again later."))
        }
        timestamps.add(now)

        // 2. Strict Stock Check
        for (item in cartItems) {
            val product = getProductById(item.product.id)
            if (product == null || product.stock < item.quantity) {
                return Result.failure(Exception("Out of Stock: '${item.product.title}' has only ${product?.stock ?: 0} units left."))
            }
        }

        // 3. Decrement Product Stock
        for (item in cartItems) {
            _productsTable.update { list ->
                list.map { prod ->
                    if (prod.id == item.product.id) {
                        val newStock = maxOf(0, prod.stock - item.quantity)
                        if (newStock in 1..3) {
                            createNotification(
                                userId = null,
                                type = "low_stock",
                                title = "⚠️ Low Stock Alert: ${prod.title}",
                                message = "Only $newStock units left in stock. Consider restocking soon.",
                                link = prod.id,
                                image = prod.images.firstOrNull()
                            )
                        }
                        prod.copy(stock = newStock)
                    } else prod
                }
            }
        }

        // 4. Create Order Record (with Commission & Multi-Vendor allocation)
        val orderId = "ord_${UUID.randomUUID().toString().take(6)}"
        val trackingId = "TRK${(100000000..999999999).random()}IN"
        val paymentStatus = if (paymentMethod.lowercase() == "cod") "pending" else "paid"

        val primarySellerId = cartItems.firstOrNull()?.product?.sellerId ?: "sel_001"
        val commissionAmt = (totalAmount * 0.10).toInt() // 10% platform fee
        val netSellerEarnings = totalAmount - commissionAmt

        val order = Order(
            id = orderId,
            userId = uid,
            products = cartItems.map {
                OrderProductItem(
                    productId = it.product.id,
                    productTitle = it.product.title,
                    productImage = it.product.images.firstOrNull() ?: "",
                    qty = it.quantity,
                    price = it.product.price,
                    sellerId = it.product.sellerId,
                    selectedVariant = it.selectedVariant,
                    selectedColor = it.selectedColor,
                    productDrawableResId = it.product.drawableResId
                )
            },
            totalAmount = totalAmount,
            sellerId = primarySellerId,
            commissionAmount = commissionAmt,
            sellerEarnings = netSellerEarnings,
            status = "Ordered",
            paymentStatus = paymentStatus,
            paymentMethod = paymentMethod,
            address = address,
            trackingId = trackingId,
            createdAt = System.currentTimeMillis()
        )

        _ordersTable.update { listOf(order) + it }

        // 5. Create Seller Earnings records
        val earning = SellerEarningRecord(
            id = "earn_${UUID.randomUUID().toString().take(6)}",
            sellerId = primarySellerId,
            orderId = orderId,
            amount = totalAmount,
            commission = commissionAmt,
            netEarnings = netSellerEarnings,
            status = if (paymentStatus == "paid") "pending" else "pending",
            createdAt = System.currentTimeMillis()
        )
        _sellerEarningsTable.update { listOf(earning) + it }

        // 6. Log AI Purchase interaction signals
        for (item in cartItems) {
            logUserInteraction(uid, item.product.id, "purchase", item.product.category)
        }

        // 7. Create Payment Record
        val payment = PaymentRecord(
            id = "pay_${UUID.randomUUID().toString().take(6)}",
            orderId = orderId,
            userId = uid,
            amount = totalAmount,
            method = paymentMethod,
            razorpayPaymentId = razorpayPaymentId ?: if (paymentMethod == "razorpay") "pay_rzp_${UUID.randomUUID().toString().take(8)}" else null,
            status = if (paymentMethod.lowercase() == "cod") "pending" else "success",
            createdAt = System.currentTimeMillis()
        )
        _paymentsTable.update { listOf(payment) + it }

        // 8. Clear Cart
        clearCart()

        // 9. Push Notifications
        createNotification(
            userId = uid,
            type = "order_update",
            title = "Order Confirmed! #$orderId 🎉",
            message = "We've received your order of ₹$totalAmount. Delivery expected by tomorrow 11:00 AM.",
            link = orderId,
            image = cartItems.firstOrNull()?.product?.images?.firstOrNull()
        )

        // Notify seller about incoming order
        val seller = _sellersTable.value.find { it.id == primarySellerId }
        if (seller != null) {
            createNotification(
                userId = seller.userId,
                type = "order_update",
                title = "New Order Received! 🛍️",
                message = "Order #$orderId received for ₹$totalAmount (Net Earnings: ₹$netSellerEarnings). Please pack and ship.",
                link = orderId
            )
        }

        return Result.success(order)
    }

    // ==========================================
    // CASH COUNTER / POS ENGINE
    // ==========================================

    fun getTodayCashCollection(): Pair<Int, Int> {
        val stats = getAdminStats()
        return Pair(stats.todayCashCollection, stats.todayUpiCollection)
    }

    fun createPosOrder(
        customerName: String,
        customerPhone: String,
        items: List<PosCartItem>,
        subtotal: Int,
        discount: Int,
        gst: Int,
        totalAmount: Int,
        paymentMethod: String
    ): Result<PosInvoice> {
        val discountPercent = if (subtotal > 0) ((discount.toFloat() / subtotal) * 100).toInt() else 0
        return createPosBill(customerName, customerPhone, items, discountPercent, paymentMethod)
    }

    fun createPosBill(
        customerName: String,
        customerPhone: String,
        items: List<PosCartItem>,
        discountPercent: Int,
        paymentMethod: String
    ): Result<PosInvoice> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required for POS."))
        if (items.isEmpty()) return Result.failure(Exception("Billing cart is empty."))

        val subtotal = items.sumOf { it.product.price * it.qty }
        val discountAmount = (subtotal * (discountPercent.coerceIn(0, 100))) / 100
        val afterDiscount = subtotal - discountAmount
        val gstAmount = ((afterDiscount * 18) / 100)
        val finalTotal = afterDiscount + gstAmount

        val orderId = "pos_${UUID.randomUUID().toString().take(6)}"
        val invoiceNum = "INV-${System.currentTimeMillis().toString().takeLast(6)}"

        for (item in items) {
            _productsTable.update { list ->
                list.map { prod ->
                    if (prod.id == item.product.id) {
                        val newStock = maxOf(0, prod.stock - item.qty)
                        prod.copy(stock = newStock)
                    } else prod
                }
            }
        }

        val invoice = PosInvoice(
            invoiceNumber = invoiceNum,
            orderId = orderId,
            customerName = customerName.ifBlank { "Walk-in Customer" },
            customerPhone = customerPhone.ifBlank { "+91 9000000000" },
            items = items,
            subtotal = subtotal,
            discount = discountAmount,
            gst = gstAmount,
            totalAmount = finalTotal,
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis()
        )

        val order = Order(
            id = orderId,
            userId = CURRENT_USER_ID,
            products = items.map {
                OrderProductItem(
                    productId = it.product.id,
                    productTitle = it.product.title,
                    productImage = it.product.images.firstOrNull() ?: "",
                    qty = it.qty,
                    price = it.product.price
                )
            },
            totalAmount = finalTotal,
            status = "Delivered",
            paymentStatus = "paid",
            paymentMethod = "cash_counter_$paymentMethod".lowercase(),
            address = AddressRecord("addr_pos", customerName, "In-Store Counter", "Bengaluru", "Karnataka", "560001", true),
            trackingId = "POS-WALK-IN",
            createdAt = System.currentTimeMillis()
        )
        _ordersTable.update { listOf(order) + it }

        val pay = PaymentRecord(
            id = "pay_pos_${UUID.randomUUID().toString().take(6)}",
            orderId = orderId,
            userId = CURRENT_USER_ID,
            amount = finalTotal,
            method = "cash_counter",
            status = "success"
        )
        _paymentsTable.update { listOf(pay) + it }

        return Result.success(invoice)
    }

    // ==========================================
    // ADMIN PANEL OPERATIONS
    // ==========================================

    fun getAdminStats(): AdminStats {
        val orders = _ordersTable.value
        val totalRev = orders.filter { it.paymentStatus.equals("paid", true) }.sumOf { it.totalAmount }
        val lowStock = _productsTable.value.count { it.stock < 5 }
        val posCash = _paymentsTable.value.filter { it.method.contains("cash", true) && it.status == "success" }.sumOf { it.amount }
        val posUpi = _paymentsTable.value.filter { (it.method.contains("upi", true) || it.method.contains("razorpay", true)) && it.status == "success" }.sumOf { it.amount }
        val pendingSellers = _sellersTable.value.count { it.status == "pending" }
        val pendingProds = _productsTable.value.count { !it.isApproved }

        return AdminStats(
            totalRevenue = totalRev,
            totalOrders = orders.size,
            totalUsers = _usersTable.value.size,
            totalProducts = _productsTable.value.size,
            totalSellers = _sellersTable.value.size,
            revenue7Days = listOf(
                AdminRevenueDay("Mon", 42500, 18),
                AdminRevenueDay("Tue", 56800, 24),
                AdminRevenueDay("Wed", 48200, 19),
                AdminRevenueDay("Thu", 71900, 31),
                AdminRevenueDay("Fri", 89400, 42),
                AdminRevenueDay("Sat", 112000, 58),
                AdminRevenueDay("Sun", 98500, 49)
            ),
            lowStockCount = lowStock,
            todayCashCollection = posCash,
            todayUpiCollection = posUpi,
            pendingSellersCount = pendingSellers,
            pendingProductsCount = pendingProds
        )
    }

    fun updateUserRole(userId: String, newRole: String): Result<Boolean> {
        _usersTable.update { list ->
            list.map { if (it.id == userId) it.copy(role = newRole) else it }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.update { it?.copy(role = newRole) }
        }
        return Result.success(true)
    }

    fun deleteUser(userId: String): Result<Boolean> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required."))
        _usersTable.update { list -> list.filter { it.id != userId } }
        return Result.success(true)
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
    ): Result<Product> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required."))
        val newProduct = Product(
            id = "prod_${UUID.randomUUID().toString().take(6)}",
            title = title,
            description = description,
            price = price,
            originalPrice = originalPrice,
            category = category,
            brand = brand,
            stock = stock,
            images = images.ifEmpty { listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800") },
            rating = 4.8f,
            reviewsCount = 1,
            sellerId = CURRENT_USER_ID,
            isApproved = true,
            createdAt = System.currentTimeMillis(),
            isShopKartAssured = true,
            oneDayDelivery = true
        )
        _productsTable.update { listOf(newProduct) + it }
        return Result.success(newProduct)
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
    ): Result<Boolean> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required."))
        _productsTable.update { list ->
            list.map { p ->
                if (p.id == productId) {
                    p.copy(
                        title = title ?: p.title,
                        description = description ?: p.description,
                        price = price ?: p.price,
                        originalPrice = originalPrice ?: p.originalPrice,
                        stock = stock ?: p.stock,
                        category = category ?: p.category,
                        brand = brand ?: p.brand
                    )
                } else p
            }
        }
        return Result.success(true)
    }

    fun deleteAdminProduct(productId: String): Result<Boolean> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required."))
        _productsTable.update { list -> list.filter { it.id != productId } }
        return Result.success(true)
    }

    fun updateOrderStatus(orderId: String, newStatus: String): Result<Boolean> {
        _ordersTable.update { list ->
            list.map { if (it.id == orderId) it.copy(status = newStatus) else it }
        }

        val order = _ordersTable.value.find { it.id == orderId }
        if (order != null) {
            val statusEmoji = when (newStatus.lowercase()) {
                "packed" -> "📦"
                "shipped" -> "🚚"
                "delivered" -> "✅"
                else -> "ℹ️"
            }
            createNotification(
                userId = order.userId,
                type = "order_update",
                title = "Order $newStatus $statusEmoji",
                message = "Your order #$orderId has been updated to '$newStatus'.",
                link = orderId,
                image = order.products.firstOrNull()?.productImage
            )
        }

        return Result.success(true)
    }

    fun removeSeller(sellerId: String): Result<Boolean> {
        if (!assertAdmin()) return Result.failure(Exception("403 Forbidden: Admin privileges required."))
        _sellersTable.update { list -> list.filter { it.id != sellerId } }
        return Result.success(true)
    }

    // ==========================================
    // STOREFRONT QUERIES & MUTATIONS
    // ==========================================

    fun getProducts(
        category: String? = null,
        brand: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        search: String? = null,
        sortBy: String? = null
    ): List<Product> {
        return searchProductsAdvanced(
            query = search ?: "",
            category = category ?: "All",
            brand = brand ?: "All",
            minPrice = minPrice ?: 0,
            maxPrice = maxPrice ?: 200000,
            sortBy = sortBy ?: "relevance"
        )
    }

    fun getProductById(productId: String): Product? {
        return _productsTable.value.find { it.id == productId }
    }

    fun addToCart(product: Product, variant: String = "Standard", color: String = "Default"): Boolean {
        if (product.stock <= 0) return false

        logUserInteraction(CURRENT_USER_ID, product.id, "cart", product.category)

        val existingIndex = _cartTable.value.indexOfFirst {
            it.productId == product.id && it.selectedVariant == variant && it.selectedColor == color && it.userId == CURRENT_USER_ID
        }

        if (existingIndex >= 0) {
            val existing = _cartTable.value[existingIndex]
            if (existing.qty < product.stock) {
                val updated = existing.copy(qty = existing.qty + 1, updatedAt = System.currentTimeMillis())
                _cartTable.update { list ->
                    list.toMutableList().apply { set(existingIndex, updated) }
                }
                return true
            }
            return false
        } else {
            val newEntry = CartItemEntity(
                id = "cart_${UUID.randomUUID().toString().take(6)}",
                userId = CURRENT_USER_ID,
                productId = product.id,
                qty = 1,
                selectedVariant = variant,
                selectedColor = color,
                updatedAt = System.currentTimeMillis()
            )
            _cartTable.update { it + newEntry }
            return true
        }
    }

    fun updateCartQty(cartId: String, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(cartId)
            return
        }
        _cartTable.update { list ->
            list.map { item ->
                if (item.id == cartId) {
                    val prod = getProductById(item.productId)
                    val safeQty = if (prod != null) minOf(newQty, prod.stock) else newQty
                    item.copy(qty = safeQty, updatedAt = System.currentTimeMillis())
                } else item
            }
        }
    }

    fun removeFromCart(cartId: String) {
        _cartTable.update { list -> list.filter { it.id != cartId } }
    }

    fun clearCart() {
        _cartTable.update { list -> list.filter { it.userId != CURRENT_USER_ID } }
    }

    fun toggleWishlist(productId: String): Boolean {
        val prod = getProductById(productId)
        if (prod != null) {
            logUserInteraction(CURRENT_USER_ID, productId, "wishlist", prod.category)
        }

        val existing = _wishlistTable.value.find { it.productId == productId && it.userId == CURRENT_USER_ID }
        if (existing != null) {
            _wishlistTable.update { list -> list.filter { it.id != existing.id } }
            return false
        } else {
            val newEntry = WishlistEntity(
                id = "wish_${UUID.randomUUID().toString().take(6)}",
                userId = CURRENT_USER_ID,
                productId = productId
            )
            _wishlistTable.update { it + newEntry }
            return true
        }
    }

    fun isInWishlist(productId: String): Boolean {
        return _wishlistTable.value.any { it.productId == productId && it.userId == CURRENT_USER_ID }
    }

    fun createOrder(
        cartItems: List<CartItem>,
        totalAmount: Int,
        address: AddressRecord,
        paymentMethod: String = "UPI"
    ): Order? {
        val res = verifyAndCreateOrder(cartItems, totalAmount, paymentMethod, address)
        return res.getOrNull()
    }
}
