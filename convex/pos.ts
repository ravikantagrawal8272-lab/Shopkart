import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

/**
 * Convex POS & Cash Counter Engine for In-Store Sales
 */

// Query: Live Cash & Digital Collection for Today
export const getTodayCashCollection = query({
  args: { adminUserId: v.string() },
  handler: async (ctx, args) => {
    const todayStart = new Date();
    todayStart.setHours(0, 0, 0, 0);

    const orders = await ctx.db
      .query("orders")
      .filter((q: any) => q.gte(q.field("createdAt"), todayStart.getTime()))
      .collect();

    let totalCash = 0;
    let totalUPI = 0;
    let totalCard = 0;
    let posOrderCount = 0;

    for (const ord of orders) {
      if (ord.paymentMethod === "cash_counter" || ord.paymentMethod === "cash") {
        totalCash += ord.totalAmount;
        posOrderCount++;
      } else if (ord.paymentMethod === "upi" || ord.paymentMethod === "razorpay") {
        totalUPI += ord.totalAmount;
        posOrderCount++;
      } else if (ord.paymentMethod === "card") {
        totalCard += ord.totalAmount;
        posOrderCount++;
      }
    }

    return {
      totalCash,
      totalUPI,
      totalCard,
      totalRevenueToday: totalCash + totalUPI + totalCard,
      posOrdersCount: posOrderCount,
      timestamp: Date.now(),
    };
  },
});

// Mutation: Create Instant POS In-Store Sale & Invoice
export const createPosOrder = mutation({
  args: {
    adminUserId: v.string(),
    customerName: v.string(),
    customerPhone: v.string(),
    items: v.array(
      v.object({
        productId: v.string(),
        qty: v.number(),
        price: v.number(),
      })
    ),
    subtotal: v.number(),
    discountAmount: v.number(),
    gstAmount: v.number(), // 18% GST breakdown
    totalAmount: v.number(),
    paymentMethod: v.string(), // "cash" | "upi" | "card"
    cashTendered: v.optional(v.number()),
    changeDue: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    // 1. Stock validation & reduction
    for (const item of args.items) {
      const prod = await ctx.db.get(item.productId as any);
      if (!prod) throw new Error(`Product ${item.productId} not found`);
      if (prod.stock < item.qty) {
        throw new Error(`Insufficient stock for '${prod.title}' (Stock: ${prod.stock})`);
      }
      await ctx.db.patch(item.productId as any, {
        stock: Math.max(0, prod.stock - item.qty),
      });
    }

    // 2. Build populated items list
    const populatedItems = await Promise.all(
      args.items.map(async (item) => {
        const prod = await ctx.db.get(item.productId as any);
        return {
          productId: item.productId,
          productTitle: prod?.title || "POS Item",
          productImage: prod?.images?.[0] || "",
          qty: item.qty,
          price: item.price,
          selectedVariant: "In-Store",
          selectedColor: "Standard",
        };
      })
    );

    const orderId = await ctx.db.insert("orders", {
      userId: args.adminUserId,
      products: populatedItems,
      totalAmount: args.totalAmount,
      status: "Delivered", // POS sales are delivered instantly at counter
      paymentStatus: "paid",
      paymentMethod: "cash_counter",
      address: {
        id: "pos_store_address",
        fullName: args.customerName.trim() || "Walk-in Customer",
        street: "ShopKart Store Counter #01",
        city: "Bengaluru",
        state: "Karnataka",
        pincode: "560001",
        isDefault: true,
      },
      trackingId: `POS-${Math.floor(1000 + Math.random() * 9000)}`,
      createdAt: Date.now(),
    });

    // Record Payment
    await ctx.db.insert("payments", {
      orderId: orderId.toString(),
      userId: args.adminUserId,
      amount: args.totalAmount,
      method: "cash_counter",
      status: "success",
      currency: "INR",
      createdAt: Date.now(),
    });

    return {
      success: true,
      orderId: orderId.toString(),
      invoiceNumber: `INV-POS-${Date.now().toString().slice(-6)}`,
      timestamp: Date.now(),
    };
  },
});
