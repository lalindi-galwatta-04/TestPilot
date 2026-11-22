package com.example.testpilot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TestPilotDarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = TextPrimary,

    secondary = PurpleSecondary,
    onSecondary = NavyBackground,

    background = NavyBackground,
    onBackground = TextPrimary,

    surface = NavySurface,
    onSurface = TextPrimary,

    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = FailureRed,
    onError = TextPrimary,

    outline = BorderColor
)

@Composable
fun TestPilotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TestPilotDarkColorScheme,
        typography = Typography,
        content = content
    )
}