@file:Suppress("MagicNumber")

package com.wateria.revamp.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wateria.R

val WateriaGreen = Color(0xFF0F8B62)
val WateriaDeepGreen = Color(0xFF004D40)
val WateriaLightGreen = Color(0xFF75C8AD)
val WateriaOrange = Color(0xFFFF9100)
val WateriaBlue = Color(0xFF38BAF2)
val WateriaRed = Color(0xFFFF002D)
val WateriaFadedGreen = Color(0xFF26A07C)
val WateriaRedBackground = Color(0x4AFFAEBB)
val WateriaCream = Color(0xFFFFFBF7)
val WateriaWhite = Color.White
val WateriaInk = Color(0xFF26332E)

val WateriaDisplayFont = FontFamily(Font(R.font.righteous, FontWeight.Normal))
val WateriaBodyFont = FontFamily(Font(R.font.rubik, FontWeight.Normal))
val WateriaNumberFont = FontFamily(Font(R.font.dosis, FontWeight.Normal))

private val LightColorScheme =
    lightColorScheme(
        primary = WateriaGreen,
        onPrimary = WateriaWhite,
        primaryContainer = WateriaWhite,
        onPrimaryContainer = WateriaDeepGreen,
        secondary = WateriaOrange,
        onSecondary = WateriaWhite,
        secondaryContainer = Color(0xFFFFE0B2),
        onSecondaryContainer = WateriaInk,
        tertiary = WateriaBlue,
        onTertiary = WateriaWhite,
        error = WateriaRed,
        onError = WateriaWhite,
        background = WateriaGreen,
        onBackground = WateriaWhite,
        surface = WateriaWhite,
        onSurface = WateriaInk,
        outline = WateriaGreen
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF75D5B4),
        onPrimary = Color(0xFF003828),
        primaryContainer = Color(0xFF00513B),
        onPrimaryContainer = Color(0xFFA5F3D2),
        secondary = Color(0xFFFFB45C),
        onSecondary = Color(0xFF4A2800),
        tertiary = Color(0xFF71D1FA),
        onTertiary = Color(0xFF003548),
        error = Color(0xFFFFB3BC),
        onError = Color(0xFF680018),
        background = Color(0xFF072219),
        onBackground = Color(0xFFDDE5DF),
        surface = Color(0xFF10201A),
        onSurface = Color(0xFFDDE5DF)
    )

private val WateriaTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = WateriaDisplayFont,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp,
                lineHeight = 46.sp
            ),
        displayMedium =
            TextStyle(
                fontFamily = WateriaDisplayFont,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 38.sp
            ),
        headlineLarge =
            TextStyle(
                fontFamily = WateriaDisplayFont,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                lineHeight = 32.sp
            ),
        headlineMedium =
            TextStyle(
                fontFamily = WateriaDisplayFont,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp
            ),
        headlineSmall =
            TextStyle(
                fontFamily = WateriaDisplayFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
        titleLarge =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 26.sp
            ),
        titleMedium =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
        titleSmall =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp
            ),
        bodyLarge =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 23.sp
            ),
        bodyMedium =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
        bodySmall =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 17.sp
            ),
        labelLarge =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp
            ),
        labelMedium =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
        labelSmall =
            TextStyle(
                fontFamily = WateriaBodyFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
    )

@Composable
fun WateriaTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = WateriaTypography,
        content = content
    )
}
