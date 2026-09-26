package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.model.ConsumerType
import com.example.data.model.DonorType
import com.example.data.model.InstitutionSubtype
import com.example.data.model.UserRole
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

/**
 * PORTAL SELECTOR GATEWAY
 * Directs users to the appropriate separated portal so donor data and consumer data
 * remain strictly segregated with zero cross-visibility.
 */
@Composable
fun AuthGatewayScreen(
    viewModel: FoodLoopViewModel,
    isRegister: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FoodLoopGreenContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Privacy-Isolated Authentication",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = FoodLoopGreenDark
                        )
                    }
                }

                Text(
                    text = if (isRegister) "Select Registration Portal" else "Choose Your Portal to Sign In",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Donors and Care Homes operate in independent, privacy-shielded environments. Select your role to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // OPTION 1: FOOD DONOR PORTAL
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FoodLoopAmberPrimary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isRegister) viewModel.navigateTo(AppScreen.DONOR_REGISTER)
                        else viewModel.navigateTo(AppScreen.DONOR_LOGIN)
                    }
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(FoodLoopAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(26.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopAmberContainer
                        ) {
                            Text(
                                text = "FOOD DONOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopOnAmberContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Food Donor Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "For Colleges, Hostels, Banquet Halls, Caterers, Hotels, Restaurants & Event Organizers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy guarantee box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopAmberContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopAmberPrimary.copy(alpha = 0.25f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔒 Strict Donor Isolation: Your surplus announcements, kitchen capacity, and commercial records are completely invisible to other donors.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopOnAmberContainer,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isRegister) viewModel.navigateTo(AppScreen.DONOR_REGISTER)
                            else viewModel.navigateTo(AppScreen.DONOR_LOGIN)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(if (isRegister) Icons.Default.AppRegistration else Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRegister) "Register as Food Donor" else "Sign In as Food Donor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // OPTION 2: RECIPIENT CARE HOME PORTAL
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FoodLoopGreenPrimary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isRegister) viewModel.navigateTo(AppScreen.CONSUMER_REGISTER)
                        else viewModel.navigateTo(AppScreen.CONSUMER_LOGIN)
                    }
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(FoodLoopGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Elderly, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(26.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FoodLoopGreenContainer
                        ) {
                            Text(
                                text = "RECIPIENT CARE HOME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopGreenDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Recipient Care Home Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exclusively for Registered Orphanages, Child Welfare Shelters & Old-Age Homes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy guarantee box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopGreenContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopGreenPrimary.copy(alpha = 0.25f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🛡️ Beneficiary Privacy Shield: Resident headcounts, care home internal rosters, and other homes' requests are strictly confidential.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopGreenDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isRegister) viewModel.navigateTo(AppScreen.CONSUMER_REGISTER)
                            else viewModel.navigateTo(AppScreen.CONSUMER_LOGIN)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(if (isRegister) Icons.Default.AppRegistration else Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRegister) "Register Care Facility" else "Sign In as Care Home", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ADMIN LINK
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.ADMIN_AUTH) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("District Administration & Auditing", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("For authorized municipal and NGO compliance officers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * DEDICATED DONOR LOGIN SCREEN
 * Completely separated for Food Donors.
 * Does not show consumer accounts or care home records.
 */
@Composable
fun DonorLoginScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val donorUsers = remember(allUsers) { allUsers.filter { it.role == UserRole.DONOR } }

    var email by remember { mutableStateOf("principal@abcinstitution.edu.in") }
    var password by remember { mutableStateOf("••••••••") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(FoodLoopAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Food Donor Sign In", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("For Colleges, Messes, Caterers & Event Halls", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy Guarantee Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopAmberContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopAmberPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🔒 Donor Privacy Shield: Your surplus announcements, kitchen capacity, logistics contacts, and commercial mess data are private. Other donors cannot see your announcements or data.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopOnAmberContainer,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Sign In to Donor Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(errorMessage!!, style = MaterialTheme.typography.bodySmall, color = Color(0xFF991B1B))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Donor, Restaurant or Commoner Email") },
                        placeholder = { Text("e.g. donor@gmail.com, hotel@restaurant.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Donor Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val err = viewModel.donorLogin(email, password)
                            if (err != null) {
                                errorMessage = err
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In as Food Donor", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.DONOR_REGISTER) }) {
                            Text("New Commoner, Restaurant or Donor? Register here", color = FoodLoopAmberPrimary)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // DEMO QUICK-ACCESS: ONLY DONOR ACCOUNTS (NO CONSUMER LEAKAGE)
                    Text(
                        "Demo Quick Sign-in (Restaurants, Commoners, Banquets, Colleges):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    donorUsers.forEach { donor ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    email = donor.email
                                    password = donor.password
                                    viewModel.donorLogin(donor.email, donor.password)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (donor.donorType) {
                                        DonorType.RESTAURANT -> Icons.Default.Restaurant
                                        DonorType.FUNCTION_HALL -> Icons.Default.Celebration
                                        DonorType.COMMONER -> Icons.Default.Person
                                        else -> Icons.Default.School
                                    },
                                    contentDescription = null,
                                    tint = FoodLoopAmberPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(donor.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("${donor.donorType?.displayName ?: "Donor"} • ${donor.email}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.DONOR_REGISTER) }) {
                            Text("Register New Donor Kitchen", color = FoodLoopAmberPrimary, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.LOGIN) }) {
                            Text("Switch Portal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

/**
 * DEDICATED DONOR REGISTRATION SCREEN
 * For onboarding colleges, restaurants, marriage functions, banquets, and commoners.
 */
@Composable
fun DonorRegisterScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    var donorName by remember { mutableStateOf("") }
    var donorEmail by remember { mutableStateOf("") }
    var donorPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var donorPhone by remember { mutableStateOf("") }
    var donorAddress by remember { mutableStateOf("") }
    var donorCity by remember { mutableStateOf("Narasaraopet") }
    var donorType by remember { mutableStateOf(DonorType.RESTAURANT) }
    var institutionSubtype by remember { mutableStateOf(InstitutionSubtype.RESTAURANT) }
    var fssaiNumber by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var regSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(FoodLoopAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AppRegistration, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Register as Food Donor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Restaurants, Functions, Banquets, Commoners & Colleges", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopAmberContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopAmberPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = FoodLoopAmberPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🔒 Universal Donor Access: Any commoner, restaurant, or banquet organizer with a valid email can register with their password and donate surplus meals with full donor capabilities.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopOnAmberContainer,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (regSubmitted) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Donor Registration Complete!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Welcome to FoodLoop AI! Your donor account has been registered. You can immediately publish surplus food, review care home requests, and coordinate delivery.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.DONOR_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary)
                        ) {
                            Text("Open Donor Dashboard")
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Food Donor Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        // 4 Donor Category Filter Chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = donorType == DonorType.RESTAURANT,
                                    onClick = {
                                        donorType = DonorType.RESTAURANT
                                        institutionSubtype = InstitutionSubtype.RESTAURANT
                                    },
                                    label = { Text("🍽️ Restaurant / Hotel") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = donorType == DonorType.FUNCTION_HALL,
                                    onClick = {
                                        donorType = DonorType.FUNCTION_HALL
                                        institutionSubtype = InstitutionSubtype.FAMILY_FUNCTION
                                    },
                                    label = { Text("🎉 Function / Banquet") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = donorType == DonorType.COMMONER,
                                    onClick = {
                                        donorType = DonorType.COMMONER
                                        institutionSubtype = InstitutionSubtype.COMMON_CITIZEN
                                    },
                                    label = { Text("🤝 Commoner / Citizen") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = donorType == DonorType.INSTITUTIONAL,
                                    onClick = {
                                        donorType = DonorType.INSTITUTIONAL
                                        institutionSubtype = InstitutionSubtype.COLLEGE
                                    },
                                    label = { Text("🏫 College / Hostel") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (validationError != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEE2E2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(validationError!!, style = MaterialTheme.typography.bodySmall, color = Color(0xFF991B1B))
                                }
                            }
                        }

                        OutlinedTextField(
                            value = donorName,
                            onValueChange = {
                                donorName = it
                                validationError = null
                            },
                            label = {
                                Text(when (donorType) {
                                    DonorType.RESTAURANT -> "Restaurant / Hotel Name *"
                                    DonorType.FUNCTION_HALL -> "Function Hall / Event Host Name *"
                                    DonorType.COMMONER -> "Commoner / Citizen Donor Name *"
                                    else -> "College / Institution Name *"
                                })
                            },
                            placeholder = {
                                Text(when (donorType) {
                                    DonorType.RESTAURANT -> "e.g. Spice Garden Restaurant"
                                    DonorType.FUNCTION_HALL -> "e.g. Sita Kalyana Mandapam (Reception)"
                                    DonorType.COMMONER -> "e.g. Srikanth / Sharma Family"
                                    else -> "e.g. ABC College Hostel Mess"
                                })
                            },
                            leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = donorEmail,
                            onValueChange = {
                                donorEmail = it
                                validationError = null
                            },
                            label = { Text("Valid Email Address *") },
                            placeholder = { Text("e.g. donor@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = donorPassword,
                            onValueChange = {
                                donorPassword = it
                                validationError = null
                            },
                            label = { Text("Create Password *") },
                            placeholder = { Text("Minimum 4 characters") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                validationError = null
                            },
                            label = { Text("Confirm Password *") },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = donorPhone,
                            onValueChange = {
                                donorPhone = it
                                validationError = null
                            },
                            label = { Text("Contact Phone Number *") },
                            placeholder = { Text("e.g. +91 98480 12345") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = donorAddress,
                            onValueChange = {
                                donorAddress = it
                                validationError = null
                            },
                            label = { Text("Pickup Location / Street Address *") },
                            placeholder = { Text("e.g. Arundelpet Main Road, Narasaraopet") },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (donorType == DonorType.RESTAURANT || donorType == DonorType.INSTITUTIONAL) {
                            OutlinedTextField(
                                value = fssaiNumber,
                                onValueChange = { fssaiNumber = it },
                                label = { Text("FSSAI / Registration No. (Optional)") },
                                placeholder = { Text("e.g. FSSAI-2026-998822") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                val trimmedEmail = donorEmail.trim()
                                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
                                if (donorName.isBlank()) {
                                    validationError = "Please enter your name / organization name."
                                    return@Button
                                }
                                if (trimmedEmail.isBlank() || !emailRegex.matches(trimmedEmail)) {
                                    validationError = "Please enter a valid email address (e.g. name@gmail.com)."
                                    return@Button
                                }
                                if (donorPassword.length < 4) {
                                    validationError = "Password must be at least 4 characters long."
                                    return@Button
                                }
                                if (donorPassword != confirmPassword) {
                                    validationError = "Passwords do not match. Please verify."
                                    return@Button
                                }
                                if (donorAddress.isBlank()) {
                                    validationError = "Please provide the pickup address in Narasaraopet."
                                    return@Button
                                }

                                viewModel.registerNewDonor(
                                    name = donorName.trim(),
                                    email = trimmedEmail,
                                    password = donorPassword.trim(),
                                    phone = if (donorPhone.isNotBlank()) donorPhone.trim() else "+91 98480 00000",
                                    donorType = donorType,
                                    institutionSubtype = institutionSubtype,
                                    address = donorAddress.trim(),
                                    city = donorCity,
                                    docName = if (fssaiNumber.isNotBlank()) "FSSAI_$fssaiNumber.pdf" else "Self_Declaration.pdf"
                                )
                                regSubmitted = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Register as Food Donor", fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(onClick = { viewModel.navigateTo(AppScreen.DONOR_LOGIN) }) {
                                Text("Already have a donor account? Sign In", color = FoodLoopAmberPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * DEDICATED CONSUMER (CARE HOME) LOGIN SCREEN
 * Completely separated for Orphanages & Old-Age Homes.
 * Does not show donor accounts or other orphanages' confidential records.
 */
@Composable
fun ConsumerLoginScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val consumerUsers = remember(allUsers) { allUsers.filter { it.role == UserRole.CONSUMER } }

    var email by remember { mutableStateOf("care@abcoldagehome.org") }
    var password by remember { mutableStateOf("••••••••") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(FoodLoopGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Elderly, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Recipient Care Home Sign In", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("For Verified Orphanages & Old-Age Homes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy Shield Notice
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopGreenContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopGreenPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🛡️ Beneficiary Protection Shield: Resident headcounts, care home internal rosters, and other homes' requests are strictly confidential. Neither donors nor other care homes can see each other's beneficiary data.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopGreenDark,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Sign In to Recipient Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(errorMessage!!, style = MaterialTheme.typography.bodySmall, color = Color(0xFF991B1B))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Care Home Official Email") },
                        placeholder = { Text("e.g. care@abcoldagehome.org") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val err = viewModel.consumerLogin(email)
                            if (err != null) {
                                errorMessage = err
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enter Recipient Hub", fontWeight = FontWeight.Bold)
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // DEMO QUICK-ACCESS: ONLY CONSUMER CARE HOME ACCOUNTS (NO DONOR LEAKAGE)
                    Text(
                        "Demo Quick Sign-in as Verified Care Home:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    consumerUsers.forEach { consumer ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    email = consumer.email
                                    viewModel.consumerLogin(consumer.email)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (consumer.consumerType == ConsumerType.ORPHANAGE) Icons.Default.ChildCare else Icons.Default.Elderly,
                                    contentDescription = null,
                                    tint = FoodLoopGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(consumer.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("${consumer.consumerType?.displayName ?: "Care Facility"} • ${consumer.email}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.CONSUMER_REGISTER) }) {
                            Text("Register New Care Facility", color = FoodLoopGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.LOGIN) }) {
                            Text("Switch Portal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

/**
 * DEDICATED CONSUMER (CARE HOME) REGISTRATION SCREEN
 * For onboarding verified orphanages and old-age homes.
 */
@Composable
fun ConsumerRegisterScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    var homeName by remember { mutableStateOf("") }
    var homeEmail by remember { mutableStateOf("") }
    var homePhone by remember { mutableStateOf("") }
    var homeAddress by remember { mutableStateOf("") }
    var homeCity by remember { mutableStateOf("Narasaraopet") }
    var consumerType by remember { mutableStateOf(ConsumerType.OLD_AGE_HOME) }
    var residentsCount by remember { mutableStateOf("85") }
    var regSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(FoodLoopGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AppRegistration, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Register Care Facility", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("For Orphanages and Old-Age Homes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FoodLoopGreenContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FoodLoopGreenPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🛡️ Confidential Beneficiary Roster: Resident headcounts and shelter internal details are encrypted and kept strictly confidential. Used solely for food allocation matching.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FoodLoopGreenDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (regSubmitted) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FoodLoopGreenPrimary, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Care Home Registration Submitted!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your Trust registration certificate and resident headcount were forwarded to the District Administration. Your shelter profile is active in verification-pending state.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.CONSUMER_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary)
                        ) {
                            Text("Continue to Recipient Hub")
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Register Recipient Care Home", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        // Type Selector
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = consumerType == ConsumerType.OLD_AGE_HOME,
                                onClick = { consumerType = ConsumerType.OLD_AGE_HOME },
                                label = { Text("Old-Age Home") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = consumerType == ConsumerType.ORPHANAGE,
                                onClick = { consumerType = ConsumerType.ORPHANAGE },
                                label = { Text("Orphanage / Child Home") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = homeName,
                            onValueChange = { homeName = it },
                            label = { Text("Organization / Facility Legal Name") },
                            placeholder = { Text("e.g. Shanti Old Age Home, Sneha Orphanage") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = residentsCount,
                            onValueChange = { residentsCount = it },
                            label = { Text("Verified Number of Residents (Confidential)") },
                            placeholder = { Text("e.g. 85") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = homeEmail,
                            onValueChange = { homeEmail = it },
                            label = { Text("Official Email Address") },
                            placeholder = { Text("e.g. care@shantihome.org") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = homePhone,
                            onValueChange = { homePhone = it },
                            label = { Text("Official Phone / WhatsApp Number") },
                            placeholder = { Text("e.g. +91 98665 00000") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = homeAddress,
                            onValueChange = { homeAddress = it },
                            label = { Text("Physical Shelter Address & Landmark") },
                            placeholder = { Text("e.g. Shanti Nagar, Near Water Tank") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = FoodLoopGreenPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Govt_Trust_Reg_Certificate.pdf", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                    Text("Societies Act / Juvenile Justice Act / Trust Deed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                viewModel.registerNewConsumer(
                                    name = if (homeName.isNotBlank()) homeName else "New Community Care Home",
                                    email = if (homeEmail.isNotBlank()) homeEmail else "care@home.org",
                                    phone = if (homePhone.isNotBlank()) homePhone else "+91 98665 00000",
                                    consumerType = consumerType,
                                    residentsCount = residentsCount.toIntOrNull() ?: 50,
                                    address = if (homeAddress.isNotBlank()) homeAddress else "Shanti Nagar, Narasaraopet",
                                    city = homeCity,
                                    docName = "Govt_Trust_Reg_Certificate.pdf"
                                )
                                regSubmitted = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit for Care Home Verification", fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(onClick = { viewModel.navigateTo(AppScreen.CONSUMER_LOGIN) }) {
                                Text("Already registered as care home? Sign In", color = FoodLoopGreenPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * DEDICATED ADMIN AUTHENTICATION SCREEN
 * Separated from normal users.
 */
@Composable
fun AdminAuthScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    var adminEmail by remember { mutableStateOf("admin@foodloop.ai") }
    var adminPasscode by remember { mutableStateOf("admin123") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = FoodLoopGreenDark, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("District Administration Portal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Authorized personnel only for donor & consumer compliance audits", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = adminEmail,
                    onValueChange = { adminEmail = it },
                    label = { Text("Administrative Email") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = adminPasscode,
                    onValueChange = { adminPasscode = it },
                    label = { Text("Security Passcode") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        viewModel.adminLogin(adminPasscode)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenDark),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Authenticate Administrator", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Backward-compatible screen composable references
@Composable
fun DonorAuthScreen(viewModel: FoodLoopViewModel, modifier: Modifier = Modifier) {
    DonorLoginScreen(viewModel = viewModel, modifier = modifier)
}

@Composable
fun ConsumerAuthScreen(viewModel: FoodLoopViewModel, modifier: Modifier = Modifier) {
    ConsumerLoginScreen(viewModel = viewModel, modifier = modifier)
}
