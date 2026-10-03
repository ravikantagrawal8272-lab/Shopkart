import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

// Query to get all products with category, brand, price filtering and sorting
export const getProducts = query({
  args: {
    category: v.optional(v.string()),
    query: v.optional(v.string()),
    brand: v.optional(v.string()),
    minPrice: v.optional(v.number()),
    maxPrice: v.optional(v.number()),
    sortBy: v.optional(v.string()), // "popularity", "price_asc", "price_desc", "rating", "discount"
    onlyInStock: v.optional(v.boolean()),
  },
  handler: async (ctx, args) => {
    let products = await ctx.db.query("products").collect();

    if (args.category && args.category !== "all") {
      products = products.filter(
        (p) => p.category.toLowerCase() === args.category!.toLowerCase()
      );
    }

    if (args.brand) {
      products = products.filter(
        (p) => p.brand.toLowerCase() === args.brand!.toLowerCase()
      );
    }

    if (args.minPrice !== undefined) {
      products = products.filter((p) => p.price >= args.minPrice!);
    }

    if (args.maxPrice !== undefined) {
      products = products.filter((p) => p.price <= args.maxPrice!);
    }

    if (args.onlyInStock) {
      products = products.filter((p) => p.stock > 0);
    }

    if (args.query && args.query.trim().length > 0) {
      const q = args.query.toLowerCase().trim();
      products = products.filter(
        (p) =>
          p.title.toLowerCase().includes(q) ||
          p.description.toLowerCase().includes(q) ||
          p.brand.toLowerCase().includes(q) ||
          p.category.toLowerCase().includes(q)
      );
    }

    // Sorting
    switch (args.sortBy) {
      case "price_asc":
        products.sort((a, b) => a.price - b.price);
        break;
      case "price_desc":
        products.sort((a, b) => b.price - a.price);
        break;
      case "rating":
        products.sort((a, b) => b.rating - a.rating);
        break;
      case "discount":
        products.sort((a, b) => b.discount - a.discount);
        break;
      default:
        // popularity by review count
        products.sort((a, b) => b.reviewsCount - a.reviewsCount);
        break;
    }

    return products;
  },
});

// Query product by id
export const getProductById = query({
  args: { id: v.string() },
  handler: async (ctx, args) => {
    const product = await ctx.db
      .query("products")
      .filter((q) => q.eq(q.field("_id"), args.id))
      .first();
    return product;
  },
});

// Search products by title and filters
export const searchProducts = query({
  args: {
    query: v.string(),
    category: v.optional(v.string()),
    minPrice: v.optional(v.number()),
    maxPrice: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    let products = await ctx.db.query("products").collect();
    const q = args.query.toLowerCase().trim();

    if (q) {
      products = products.filter(
        (p) =>
          p.title.toLowerCase().includes(q) ||
          p.brand.toLowerCase().includes(q) ||
          p.category.toLowerCase().includes(q)
      );
    }

    if (args.category && args.category !== "all") {
      products = products.filter(
        (p) => p.category.toLowerCase() === args.category!.toLowerCase()
      );
    }

    if (args.minPrice !== undefined) {
      products = products.filter((p) => p.price >= args.minPrice!);
    }

    if (args.maxPrice !== undefined) {
      products = products.filter((p) => p.price <= args.maxPrice!);
    }

    return products;
  },
});

// Mutation to update stock (e.g. on order creation or stock replenishment)
export const updateStock = mutation({
  args: {
    productId: v.id("products"),
    quantityDelta: v.number(), // negative to decrease on purchase, positive to restock
  },
  handler: async (ctx, args) => {
    const product = await ctx.db.get(args.productId);
    if (!product) throw new Error("Product not found");

    const newStock = Math.max(0, product.stock + args.quantityDelta);
    await ctx.db.patch(args.productId, { stock: newStock });
    return { success: true, newStock };
  },
});

// Mutation to create a new product
export const createProduct = mutation({
  args: {
    title: v.string(),
    description: v.string(),
    price: v.number(),
    originalPrice: v.number(),
    discount: v.number(),
    category: v.string(),
    brand: v.string(),
    images: v.array(v.string()),
    stock: v.number(),
    rating: v.number(),
    reviewsCount: v.number(),
    sellerId: v.string(),
    highlights: v.optional(v.array(v.string())),
    variants: v.optional(v.array(v.string())),
    colors: v.optional(v.array(v.string())),
    tag: v.optional(v.string()),
    isShopKartAssured: v.optional(v.boolean()),
    oneDayDelivery: v.optional(v.boolean()),
  },
  handler: async (ctx, args) => {
    const productId = await ctx.db.insert("products", {
      ...args,
      createdAt: Date.now(),
    });
    return productId;
  },
});
