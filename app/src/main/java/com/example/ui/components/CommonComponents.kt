package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.model.DonationStatus
import com.example.data.model.RequestStatus
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.localization.SupportedLanguage
import com.example.localization.LanguageCatalog
import androidx.compose.material.icons.filled.Language

@Composable
fun FoodLoopTopBar(
    title: String,
    currentUser: UserEntity?,
    unreadNotifCount: Int,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit,
    onRoleSwitchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onSignOutClick: () -> Unit = {},
    currentLanguage: SupportedLanguage = LanguageCatalog.DEFAULT_LANGUAGE,
    onLanguageClick: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canNavigateBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FoodLoopGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "FoodLoop Logo",
                            tint = FoodLoopGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                    if (currentUser != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${currentUser.name} (${currentUser.role.name})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Language Switcher Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clickable { onLanguageClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = currentLanguage.flagEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = currentLanguage.code.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // AI Assistant Quick Access
                IconButton(onClick = onAiAssistantClick) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = FoodLoopAmberPrimary
                    )
                }

                // Notifications with Badge
                IconButton(onClick = onNotificationsClick) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = FoodLoopAmberPrimary,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotifCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Sign Out Button when logged in
                if (currentUser != null) {
                    IconButton(onClick = onSignOutClick) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out to Home",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }

                // Role Switcher Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FoodLoopGreenContainer,
                    modifier = Modifier.clickable { onRoleSwitchClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            tint = FoodLoopGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Role",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = FoodLoopGreenDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLoopBottomNavigation(
    currentScreen: AppScreen,
    userRole: UserRole?,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        // Public Home only available when signed out
        if (userRole == null) {
            NavigationBarItem(
                selected = currentScreen == AppScreen.LANDING,
                onClick = { onNavigate(AppScreen.LANDING) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = currentScreen == AppScreen.LOGIN,
                onClick = { onNavigate(AppScreen.LOGIN) },
                icon = { Icon(Icons.Default.MeetingRoom, contentDescription = "Portals") },
                label = { Text("Portals") }
            )
        }

        when (userRole) {
            UserRole.DONOR -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DONOR_DASHBOARD,
                    onClick = { onNavigate(AppScreen.DONOR_DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DONATE_FOOD,
                    onClick = { onNavigate(AppScreen.DONATE_FOOD) },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Donate") },
                    label = { Text("Donate") }
                )
            }
            UserRole.CONSUMER -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.CONSUMER_DASHBOARD,
                    onClick = { onNavigate(AppScreen.CONSUMER_DASHBOARD) },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Surplus Food") },
                    label = { Text("Find Food") }
                )
            }
            UserRole.ADMIN -> {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN_DASHBOARD,
                    onClick = { onNavigate(AppScreen.ADMIN_DASHBOARD) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin") }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN_VERIFICATION,
                    onClick = { onNavigate(AppScreen.ADMIN_VERIFICATION) },
                    icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Verify") },
                    label = { Text("Verify") }
                )
            }
            null -> {}
        }

        NavigationBarItem(
            selected = currentScreen == AppScreen.AI_ASSISTANT,
            onClick = { onNavigate(AppScreen.AI_ASSISTANT) },
            icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Assistant") },
            label = { Text("AI Copilot") }
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.IMPACT_DASHBOARD,
            onClick = { onNavigate(AppScreen.IMPACT_DASHBOARD) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Impact") },
            label = { Text("Impact") }
        )
    }
}

@Composable
fun VerificationBadge(status: VerificationStatus, modifier: Modifier = Modifier) {
    val (bg, textColor, icon, label) = when (status) {
        VerificationStatus.VERIFIED -> Quad(
            FoodLoopGreenContainer,
            StatusVerifiedGreen,
            Icons.Default.CheckCircle,
            "✓ Verified"
        )
        VerificationStatus.PENDING -> Quad(
            FoodLoopAmberContainer,
            StatusPendingAmber,
            Icons.Default.HourglassTop,
            "Verification Pending"
        )
        VerificationStatus.REJECTED -> Quad(
            Color(0xFFFEE2E2),
            StatusRejectedRed,
            Icons.Default.Cancel,
            "Rejected"
        )
        VerificationStatus.SUSPENDED -> Quad(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            Icons.Default.Block,
            "Suspended"
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun DonationStatusBadge(status: DonationStatus) {
    val (bg, textCol) = when (status) {
        DonationStatus.PUBLISHED -> Pair(FoodLoopGreenContainer, FoodLoopGreenDark)
        DonationStatus.PARTIALLY_RESERVED -> Pair(FoodLoopAmberContainer, FoodLoopOnAmberContainer)
        DonationStatus.FULLY_RESERVED -> Pair(Color(0xFFE0E7FF), Color(0xFF3730A3))
        DonationStatus.COMPLETED -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        DonationStatus.EXPIRED -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
        DonationStatus.CANCELLED -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
        DonationStatus.DRAFT -> Pair(Color(0xFFF3F4F6), Color(0xFF4B5563))
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textCol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RequestStatusBadge(status: RequestStatus) {
    val (bg, textCol) = when (status) {
        RequestStatus.PENDING -> Pair(FoodLoopAmberContainer, FoodLoopOnAmberContainer)
        RequestStatus.ACCEPTED -> Pair(FoodLoopGreenContainer, FoodLoopGreenDark)
        RequestStatus.READY_FOR_PICKUP -> Pair(FoodLoopSkyContainer, FoodLoopOnSkyContainer)
        RequestStatus.IN_TRANSIT -> Pair(Color(0xFFDDD6FE), Color(0xFF5B21B6))
        RequestStatus.RECEIVED -> Pair(Color(0xFFBBF7D0), Color(0xFF14532D))
        RequestStatus.COMPLETED -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        RequestStatus.DECLINED -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
        RequestStatus.CANCELLED -> Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textCol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    iconTint: Color = FoodLoopGreenPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderTimelineTracker(currentStatus: RequestStatus) {
    val steps = listOf(
        Pair("REQUESTED", RequestStatus.PENDING),
        Pair("ACCEPTED", RequestStatus.ACCEPTED),
        Pair("READY", RequestStatus.READY_FOR_PICKUP),
        Pair("IN TRANSIT", RequestStatus.IN_TRANSIT),
        Pair("RECEIVED", RequestStatus.RECEIVED),
        Pair("COMPLETED", RequestStatus.COMPLETED)
    )

    val currentIdx = when (currentStatus) {
        RequestStatus.PENDING -> 0
        RequestStatus.ACCEPTED -> 1
        RequestStatus.READY_FOR_PICKUP -> 2
        RequestStatus.IN_TRANSIT -> 3
        RequestStatus.RECEIVED -> 4
        RequestStatus.COMPLETED -> 5
        RequestStatus.DECLINED, RequestStatus.CANCELLED -> -1
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Redistribution Lifecycle",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            steps.forEachIndexed { index, pair ->
                val isCompleted = currentIdx > index
                val isCurrent = currentIdx == index
                val isUpcoming = currentIdx < index

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Indicator Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> FoodLoopGreenPrimary
                                        isCurrent -> FoodLoopAmberPrimary
                                        else -> Color(0xFFE2E8F0)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(
                                        if (isCompleted) FoodLoopGreenPrimary else Color(0xFFE2E8F0)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pair.first,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isCompleted -> FoodLoopGreenDark
                                isCurrent -> FoodLoopAmberPrimary
                                else -> FoodLoopTextMuted
                            }
                        )
                        Text(
                            text = when (pair.second) {
                                RequestStatus.PENDING -> "Official request submitted by verified recipient"
                                RequestStatus.ACCEPTED -> "Approved by donor, surplus reserved"
                                RequestStatus.READY_FOR_PICKUP -> "Hygienically packaged & prepared at donor location"
                                RequestStatus.IN_TRANSIT -> "Collection team / vehicle on route"
                                RequestStatus.RECEIVED -> "Delivery reached recipient home safely"
                                RequestStatus.COMPLETED -> "Confirmed & logged in district impact ledger"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

// Role Switcher Dialog for convenient Hackathon evaluation
@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit,
    onSignOut: () -> Unit = {},
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwitchAccount,
                    contentDescription = null,
                    tint = FoodLoopGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Prototype Role", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (currentRole != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FoodLoopAmberContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Active ${currentRole.displayName} session is active. To open other accounts or return to the public home, you must sign out first.",
                                style = MaterialTheme.typography.labelSmall,
                                color = FoodLoopOnAmberContainer
                            )
                        }
                    }
                } else {
                    Text(
                        "Select active perspective to test end-to-end food redistribution flows with full privacy isolation:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                RoleSelectCard(
                    role = UserRole.DONOR,
                    title = "ABC Institution (Donor)",
                    subtitle = "Engineering College • Narasaraopet • Verified",
                    icon = Icons.Default.School,
                    isSelected = currentRole == UserRole.DONOR,
                    onClick = {
                        if (currentRole == null || currentRole == UserRole.DONOR) {
                            onRoleSelected(UserRole.DONOR)
                            onDismiss()
                        } else {
                            onDismiss()
                            onSignOut()
                        }
                    }
                )

                RoleSelectCard(
                    role = UserRole.CONSUMER,
                    title = "ABC Old Age Home (Recipient)",
                    subtitle = "85 Senior Citizens • Narasaraopet • Verified",
                    icon = Icons.Default.Elderly,
                    isSelected = currentRole == UserRole.CONSUMER,
                    onClick = {
                        if (currentRole == null || currentRole == UserRole.CONSUMER) {
                            onRoleSelected(UserRole.CONSUMER)
                            onDismiss()
                        } else {
                            onDismiss()
                            onSignOut()
                        }
                    }
                )

                RoleSelectCard(
                    role = UserRole.ADMIN,
                    title = "District Administrator",
                    subtitle = "Verifications • All Deliveries • Analytics",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = currentRole == UserRole.ADMIN,
                    onClick = {
                        if (currentRole == null || currentRole == UserRole.ADMIN) {
                            onRoleSelected(UserRole.ADMIN)
                            onDismiss()
                        } else {
                            onDismiss()
                            onSignOut()
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onSignOut()
            }) {
                Text("Sign Out to Portal Selection", color = Color(0xFFDC2626))
            }
        }
    )
}

@Composable
private fun RoleSelectCard(
    role: UserRole,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FoodLoopGreenContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FoodLoopGreenPrimary) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FoodLoopGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) FoodLoopGreenDark else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = FoodLoopGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
