package com.example.model

/**
 * Convex Database Table 1: users table
 * id, name, email, password_hash, phone, avatar, role (user/admin/seller), addresses array, createdAt
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password_hash: String,
    val phone: String,
    val avatar: String? = null,
    val role: String = "user", // "user" | "admin" | "seller"
    val addresses: List<AddressRecord> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean get() = role.equals("admin", ignoreCase = true)
    val isSeller: Boolean get() = role.equals("seller", ignoreCase = true) || isAdmin
}

data class AddressRecord(
    val id: String,
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val pincode: String,
    val isDefault: Boolean = false
)

/**
 * Convex Database Table 2: products table (Multi-Vendor catalog)
 * id, title, description, price, originalPrice, discount, category, brand, images array, stock, rating, reviewsCount, sellerId, isApproved, commissionRate, createdAt
 */
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val originalPrice: Int,
    val discount: Int = (((originalPrice - price).toFloat() / originalPrice) * 100).toInt(),
    val category: String,
    val brand: String,
    val images: List<String>,
    val stock: Int, // stock check: if 0 show Out of Stock
    val rating: Float,
    val reviewsCount: Int,
    val sellerId: String,
    val isApproved: Boolean = true, // Admin product review status
    val commissionRate: Int = 10,
    val createdAt: Long = System.currentTimeMillis(),
    val drawableResId: Int? = null,
    val highlights: List<String> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val isShopKartAssured: Boolean = true,
    val oneDayDelivery: Boolean = true,
    val sellerName: String = "ShopKart Official Hub",
    val sellerRating: Float = 4.8f,
    val colors: List<String> = listOf("Silver", "Space Gray", "Midnight Blue"),
    val variants: List<String> = listOf("128 GB", "256 GB", "512 GB"),
    val tag: String? = null,
    val bankOffers: List<String> = listOf(
        "10% Instant Discount up to ₹1,500 on HDFC Bank Credit Cards",
        "5% Unlimited Cashback on ShopKart Axis Bank Card",
        "Flat ₹500 off on ICICI Bank Netbanking transactions",
        "Special Festive Price: Extra ₹2,000 Off"
    )
) {
    val isOutOfStock: Boolean
        get() = stock <= 0

    // Compatibility getters for frontend UI components
    val imageUrls: List<String>
        get() = images

    val discountPercent: Int
        get() = discount

    val ratingCount: Int
        get() = reviewsCount * 4

    val reviewCount: Int
        get() = reviewsCount
}

/**
 * Convex Database Table 3: orders table (With Multi-Vendor commissions & earnings)
 * id, userId, products array, totalAmount, sellerId, commissionAmount, sellerEarnings, status, paymentStatus, paymentMethod, address, trackingId, createdAt
 */
data class Order(
    val id: String,
    val userId: String,
    val products: List<OrderProductItem>,
    val totalAmount: Int,
    val sellerId: String? = null,
    val commissionAmount: Int = (totalAmount * 0.10).toInt(),
    val sellerEarnings: Int = totalAmount - (totalAmount * 0.10).toInt(),
    val status: String, // "Ordered" | "Packed" | "Shipped" | "Delivered"
    val paymentStatus: String = "Paid",
    val paymentMethod: String = "UPI",
    val address: AddressRecord,
    val trackingId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val trackingSteps: List<String> = listOf("Ordered", "Packed", "Shipped", "Delivered"),
    val currentStepIndex: Int = when (status) {
        "Delivered" -> 3
        "Shipped" -> 2
        "Packed" -> 1
        else -> 0
    }
) {
    val orderId: String get() = id
    val orderDate: String get() = "28 Sep 2026"
    val deliveryDate: String get() = "Tomorrow by 11:00 AM"
    val items: List<OrderProductItem> get() = products
}

typealias OrderItemRecord = Order

data class OrderProductItem(
    val productId: String,
    val productTitle: String = "",
    val productImage: String = "",
    val qty: Int = 1,
    val price: Int = 0,
    val sellerId: String? = null,
    val selectedVariant: String = "Standard",
    val selectedColor: String = "Default",
    val productDrawableResId: Int? = null
) {
    val quantity: Int get() = qty
    val title: String get() = productTitle
}

/**
 * Convex Database Table 4: sellers table (Multi-Vendor Marketplace)
 * id, userId, shopName, shopLogo, shopDescription, gstNumber, address, rating, totalProducts, totalSales, commission, status, createdAt
 */
data class Seller(
    val id: String,
    val userId: String,
    val shopName: String,
    val shopLogo: String? = "https://images.unsplash.com/photo-1556742049-0a67e5572293?w=300",
    val shopDescription: String? = "Authorized official dealer with 100% genuine guaranteed products.",
    val gstNumber: String = "29ABCDE1234F1Z5",
    val gst: String = gstNumber,
    val address: String? = "Bengaluru, Karnataka",
    val products: List<String> = emptyList(),
    val rating: Float = 4.9f,
    val totalProducts: Int = products.size,
    val totalSales: Int = 0,
    val commission: Int = 10, // 10% standard marketplace fee
    val status: String = "approved", // "pending" | "approved" | "rejected"
    val isApproved: Boolean = status.equals("approved", ignoreCase = true),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 5: sellerEarnings table
 * id, sellerId, orderId, amount, commission, netEarnings, status (pending/paid), payoutMethod, createdAt
 */
data class SellerEarningRecord(
    val id: String,
    val sellerId: String,
    val orderId: String,
    val amount: Int,
    val commission: Int,
    val netEarnings: Int,
    val status: String = "pending", // "pending" | "paid"
    val payoutMethod: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 6: recentlyViewed table (AI Feed)
 * id, userId, productId, createdAt
 */
data class RecentlyViewedItem(
    val id: String,
    val userId: String,
    val productId: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 7: recommendedLogs table (AI interaction signals)
 * id, userId, productId, action (view/cart/wishlist/purchase), category, createdAt
 */
data class RecommendedLogItem(
    val id: String,
    val userId: String,
    val productId: String,
    val action: String,
    val category: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * AI-Generated Frequently Bought Together Bundle
 */
data class FrequentlyBoughtBundle(
    val mainProduct: Product,
    val bundleProduct: Product,
    val totalOriginal: Int,
    val bundlePrice: Int,
    val savings: Int
)

/**
 * Convex Database Table 8: cart table
 */
data class CartItemEntity(
    val id: String,
    val userId: String,
    val productId: String,
    val qty: Int = 1,
    val selectedVariant: String = "Standard",
    val selectedColor: String = "Default",
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 9: wishlist table
 */
data class WishlistEntity(
    val id: String,
    val userId: String,
    val productId: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 10: payments table
 */
data class PaymentRecord(
    val id: String,
    val orderId: String,
    val userId: String,
    val amount: Int,
    val method: String,
    val razorpayPaymentId: String? = null,
    val razorpayOrderId: String? = null,
    val status: String = "success",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 11: notifications table
 */
data class NotificationItem(
    val id: String,
    val userId: String? = null,
    val type: String, // "order_update" | "low_stock" | "new_product" | "offer" | "system" | "seller_approval" | "payout"
    val title: String,
    val message: String,
    val link: String? = null,
    val isRead: Boolean = false,
    val image: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 12: searchHistory table
 */
data class SearchHistoryItem(
    val id: String,
    val userId: String,
    val query: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convex Database Table 13: trendingSearches table
 */
data class TrendingSearchItem(
    val id: String,
    val query: String,
    val count: Int,
    val createdAt: Long = System.currentTimeMillis()
)

// UI Populated Models
data class CartItem(
    val cartId: String,
    val product: Product,
    val quantity: Int = 1,
    val selectedVariant: String = product.variants.firstOrNull() ?: "Standard",
    val selectedColor: String = product.colors.firstOrNull() ?: "Default"
)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val subcategories: List<String> = emptyList()
)

data class BankOffer(
    val id: String,
    val bankName: String,
    val cardType: String,
    val discountText: String,
    val code: String,
    val minSpend: Int,
    val maxDiscount: Int,
    val validTill: String = "Valid till 30 Sep"
)

data class ProductReview(
    val id: String,
    val userName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val verifiedPurchase: Boolean = true,
    val helpfulCount: Int = 42
)

enum class AdminTab {
    DASHBOARD,
    POS,
    PRODUCTS,
    ORDERS,
    USERS,
    SELLERS,
    ANALYTICS,
    NOTIFICATIONS
}

enum class SellerHubTab {
    DASHBOARD,
    MY_PRODUCTS,
    ORDERS,
    EARNINGS,
    SETTINGS
}

data class PosCartItem(
    val product: Product,
    val qty: Int
)

data class PosInvoice(
    val invoiceNumber: String,
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val items: List<PosCartItem>,
    val subtotal: Int,
    val discount: Int,
    val gst: Int,
    val totalAmount: Int,
    val paymentMethod: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class AdminRevenueDay(
    val day: String,
    val revenue: Int,
    val orders: Int
)

data class AdminStats(
    val totalRevenue: Int,
    val totalOrders: Int,
    val totalUsers: Int,
    val totalProducts: Int,
    val totalSellers: Int,
    val revenue7Days: List<AdminRevenueDay>,
    val lowStockCount: Int,
    val todayCashCollection: Int = 0,
    val todayUpiCollection: Int = 0,
    val pendingSellersCount: Int = 0,
    val pendingProductsCount: Int = 0
)

data class SellerStats(
    val totalSales: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val netEarnings: Int,
    val pendingPayout: Int,
    val sales7Days: List<AdminRevenueDay>,
    val lowStockCount: Int
)

data class TestResultItem(
    val title: String,
    val description: String,
    val passed: Boolean,
    val details: String
)

enum class AppScreen {
    HOME,
    CATEGORIES,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    SEARCH_FILTER,
    WISHLIST,
    ORDERS,
    BECOME_SELLER,
    AUTH,
    ADMIN_PANEL,
    DEV_TESTS,
    NOTIFICATIONS
}
