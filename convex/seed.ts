import { mutation } from "./_generated/server";

export const seedDatabase = mutation({
  handler: async (ctx) => {
    const existing = await ctx.db.query("products").first();
    if (existing) {
      return { message: "Database already seeded" };
    }

    // 1. Seed Demo User
    const demoUserId = await ctx.db.insert("users", {
      name: "Ritu Agrawal",
      email: "ritu@shopkart.com",
      password_hash: "pbkdf2_sha256_mock_hash_982734",
      phone: "+91 9876543210",
      avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
      role: "user",
      addresses: [
        {
          id: "addr_main",
          fullName: "Ritu Agrawal",
          street: "Flat 402, Skyline Residency, Tech Hub Road",
          city: "Bengaluru",
          state: "Karnataka",
          pincode: "560001",
          isDefault: true,
        },
      ],
      createdAt: Date.now(),
    });

    // 2. Seed Default Seller
    const sellerId = await ctx.db.insert("sellers", {
      userId: demoUserId,
      shopName: "ShopKart Official Electronics Hub",
      gst: "29AAAAA0000A1Z5",
      products: [],
      rating: 4.9,
      totalSales: 45000,
      createdAt: Date.now(),
    });

    // 3. Seed Products
    const productsData = [
      {
        title: "Apple iPhone 16 Pro Max (256 GB) - Natural Titanium",
        description: "iPhone 16 Pro Max. Forged in grade 5 titanium and featuring the groundbreaking A18 Pro chip, Camera Control button, and 48MP Fusion camera with 5x telephoto optical zoom.",
        price: 134900,
        originalPrice: 144900,
        discount: 7,
        category: "mobiles",
        brand: "Apple",
        images: [
          "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 14,
        rating: 4.9,
        reviewsCount: 8450,
        sellerId: sellerId,
        highlights: [
          "A18 Pro Chip with 6-core GPU for unprecedented AI speed",
          "Camera Control button for instant photo & video tuning",
          "48MP Fusion Camera with 5x Telephoto optical zoom",
          "Grade 5 Titanium design with Ceramic Shield front glass",
          "Up to 33 hours video playback with ultra-efficient battery",
        ],
        variants: ["256 GB", "512 GB", "1 TB"],
        colors: ["Natural Titanium", "Desert Titanium", "White Titanium", "Black Titanium"],
        tag: "Flagship Choice",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "Sony WH-1000XM5 Wireless Noise Canceling Headphones",
        description: "The Sony WH-1000XM5 headphones rewrite the rules for distraction-free listening. Two processors control 8 microphones for industry-leading active noise cancellation.",
        price: 26990,
        originalPrice: 34990,
        discount: 23,
        category: "electronics",
        brand: "Sony",
        images: [
          "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 22,
        rating: 4.8,
        reviewsCount: 14210,
        sellerId: sellerId,
        highlights: [
          "Industry-leading Active Noise Cancellation with 8 microphones",
          "Magnificent sound engineered with High-Resolution Audio",
          "Crystal clear hands-free calling with 4 beamforming mics",
          "Up to 30-hour battery life with quick 3-min charge",
        ],
        variants: ["Standard Edition", "Carry Case Bundle"],
        colors: ["Silver White", "Midnight Black", "Smoky Pink"],
        tag: "Bestseller",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "Samsung Galaxy S24 Ultra 5G (Titanium Gray, 12GB RAM, 256GB)",
        description: "Welcome to the era of mobile AI. With Galaxy S24 Ultra in your hands, unleash whole new levels of creativity with 200MP camera and Snapdragon 8 Gen 3.",
        price: 119999,
        originalPrice: 134999,
        discount: 11,
        category: "mobiles",
        brand: "Samsung",
        images: [
          "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 9,
        rating: 4.8,
        reviewsCount: 9600,
        sellerId: sellerId,
        highlights: [
          "Galaxy AI: Circle to Search, Live Translate, Note Assist",
          "200MP Main Camera with AI Zoom Engine",
          "Integrated S-Pen stylus with ultra-low latency",
          "Corning Gorilla Armor anti-reflective glass",
        ],
        variants: ["256 GB", "512 GB", "1 TB"],
        colors: ["Titanium Gray", "Titanium Violet", "Titanium Yellow", "Titanium Black"],
        tag: "Top Rated",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "Apple MacBook Air 15-inch M3 Chip (16GB Unified Memory, 512GB SSD)",
        description: "The 15-inch MacBook Air is impossibly thin with liquid retina display. Supercharged by M3 chip with up to 18 hours of battery life.",
        price: 144900,
        originalPrice: 154900,
        discount: 6,
        category: "electronics",
        brand: "Apple",
        images: [
          "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 12,
        rating: 4.9,
        reviewsCount: 5230,
        sellerId: sellerId,
        highlights: [
          "Supercharged by M3 with 8-core CPU and 10-core GPU",
          "Strikingly thin and fast with 18 hours battery life",
          "15.3-inch Liquid Retina display with 500 nits brightness",
        ],
        variants: ["16GB / 512GB", "24GB / 512GB", "24GB / 1TB"],
        colors: ["Starlight", "Midnight", "Space Gray", "Silver"],
        tag: "Editor's Choice",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "Nike Air Max 270 React Premium Sneakers - Triple White",
        description: "The Nike Air Max 270 React merges the bold look of Air Max with the ultra-plush cushioning of Nike React foam for pure street elegance.",
        price: 11495,
        originalPrice: 14995,
        discount: 23,
        category: "fashion",
        brand: "Nike",
        images: [
          "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 35,
        rating: 4.7,
        reviewsCount: 6840,
        sellerId: sellerId,
        highlights: [
          "Nike's biggest heel Air unit yet for maximum bounce",
          "Nike React technology delivers an extremely smooth ride",
          "Lightweight layered no-sew breathable upper",
        ],
        variants: ["UK 7", "UK 8", "UK 9", "UK 10", "UK 11"],
        colors: ["Pure White", "Summit White / Electric Orange", "Triple Black"],
        tag: "Trending Fashion",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "Dyson V15 Detect Extra Cordless Vacuum Cleaner",
        description: "Dyson's most powerful, intelligent cordless vacuum. Laser Slim Fluffy cleaner head illuminates invisible particles. LCD screen shows real-time proof of deep clean.",
        price: 59900,
        originalPrice: 69900,
        discount: 14,
        category: "appliances",
        brand: "Dyson",
        images: [
          "https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 6,
        rating: 4.8,
        reviewsCount: 3120,
        sellerId: sellerId,
        highlights: [
          "Laser reveals microscopic dust on hard floors",
          "Piezo sensor continuously counts dust particles",
          "Dyson Hyperdymium motor spins at 125,000rpm",
        ],
        variants: ["Standard Kit", "Complete Pro Suite"],
        colors: ["Yellow / Nickel", "Prussian Blue / Copper"],
        tag: "Premium Clean",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
      {
        title: "De'Longhi Magnifica S Automatic Espresso Coffee Machine",
        description: "Enjoy barista-style fresh bean coffee at home with 15 bar pump pressure, conical steel grinder and manual milk froth system.",
        price: 48999,
        originalPrice: 62990,
        discount: 22,
        category: "appliances",
        brand: "De'Longhi",
        images: [
          "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=800&q=80",
          "https://images.unsplash.com/photo-1509785307050-d4066910ec1e?auto=format&fit=crop&w=800&q=80",
        ],
        stock: 0, // Testing Out of Stock requirement
        rating: 4.9,
        reviewsCount: 2890,
        sellerId: sellerId,
        highlights: [
          "Bean-to-cup fresh grinding with 13 settings",
          "Traditional milk frother for velvety cappuccino",
          "One-touch customizable aroma and temperature",
        ],
        variants: ["Machine Only", "Barista Starter Pack"],
        colors: ["Pearl White", "Silver Black"],
        tag: "Barista Choice",
        isShopKartAssured: true,
        oneDayDelivery: true,
      },
    ];

    for (const p of productsData) {
      await ctx.db.insert("products", {
        ...p,
        createdAt: Date.now(),
      });
    }

    return { seededCount: productsData.length };
  },
});
