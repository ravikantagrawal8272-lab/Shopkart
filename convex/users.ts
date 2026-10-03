import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Get user by ID or default demo profile
export const getUser = query({
  args: { userId: v.string() },
  handler: async (ctx, args) => {
    const user = await ctx.db
      .query("users")
      .filter((q) => q.eq(q.field("_id"), args.userId))
      .first();
    return user;
  },
});

// Create new user or return existing
export const createUser = mutation({
  args: {
    name: v.string(),
    email: v.string(),
    password_hash: v.string(),
    phone: v.string(),
    role: v.union(v.literal("user"), v.literal("admin"), v.literal("seller")),
    avatar: v.optional(v.string()),
  },
  handler: async (ctx, args) => {
    const existing = await ctx.db
      .query("users")
      .withIndex("by_email", (q) => q.eq("email", args.email))
      .first();

    if (existing) {
      return existing._id;
    }

    const userId = await ctx.db.insert("users", {
      name: args.name,
      email: args.email,
      password_hash: args.password_hash,
      phone: args.phone,
      avatar: args.avatar,
      role: args.role,
      addresses: [
        {
          id: "addr_1",
          fullName: args.name,
          street: "Flat 402, Skyline Residency, Tech Hub Road",
          city: "Bengaluru",
          state: "Karnataka",
          pincode: "560001",
          isDefault: true,
        },
      ],
      createdAt: Date.now(),
    });

    return userId;
  },
});

// Add or update delivery address
export const addAddress = mutation({
  args: {
    userId: v.id("users"),
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
    const user = await ctx.db.get(args.userId);
    if (!user) throw new Error("User not found");

    const updatedAddresses = [...user.addresses, args.address];
    await ctx.db.patch(args.userId, { addresses: updatedAddresses });
    return { success: true };
  },
});
