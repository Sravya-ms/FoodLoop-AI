package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.UserEntity
import com.example.data.model.DonationStatus
import com.example.data.model.RequestStatus
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.StatCard
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsState()
    val donations by viewModel.allDonations.collectAsState()
    val requests by viewModel.allRequests.collectAsState()

    val donors = users.filter { it.role == UserRole.DONOR }
    val verifiedDonors = donors.count { it.verificationStatus == VerificationStatus.VERIFIED }
    val consumers = users.filter { it.role == UserRole.CONSUMER }
    val verifiedConsumers = consumers.count { it.verificationStatus == VerificationStatus.VERIFIED }
    val pendingVerifications = users.count { it.verificationStatus == VerificationStatus.PENDING }
    val pendingRequests = requests.count { it.status == RequestStatus.PENDING }

    val todayPlatesAvailable = donations
        .filter { it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED }
        .sumOf { it.remainingPlates }

    val completedRedistributions = requests.count { it.status == RequestStatus.COMPLETED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("District Coordination Hub", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Admin Control Center", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_VERIFICATION) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Queue ($pendingVerifications)")
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Donors", "${donors.size}", "$verifiedDonors Verified", Icons.Default.CorporateFare, iconTint = FoodLoopSkyPrimary, modifier = Modifier.weight(1f))
                    StatCard("Recipients", "${consumers.size}", "$verifiedConsumers Verified", Icons.Default.Elderly, iconTint = FoodLoopGreenPrimary, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Plates Available", "$todayPlatesAvailable", "Active in district", Icons.Default.SoupKitchen, iconTint = FoodLoopAmberPrimary, modifier = Modifier.weight(1f))
                    StatCard("Redistributions", "$completedRedistributions", "Verified receipts", Icons.Default.CheckCircle, iconTint = FoodLoopGreenDark, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Pending Verification", "$pendingVerifications", "Review queue", Icons.Default.HourglassTop, iconTint = Color(0xFFDC2626), modifier = Modifier.weight(1f))
                    StatCard("Active Requests", "$pendingRequests", "Awaiting donor", Icons.Default.PendingActions, iconTint = FoodLoopAmberPrimary, modifier = Modifier.weight(1f))
                }
            }
        }

        // Analytical Visualizations
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Donations Over Time (Weekly Benchmark)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdminBarChart(
                        data = listOf(
                            Pair("Mon", 320),
                            Pair("Tue", 450),
                            Pair("Wed", 600),
                            Pair("Thu", 520),
                            Pair("Fri", 710),
                            Pair("Sat", 480),
                            Pair("Sun", 340)
                        ),
                        maxVal = 800
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Meal Period Distribution",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AdminPeriodBreakdown(
                        breakfast = 18,
                        lunch = 54,
                        evening = 8,
                        dinner = 20
                    )
                }
            }
        }
    }
}

@Composable
fun AdminVerificationScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsState()
    val pendingUsers = remember(users) {
        users.filter { it.verificationStatus == VerificationStatus.PENDING }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: All Organizations

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Organization Verification Review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Strict document and background check before granting platform privileges", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Pending Review (${pendingUsers.size})") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("All Organizations (${users.size})") }
                        )
                    }
                }
            }
        }

        val displayList = if (selectedTab == 0) pendingUsers else users

        if (displayList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No organizations in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(displayList) { user ->
                AdminUserVerificationCard(
                    user = user,
                    onApprove = { viewModel.adminVerifyUser(user.id, VerificationStatus.VERIFIED, "Official documentation approved by administration.") },
                    onReject = { viewModel.adminVerifyUser(user.id, VerificationStatus.REJECTED, "Registration credentials failed verification.") },
                    onRequestClarification = { viewModel.adminVerifyUser(user.id, VerificationStatus.PENDING, "Clarification requested: Please provide renewed trust certificate.") }
                )
            }
        }
    }
}

@Composable
private fun AdminUserVerificationCard(
    user: UserEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRequestClarification: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${user.role.name} • ${user.donorType?.displayName ?: user.consumerType?.displayName ?: "Participant"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                VerificationBadge(status = user.verificationStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Email: ${user.email} • Phone: ${user.phone}", style = MaterialTheme.typography.bodySmall)
            Text("Address: ${user.address}, ${user.city}", style = MaterialTheme.typography.bodySmall)

            if (user.numberOfResidents != null) {
                Text("Residents / Beneficiaries: ${user.numberOfResidents} people", fontWeight = FontWeight.SemiBold, color = FoodLoopGreenDark)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = FoodLoopSkyPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Document: ${user.documentName ?: "Govt_Reg_Cert_2026.pdf"}", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (user.verificationNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Notes: ${user.verificationNotes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = onRequestClarification,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text("Clarification", fontSize = 11.sp)
                }
                Button(
                    onClick = onApprove,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminBarChart(data: List<Pair<String, Int>>, maxVal: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { pair ->
            val fraction = (pair.second.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
            ) {
                Text("${pair.second}", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(FoodLoopGreenPrimary)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(pair.first, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminPeriodBreakdown(
    breakfast: Int,
    lunch: Int,
    evening: Int,
    dinner: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodBar("Lunch Meals (Peak Surplus)", lunch, FoodLoopGreenPrimary)
        PeriodBar("Dinner Meals", dinner, FoodLoopAmberPrimary)
        PeriodBar("Breakfast Repurposed", breakfast, FoodLoopSkyPrimary)
        PeriodBar("Evening Snacks", evening, Color(0xFF8B5CF6))
    }
}

@Composable
private fun PeriodBar(label: String, percent: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text("$percent%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
