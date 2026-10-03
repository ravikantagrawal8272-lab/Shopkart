import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Query user's wishlist
export const getWishlist = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const items = await ctx.db
      .query("wishlist")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    const populated = await Promise.all(
      items.map(async (item) => {
        const product = await ctx.db
          .query("products")
          .filter((q) => q.eq(q.field("_id"), item.productId))
          .first();
        return {
          wishlistId: item._id,
          productId: item.productId,
          product: product,
          createdAt: item.createdAt,
        };
      })
    );

    return populated;
  },
});

// Toggle product in wishlist (add if missing, remove if present)
export const toggleWishlist = mutation({
  args: {
    userId: v.string(),
    productId: v.string(),
  },
  handler: async (ctx, args) => {
    const existing = await ctx.db
      .query("wishlist")
      .withIndex("by_user_and_product", (q) =>
        q.eq("userId", args.userId).eq("productId", args.productId)
      )
      .first();

    if (existing) {
      await ctx.db.delete(existing._id);
      return { inWishlist: false, action: "removed" };
    } else {
      const id = await ctx.db.insert("wishlist", {
        userId: args.userId,
        productId: args.productId,
        createdAt: Date.now(),
      });
      return { inWishlist: true, action: "added", id };
    }
  },
});
