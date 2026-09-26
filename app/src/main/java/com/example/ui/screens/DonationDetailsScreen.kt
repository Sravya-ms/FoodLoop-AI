package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.isBookingWindowOpen
import com.example.data.model.TransportPreference
import com.example.data.model.VerificationStatus
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel
import com.example.localization.LocalAppStrings

@Composable
fun DonationDetailsScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val selectedId by viewModel.selectedDonationId.collectAsState()
    val allDonations by viewModel.allDonations.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val peopleCount by viewModel.requestPeopleCount.collectAsState()
    val requestedPlates by viewModel.requestPlates.collectAsState()
    val transportPref by viewModel.requestTransportPref.collectAsState()
    val notes by viewModel.requestNotes.collectAsState()

    val donation = remember(allDonations, selectedId) {
        allDonations.firstOrNull { it.id == selectedId }
    }

    if (donation == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Donation record not found.")
        }
        return
    }

    val maxAllowed = donation.remainingPlates
    val recommendedExtra = (peopleCount + 10).coerceAtMost(maxAllowed)
    val isWindowOpen = donation.isBookingWindowOpen()

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Request Summary",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$requestedPlates Plates for $peopleCount People",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isWindowOpen) FoodLoopGreenDark else Color(0xFFDC2626)
                            )
                        }
                        Button(
                            onClick = { viewModel.submitFoodRequest() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isWindowOpen) FoodLoopAmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isWindowOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = isWindowOpen && requestedPlates in 1..maxAllowed && (currentUser?.verificationStatus == VerificationStatus.VERIFIED)
                        ) {
                            Icon(
                                if (isWindowOpen) Icons.Default.Send else Icons.Default.TimerOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isWindowOpen) strings.placeOfficialRequestBtn else strings.bookingClosedBtn, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (!isWindowOpen) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⏰ [${donation.availableUntilTime}] ${strings.bookingTimeCompletedAlert}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFDC2626)
                        )
                    } else if (currentUser?.verificationStatus != VerificationStatus.VERIFIED) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠ Only verified organizations can place official food requests.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
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
            // Main Donation Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (!isWindowOpen) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEE2E2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.TimerOff, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Ordering Window Completed", fontWeight = FontWeight.Bold, color = Color(0xFF991B1B), style = MaterialTheme.typography.labelLarge)
                                        Text("Cutoff time (${donation.availableUntilTime}) has passed. No further requests can be placed.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF991B1B))
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FOOD DONATION #${donation.id}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopGreenDark
                            )
                            VerificationBadge(status = VerificationStatus.VERIFIED)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = donation.donorName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${donation.pickupAddress}, ${donation.city} (${donation.distanceKm} km away)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Remaining Available", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${donation.remainingPlates} / ${donation.totalPlates} Plates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FoodLoopGreenDark)
                            }
                            Column {
                                Text("Meal Period", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(donation.mealPeriod.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Available Until", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(donation.availableUntilTime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FoodLoopAmberPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Menu Components:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = donation.menuSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Official Food Request Form Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Official Request Specification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A request does not automatically guarantee food confirmation. Donor must approve first to lock quantities.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Headcount input
                        OutlinedTextField(
                            value = peopleCount.toString(),
                            onValueChange = { input ->
                                val count = input.toIntOrNull() ?: 0
                                viewModel.requestPeopleCount.value = count
                                viewModel.requestPlates.value = (count + 5).coerceAtMost(maxAllowed)
                            },
                            label = { Text("People Requiring Food (Residents Headcount)") },
                            leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = FoodLoopGreenPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Plates Stepper
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Requested Plates:", fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "$requestedPlates Plates (Max: $maxAllowed)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (requestedPlates > maxAllowed) Color.Red else FoodLoopAmberPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (requestedPlates > 5) viewModel.requestPlates.value = requestedPlates - 5
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("-5")
                                }
                                Slider(
                                    value = requestedPlates.toFloat().coerceIn(1f, maxAllowed.toFloat()),
                                    onValueChange = { viewModel.requestPlates.value = it.toInt() },
                                    valueRange = 1f..maxAllowed.toFloat(),
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = FoodLoopAmberPrimary,
                                        activeTrackColor = FoodLoopAmberPrimary
                                    )
                                )
                                OutlinedButton(
                                    onClick = {
                                        if (requestedPlates + 5 <= maxAllowed) viewModel.requestPlates.value = requestedPlates + 5
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+5")
                                }
                            }
                        }

                        // Smart recommendation note
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopGreenContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Smart Recommendation: Required $peopleCount + extra buffer = $recommendedExtra plates",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = FoodLoopGreenDark
                                )
                            }
                        }

                        // Delivery Ways Selection (2 Ways: Uber/Rapido by care home, or Own Transport by donor/recipient)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Delivery Method (Choose Delivery Way):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                }

                                // WAY 1: UBER / RAPIDO BOOKING BY CARE HOME
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (transportPref == TransportPreference.UBER_RAPIDO_BOOKING || transportPref == TransportPreference.REQUEST_TRANSPORT)
                                        FoodLoopGreenContainer else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (transportPref == TransportPreference.UBER_RAPIDO_BOOKING || transportPref == TransportPreference.REQUEST_TRANSPORT)
                                            FoodLoopGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.requestTransportPref.value = TransportPreference.UBER_RAPIDO_BOOKING }
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = transportPref == TransportPreference.UBER_RAPIDO_BOOKING || transportPref == TransportPreference.REQUEST_TRANSPORT,
                                            onClick = { viewModel.requestTransportPref.value = TransportPreference.UBER_RAPIDO_BOOKING }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("🛵 Way 1: Book Uber / Rapido", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(shape = RoundedCornerShape(6.dp), color = FoodLoopAmberContainer) {
                                                    Text("~₹60", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FoodLoopOnAmberContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            Text(
                                                "Booked by Orphanage / Old-Age Home: On-demand delivery driver (Uber Auto, Rapido bike, Porter) assigned for collection and drop-off.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // WAY 2: OWN TRANSPORTATION
                                Text("Way 2: Provide Own Transportation (Direct)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = FoodLoopGreenDark)

                                // Option 2A: Donor / Commoner's Own Transport
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (transportPref == TransportPreference.OWN_TRANSPORT_DONOR)
                                        FoodLoopGreenContainer else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (transportPref == TransportPreference.OWN_TRANSPORT_DONOR)
                                            FoodLoopGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.requestTransportPref.value = TransportPreference.OWN_TRANSPORT_DONOR }
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = transportPref == TransportPreference.OWN_TRANSPORT_DONOR,
                                            onClick = { viewModel.requestTransportPref.value = TransportPreference.OWN_TRANSPORT_DONOR }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("🚗 2A: Food Donor / Commoner Delivers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "Delivered directly by the institution, restaurant, function organizer, or commoner donor to your care home.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Option 2B: Recipient Care Home's Own Transport
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (transportPref == TransportPreference.OWN_TRANSPORT_RECIPIENT || transportPref == TransportPreference.SELF_PICKUP)
                                        FoodLoopGreenContainer else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (transportPref == TransportPreference.OWN_TRANSPORT_RECIPIENT || transportPref == TransportPreference.SELF_PICKUP)
                                            FoodLoopGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.requestTransportPref.value = TransportPreference.OWN_TRANSPORT_RECIPIENT }
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = transportPref == TransportPreference.OWN_TRANSPORT_RECIPIENT || transportPref == TransportPreference.SELF_PICKUP,
                                            onClick = { viewModel.requestTransportPref.value = TransportPreference.OWN_TRANSPORT_RECIPIENT }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("🚐 2B: Care Home Collects (Self Pickup)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "Direct pickup by care home staff, trust vehicle, ambulance, or local volunteer.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Notes
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { viewModel.requestNotes.value = it },
                            label = { Text("Coordination Notes for Donor (Optional)") },
                            placeholder = { Text("e.g. Will bring sanitized stainless steel containers") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}
