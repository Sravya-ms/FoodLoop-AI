package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.RequestStatus
import com.example.data.model.TransportPreference
import com.example.ui.components.OrderTimelineTracker
import com.example.ui.components.RequestStatusBadge
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun OrderTrackingScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val selectedId by viewModel.selectedRequestId.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val request = remember(allRequests, selectedId) {
        allRequests.firstOrNull { it.id == selectedId }
    }

    if (request == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Request record not found.")
        }
        return
    }

    val isAuthorized = currentUser == null ||
        currentUser?.role == com.example.data.model.UserRole.ADMIN ||
        currentUser?.id == request.donorId ||
        currentUser?.id == request.consumerId

    if (!isAuthorized) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text("Confidential Logistics Record", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This food dispatch record is private between the donor and the recipient care home. Other organizations cannot view these logistics details.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.navigateBack() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Return to My Portal")
            }
        }
        return
    }

    val isConsumer = currentUser?.id == request.consumerId

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
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (request.status != RequestStatus.COMPLETED && request.status != RequestStatus.DECLINED) {
                        Button(
                            onClick = {
                                viewModel.advanceOrderTracking(request.id, request.status)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (request.status == RequestStatus.IN_TRANSIT || request.status == RequestStatus.RECEIVED)
                                    FoodLoopGreenPrimary else FoodLoopSkyPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(
                                imageVector = when (request.status) {
                                    RequestStatus.PENDING -> Icons.Default.Check
                                    RequestStatus.ACCEPTED -> Icons.Default.DoneAll
                                    RequestStatus.READY_FOR_PICKUP -> Icons.Default.LocalShipping
                                    RequestStatus.IN_TRANSIT -> Icons.Default.Restaurant
                                    RequestStatus.RECEIVED -> Icons.Default.Verified
                                    else -> Icons.Default.Check
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (request.status) {
                                    RequestStatus.PENDING -> "Simulate Donor Acceptance"
                                    RequestStatus.ACCEPTED -> "Mark Ready for Pickup"
                                    RequestStatus.READY_FOR_PICKUP -> "Start Transport Dispatch"
                                    RequestStatus.IN_TRANSIT -> "Mark Food Received"
                                    RequestStatus.RECEIVED -> "CONFIRM RECEIPT & COMPLETE"
                                    else -> "Completed"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (request.status == RequestStatus.COMPLETED) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.THANK_YOU) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("VIEW IMPACT & THANK YOU SCREEN", fontWeight = FontWeight.Bold)
                        }
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
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COORDINATION #${request.id}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopAmberPrimary
                            )
                            RequestStatusBadge(status = request.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${request.requestedPlates} Plates Redistribution",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "From ${request.donorName} → To ${request.consumerName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FoodLoopGreenContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Booked within active time window • Fulfillment proceeding on schedule",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FoodLoopGreenDark
                                )
                            }
                        }
                    }
                }
            }

            // Real-time Chat Entry Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                            FoodLoopGreenContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                                        FoodLoopGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                                        "Coordination Chat Active"
                                    else "Coordination Chat (Pending Acceptance)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                                        FoodLoopGreenDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                                "Real-time messaging between donor and recipient is active. Coordinate arrival, vehicle details, gate number, and vessel hygiene."
                            else "Chat will automatically be initiated once ABC Institution accepts this food request.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED)
                                FoodLoopGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (request.status != RequestStatus.PENDING && request.status != RequestStatus.DECLINED) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.openChatForRequest(request.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Coordination Chat", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Timeline Tracker Component
            item {
                OrderTimelineTracker(currentStatus = request.status)
            }

            // Pickup / Transport Details Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = FoodLoopSkyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Transport & Logistics Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Selected Delivery Way Banner
                        val isUberRapido = request.transportPreference == TransportPreference.UBER_RAPIDO_BOOKING || request.transportPreference == TransportPreference.REQUEST_TRANSPORT
                        val isDonorTransport = request.transportPreference == TransportPreference.OWN_TRANSPORT_DONOR

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUberRapido) FoodLoopAmberContainer.copy(alpha = 0.5f) else FoodLoopGreenContainer.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUberRapido) FoodLoopAmberPrimary else FoodLoopGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isUberRapido) Icons.Default.TwoWheeler else (if (isDonorTransport) Icons.Default.LocalShipping else Icons.Default.DirectionsCar),
                                    contentDescription = null,
                                    tint = if (isUberRapido) FoodLoopAmberPrimary else FoodLoopGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isUberRapido) "Way 1: Booked Uber / Rapido Delivery"
                                        else (if (isDonorTransport) "Way 2A: Food Donor's Own Transport" else "Way 2B: Recipient's Own Transport"),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if (isUberRapido) "Driver: Suresh (Rapido Auto AP 07 TX 4821) • Handoff PIN: 4892"
                                        else (if (isDonorTransport) "Delivered directly by ${request.donorName} staff / vehicle" else "Collected directly by ${request.consumerName} staff / vehicle"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Delivery Mode", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(if (isUberRapido) "Uber / Rapido" else "Own Transport", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Estimated Distance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${request.estimatedDistanceKm} km", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Delivery Fee", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(if (isUberRapido) "₹${request.estimatedTransportCost}" else "Direct (₹0)", fontWeight = FontWeight.Bold, color = FoodLoopAmberPrimary)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Pickup Address:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(request.pickupAddress, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Drop / Recipient Address:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(request.dropAddress, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Food Received Confirmation Card (Section 18)
            if (request.status == RequestStatus.IN_TRANSIT || request.status == RequestStatus.RECEIVED) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FoodLoopGreenContainer),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FoodLoopGreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FoodLoopGreenDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confirm Safe Receipt",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FoodLoopGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Recipient home confirms that ${request.requestedPlates} plates were received in good hygienic condition.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopGreenDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.confirmReceipt(request.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("CONFIRM RECEIPT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
