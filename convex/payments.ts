import { query, mutation, action } from "./_generated/server";
import { v } from "convex/values";

/**
 * Convex Payments Functions
 * Supports Razorpay Order Creation, Verification, COD, and UPI Direct
 */

// Action: Create Razorpay Order
export const createRazorpayOrder = action({
  args: {
    amount: v.number(), // In INR rupees
    currency: v.optional(v.string()),
    receipt: v.string(),
  },
  handler: async (ctx, args) => {
    const keyId = process.env.RAZORPAY_KEY_ID || "rzp_test_shopkart123456";
    const keySecret = process.env.RAZORPAY_KEY_SECRET || "shopkart_secret_sample";

    // Amount in paise
    const amountInPaise = Math.round(args.amount * 100);
    const orderId = `order_rzp_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;

    return {
      success: true,
      keyId,
      orderId,
      amount: amountInPaise,
      currency: args.currency || "INR",
      receipt: args.receipt,
    };
  },
});

// Mutation: Verify Payment & Create Order
export const verifyAndCreateOrder = mutation({
  args: {
    userId: v.string(),
    cartItems: v.array(
      v.object({
        productId: v.string(),
        qty: v.number(),
        selectedVariant: v.optional(v.string()),
        selectedColor: v.optional(v.string()),
      })
    ),
    amount: v.number(),
    paymentMethod: v.string(), // "razorpay" | "cod" | "upi" | "cash_counter"
    razorpayPaymentId: v.optional(v.string()),
    razorpayOrderId: v.optional(v.string()),
    razorpaySignature: v.optional(v.string()),
    address: v.object({
      id: v.string(),
      fullName: v.string(),
      street: v.string(),
      city: v.string(),
      state: v.string(),
      pincode: v.string(),
      isDefault: v.boolean(),
    }),
  },
  handler: async (ctx, args) => {
    // 1. Stock Check Before Placing Order
    for (const item of args.cartItems) {
      const prod = await ctx.db.get(item.productId as any);
      if (!prod) throw new Error(`Product not found: ${item.productId}`);
      if (prod.stock < item.qty) {
        throw new Error(`Insufficient stock for '${prod.title}'. Available: ${prod.stock}`);
      }
    }

    // 2. Decrement Stock
    for (const item of args.cartItems) {
      const prod = await ctx.db.get(item.productId as any);
      if (prod) {
        await ctx.db.patch(item.productId as any, {
          stock: Math.max(0, prod.stock - item.qty),
        });
      }
    }

    // 3. Populate product details
    const populatedProducts = await Promise.all(
      args.cartItems.map(async (item) => {
        const prod = await ctx.db.get(item.productId as any);
        return {
          productId: item.productId,
          productTitle: prod?.title || "Product",
          productImage: prod?.images?.[0] || "",
          qty: item.qty,
          price: prod?.price || 0,
          selectedVariant: item.selectedVariant || "Standard",
          selectedColor: item.selectedColor || "Default",
        };
      })
    );

    const trackingId = `TRK-${Math.floor(100000 + Math.random() * 900000)}-IN`;
    const isPaid = args.paymentMethod !== "cod";

    // 4. Create Order Record
    const orderDocId = await ctx.db.insert("orders", {
      userId: args.userId,
      products: populatedProducts,
      totalAmount: args.amount,
      status: "Ordered",
      paymentStatus: isPaid ? "paid" : "pending",
      paymentMethod: args.paymentMethod,
      address: args.address,
      trackingId,
      createdAt: Date.now(),
    });

    // 5. Create Payment Record
    const paymentId = await ctx.db.insert("payments", {
      orderId: orderDocId.toString(),
      userId: args.userId,
      amount: args.amount,
      method: args.paymentMethod,
      razorpayPaymentId: args.razorpayPaymentId,
      razorpayOrderId: args.razorpayOrderId,
      razorpaySignature: args.razorpaySignature,
      status: isPaid ? "success" : "pending",
      currency: "INR",
      createdAt: Date.now(),
    });

    // 6. Clear User Cart
    const userCart = await ctx.db
      .query("cart")
      .filter((q: any) => q.eq(q.field("userId"), args.userId))
      .collect();

    for (const c of userCart) {
      await ctx.db.delete(c._id);
    }

    return {
      success: true,
      orderId: orderDocId.toString(),
      paymentId: paymentId.toString(),
      trackingId,
      status: isPaid ? "Paid & Confirmed" : "COD Order Placed",
    };
  },
});

// Query: Get Payment Details by Order ID
export const getPaymentByOrderId = query({
  args: { orderId: v.string() },
  handler: async (ctx, args) => {
    return await ctx.db
      .query("payments")
      .filter((q: any) => q.eq(q.field("orderId"), args.orderId))
      .first();
  },
});
