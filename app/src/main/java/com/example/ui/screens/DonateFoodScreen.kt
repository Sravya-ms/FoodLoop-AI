package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.MealPeriod
import com.example.ui.components.VerificationBadge
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun DonateFoodScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.donateStep.collectAsState()
    val mealPeriod by viewModel.donateMealPeriod.collectAsState()
    val plates by viewModel.donatePlates.collectAsState()
    val menuItems by viewModel.donateMenuItems.collectAsState()
    val isVeg by viewModel.donateIsVeg.collectAsState()
    val city by viewModel.donateCity.collectAsState()
    val area by viewModel.donateArea.collectAsState()
    val pickupAddress by viewModel.donatePickupAddress.collectAsState()
    val availableUntil by viewModel.donateAvailableUntil.collectAsState()
    val safety1 by viewModel.donateSafetyCheck1.collectAsState()
    val safety2 by viewModel.donateSafetyCheck2.collectAsState()
    val safety3 by viewModel.donateSafetyCheck3.collectAsState()
    val safety4 by viewModel.donateSafetyCheck4.collectAsState()
    val safety5 by viewModel.donateSafetyCheck5.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var newItemText by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { viewModel.donateStep.value = step - 1 },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.DONOR_DASHBOARD) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }

                    if (step < 7) {
                        Button(
                            onClick = { viewModel.donateStep.value = step + 1 },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary)
                        ) {
                            Text("Next Step")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.publishDonation() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopAmberPrimary),
                            enabled = safety1 && safety2 && safety3 && safety4 && safety5
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PUBLISH DONATION", fontWeight = FontWeight.Bold)
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
            // Step Progress Indicator
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Step $step of 7",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = FoodLoopAmberPrimary
                            )
                            Text(
                                text = when (step) {
                                    1 -> "Meal Period"
                                    2 -> "Quantity"
                                    3 -> "Food Menu"
                                    4 -> "Location"
                                    5 -> "Availability"
                                    6 -> "Safety Declaration"
                                    else -> "Preview & Confirmation"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { step / 7f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = FoodLoopGreenPrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            // Step Content
            item {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "step_content"
                ) { targetStep ->
                    when (targetStep) {
                        1 -> Step1MealPeriod(
                            selectedPeriod = mealPeriod,
                            onSelect = {
                                viewModel.donateMealPeriod.value = it
                                viewModel.donateAvailableUntil.value = it.lateDeadline
                            }
                        )
                        2 -> Step2FoodQuantity(
                            plates = plates,
                            onPlatesChange = { viewModel.donatePlates.value = it }
                        )
                        3 -> Step3FoodMenu(
                            items = menuItems,
                            isVeg = isVeg,
                            newItemText = newItemText,
                            onNewItemTextChange = { newItemText = it },
                            onAddItem = {
                                if (newItemText.isNotBlank()) {
                                    viewModel.donateMenuItems.value = menuItems + newItemText.trim()
                                    newItemText = ""
                                }
                            },
                            onRemoveItem = { itemToRemove ->
                                viewModel.donateMenuItems.value = menuItems.filter { it != itemToRemove }
                            },
                            onVegToggle = { viewModel.donateIsVeg.value = it }
                        )
                        4 -> Step4Location(
                            city = city,
                            area = area,
                            pickupAddress = pickupAddress,
                            onCityChange = { viewModel.donateCity.value = it },
                            onAreaChange = { viewModel.donateArea.value = it },
                            onAddressChange = { viewModel.donatePickupAddress.value = it }
                        )
                        5 -> Step5Availability(
                            mealPeriod = mealPeriod,
                            availableUntil = availableUntil,
                            onTimeChange = { viewModel.donateAvailableUntil.value = it }
                        )
                        6 -> Step6Safety(
                            check1 = safety1,
                            check2 = safety2,
                            check3 = safety3,
                            check4 = safety4,
                            check5 = safety5,
                            onCheck1 = { viewModel.donateSafetyCheck1.value = it },
                            onCheck2 = { viewModel.donateSafetyCheck2.value = it },
                            onCheck3 = { viewModel.donateSafetyCheck3.value = it },
                            onCheck4 = { viewModel.donateSafetyCheck4.value = it },
                            onCheck5 = { viewModel.donateSafetyCheck5.value = it }
                        )
                        7 -> Step7Preview(
                            donorName = currentUser?.name ?: "ABC Institution",
                            city = city,
                            plates = plates,
                            mealPeriod = mealPeriod,
                            menu = menuItems.joinToString(", "),
                            availableUntil = availableUntil,
                            isVeg = isVeg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1MealPeriod(
    selectedPeriod: MealPeriod,
    onSelect: (MealPeriod) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Select Meal Period", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Configure redistribution matching rules based on preparation timing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            MealPeriod.entries.forEach { period ->
                val isSelected = period == selectedPeriod
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) FoodLoopGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FoodLoopGreenPrimary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelect(period) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(period) },
                            colors = RadioButtonDefaults.colors(selectedColor = FoodLoopGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(period.displayName, fontWeight = FontWeight.Bold)
                            Text("Usual Window: ${period.typicalTime} (Late: ${period.lateDeadline})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step2FoodQuantity(
    plates: String,
    onPlatesChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Food Quantity (Number of Plates)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Standardized plate measurements ensure direct recipient allocation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plates,
                onValueChange = onPlatesChange,
                label = { Text("Available Plates") },
                placeholder = { Text("e.g. 100") },
                leadingIcon = { Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = FoodLoopGreenPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("50", "75", "100", "150", "200").forEach { preset ->
                    OutlinedButton(
                        onClick = { onPlatesChange(preset) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (plates == preset) FoodLoopGreenContainer else Color.Transparent
                        )
                    ) {
                        Text("$preset")
                    }
                }
            }
        }
    }
}

@Composable
private fun Step3FoodMenu(
    items: List<String>,
    isVeg: Boolean,
    newItemText: String,
    onNewItemTextChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onVegToggle: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Food Menu Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("List menu components for dietary accommodation at recipient homes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Veg Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dietary Classification: ${if (isVeg) "Vegetarian" else "Non-Vegetarian"}", fontWeight = FontWeight.SemiBold)
                Switch(
                    checked = isVeg,
                    onCheckedChange = onVegToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = FoodLoopGreenPrimary, checkedTrackColor = FoodLoopGreenContainer)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = onNewItemTextChange,
                    placeholder = { Text("Add item (e.g. Rice, Dal, Sambar)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onAddItem,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FoodLoopGreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current items tags
            Text("Current Menu Items (${items.size}):", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(6.dp))
            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item, fontWeight = FontWeight.Medium)
                        IconButton(onClick = { onRemoveItem(item) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step4Location(
    city: String,
    area: String,
    pickupAddress: String,
    onCityChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Pickup Location & Geotag", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Specifies the kitchen entrance or dispatch bay for recipient collectors", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City / Town") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = area,
                onValueChange = onAreaChange,
                label = { Text("Locality / Landmark") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = pickupAddress,
                onValueChange = onAddressChange,
                label = { Text("Exact Pickup Address & Gate") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun Step5Availability(
    mealPeriod: MealPeriod,
    availableUntil: String,
    onTimeChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Time-Bound Expiration Window", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Donations strictly expire automatically to eliminate health risks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FoodLoopGreenContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Auto-Calculated Window for ${mealPeriod.displayName}:", style = MaterialTheme.typography.labelMedium, color = FoodLoopGreenDark)
                    Text("Available Until: $availableUntil", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FoodLoopGreenDark)
                    Text("Configured deadline: ${mealPeriod.normalDeadline} • Late cut-off: ${mealPeriod.lateDeadline}", style = MaterialTheme.typography.bodySmall, color = FoodLoopGreenDark)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = availableUntil,
                onValueChange = onTimeChange,
                label = { Text("Override Expiry Time") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun Step6Safety(
    check1: Boolean,
    check2: Boolean,
    check3: Boolean,
    check4: Boolean,
    check5: Boolean,
    onCheck1: (Boolean) -> Unit,
    onCheck2: (Boolean) -> Unit,
    onCheck3: (Boolean) -> Unit,
    onCheck4: (Boolean) -> Unit,
    onCheck5: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = FoodLoopGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mandatory Safety Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text("You must confirm all 5 conditions before announcing surplus food", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            SafetyCheckboxItem("Food was prepared hygienically under clean supervision", check1, onCheck1)
            SafetyCheckboxItem("Food has been stored appropriately in clean, covered vessels", check2, onCheck2)
            SafetyCheckboxItem("Food is well within the applicable safe-use window", check3, onCheck3)
            SafetyCheckboxItem("Food has not been knowingly contaminated or unsealed", check4, onCheck4)
            SafetyCheckboxItem("Information provided (quantity & items) is fully accurate", check5, onCheck5)

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Legal Disclaimer: Institutional donor assumes primary responsibility for hygienic storage and timely handover. Verification confirms administrative existence, not lab-tested safety.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun SafetyCheckboxItem(text: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(checkedColor = FoodLoopGreenPrimary)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Step7Preview(
    donorName: String,
    city: String,
    plates: String,
    mealPeriod: MealPeriod,
    menu: String,
    availableUntil: String,
    isVeg: Boolean
) {
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
                Text("FOOD DONATION PREVIEW", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = FoodLoopAmberPrimary)
                VerificationBadge(status = com.example.data.model.VerificationStatus.VERIFIED)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Donor: $donorName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Location: $city", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Available Quantity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$plates Plates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FoodLoopGreenDark)
                }
                Column {
                    Text("Meal Period", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(mealPeriod.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if (isVeg) "Vegetarian" else "Non-Veg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Menu Breakdown:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(menu, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Surface(shape = RoundedCornerShape(10.dp), color = FoodLoopAmberContainer) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = FoodLoopOnAmberContainer, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Available until: $availableUntil", fontWeight = FontWeight.Bold, color = FoodLoopOnAmberContainer)
                }
            }
        }
    }
}
