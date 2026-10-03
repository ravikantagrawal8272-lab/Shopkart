import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Get seller profile by userId
export const getSellerProfile = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const seller = await ctx.db
      .query("sellers")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .first();
    return seller;
  },
});

// Register seller
export const registerSeller = mutation({
  args: {
    userId: v.string(),
    shopName: v.string(),
    gst: v.string(),
  },
  handler: async (ctx, args) => {
    const existing = await ctx.db
      .query("sellers")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .first();

    if (existing) {
      await ctx.db.patch(existing._id, {
        shopName: args.shopName,
        gst: args.gst,
      });
      return existing._id;
    }

    const sellerId = await ctx.db.insert("sellers", {
      userId: args.userId,
      shopName: args.shopName,
      gst: args.gst,
      products: [],
      rating: 4.9,
      totalSales: 0,
      createdAt: Date.now(),
    });

    return sellerId;
  },
});
