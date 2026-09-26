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
import com.example.data.local.DonationEntity
import com.example.data.local.FoodRequestEntity
import com.example.data.model.DonationStatus
import com.example.data.model.RequestStatus
import com.example.data.model.VerificationStatus
import com.example.ui.components.*
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun DonorDashboardScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allDonations by viewModel.allDonations.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()

    val myDonations = remember(allDonations, currentUser) {
        val uid = currentUser?.id ?: "donor_abc_inst"
        allDonations.filter { it.donorId == uid }
    }

    val activeDonations = remember(myDonations) {
        myDonations.filter {
            it.status == DonationStatus.PUBLISHED || it.status == DonationStatus.PARTIALLY_RESERVED
        }
    }

    val myRequests = remember(allRequests, currentUser) {
        val uid = currentUser?.id ?: "donor_abc_inst"
        allRequests.filter { it.donorId == uid }
    }

    val pendingRequests = remember(myRequests) {
        myRequests.filter { it.status == RequestStatus.PENDING }
    }

    val totalPlatesDonated = remember(myDonations) {
        myDonations.sumOf { it.totalPlates }
    }

    val successfulCount = remember(myDonations) {
        myDonations.count { it.status == DonationStatus.COMPLETED }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.DONATE_FOOD) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("+ Donate Food", fontWeight = FontWeight.Bold) },
                containerColor = FoodLoopAmberPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                Text(
                                    text = "Good Morning,",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentUser?.name ?: "ABC Institution",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            VerificationBadge(
                                status = currentUser?.verificationStatus ?: VerificationStatus.VERIFIED
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = FoodLoopGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUser?.city ?: "Narasaraopet"} • ${currentUser?.address ?: "Kotappakonda Road"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Donor Privacy Isolation Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopAmberContainer.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopAmberPrimary.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🔒 Donor Privacy Shield Active: Isolated kitchen records for ${currentUser?.name}. Invisible to other donors.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FoodLoopOnAmberContainer,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Stat Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Total Donations",
                            value = "${myDonations.size}",
                            subtitle = "Surplus announcements",
                            icon = Icons.Default.Inventory,
                            iconTint = FoodLoopSkyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Plates Donated",
                            value = "$totalPlatesDonated",
                            subtitle = "Meals preserved",
                            icon = Icons.Default.SoupKitchen,
                            iconTint = FoodLoopGreenPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Redistributions",
                            value = "$successfulCount",
                            subtitle = "Completed receipts",
                            icon = Icons.Default.CheckCircle,
                            iconTint = FoodLoopGreenDark,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Active Donations",
                            value = "${activeDonations.size}",
                            subtitle = "Open for requests",
                            icon = Icons.Default.HourglassTop,
                            iconTint = FoodLoopAmberPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Pending Inbound Requests for this donor
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Beneficiary Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (pendingRequests.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopAmberContainer
                        ) {
                            Text(
                                text = "${pendingRequests.size} Pending",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopOnAmberContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (pendingRequests.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No pending food requests awaiting review.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(pendingRequests) { request ->
                    DonorInboundRequestCard(
                        request = request,
                        onAccept = { viewModel.acceptRequest(request.id) },
                        onDecline = { viewModel.declineRequest(request.id) },
                        onTrack = { viewModel.selectRequest(request.id) },
                        onChat = { viewModel.openChatForRequest(request.id) }
                    )
                }
            }

            // Accepted & In-Coordination Requests Section
            val acceptedInCoordination = myRequests.filter {
                it.status == RequestStatus.ACCEPTED ||
                it.status == RequestStatus.READY_FOR_PICKUP ||
                it.status == RequestStatus.IN_TRANSIT
            }

            if (acceptedInCoordination.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Coordinated Dispatches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(acceptedInCoordination) { request ->
                    DonorInboundRequestCard(
                        request = request,
                        onAccept = {},
                        onDecline = {},
                        onTrack = { viewModel.selectRequest(request.id) },
                        onChat = { viewModel.openChatForRequest(request.id) }
                    )
                }
            }

            // Active Surplus Announcements
            item {
                Text(
                    text = "Active Surplus Donations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (activeDonations.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = FoodLoopGreenPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All surplus batches are currently distributed.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap '+ Donate Food' to announce fresh surplus.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(activeDonations) { donation ->
                    DonorActiveDonationCard(
                        donation = donation,
                        onClick = { viewModel.selectDonation(donation.id) }
                    )
                }
            }

            // Donation History
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Donation History Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.DONOR_PROFILE) }) {
                        Text("View Profile")
                    }
                }
            }

            items(myDonations) { donation ->
                DonationHistoryItem(donation = donation)
            }
        }
    }
}

@Composable
private fun DonorInboundRequestCard(
    request: FoodRequestEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onTrack: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopAmberLight.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = request.consumerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (request.consumerVerified) {
                        Surface(shape = RoundedCornerShape(4.dp), color = FoodLoopGreenContainer) {
                            Text(
                                "✓ Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = FoodLoopGreenDark,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                RequestStatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Requested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${request.requestedPlates} Plates", fontWeight = FontWeight.Bold, color = FoodLoopAmberPrimary)
                }
                Column {
                    Text("Residents", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${request.peopleCount} People", fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Transport", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(request.transportPreference.displayName.take(15), fontWeight = FontWeight.SemiBold)
                }
            }

            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${request.notes}”",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (request.status == RequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decline")
                    }
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onTrack,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Track Order")
                    }
                    Button(
                        onClick = onChat,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Chat")
                    }
                }
            }
        }
    }
}

@Composable
private fun DonorActiveDonationCard(
    donation: DonationEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🍱 ${donation.remainingPlates} / ${donation.totalPlates} Plates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FoodLoopGreenDark
                    )
                }
                DonationStatusBadge(status = donation.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = donation.menuSummary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Until ${donation.availableUntilTime} (${donation.mealPeriod.displayName})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Reserved: ${donation.reservedPlates} plates",
                    style = MaterialTheme.typography.bodySmall,
                    color = FoodLoopAmberPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DonationHistoryItem(donation: DonationEntity) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${donation.totalPlates} Plates • ${donation.mealPeriod.displayName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = donation.menuSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            DonationStatusBadge(status = donation.status)
        }
    }
}
