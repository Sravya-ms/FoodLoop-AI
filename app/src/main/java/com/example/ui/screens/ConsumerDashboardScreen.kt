package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.local.isBookingWindowOpen
import com.example.data.model.DonationStatus
import com.example.data.model.MealPeriod
import com.example.data.model.RequestStatus
import com.example.data.model.VerificationStatus
import com.example.ui.components.*
import com.example.ui.navigation.AppScreen
import com.example.localization.LocalAppStrings
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun ConsumerDashboardScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allDonations by viewModel.allDonations.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMealFilter by remember { mutableStateOf<MealPeriod?>(null) }
    var vegOnlyFilter by remember { mutableStateOf(false) }

    val myRequests = remember(allRequests, currentUser) {
        val uid = currentUser?.id ?: "consumer_abc_oldage"
        allRequests.filter { it.consumerId == uid }
    }

    val activeRequests = remember(myRequests) {
        myRequests.filter {
            it.status == RequestStatus.PENDING ||
            it.status == RequestStatus.ACCEPTED ||
            it.status == RequestStatus.READY_FOR_PICKUP ||
            it.status == RequestStatus.IN_TRANSIT
        }
    }

    val completedReceipts = remember(myRequests) {
        myRequests.count { it.status == RequestStatus.COMPLETED }
    }

    val filteredDonations = remember(allDonations, searchQuery, selectedMealFilter, vegOnlyFilter) {
        allDonations.filter { donation ->
            val matchesAvailability = donation.status == DonationStatus.PUBLISHED || donation.status == DonationStatus.PARTIALLY_RESERVED
            val matchesQuery = searchQuery.isBlank() ||
                donation.menuSummary.contains(searchQuery, ignoreCase = true) ||
                donation.donorName.contains(searchQuery, ignoreCase = true) ||
                donation.city.contains(searchQuery, ignoreCase = true) ||
                donation.mealPeriod.displayName.contains(searchQuery, ignoreCase = true)
            val matchesMeal = selectedMealFilter == null || donation.mealPeriod == selectedMealFilter
            val matchesVeg = !vegOnlyFilter || donation.isVeg

            matchesAvailability && matchesQuery && matchesMeal && matchesVeg
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Consumer Header
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
                                text = currentUser?.name ?: "ABC Old Age Home",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        VerificationBadge(status = currentUser?.verificationStatus ?: VerificationStatus.VERIFIED)
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
                            text = "${currentUser?.city ?: "Narasaraopet"} • ${currentUser?.address ?: "Shanti Nagar"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FoodLoopGreenContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopGreenPrimary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🛡️ Beneficiary Privacy Shield: Resident headcount & requests are confidential. Other homes cannot view your data.",
                                style = MaterialTheme.typography.labelSmall,
                                color = FoodLoopGreenDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Stats Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Residents Needing Food",
                        value = "${currentUser?.numberOfResidents ?: 85}",
                        subtitle = "Verified headcount",
                        icon = Icons.Default.Groups,
                        iconTint = FoodLoopSkyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Requests",
                        value = "${activeRequests.size}",
                        subtitle = "In fulfillment",
                        icon = Icons.Default.PendingActions,
                        iconTint = FoodLoopAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Food Received",
                        value = "${completedReceipts + 38} Batches",
                        subtitle = "3,400+ plates delivered",
                        icon = Icons.Default.CheckCircle,
                        iconTint = FoodLoopGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Success Rate",
                        value = "98.5%",
                        subtitle = "Transparent logs",
                        icon = Icons.Default.TaskAlt,
                        iconTint = FoodLoopGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Orders in Transit / Awaiting
        if (activeRequests.isNotEmpty()) {
            item {
                Text(
                    text = "Live Coordination Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(activeRequests) { request ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopSkyPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectRequest(request.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Request #${request.id}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            RequestStatusBadge(status = request.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${request.requestedPlates} plates from ${request.donorName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Transport: ${request.transportPreference.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.selectRequest(request.id) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tracker", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { viewModel.openChatForRequest(request.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Chat", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Search & Discovery
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Nearby Food Surplus Donations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search donations... rice, lunch, 100 plates, Narasaraopet") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                // Quick Filters
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedMealFilter == null,
                            onClick = { selectedMealFilter = null },
                            label = { Text("All Meals") }
                        )
                    }
                    items(MealPeriod.entries) { meal ->
                        FilterChip(
                            selected = selectedMealFilter == meal,
                            onClick = { selectedMealFilter = if (selectedMealFilter == meal) null else meal },
                            label = { Text(meal.displayName) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = vegOnlyFilter,
                            onClick = { vegOnlyFilter = !vegOnlyFilter },
                            label = { Text("🥦 Vegetarian") }
                        )
                    }
                }
            }
        }

        // Donation Results
        if (filteredDonations.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No active donations match your search filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try clearing filters or check back around meal distribution windows.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredDonations) { donation ->
                ConsumerDonationCard(
                    donation = donation,
                    onViewDetails = { viewModel.selectDonation(donation.id) }
                )
            }
        }
    }
}

@Composable
private fun ConsumerDonationCard(
    donation: DonationEntity,
    onViewDetails: () -> Unit
) {
    val strings = LocalAppStrings.current
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🍱 ${donation.remainingPlates} Plates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FoodLoopGreenDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (donation.isVeg) {
                        Surface(shape = RoundedCornerShape(4.dp), color = FoodLoopGreenContainer) {
                            Text(
                                "VEG",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopGreenDark,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                val isWindowOpen = donation.isBookingWindowOpen()
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isWindowOpen) FoodLoopGreenContainer else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (isWindowOpen) strings.verifiedDonorBadge else strings.bookingClosedBadge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isWindowOpen) FoodLoopGreenDark else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = donation.donorName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NearMe, contentDescription = null, tint = FoodLoopSkyPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${donation.distanceKm} km away • ${donation.area}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = donation.menuSummary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val isWindowOpen = donation.isBookingWindowOpen()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isWindowOpen) Icons.Default.AccessTime else Icons.Default.TimerOff,
                        contentDescription = null,
                        tint = if (isWindowOpen) FoodLoopAmberPrimary else Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isWindowOpen) "${strings.availableUntilLabel} ${donation.availableUntilTime}" else "${strings.windowClosedNotice} (${donation.availableUntilTime})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isWindowOpen) FoodLoopAmberPrimary else Color(0xFFDC2626)
                    )
                }

                Button(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWindowOpen) FoodLoopGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isWindowOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(if (isWindowOpen) strings.viewDetails else strings.bookingClosedBtn, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
