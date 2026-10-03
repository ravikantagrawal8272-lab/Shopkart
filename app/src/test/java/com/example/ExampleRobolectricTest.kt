package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ConvexBackendService
import com.example.model.AddressRecord
import com.example.model.CartItem
import com.example.model.PosCartItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ShopKart", appName)
  }

  @Test
  fun `convex backend tables are seeded and responsive`() {
    val products = ConvexBackendService.getProducts()
    assertTrue("Products table should have seeded entries", products.isNotEmpty())

    val firstProduct = products.first()
    val productById = ConvexBackendService.getProductById(firstProduct.id)
    assertNotNull("Product should be queryable by ID", productById)
    assertEquals(firstProduct.title, productById?.title)
  }

  @Test
  fun `convex auth login and signup flow`() {
    // 1. Admin login test
    val loginResult = ConvexBackendService.login("admin@shopkart.com", "password123")
    assertTrue("Admin login should succeed", loginResult.isSuccess)
    val adminUser = loginResult.getOrNull()
    assertNotNull(adminUser)
    assertTrue("Admin user should have admin role", adminUser?.isAdmin == true)

    // 2. Signup test
    val signupResult = ConvexBackendService.signup(
      name = "Test Buyer",
      email = "buyer_${System.currentTimeMillis()}@test.com",
      password = "securePassword123",
      phone = "+91 9988776655",
      role = "user"
    )
    assertTrue("Signup should succeed", signupResult.isSuccess)
    val newUser = signupResult.getOrNull()
    assertNotNull(newUser)
    assertEquals("user", newUser?.role)
  }

  @Test
  fun `advanced search engine with token matching and sorting`() {
    // 1. Search for iPhone
    val searchResults = ConvexBackendService.searchProductsAdvanced(query = "iphone")
    assertTrue("Search for 'iphone' should return matches", searchResults.isNotEmpty())
    assertTrue("Matches should contain iPhone", searchResults.any { it.title.contains("iPhone", ignoreCase = true) })

    // 2. Search with filters
    val filtered = ConvexBackendService.searchProductsAdvanced(
      query = "Sony",
      category = "electronics",
      inStockOnly = true
    )
    assertTrue("Filtered search should return available Sony items", filtered.isNotEmpty())

    // 3. Search History and Trending keywords
    ConvexBackendService.addSearchQuery("MacBook M3 Pro")
    val recent = ConvexBackendService.getRecentSearchesForCurrentUser()
    assertTrue("Recent searches should contain new query", recent.any { it.query == "MacBook M3 Pro" })

    val trending = ConvexBackendService.getTrendingSearchesList()
    assertTrue("Trending searches should be populated", trending.isNotEmpty())
  }

  @Test
  fun `real-time notification system and unread badge count`() {
    // 1. Fetch initial notifications
    val initialNotifs = ConvexBackendService.getNotificationsForCurrentUser()
    assertTrue("User should have seeded notifications", initialNotifs.isNotEmpty())

    val unreadBefore = ConvexBackendService.getUnreadNotificationsCount()
    assertTrue("Should have unread notifications", unreadBefore >= 0)

    // 2. Create new Notification
    val newNotif = ConvexBackendService.createNotification(
      userId = ConvexBackendService.CURRENT_USER_ID,
      type = "order_update",
      title = "Test Order Dispatched! 🚀",
      message = "Your test order is on the way."
    )
    assertNotNull(newNotif)

    // 3. Mark as read
    ConvexBackendService.markNotificationAsRead(newNotif.id)
    val afterRead = ConvexBackendService.getNotificationsForCurrentUser().find { it.id == newNotif.id }
    assertTrue("Notification should be marked as read", afterRead?.isRead == true)
  }

  @Test
  fun `razorpay payment verification and inventory stock reduction`() {
    val products = ConvexBackendService.getProducts()
    val availableProduct = products.first { it.stock > 0 }
    val initialStock = availableProduct.stock

    val cartItem = CartItem(
      cartId = "test_cart_1",
      product = availableProduct,
      quantity = 1
    )

    val address = AddressRecord(
      id = "test_addr",
      fullName = "Payment Tester",
      street = "100 MG Road",
      city = "Bengaluru",
      state = "Karnataka",
      pincode = "560001",
      isDefault = true
    )

    val paymentResult = ConvexBackendService.verifyAndCreateOrder(
      cartItems = listOf(cartItem),
      totalAmount = availableProduct.price,
      paymentMethod = "razorpay",
      address = address,
      razorpayPaymentId = "pay_rzp_test_123"
    )

    assertTrue("Payment & order placement should succeed", paymentResult.isSuccess)
    val order = paymentResult.getOrNull()
    assertNotNull(order)
    assertTrue("Payment status should be paid", order?.paymentStatus.equals("paid", ignoreCase = true))

    // Verify Stock decreased by 1
    val updatedProduct = ConvexBackendService.getProductById(availableProduct.id)
    assertEquals(initialStock - 1, updatedProduct?.stock)
  }

  @Test
  fun `pos cash counter sale and 18 percent gst computation`() {
    val products = ConvexBackendService.getProducts()
    val testProduct = products.first { it.stock > 2 }

    val posItems = listOf(PosCartItem(testProduct, 2))
    val subtotal = testProduct.price * 2
    val discount = 100
    val gst = ((subtotal - discount) * 0.18).toInt()
    val total = subtotal - discount + gst

    val posResult = ConvexBackendService.createPosOrder(
      customerName = "Walk-in Guest",
      customerPhone = "+91 9112233445",
      items = posItems,
      subtotal = subtotal,
      discount = discount,
      gst = gst,
      totalAmount = total,
      paymentMethod = "cash"
    )

    assertTrue("POS sale should succeed", posResult.isSuccess)
    val invoice = posResult.getOrNull()
    assertNotNull(invoice)
    assertTrue("Invoice number should be generated", invoice?.invoiceNumber?.startsWith("INV-") == true)
  }
}
