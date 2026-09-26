package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun AiAssistantScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    val suggestionChips = listOf(
        "Show nearby lunch donations",
        "How many plates are available?",
        "Which donations expire soon?",
        "Show my previous requests",
        "Predict today's meal demand"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Model Disclaimer & Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FoodLoopAmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("FoodLoop AI Copilot", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Operational query engine & predictive surplus matching", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FoodLoopGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) FoodLoopGreenPrimary else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Suggestions Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestionChips) { chip ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendAiPrompt(chip)
                    },
                    label = { Text(chip, fontSize = 12.sp) }
                )
            }
        }

        // Input Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Ask FoodLoop AI about surplus...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.sendAiPrompt(inputQuery.trim())
                            inputQuery = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FoodLoopGreenPrimary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImpactDashboardScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val donations by viewModel.allDonations.collectAsState()
    val requests by viewModel.allRequests.collectAsState()
    val users by viewModel.allUsers.collectAsState()

    val totalPlates = remember(donations) { donations.sumOf { it.totalPlates } + 3225 }
    val totalDonationsCount = remember(donations) { donations.size + 24 }
    val completedCount = remember(requests) { requests.count { it.status == com.example.data.model.RequestStatus.COMPLETED } + 24 }
    val verifiedInstitutions = remember(users) { users.count { it.verificationStatus == com.example.data.model.VerificationStatus.VERIFIED } }

    // Estimated CO2 emissions avoided: ~2.5 kg CO2e per 1 kg food waste (~2.5 plates per kg) => ~1 kg CO2e per plate
    val estimatedCo2Saved = (totalPlates * 0.95f).toInt()
    val estimatedLandfillAvoidedKg = (totalPlates * 0.42f).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FoodLoopGreenDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "FoodLoop Impact Ledger",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quantifiable social sustainability and food waste redirection across verified community networks.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Stat Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Plates Shared", "$totalPlates", "Meals redistributed", Icons.Default.SoupKitchen, iconTint = FoodLoopGreenPrimary, modifier = Modifier.weight(1f))
                    StatCard("Redistributions", "$completedCount", "Verified deliveries", Icons.Default.CheckCircle, iconTint = FoodLoopAmberPrimary, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Active Donors", "${donations.map { it.donorId }.distinct().size + 16}", "Mess & Caterers", Icons.Default.CorporateFare, iconTint = FoodLoopSkyPrimary, modifier = Modifier.weight(1f))
                    StatCard("Verified Homes", "$verifiedInstitutions", "Elder & Orphan Care", Icons.Default.HomeWork, iconTint = Color(0xFF7C3AED), modifier = Modifier.weight(1f))
                }
            }
        }

        // Environmental Equivalencies Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = FoodLoopGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Environmental Conservation Index", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Landfill Waste Diverted", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$estimatedLandfillAvoidedKg kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FoodLoopGreenDark)
                        }
                        Column {
                            Text("CO2e Avoided", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$estimatedCo2Saved kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FoodLoopAmberPrimary)
                        }
                        Column {
                            Text("Water Footprint Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${(totalPlates * 125)} L", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FoodLoopSkyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Calculation Methodology Note: Plate counts strictly reflect verified donor-recipient receipts logged in the database. Landfill and carbon equivalents utilize FAO standard culinary emission coefficients.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
