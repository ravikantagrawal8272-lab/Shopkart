import { defineSchema, defineTable } from "convex/server";
import { v } from "convex/values";

export default defineSchema({
  // 1. users table
  users: defineTable({
    name: v.string(),
    email: v.string(),
    password_hash: v.string(),
    phone: v.string(),
    avatar: v.optional(v.string()),
    role: v.string(), // "user" | "admin" | "seller"
    addresses: v.array(
      v.object({
        id: v.string(),
        fullName: v.string(),
        street: v.string(),
        city: v.string(),
        state: v.string(),
        pincode: v.string(),
        isDefault: v.boolean(),
      })
    ),
    createdAt: v.number(),
  })
    .index("by_email", ["email"])
    .index("by_role", ["role"]),

  // 2. products table (with Multi-Vendor fields)
  products: defineTable({
    title: v.string(),
    description: v.string(),
    price: v.number(),
    originalPrice: v.number(),
    discount: v.number(),
    category: v.string(),
    brand: v.string(),
    images: v.array(v.string()),
    stock: v.number(), // 0 = out of stock
    rating: v.number(),
    reviewsCount: v.number(),
    sellerId: v.string(),
    isApproved: v.boolean(), // Product approval from Admin (default false for vendor additions)
    commissionRate: v.optional(v.number()), // e.g. 10 (%)
    createdAt: v.number(),
  })
    .index("by_category", ["category"])
    .index("by_brand", ["brand"])
    .index("by_price", ["price"])
    .index("by_seller", ["sellerId"])
    .index("by_approved", ["isApproved"])
    .searchIndex("search_title", {
      searchField: "title",
      filterFields: ["category", "brand", "price", "isApproved"],
    }),

  // 3. orders table (with Seller commission & earnings)
  orders: defineTable({
    userId: v.string(),
    products: v.array(
      v.object({
        productId: v.string(),
        productTitle: v.optional(v.string()),
        productImage: v.optional(v.string()),
        qty: v.number(),
        price: v.number(),
        sellerId: v.optional(v.string()),
        selectedVariant: v.optional(v.string()),
        selectedColor: v.optional(v.string()),
      })
    ),
    totalAmount: v.number(),
    sellerId: v.optional(v.string()),
    commissionAmount: v.optional(v.number()), // Commission deducted (e.g. 10%)
    sellerEarnings: v.optional(v.number()), // totalAmount - commissionAmount
    status: v.string(), // "Ordered" | "Packed" | "Shipped" | "Delivered"
    paymentStatus: v.string(), // "paid" | "pending" | "failed"
    paymentMethod: v.string(), // "razorpay" | "cod" | "upi" | "cash_counter"
    address: v.object({
      id: v.string(),
      fullName: v.string(),
      street: v.string(),
      city: v.string(),
      state: v.string(),
      pincode: v.string(),
      isDefault: v.boolean(),
    }),
    trackingId: v.string(),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_seller", ["sellerId"])
    .index("by_status", ["status"]),

  // 4. sellers table (Multi-Vendor Marketplace)
  sellers: defineTable({
    userId: v.string(),
    shopName: v.string(),
    shopLogo: v.optional(v.string()),
    shopDescription: v.optional(v.string()),
    gstNumber: v.string(),
    address: v.optional(v.string()),
    rating: v.number(),
    totalProducts: v.number(),
    totalSales: v.number(),
    commission: v.number(), // default 10%
    status: v.string(), // "pending" | "approved" | "rejected"
    products: v.array(v.string()),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_status", ["status"]),

  // 5. sellerEarnings table
  sellerEarnings: defineTable({
    sellerId: v.string(),
    orderId: v.string(),
    amount: v.number(), // gross product sales
    commission: v.number(), // commission fee taken by platform
    netEarnings: v.number(), // amount - commission
    status: v.string(), // "pending" | "paid"
    payoutMethod: v.optional(v.string()), // "UPI" | "Bank Transfer"
    createdAt: v.number(),
  })
    .index("by_seller", ["sellerId"])
    .index("by_status", ["status"]),

  // 6. recentlyViewed table (AI Recommendation Feed)
  recentlyViewed: defineTable({
    userId: v.string(),
    productId: v.string(),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_user_product", ["userId", "productId"]),

  // 7. recommendedLogs table (AI interaction logs: view/cart/wishlist/purchase)
  recommendedLogs: defineTable({
    userId: v.string(),
    productId: v.string(),
    action: v.string(), // "view" | "cart" | "wishlist" | "purchase"
    category: v.optional(v.string()),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_product", ["productId"])
    .index("by_action", ["action"]),

  // 8. cart table
  cart: defineTable({
    userId: v.string(),
    productId: v.string(),
    qty: v.number(),
    selectedVariant: v.optional(v.string()),
    selectedColor: v.optional(v.string()),
    updatedAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_user_product", ["userId", "productId"]),

  // 9. wishlist table
  wishlist: defineTable({
    userId: v.string(),
    productId: v.string(),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_user_product", ["userId", "productId"]),

  // 10. payments table
  payments: defineTable({
    orderId: v.string(),
    userId: v.string(),
    amount: v.number(),
    method: v.string(), // "razorpay" | "cod" | "upi" | "card" | "cash_counter"
    razorpayPaymentId: v.optional(v.string()),
    razorpayOrderId: v.optional(v.string()),
    razorpaySignature: v.optional(v.string()),
    status: v.string(), // "pending" | "success" | "failed"
    currency: v.string(),
    createdAt: v.number(),
  })
    .index("by_order", ["orderId"])
    .index("by_user", ["userId"]),

  // 11. notifications table
  notifications: defineTable({
    userId: v.optional(v.string()), // null = broadcast/admins
    type: v.string(), // "order_update" | "low_stock" | "new_product" | "offer" | "system" | "seller_approval" | "payout"
    title: v.string(),
    message: v.string(),
    link: v.optional(v.string()),
    isRead: v.boolean(),
    image: v.optional(v.string()),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_created", ["createdAt"]),

  // 12. searchHistory table
  searchHistory: defineTable({
    userId: v.string(),
    query: v.string(),
    createdAt: v.number(),
  })
    .index("by_user", ["userId"])
    .index("by_user_created", ["userId", "createdAt"]),

  // 13. trendingSearches table
  trendingSearches: defineTable({
    query: v.string(),
    count: v.number(),
    createdAt: v.number(),
  })
    .index("by_query", ["query"])
    .index("by_count", ["count"]),

  // 14. rate limits table
  rateLimits: defineTable({
    key: v.string(),
    count: v.number(),
    windowStart: v.number(),
  }).index("by_key", ["key"]),
});
