import { query, mutation } from "./_generated/server";
import { v } from "convex/values";

/**
 * Real-Time Notification System for ShopKart
 * Handles User order updates, low-stock alerts for admins, festive deals, and system announcements
 */

// Query: Get notifications for a user (or broadcast/admin if user is admin)
export const getNotificationsByUser = query({
  args: {
    userId: v.string(),
    isAdmin: v.optional(v.boolean()),
  },
  handler: async (ctx, args) => {
    // 1. Fetch user-specific notifications
    const userNotifications = await ctx.db
      .query("notifications")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .collect();

    // 2. Fetch broadcast notifications (userId is undefined/null)
    const broadcastNotifications = await ctx.db
      .query("notifications")
      .filter((q) => q.eq(q.field("userId"), undefined))
      .collect();

    let combined = [...userNotifications, ...broadcastNotifications];

    // Deduplicate by ID
    const seen = new Set<string>();
    const unique = combined.filter((item) => {
      const idStr = item._id.toString();
      if (seen.has(idStr)) return false;
      seen.add(idStr);
      return true;
    });

    unique.sort((a, b) => b.createdAt - a.createdAt);
    return unique;
  },
});

// Query: Get unread count for badges
export const getUnreadCount = query({
  args: {
    userId: v.string(),
  },
  handler: async (ctx, args) => {
    const list = await ctx.db
      .query("notifications")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .filter((q) => q.eq(q.field("isRead"), false))
      .collect();

    return list.length;
  },
});

// Query: Get all notifications for admin console
export const getAllNotificationsForAdmin = query({
  args: {},
  handler: async (ctx) => {
    const all = await ctx.db.query("notifications").collect();
    all.sort((a, b) => b.createdAt - a.createdAt);
    return all;
  },
});

// Mutation: Create a notification
export const createNotification = mutation({
  args: {
    userId: v.optional(v.string()), // null = broadcast/all admins
    type: v.string(), // "order_update" | "low_stock" | "new_product" | "offer" | "system"
    title: v.string(),
    message: v.string(),
    link: v.optional(v.string()),
    image: v.optional(v.string()),
  },
  handler: async (ctx, args) => {
    return await ctx.db.insert("notifications", {
      userId: args.userId,
      type: args.type,
      title: args.title,
      message: args.message,
      link: args.link,
      image: args.image,
      isRead: false,
      createdAt: Date.now(),
    });
  },
});

// Mutation: Mark single notification as read
export const markAsRead = mutation({
  args: {
    id: v.id("notifications"),
  },
  handler: async (ctx, args) => {
    await ctx.db.patch(args.id, { isRead: true });
    return { success: true };
  },
});

// Mutation: Mark all notifications as read for a user
export const markAllAsRead = mutation({
  args: {
    userId: v.string(),
  },
  handler: async (ctx, args) => {
    const notifications = await ctx.db
      .query("notifications")
      .withIndex("by_user", (q) => q.eq("userId", args.userId))
      .filter((q) => q.eq(q.field("isRead"), false))
      .collect();

    for (const n of notifications) {
      await ctx.db.patch(n._id, { isRead: true });
    }

    return { updated: notifications.length };
  },
});

// Mutation: Delete notification
export const deleteNotification = mutation({
  args: {
    id: v.id("notifications"),
  },
  handler: async (ctx, args) => {
    await ctx.db.delete(args.id);
    return { success: true };
  },
});
