package com.example.data

import com.example.model.AddressRecord
import com.example.model.BankOffer
import com.example.model.CartItem
import com.example.model.CategoryItem
import com.example.model.Order
import com.example.model.Product
import com.example.model.ProductReview
import com.example.model.Seller
import com.example.model.User

/**
 * ShopKart Repository
 * Provides category lists, sample bank offers, reviews, and forwards to ConvexBackendService.
 */
object ShopKartRepository {

    val categories: List<CategoryItem> = listOf(
        CategoryItem("all", "All", "✨"),
        CategoryItem("mobiles", "Mobiles", "📱", listOf("Smartphones", "iPhones", "5G Phones", "Foldables")),
        CategoryItem("electronics", "Electronics", "💻", listOf("Laptops", "Headphones", "Smartwatches", "Speakers")),
        CategoryItem("fashion", "Fashion", "👔", listOf("Men's Wear", "Women's Wear", "Footwear", "Watches")),
        CategoryItem("home", "Home", "🏠", listOf("Appliances", "Living Room", "Kitchen", "Decor")),
        CategoryItem("grocery", "Grocery", "🛒", listOf("Daily Staples", "Beverages", "Snacks", "Personal Care"))
    )

    val bankOffers: List<BankOffer> = listOf(
        BankOffer(
            id = "off_1",
            bankName = "HDFC Bank",
            cardType = "Credit & Debit Cards",
            discountText = "Flat 10% Instant Discount up to ₹1,500 on orders above ₹7,499",
            code = "HDFC10",
            minSpend = 7499,
            maxDiscount = 1500
        ),
        BankOffer(
            id = "off_2",
            bankName = "ShopKart Axis",
            cardType = "Co-branded Credit Card",
            discountText = "5% Unlimited Cashback + ₹500 Welcome Bonus voucher",
            code = "AXIS5",
            minSpend = 2000,
            maxDiscount = 2500
        ),
        BankOffer(
            id = "off_3",
            bankName = "ICICI Bank",
            cardType = "Net Banking & Cards",
            discountText = "Extra ₹750 Off on orders above ₹4,999 with ICICI Netbanking",
            code = "ICICI750",
            minSpend = 4999,
            maxDiscount = 750
        )
    )

    val sampleReviews: List<ProductReview> = listOf(
        ProductReview(
            id = "rev_1",
            userName = "Aarav Mehta",
            rating = 5,
            title = "Absolute beast of a device! Super fast delivery.",
            comment = "Received within 18 hours in Bengaluru. Premium unboxing experience and 100% genuine product with Apple warranty.",
            date = "26 Sep 2026",
            verifiedPurchase = true
        ),
        ProductReview(
            id = "rev_2",
            userName = "Neha Sen",
            rating = 5,
            title = "Best audio quality in this price bracket",
            comment = "Noise cancellation is mind blowing. Battery easily lasts 3 full days of heavy work and calls.",
            date = "24 Sep 2026",
            verifiedPurchase = true
        )
    )

    val defaultAddress: AddressRecord = AddressRecord(
        id = "addr_001",
        fullName = "Ritu Agrawal",
        street = "402, Prestige Tower, Silicon Valley Blvd",
        city = "Bengaluru",
        state = "Karnataka",
        pincode = "560001",
        isDefault = true
    )

    val products: List<Product>
        get() = ConvexBackendService.productsTable.value

    fun getTrendingProducts(): List<Product> {
        return products.take(4)
    }

    fun getElectronicsDeals(): List<Product> {
        return products.filter { it.category == "electronics" || it.category == "mobiles" }.take(4)
    }

    fun getFashionTopPicks(): List<Product> {
        return products.filter { it.category == "fashion" || it.category == "home" }.take(4)
    }

    fun filterProducts(
        category: String? = null,
        query: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        sortBy: String? = null
    ): List<Product> = ConvexBackendService.getProducts(
        category = category,
        search = query,
        minPrice = minPrice,
        maxPrice = maxPrice,
        sortBy = sortBy
    )

    // Calculation helpers for CartScreen
    fun calculateTotalMRP(items: List<CartItem>): Int {
        return items.sumOf { it.product.originalPrice * it.quantity }
    }

    fun calculateDiscount(items: List<CartItem>): Int {
        val totalMRP = calculateTotalMRP(items)
        val actualPrice = items.sumOf { it.product.price * it.quantity }
        return totalMRP - actualPrice
    }

    fun calculateTotal(items: List<CartItem>, appliedCoupon: BankOffer? = null): Int {
        val actualPrice = items.sumOf { it.product.price * it.quantity }
        val couponDiscount = appliedCoupon?.maxDiscount ?: 0
        return maxOf(0, actualPrice - couponDiscount)
    }
}
