import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

/**
 * Super Advanced Search Engine for ShopKart (Flipkart / Amazon style)
 * Full text search, multi-faceted filtering, sorting, recent searches, trending keywords
 */

// Query: Search Products with advanced filters and sorting
export const searchProducts = query({
  args: {
    query: v.string(),
    category: v.optional(v.string()),
    brand: v.optional(v.string()),
    minPrice: v.optional(v.number()),
    maxPrice: v.optional(v.number()),
    minRating: v.optional(v.number()),
    inStockOnly: v.optional(v.boolean()),
    sortBy: v.optional(v.string()), // "relevance" | "price_low" | "price_high" | "rating" | "newest"
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const rawQuery = (args.query || "").trim().toLowerCase();
    const queryTokens = rawQuery.split(/\s+/).filter((t) => t.length > 0);

    // Fetch all products or use search index
    let allProducts = await ctx.db.query("products").collect();

    // 1. Text Search & Relevance scoring
    let results = allProducts.filter((product) => {
      if (queryTokens.length === 0) return true;

      const titleLower = product.title.toLowerCase();
      const descLower = product.description.toLowerCase();
      const brandLower = product.brand.toLowerCase();
      const categoryLower = product.category.toLowerCase();

      // Check full query match
      if (titleLower.includes(rawQuery) || descLower.includes(rawQuery) || brandLower.includes(rawQuery)) {
        return true;
      }

      // Check token match (typo tolerance / partial word overlap)
      return queryTokens.some(
        (token) =>
          titleLower.includes(token) ||
          descLower.includes(token) ||
          brandLower.includes(token) ||
          categoryLower.includes(token)
      );
    });

    // 2. Category Filter
    if (args.category && args.category !== "All") {
      results = results.filter(
        (p) => p.category.toLowerCase() === args.category!.toLowerCase()
      );
    }

    // 3. Brand Filter
    if (args.brand && args.brand !== "All") {
      results = results.filter(
        (p) => p.brand.toLowerCase() === args.brand!.toLowerCase()
      );
    }

    // 4. Price Range Filters
    if (args.minPrice !== undefined) {
      results = results.filter((p) => p.price >= args.minPrice!);
    }
    if (args.maxPrice !== undefined) {
      results = results.filter((p) => p.price <= args.maxPrice!);
    }

    // 5. Min Rating Filter
    if (args.minRating !== undefined && args.minRating > 0) {
      results = results.filter((p) => p.rating >= args.minRating!);
    }

    // 6. In-stock Only Filter
    if (args.inStockOnly) {
      results = results.filter((p) => p.stock > 0);
    }

    // 7. Sort Options
    const sortBy = args.sortBy || "relevance";
    results.sort((a, b) => {
      // Out of stock items always sink to the bottom unless sorted strictly
      if (a.stock <= 0 && b.stock > 0) return 1;
      if (a.stock > 0 && b.stock <= 0) return -1;

      switch (sortBy) {
        case "price_low":
          return a.price - b.price;
        case "price_high":
          return b.price - a.price;
        case "rating":
          return b.rating - a.rating || b.reviewsCount - a.reviewsCount;
        case "newest":
          return b.createdAt - a.createdAt;
        case "relevance":
        default:
          if (queryTokens.length > 0) {
            const aTitle = a.title.toLowerCase();
            const bTitle = b.title.toLowerCase();
            const aExact = aTitle.includes(rawQuery) ? 2 : 0;
            const bExact = bTitle.includes(rawQuery) ? 2 : 0;
            return bExact - aExact || b.rating - a.rating;
          }
          return b.rating - a.rating;
      }
    });

    if (args.limit && args.limit > 0) {
      return results.slice(0, args.limit);
    }

    return results;
  },
});

// Query: Live autocomplete search suggestions
export const getAutocompleteSuggestions = query({
  args: {
    query: v.string(),
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const q = (args.query || "").trim().toLowerCase();
    if (!q) return [];

    const products = await ctx.db.query("products").collect();
    const matches = products.filter(
      (p) =>
        p.title.toLowerCase().includes(q) ||
        p.brand.toLowerCase().includes(q) ||
        p.category.toLowerCase().includes(q)
    );

    const max = args.limit || 5;
    return matches.slice(0, max).map((p) => ({
      id: p._id,
      title: p.title,
      category: p.category,
      brand: p.brand,
      price: p.price,
      image: p.images[0] || "",
      rating: p.rating,
      stock: p.stock,
    }));
  },
});

// Query: Get user's recent search history
export const getRecentSearches = query({
  args: {
    userId: v.string(),
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const history = await ctx.db
      .query("searchHistory")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    history.sort((a, b) => b.createdAt - a.createdAt);
    const max = args.limit || 8;
    return history.slice(0, max);
  },
});

// Query: Get top trending searches across the platform
export const getTrendingSearches = query({
  args: {
    limit: v.optional(v.number()),
  },
  handler: async (ctx, args) => {
    const trending = await ctx.db.query("trendingSearches").collect();
    trending.sort((a, b) => b.count - a.count);
    const max = args.limit || 6;
    return trending.slice(0, max);
  },
});

// Mutation: Save a search to user history
export const addSearchHistory = mutation({
  args: {
    userId: v.string(),
    query: v.string(),
  },
  handler: async (ctx, args) => {
    const trimmed = args.query.trim();
    if (!trimmed) return null;

    // Check if already in recent history to update timestamp
    const existing = await ctx.db
      .query("searchHistory")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .filter((q) => q.eq(q.field("query"), trimmed))
      .first();

    if (existing) {
      await ctx.db.patch(existing._id, { createdAt: Date.now() });
      return existing._id;
    }

    return await ctx.db.insert("searchHistory", {
      userId: args.userId,
      query: trimmed,
      createdAt: Date.now(),
    });
  },
});

// Mutation: Delete single search history item
export const deleteSearchHistory = mutation({
  args: {
    id: v.id("searchHistory"),
  },
  handler: async (ctx, args) => {
    await ctx.db.delete(args.id);
    return { success: true };
  },
});

// Mutation: Clear all search history for user
export const clearSearchHistory = mutation({
  args: {
    userId: v.string(),
  },
  handler: async (ctx, args) => {
    const history = await ctx.db
      .query("searchHistory")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    for (const item of history) {
      await ctx.db.delete(item._id);
    }
    return { count: history.length };
  },
});

// Mutation: Increment trending search counter
export const incrementTrendingSearch = mutation({
  args: {
    query: v.string(),
  },
  handler: async (ctx, args) => {
    const normalized = args.query.trim();
    if (!normalized) return null;

    const existing = await ctx.db
      .query("trendingSearches")
      .withIndex("by_query", (q) => q.eq("query", normalized))
      .first();

    if (existing) {
      await ctx.db.patch(existing._id, {
        count: existing.count + 1,
        createdAt: Date.now(),
      });
      return existing._id;
    } else {
      return await ctx.db.insert("trendingSearches", {
        query: normalized,
        count: 1,
        createdAt: Date.now(),
      });
    }
  },
});
