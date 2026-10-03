import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Query orders by user
export const getOrdersByUser = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const orders = await ctx.db
      .query("orders")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .order("desc")
      .collect();
    return orders;
  },
});

// Query single order by id
export const getOrderById = query({
  args: { orderId: v.id("orders") },
  handler: async (ctx, args) => {
    return await ctx.db.get(args.orderId);
  },
});

// Mutation to create a new order and decrement stock
export const createOrder = mutation({
  args: {
    userId: v.string(),
    products: v.array(
      v.object({
        productId: v.string(),
        productTitle: v.optional(v.string()),
        productImage: v.optional(v.string()),
        qty: v.number(),
        price: v.number(),
        selectedVariant: v.optional(v.string()),
        selectedColor: v.optional(v.string()),
      })
    ),
    totalAmount: v.number(),
    paymentMethod: v.string(),
    address: v.object({
      fullName: v.string(),
      street: v.string(),
      city: v.string(),
      state: v.string(),
      pincode: v.string(),
    }),
  },
  handler: async (ctx, args) => {
    // 1. Check stock for each product and decrement
    for (const item of args.products) {
      const product = await ctx.db
        .query("products")
        .filter((q) => q.eq(q.field("_id"), item.productId))
        .first();

      if (product) {
        if (product.stock < item.qty) {
          throw new Error(`Insufficient stock for product ${product.title}`);
        }
        await ctx.db.patch(product._id, {
          stock: Math.max(0, product.stock - item.qty),
        });
      }
    }

    // 2. Generate clean tracking ID
    const randomTrack = Math.floor(100000 + Math.random() * 900000);
    const trackingId = `SK-TRK-${randomTrack}`;

    // 3. Insert order record
    const orderId = await ctx.db.insert("orders", {
      userId: args.userId,
      products: args.products,
      totalAmount: args.totalAmount,
      status: "Ordered",
      paymentStatus: "Paid",
      paymentMethod: args.paymentMethod,
      address: args.address,
      trackingId: trackingId,
      createdAt: Date.now(),
    });

    // 4. Automatically clear the user's cart
    const cartItems = await ctx.db
      .query("cart")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    for (const c of cartItems) {
      await ctx.db.delete(c._id);
    }

    return { orderId, trackingId };
  },
});

// Mutation to advance order status
export const updateOrderStatus = mutation({
  args: {
    orderId: v.id("orders"),
    status: v.union(
      v.literal("Ordered"),
      v.literal("Packed"),
      v.literal("Shipped"),
      v.literal("Delivered")
    ),
  },
  handler: async (ctx, args) => {
    await ctx.db.patch(args.orderId, { status: args.status });
    return { success: true };
  },
});
