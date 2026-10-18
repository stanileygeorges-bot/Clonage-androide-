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
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = CardSlate,
    onPrimaryContainer = CyanBright,
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = DarkPurpleCard,
    onSecondaryContainer = NeonViolet,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = AccentRed,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme( // Defaulting to sleek dark cyberpunk theme for Clonage
    primary = CyberCyan,
    onPrimary = Color.Black,
    secondary = CyberPurple,
    onSecondary = Color.White,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark
)

@Composable
fun ClonageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    phoneId: Int = 1,
    content: @Composable () -> Unit
) {
    val primaryColor = if (phoneId == 1) CyberCyan else NeonViolet
    val secondaryColor = if (phoneId == 1) OceanBlue else CyberPurple

    val customScheme = DarkColorScheme.copy(
        primary = primaryColor,
        secondary = secondaryColor
    )

    MaterialTheme(
        colorScheme = customScheme,
        typography = Typography,
        content = content
    )
}
