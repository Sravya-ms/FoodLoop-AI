package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FoodLoopGreenLight,
    onPrimary = Color(0xFF003915),
    primaryContainer = FoodLoopGreenDark,
    onPrimaryContainer = FoodLoopGreenContainer,
    secondary = FoodLoopAmberLight,
    onSecondary = Color(0xFF452200),
    secondaryContainer = FoodLoopAmberPrimary,
    onSecondaryContainer = FoodLoopAmberContainer,
    tertiary = Color(0xFF38BDF8),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B)
)

private val LightColorScheme = lightColorScheme(
    primary = FoodLoopGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FoodLoopGreenContainer,
    onPrimaryContainer = FoodLoopOnGreenContainer,
    secondary = FoodLoopAmberPrimary,
    onSecondary = Color.White,
    secondaryContainer = FoodLoopAmberContainer,
    onSecondaryContainer = FoodLoopOnAmberContainer,
    tertiary = FoodLoopSkyPrimary,
    onTertiary = Color.White,
    tertiaryContainer = FoodLoopSkyContainer,
    onTertiaryContainer = FoodLoopOnSkyContainer,
    background = FoodLoopBackground,
    surface = FoodLoopSurface,
    onBackground = FoodLoopTextPrimary,
    onSurface = FoodLoopTextPrimary,
    surfaceVariant = FoodLoopSurfaceVariant,
    onSurfaceVariant = FoodLoopTextSecondary,
    outline = FoodLoopBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep FoodLoop branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
