@file:Suppress("MagicNumber")

package com.wateria.revamp.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WateriaGreen = Color(0xFF006B4F)
private val WateriaGreenContainer = Color(0xFF9BF5CD)
private val WateriaOrange = Color(0xFFFF8A00)
private val WateriaOrangeContainer = Color(0xFFFFDDB8)
private val WateriaCream = Color(0xFFFFFBF7)
private val WateriaDarkSurface = Color(0xFF10201A)

private val LightColorScheme =
    lightColorScheme(
        primary = WateriaGreen,
        onPrimary = Color.White,
        primaryContainer = WateriaGreenContainer,
        onPrimaryContainer = Color(0xFF002116),
        secondary = Color(0xFF8A4A00),
        onSecondary = Color.White,
        secondaryContainer = WateriaOrangeContainer,
        onSecondaryContainer = Color(0xFF2C1600),
        tertiary = WateriaOrange,
        onTertiary = Color(0xFF2B1700),
        background = WateriaCream,
        onBackground = Color(0xFF17201C),
        surface = Color.White,
        onSurface = Color(0xFF17201C)
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF7DDBB5),
        onPrimary = Color(0xFF003828),
        primaryContainer = Color(0xFF00513B),
        onPrimaryContainer = WateriaGreenContainer,
        secondary = Color(0xFFFFB86B),
        onSecondary = Color(0xFF4A2800),
        secondaryContainer = Color(0xFF693A00),
        onSecondaryContainer = WateriaOrangeContainer,
        tertiary = Color(0xFFFFB86B),
        onTertiary = Color(0xFF4A2800),
        background = Color(0xFF0C1511),
        onBackground = Color(0xFFDDE5DF),
        surface = WateriaDarkSurface,
        onSurface = Color(0xFFDDE5DF)
    )

@Composable
fun WateriaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
