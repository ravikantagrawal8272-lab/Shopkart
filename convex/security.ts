import { mutation, query } from "./_generated/server";
import { v } from "convex/values";

/**
 * Convex Security, Rate Limiting & Input Validation Utilities
 */

// Helper to check Rate Limiting (max 5 orders/minute per user)
export async function checkRateLimit(ctx: any, key: string, limit: number = 5, windowMs: number = 60000): Promise<boolean> {
  const now = Date.now();
  const entry = await ctx.db
    .query("rateLimits")
    .filter((q: any) => q.eq(q.field("key"), key))
    .first();

  if (!entry) {
    await ctx.db.insert("rateLimits", {
      key,
      count: 1,
      windowStart: now,
    });
    return true;
  }

  if (now - entry.windowStart > windowMs) {
    await ctx.db.patch(entry._id, {
      count: 1,
      windowStart: now,
    });
    return true;
  }

  if (entry.count >= limit) {
    throw new Error("Rate limit exceeded. Please wait a moment before trying again.");
  }

  await ctx.db.patch(entry._id, {
    count: entry.count + 1,
  });
  return true;
}

// Input sanitization helpers
export function sanitizeText(text: string): string {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;")
    .trim();
}

export function validateEmail(email: string): boolean {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email.toLowerCase());
}

export function validatePhone(phone: string): boolean {
  const cleaned = phone.replace(/\D/g, "");
  return cleaned.length >= 10;
}
