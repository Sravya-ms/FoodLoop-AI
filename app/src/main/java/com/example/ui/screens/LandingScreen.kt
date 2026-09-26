package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.StatCard
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel
import com.example.localization.LocalAppStrings
import com.example.localization.LanguageCatalog
import com.example.localization.LanguageSelectionDialog

@Composable
fun LandingScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showLanguagePicker by remember { mutableStateOf(false) }
    val currentLang by viewModel.currentLanguage.collectAsState()

    if (showLanguagePicker) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLang.code,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { showLanguagePicker = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            HeroSection(
                viewModel = viewModel,
                onDonateClick = {
                    viewModel.navigateTo(AppScreen.DONOR_LOGIN)
                },
                onFindFoodClick = {
                    viewModel.navigateTo(AppScreen.CONSUMER_LOGIN)
                },
                onAdminPortalClick = {
                    viewModel.navigateTo(AppScreen.ADMIN_AUTH)
                },
                onOpenLanguageDialog = {
                    showLanguagePicker = true
                }
            )
        }

        // Live Prototype Demo Stats
        item {
            DemoStatisticsSection()
        }

        // Visual Pipeline (Donor -> FoodLoop AI -> Recipient -> Delivered)
        item {
            WorkflowPipelineSection()
        }

        // Core Pillars (Donors vs Consumers)
        item {
            StakeholdersSection(
                onDonorExplore = {
                    viewModel.navigateTo(AppScreen.DONOR_LOGIN)
                },
                onConsumerExplore = {
                    viewModel.navigateTo(AppScreen.CONSUMER_LOGIN)
                }
            )
        }

        // Time-bound & Location features
        item {
            TimeAndLocationSection()
        }

        // AI Engine Highlights
        item {
            AiFeaturesSection(
                onOpenAssistant = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) }
            )
        }

        // Safety & Verification Policy
        item {
            SafetyVerificationSection()
        }

        // Call to action & Demo quick-access
        item {
            CallToActionSection(
                onDonate = {
                    viewModel.navigateTo(AppScreen.DONOR_LOGIN)
                },
                onFind = {
                    viewModel.navigateTo(AppScreen.CONSUMER_LOGIN)
                }
            )
        }
    }
}

@Composable
private fun HeroSection(
    viewModel: FoodLoopViewModel,
    onDonateClick: () -> Unit,
    onFindFoodClick: () -> Unit,
    onAdminPortalClick: () -> Unit,
    onOpenLanguageDialog: () -> Unit
) {
    val strings = LocalAppStrings.current
    val currentLang by viewModel.currentLanguage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FoodLoopGreenDark,
                        FoodLoopGreenPrimary
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Language Quick Switcher Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.clickable { onOpenLanguageDialog() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentLang.flagEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentLang.nativeName} (${currentLang.englishName})",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FoodLoopAmberPrimary
                    ) {
                        Text(
                            text = "🌐 ${strings.language}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = FoodLoopAmberLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.pilotBadge,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = strings.appName,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "“${strings.landingHeroTitle}”",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FoodLoopAmberLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = strings.landingHeroSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDonateClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FoodLoopAmberPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.donorPortalBtn, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onFindFoodClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = FoodLoopGreenDark
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.consumerPortalBtn, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onAdminPortalClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.adminDashboardTitle, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun DemoStatisticsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cluster Redistribution Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Narasaraopet Pilot (Sample Data)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Donations",
                value = "27",
                subtitle = "Active & Historical",
                icon = Icons.Default.Fastfood,
                iconTint = FoodLoopAmberPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Plates Shared",
                value = "3,420",
                subtitle = "Meals saved",
                icon = Icons.Default.SoupKitchen,
                iconTint = FoodLoopGreenPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Verified Donors",
                value = "18",
                subtitle = "Hostels, Colleges, Halls",
                icon = Icons.Default.CorporateFare,
                iconTint = FoodLoopSkyPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Care Homes",
                value = "14",
                subtitle = "Elderly & Orphanages",
                icon = Icons.Default.HomeWork,
                iconTint = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WorkflowPipelineSection() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "End-to-End Coordination Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Transparent 4-stage lifecycle from mess surplus to senior meal table",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            PipelineStepItem(
                step = "1",
                title = "Donor Announces Surplus",
                description = "Restaurants, marriage functions, commoners, banquet halls, or colleges specify plate count, menu & availability deadline.",
                icon = Icons.Default.SoupKitchen,
                accentColor = FoodLoopAmberPrimary
            )

            PipelineStepItem(
                step = "2",
                title = "FoodLoop AI Smart Matching",
                description = "System notifies verified care homes within 0–5 km with zero food overbooking safeguards.",
                icon = Icons.Default.Hub,
                accentColor = FoodLoopSkyPrimary
            )

            PipelineStepItem(
                step = "3",
                title = "Official Request & Approval",
                description = "Care home specifies headcount. Delivery way chosen (Uber/Rapido or Direct Own Transport). Donor accepts.",
                icon = Icons.Default.FactCheck,
                accentColor = FoodLoopGreenPrimary
            )

            PipelineStepItem(
                step = "4",
                title = "Two-Way Delivery & Receipt",
                description = "Way 1: Care home books Uber/Rapido, or Way 2: Direct transport by donor/recipient. Safe delivery confirmed.",
                icon = Icons.Default.CheckCircle,
                accentColor = FoodLoopGreenDark,
                isLast = true
            )
        }
    }
}

@Composable
private fun PipelineStepItem(
    step: String,
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun StakeholdersSection(
    onDonorExplore: () -> Unit,
    onConsumerExplore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Engineered for Dual Verification",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Every participant is authenticated to protect food safety and recipient dignity",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Donor Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDonorExplore() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FoodLoopAmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = FoodLoopOnAmberContainer)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("For All Donors & Commoners", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Restaurants, Functions, Commoners, Banquets & Colleges", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FoodLoopAmberPrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Any commoner, restaurant, or banquet with valid email can register\n• Two delivery ways: Uber/Rapido booking or direct own transport\n• Full donor capabilities & real-time coordination with care homes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Consumer Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onConsumerExplore() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FoodLoopGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Elderly, contentDescription = null, tint = FoodLoopGreenDark)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("For Orphanages & Old-Age Homes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Verified Non-Profit Beneficiaries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FoodLoopGreenPrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Instant surplus notifications within 5 km\n• Partial quantity reservation (e.g. 85 plates out of 100)\n• Dispatch collection or request simulated transport",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimeAndLocationSection() {
    val strings = LocalAppStrings.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = FoodLoopAmberPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.mealWindowsTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.mealWindowsSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MealTimeChip("Breakfast", "7:30–9:30 AM")
                MealTimeChip("Lunch", "12:30–3:30 PM")
                MealTimeChip("Evening/Dinner", "7:00–8:30 PM")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = FoodLoopAmberContainer
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.lateWarningNotice,
                        style = MaterialTheme.typography.labelSmall,
                        color = FoodLoopOnAmberContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun MealTimeChip(meal: String, hours: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(meal, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(hours, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AiFeaturesSection(onOpenAssistant: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = FoodLoopGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI-Driven Logistics Engine",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onOpenAssistant) {
                    Text("Chat Copilot", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Deterministic transaction workflows backed by smart prediction models:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AiFeaturePill("Smart Matching", Icons.Default.JoinRight, Modifier.weight(1f))
                AiFeaturePill("Demand Forecast", Icons.Default.TrendingUp, Modifier.weight(1f))
                AiFeaturePill("Urgency Alert", Icons.Default.Timer, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AiFeaturePill(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = FoodLoopGreenContainer,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FoodLoopGreenDark)
        }
    }
}

@Composable
private fun SafetyVerificationSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = FoodLoopGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Food Hygiene & Verification Standard",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verification validates genuine organizational existence (Govt Reg / FSSAI / NGO Darpan). Donors must complete a mandatory 5-point hygiene checklist before every surplus announcement.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CallToActionSection(
    onDonate: () -> Unit,
    onFind: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FoodLoopGreenPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ready to close the surplus loop?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Join institutional kitchens and verified beneficiary homes across Narasaraopet.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDonate,
                    colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Donate as Institution", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onFind,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = FoodLoopGreenDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Recipient Portal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
