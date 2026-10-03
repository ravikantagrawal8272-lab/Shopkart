import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Query to get user's active cart with populated product details
export const getCart = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const cartEntries = await ctx.db
      .query("cart")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    const items = await Promise.all(
      cartEntries.map(async (entry) => {
        let product = null;
        try {
          // Try fetching product by id
          product = await ctx.db
            .query("products")
            .filter((q) => q.eq(q.field("_id"), entry.productId))
            .first();
        } catch (e) {
          // ignore
        }
        return {
          cartId: entry._id,
          userId: entry.userId,
          productId: entry.productId,
          qty: entry.qty,
          selectedVariant: entry.selectedVariant || "Standard",
          selectedColor: entry.selectedColor || "Default",
          product: product,
        };
      })
    );

    return items;
  },
});

// Mutation: Add or increment item in cart
export const addToCart = mutation({
  args: {
    userId: v.string(),
    productId: v.string(),
    qty: v.number(),
    selectedVariant: v.optional(v.string()),
    selectedColor: v.optional(v.string()),
  },
  handler: async (ctx, args) => {
    // Check product stock first
    const product = await ctx.db
      .query("products")
      .filter((q) => q.eq(q.field("_id"), args.productId))
      .first();

    if (product && product.stock <= 0) {
      throw new Error("Item is currently Out of Stock!");
    }

    const existing = await ctx.db
      .query("cart")
      .withIndex("by_user_and_product", (q) =>
        q.eq("userId", args.userId).eq("productId", args.productId)
      )
      .first();

    if (existing) {
      await ctx.db.patch(existing._id, {
        qty: existing.qty + args.qty,
        selectedVariant: args.selectedVariant ?? existing.selectedVariant,
        selectedColor: args.selectedColor ?? existing.selectedColor,
        updatedAt: Date.now(),
      });
      return existing._id;
    } else {
      const id = await ctx.db.insert("cart", {
        userId: args.userId,
        productId: args.productId,
        qty: args.qty,
        selectedVariant: args.selectedVariant ?? "Standard",
        selectedColor: args.selectedColor ?? "Default",
        updatedAt: Date.now(),
      });
      return id;
    }
  },
});

// Mutation: Update cart quantity
export const updateCartQty = mutation({
  args: {
    cartId: v.id("cart"),
    qty: v.number(),
  },
  handler: async (ctx, args) => {
    if (args.qty <= 0) {
      await ctx.db.delete(args.cartId);
      return { deleted: true };
    } else {
      await ctx.db.patch(args.cartId, {
        qty: args.qty,
        updatedAt: Date.now(),
      });
      return { updated: true, qty: args.qty };
    }
  },
});

// Mutation: Remove from cart
export const removeFromCart = mutation({
  args: { cartId: v.id("cart") },
  handler: async (ctx, args) => {
    await ctx.db.delete(args.cartId);
    return { success: true };
  },
});

// Mutation: Clear cart after successful checkout
export const clearCart = mutation({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const items = await ctx.db
      .query("cart")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    for (const item of items) {
      await ctx.db.delete(item._id);
    }
    return { clearedCount: items.length };
  },
});
