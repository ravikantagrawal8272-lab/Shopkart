import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

/**
 * Amazon-Style Multi-Vendor Marketplace & Seller Hub Backend
 * Handles Merchant registration, Commission, Earnings, Payouts, and Product Review Workflow
 */

// Mutation: Register as a new seller (pending status)
export const becomeSeller = mutation({
  args: {
    userId: v.string(),
    shopName: v.string(),
    gstNumber: v.string(),
    shopDescription: v.string(),
    shopLogo: v.optional(v.string()),
    address: v.optional(v.string()),
  },
  handler: async (ctx, args) => {
    // Check if user already submitted a merchant profile
    const existing = await ctx.db
      .query("sellers")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .first();

    if (existing) {
      if (existing.status === "approved") {
        throw new Error("You are already an approved seller.");
      }
      // Update pending details
      await ctx.db.patch(existing._id, {
        shopName: args.shopName,
        gstNumber: args.gstNumber,
        shopDescription: args.shopDescription,
        shopLogo: args.shopLogo,
        address: args.address,
        status: "pending",
      });
      return existing._id;
    }

    const sellerId = await ctx.db.insert("sellers", {
      userId: args.userId,
      shopName: args.shopName,
      shopLogo: args.shopLogo || "https://images.unsplash.com/photo-1556742049-0a67e5572293?w=300",
      shopDescription: args.shopDescription,
      gstNumber: args.gstNumber,
      address: args.address || "Bengaluru, India",
      rating: 4.8,
      totalProducts: 0,
      totalSales: 0,
      commission: 10, // 10% standard platform fee
      status: "pending", // Requires Admin Approval
      products: [],
      createdAt: Date.now(),
    });

    // Notify admins about new seller registration
    await ctx.db.insert("notifications", {
      type: "system",
      title: "New Seller Application Submitted 🏪",
      message: `${args.shopName} (GST: ${args.gstNumber}) has applied for merchant status.`,
      link: sellerId.toString(),
      isRead: false,
      createdAt: Date.now(),
    });

    return sellerId;
  },
});

// Query: Get Seller by User ID
export const getSellerByUserId = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    return await ctx.db
      .query("sellers")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .first();
  },
});

// Query: Get all Approved Sellers
export const getApprovedSellers = query({
  args: {},
  handler: async (ctx) => {
    return await ctx.db
      .query("sellers")
      .withIndex("by_status", (q) => q.eq("status", "approved"))
      .collect();
  },
});

// Query: Get all Pending Sellers (for Admin review)
export const getPendingSellers = query({
  args: {},
  handler: async (ctx) => {
    return await ctx.db
      .query("sellers")
      .withIndex("by_status", (q) => q.eq("status", "pending"))
      .collect();
  },
});

// Admin Mutation: Approve Seller
export const approveSeller = mutation({
  args: { sellerId: v.id("sellers") },
  handler: async (ctx, args) => {
    const seller = await ctx.db.get(args.sellerId);
    if (!seller) throw new Error("Seller not found");

    await ctx.db.patch(args.sellerId, { status: "approved" });

    // Elevate user role to 'seller' in users table
    const user = await ctx.db
      .query("users")
      .filter((q) => q.eq(q.field("_id"), seller.userId))
      .first();

    if (user && user.role !== "admin") {
      await ctx.db.patch(user._id, { role: "seller" });
    }

    // Send congratulatory notification to the new seller
    await ctx.db.insert("notifications", {
      userId: seller.userId,
      type: "seller_approval",
      title: "Seller Account Approved! 🎉",
      message: `Congratulations! ${seller.shopName} is now live on ShopKart Multi-Vendor Marketplace. You can start listing products.`,
      link: "seller_dashboard",
      isRead: false,
      createdAt: Date.now(),
    });

    return { success: true };
  },
});

// Admin Mutation: Reject Seller
export const rejectSeller = mutation({
  args: { sellerId: v.id("sellers"), reason: v.optional(v.string()) },
  handler: async (ctx, args) => {
    const seller = await ctx.db.get(args.sellerId);
    if (!seller) throw new Error("Seller not found");

    await ctx.db.patch(args.sellerId, { status: "rejected" });

    // Notify user
    await ctx.db.insert("notifications", {
      userId: seller.userId,
      type: "system",
      title: "Seller Application Status Update",
      message: `Your seller application for ${seller.shopName} was not approved. Reason: ${args.reason || "GST documentation incomplete"}.`,
      isRead: false,
      createdAt: Date.now(),
    });

    return { success: true };
  },
});

// Admin Mutation: Approve Product listing
export const approveProduct = mutation({
  args: { productId: v.id("products") },
  handler: async (ctx, args) => {
    const prod = await ctx.db.get(args.productId);
    if (!prod) throw new Error("Product not found");

    await ctx.db.patch(args.productId, { isApproved: true });

    // Notify seller
    const seller = await ctx.db.get(prod.sellerId as any);
    if (seller) {
      await ctx.db.insert("notifications", {
        userId: seller.userId,
        type: "new_product",
        title: "Product Approved & Live! ✓",
        message: `'${prod.title}' has been reviewed and is now live for buyers across India.`,
        link: prod._id.toString(),
        isRead: false,
        createdAt: Date.now(),
      });
    }

    return { success: true };
  },
});

// Mutation: Seller adds a new product (submitted for approval)
export const addSellerProduct = mutation({
  args: {
    sellerId: v.string(),
    title: v.string(),
    description: v.string(),
    price: v.number(),
    originalPrice: v.number(),
    category: v.string(),
    brand: v.string(),
    stock: v.number(),
    images: v.array(v.string()),
  },
  handler: async (ctx, args) => {
    const discount = Math.max(0, Math.round(((args.originalPrice - args.price) / args.originalPrice) * 100));

    const productId = await ctx.db.insert("products", {
      title: args.title,
      description: args.description,
      price: args.price,
      originalPrice: args.originalPrice,
      discount: discount,
      category: args.category,
      brand: args.brand,
      images: args.images,
      stock: args.stock,
      rating: 4.8,
      reviewsCount: 0,
      sellerId: args.sellerId,
      isApproved: false, // Must be approved by Admin
      commissionRate: 10,
      createdAt: Date.now(),
    });

    // Notify admins to review new product
    await ctx.db.insert("notifications", {
      type: "system",
      title: "New Product Pending Approval 📦",
      message: `Seller submitted '${args.title.slice(0, 30)}...' (₹${args.price}) for catalog review.`,
      link: productId.toString(),
      isRead: false,
      createdAt: Date.now(),
    });

    return productId;
  },
});

// Mutation: Request Earnings Payout
export const requestPayout = mutation({
  args: {
    sellerId: v.string(),
    amount: v.number(),
    method: v.string(), // "UPI" | "Bank Transfer"
    payoutDetails: v.string(), // UPI ID or Account Number
  },
  handler: async (ctx, args) => {
    // Check pending earnings
    const earnings = await ctx.db
      .query("sellerEarnings")
      .withIndex("by_seller", (q) => q.eq("sellerId", args.sellerId))
      .filter((q) => q.eq(q.field("status"), "pending"))
      .collect();

    const totalAvailable = earnings.reduce((acc, curr) => acc + curr.netEarnings, 0);
    if (args.amount > totalAvailable) {
      throw new Error(`Requested amount ₹${args.amount} exceeds available pending balance of ₹${totalAvailable}`);
    }

    // Mark these earnings as paid
    for (const record of earnings) {
      await ctx.db.patch(record._id, {
        status: "paid",
        payoutMethod: `${args.method}: ${args.payoutDetails}`,
      });
    }

    // Record payout notification
    const seller = await ctx.db
      .query("sellers")
      .filter((q) => q.eq(q.field("_id"), args.sellerId))
      .first();

    if (seller) {
      await ctx.db.insert("notifications", {
        userId: seller.userId,
        type: "payout",
        title: "Payout Request Processed 💰",
        message: `₹${args.amount} has been disbursed to ${args.method} (${args.payoutDetails}).`,
        isRead: false,
        createdAt: Date.now(),
      });
    }

    return { success: true, disbursedAmount: args.amount };
  },
});
