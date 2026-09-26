package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.*
import com.example.viewmodel.FoodLoopViewModel

@Composable
fun ThankYouScreen(
    viewModel: FoodLoopViewModel,
    modifier: Modifier = Modifier
) {
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
                .size(88.dp)
                .clip(CircleShape)
                .background(FoodLoopGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Love",
                tint = FoodLoopGreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "❤️ Thank You!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your contribution helped redistribute surplus food instead of letting it go to waste.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImpactBadgeRow(
                icon = Icons.Default.Fastfood,
                title = "🍱 Food Redistributed",
                desc = "Surplus meals transferred safely within freshness window",
                color = FoodLoopAmberPrimary
            )
            ImpactBadgeRow(
                icon = Icons.Default.Handshake,
                title = "🤝 Community Supported",
                desc = "Direct nutritional sustenance delivered to elder and child care homes",
                color = FoodLoopGreenPrimary
            )
            ImpactBadgeRow(
                icon = Icons.Default.Eco,
                title = "🌱 Resources Better Utilized",
                desc = "Prevented avoidable methane emissions from kitchen landfill disposal",
                color = FoodLoopGreenDark
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { viewModel.navigateTo(AppScreen.IMPACT_DASHBOARD) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FoodLoopGreenPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.BarChart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("VIEW PLATFORM IMPACT", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = { viewModel.navigateTo(AppScreen.LANDING) }
        ) {
            Text("Return to Home")
        }
    }
}

@Composable
private fun ImpactBadgeRow(
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
