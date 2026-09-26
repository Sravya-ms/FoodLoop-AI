package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEntity
import com.example.data.model.NotificationType
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel
import com.example.localization.LocalAppStrings
import com.example.localization.LanguageSelectionDialog

@Composable
fun NotificationsScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notification Center", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = {
                            viewModel.markAllNotificationsRead()
                        }) {
                            Text("Mark all as read")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FoodLoopGreenContainer.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopGreenPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🛡️ Privacy Isolated: Alerts for ${currentUser?.name ?: "Guest"} (${currentUser?.role?.displayName ?: ""}). Donors cannot view Care Home alerts, and Care Homes cannot view Donor alerts.",
                            style = MaterialTheme.typography.labelSmall,
                            color = FoodLoopGreenDark
                        )
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No notifications for your account.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(notifications) { notif ->
                NotificationItemCard(
                    notification = notif,
                    onClick = {
                        if (notif.relatedRequestId != null) {
                            viewModel.selectRequest(notif.relatedRequestId)
                        } else if (notif.relatedDonationId != null) {
                            viewModel.selectDonation(notif.relatedDonationId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else FoodLoopGreenContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            NotificationType.NEW_DONATION -> FoodLoopAmberContainer
                            NotificationType.REQUEST_ACCEPTED -> FoodLoopGreenContainer
                            NotificationType.IN_DELIVERY -> FoodLoopSkyContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.NEW_DONATION -> Icons.Default.SoupKitchen
                        NotificationType.REQUEST_ACCEPTED -> Icons.Default.CheckCircle
                        NotificationType.IN_DELIVERY -> Icons.Default.LocalShipping
                        NotificationType.URGENT_EXPIRY -> Icons.Default.Warning
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = when (notification.type) {
                        NotificationType.NEW_DONATION -> FoodLoopAmberPrimary
                        NotificationType.REQUEST_ACCEPTED -> FoodLoopGreenPrimary
                        NotificationType.IN_DELIVERY -> FoodLoopSkyPrimary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDonor = currentUser?.role == UserRole.DONOR

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (isDonor) FoodLoopAmberContainer else FoodLoopGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDonor) Icons.Default.School else Icons.Default.Elderly,
                            contentDescription = null,
                            tint = if (isDonor) FoodLoopAmberPrimary else FoodLoopGreenDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(currentUser?.name ?: "Organization", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(currentUser?.address ?: "Narasaraopet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(8.dp))

                    VerificationBadge(status = currentUser?.verificationStatus ?: VerificationStatus.VERIFIED)
                }
            }
        }

        // Profile Metrics
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Verified Organizational Record", fontWeight = FontWeight.Bold)

                    if (isDonor) {
                        ProfileInfoRow("Donor Type", currentUser?.donorType?.displayName ?: "Institutional")
                        ProfileInfoRow("Joined", "2026")
                        ProfileInfoRow("Food Donations", "27")
                        ProfileInfoRow("Total Plates Donated", "3,420")
                        ProfileInfoRow("Successful Redistributions", "25")
                    } else {
                        ProfileInfoRow("Organization Type", currentUser?.consumerType?.displayName ?: "Care Home")
                        ProfileInfoRow("Residents Headcount", "${currentUser?.numberOfResidents ?: 85} People")
                        ProfileInfoRow("Food Requests", "42")
                        ProfileInfoRow("Successful Receipts", "39")
                    }

                    ProfileInfoRow("Email", currentUser?.email ?: "")
                    ProfileInfoRow("Phone", currentUser?.phone ?: "")
                    ProfileInfoRow("Verification Document", currentUser?.documentName ?: "Govt_Reg_Cert_2026.pdf")

                    Spacer(modifier = Modifier.height(4.dp))

                    // Privacy Shield Notice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDonor) FoodLoopAmberContainer.copy(alpha = 0.5f) else FoodLoopGreenContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDonor) FoodLoopAmberPrimary.copy(alpha = 0.3f) else FoodLoopGreenPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDonor) Icons.Default.Lock else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isDonor) FoodLoopAmberPrimary else FoodLoopGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDonor)
                                    "🔒 Donor Privacy Active: Your institution records and food redistribution metrics are strictly isolated from other food donors."
                                else
                                    "🛡️ Beneficiary Privacy Active: Resident headcounts and internal care home records are confidential and encrypted.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDonor) FoodLoopOnAmberContainer else FoodLoopGreenDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Language Preference Card
        item {
            var showLanguageDialog by remember { mutableStateOf(false) }
            val currentLang by viewModel.currentLanguage.collectAsState()
            val strings = LocalAppStrings.current

            if (showLanguageDialog) {
                LanguageSelectionDialog(
                    currentLanguageCode = currentLang.code,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    onDismiss = { showLanguageDialog = false }
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopGreenContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(strings.language, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${currentLang.flagEmoji} ${currentLang.nativeName} (${currentLang.englishName})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        text = "Change",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FoodLoopGreenPrimary
                    )
                }
            }
        }

        // Quick Actions & Sign Out
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isDonor) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.DONATE_FOOD) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Donate Food")
                        }
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.DONOR_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Donation History")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.CONSUMER_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Find Food")
                        }
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.CONSUMER_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Request History")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.logout() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out & Return to Home", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
