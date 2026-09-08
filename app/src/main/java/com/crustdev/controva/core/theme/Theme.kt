package com.crustdev.controva.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccentColor = staticCompositionLocalOf { ElectricCyan }

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = ElectricViolet,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun ControvaTheme(
    accentColor: Color = ElectricCyan,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAccentColor provides accentColor
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme.copy(
                primary = accentColor,
                onPrimaryContainer = accentColor
            ),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
