package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartOrange
import com.example.ui.theme.ShopKartOrangeLight
import com.example.ui.theme.SurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ShopKartUiState
import com.example.viewmodel.ShopKartViewModel

@Composable
fun AuthScreen(
    viewModel: ShopKartViewModel,
    uiState: ShopKartUiState,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Signup

    // Login Form Fields
    var loginEmail by remember { mutableStateOf("admin@shopkart.com") }
    var loginPassword by remember { mutableStateOf("password123") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Signup Form Fields
    var signupName by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var signupRole by remember { mutableStateOf("user") } // "user" | "seller" | "admin"
    var signupPasswordVisible by remember { mutableStateOf(false) }

    // Dialogs
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var otpInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Sticky Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ShopKartOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ShopKart Auth",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Brand Presentation Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp)),
                color = PureWhite
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ShopKartOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Secure Auth",
                            tint = ShopKartOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ultra-Secure ShopKart Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Powered by Convex Auth • Instant Verification",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auth Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp)),
                color = PureWhite
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = PureWhite,
                        contentColor = ShopKartOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = ShopKartOrange,
                                height = 3.dp
                            )
                        },
                        divider = { HorizontalDivider(color = BorderLight) }
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = {
                                Text(
                                    "Log In",
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Text(
                                    "Sign Up",
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (uiState.authErrorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = uiState.authErrorMessage,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (selectedTabIndex == 0) {
                        // ================= LOGIN FORM =================
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                resetEmail = loginEmail
                                showForgotDialog = true
                            }) {
                                Text("Forgot Password?", color = ShopKartOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { viewModel.login(loginEmail, loginPassword) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ShopKartOrange,
                                contentColor = PureWhite
                            ),
                            enabled = !uiState.isAuthLoading
                        ) {
                            if (uiState.isAuthLoading) {
                                CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Sign In with ShopKart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // ================= SIGNUP FORM =================
                        OutlinedTextField(
                            value = signupName,
                            onValueChange = { signupName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupEmail,
                            onValueChange = { signupEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = signupPhone,
                                onValueChange = { signupPhone = it },
                                label = { Text("Phone Number") },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShopKartOrange,
                                    unfocusedBorderColor = BorderMedium
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.triggerOtpFlow(signupPhone) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text("Verify OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShopKartOrange)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupPassword,
                            onValueChange = { signupPassword = it },
                            label = { Text("Create Password") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary) },
                            trailingIcon = {
                                IconButton(onClick = { signupPasswordVisible = !signupPasswordVisible }) {
                                    Icon(
                                        imageVector = if (signupPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (signupPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShopKartOrange,
                                unfocusedBorderColor = BorderMedium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Role Selector
                        Text("Select Account Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("user" to "Customer", "seller" to "Seller", "admin" to "Admin").forEach { (roleKey, label) ->
                                val isSelected = signupRole == roleKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ShopKartOrangeLight else SurfaceBg)
                                        .border(1.5.dp, if (isSelected) ShopKartOrange else BorderMedium, RoundedCornerShape(8.dp))
                                        .clickable { signupRole = roleKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) ShopKartOrange else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (signupName.isNotBlank() && signupEmail.isNotBlank()) {
                                    viewModel.signup(signupName, signupEmail, signupPassword.ifBlank { "password123" }, signupPhone, signupRole)
                                } else {
                                    viewModel.showSnackbar("Please fill in Name and Email")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ShopKartOrange,
                                contentColor = PureWhite
                            ),
                            enabled = !uiState.isAuthLoading
                        ) {
                            if (uiState.isAuthLoading) {
                                CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Create $signupRole Account", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                        Text(
                            text = " OR CONTINUE WITH ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google OAuth Button
                    OutlinedButton(
                        onClick = { viewModel.loginWithGoogle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("google_login_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderMedium),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PureWhite,
                            contentColor = TextPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sign in with Google", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fast Demo Profile Switchers
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceBg)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "⚡ Quick Demo Access:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.switchToAdminRole()
                                        viewModel.navigateBack()
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                ) {
                                    Text("Admin Login", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartOrange)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.switchToUserRole()
                                        viewModel.navigateBack()
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                ) {
                                    Text("User Login", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // OTP Verification Dialog
    if (uiState.showOtpDialog) {
        AlertDialog(
            onDismissRequest = { /* dismiss */ },
            title = { Text("Phone OTP Verification", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter 6-digit verification code sent to ${uiState.pendingOtpPhone.ifEmpty { "+91 9876543210" }}:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.length <= 6) otpInput = it },
                        label = { Text("OTP (Use 123456)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = viewModel.verifyOtp(otpInput)
                        if (ok) otpInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Text("Verify & Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.verifyOtp("123456") }) {
                    Text("Auto-fill 123456", color = ShopKartOrange)
                }
            }
        )
    }

    // Forgot Password Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter your email and set a new password:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isNotBlank() && newPassword.isNotBlank()) {
                            viewModel.resetPassword(resetEmail, newPassword)
                            showForgotDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartOrange)
                ) {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
