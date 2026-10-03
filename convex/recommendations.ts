import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

/**
 * Amazon-Style AI Recommendation & Personalization Engine
 * Tracks user touchpoints, generates Frequently Bought Together bundles,
 * and delivers personalized feeds.
 */

// Mutation: Log a user interaction (view / cart / wishlist / purchase)
export const logUserInteraction = mutation({
  args: {
    userId: v.string(),
    productId: v.string(),
    action: v.string(), // "view" | "cart" | "wishlist" | "purchase"
    category: v.optional(v.string()),
  },
  handler: async (ctx, args) => {
    // 1. If view action, log to recentlyViewed table
    if (args.action === "view") {
      const existing = await ctx.db
        .query("recentlyViewed")
        .withIndex("by_user_product", (q) =>
          q.eq("userId", args.userId).eq("productId", args.productId)
        )
        .first();

      if (existing) {
        await ctx.db.patch(existing._id, { createdAt: Date.now() });
      } else {
        await ctx.db.insert("recentlyViewed", {
          userId: args.userId,
          productId: args.productId,
          createdAt: Date.now(),
        });
      }
    }

    // 2. Insert interaction log for AI ranking model
    return await ctx.db.insert("recommendedLogs", {
      userId: args.userId,
      productId: args.productId,
      action: args.action,
      category: args.category,
      createdAt: Date.now(),
    });
  },
});

// Query: Get Recently Viewed Products for User
export const getRecentlyViewed = query({
  args: {
    userId: v.string(),
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const list = await ctx.db
      .query("recentlyViewed")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    list.sort((a, b) => b.createdAt - a.createdAt);
    const max = args.limit || 8;
    const topIds = list.slice(0, max).map((item) => item.productId);

    const allProducts = await ctx.db.query("products").collect();
    return topIds.mapNotNull((id) => allProducts.find((p) => p._id.toString() === id || (p as any).id === id));
  },
});

// Query: AI-Powered "Frequently Bought Together" bundle
export const getFrequentlyBoughtTogether = query({
  args: {
    productId: v.string(),
  },
  handler: async (ctx, args) => {
    const allProducts = await ctx.db.query("products").collect();
    const targetProduct = allProducts.find(
      (p) => p._id.toString() === args.productId || (p as any).id === args.productId
    );

    if (!targetProduct) return null;

    // AI Bundle matching rules:
    // Mobiles -> Pair with Headphones or Smartwatch
    // Electronics / Laptops -> Pair with Wireless Mouse / Audio
    // Fashion -> Pair with Footwear or Accessories
    let complementary = allProducts.filter((p) => (p._id.toString() !== args.productId && (p as any).id !== args.productId) && p.stock > 0);

    if (targetProduct.category === "mobiles") {
      complementary = complementary.filter(
        (p) => p.category === "electronics" || p.brand === targetProduct.brand
      );
    } else if (targetProduct.category === "electronics") {
      complementary = complementary.filter((p) => p.category === "electronics" || p.category === "appliances");
    }

    const bundleItem = complementary.sort((a, b) => b.rating - a.rating)[0] || complementary[0];
    if (!bundleItem) return null;

    const bundleTotalPrice = targetProduct.price + bundleItem.price;
    const bundleDiscountPrice = Math.round(bundleTotalPrice * 0.92); // Extra 8% bundle savings

    return {
      mainProduct: targetProduct,
      bundleProduct: bundleItem,
      totalOriginal: targetProduct.originalPrice + bundleItem.originalPrice,
      bundlePrice: bundleDiscountPrice,
      savings: bundleTotalPrice - bundleDiscountPrice,
    };
  },
});

// Query: AI Personalized Recommended Feed
export const getAIRecommendedFeed = query({
  args: {
    userId: v.string(),
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const allProducts = await ctx.db.query("products").collect();
    const available = allProducts.filter((p) => p.stock > 0);

    // Fetch user interaction history
    const logs = await ctx.db
      .query("recommendedLogs")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    if (logs.length === 0) {
      // Cold start: return top rated & trending products
      return available.sort((a, b) => b.rating - a.rating || b.reviewsCount - a.reviewsCount).slice(0, args.limit || 8);
    }

    // Tally category preferences
    const categoryScores: Record<string, number> = {};
    for (const log of logs) {
      const cat = log.category || "electronics";
      const weight = log.action === "purchase" ? 5 : log.action === "cart" ? 3 : log.action === "wishlist" ? 2 : 1;
      categoryScores[cat] = (categoryScores[cat] || 0) + weight;
    }

    // Rank products by user affinity score + product rating
    const scored = available.map((prod) => {
      const catScore = categoryScores[prod.category] || 0;
      const finalScore = catScore * 10 + prod.rating * 2 + (prod.discount > 20 ? 5 : 0);
      return { product: prod, score: finalScore };
    });

    scored.sort((a, b) => b.score - a.score);
    return scored.slice(0, args.limit || 8).map((item) => item.product);
  },
});
