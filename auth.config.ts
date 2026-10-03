// Convex Auth configuration
export default {
  providers: [
    {
      domain: process.env.CONVEX_SITE_URL || "https://shopkart-auth.convex.cloud",
      applicationID: "shopkart-web",
    },
  ],
};
