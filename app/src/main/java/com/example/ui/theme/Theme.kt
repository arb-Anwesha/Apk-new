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

private val StudioColorScheme = darkColorScheme(
    primary = StudioCrimsonSecondary,
    onPrimary = Color.White,
    primaryContainer = StudioMaroonSurface,
    onPrimaryContainer = StudioCrimsonLight,
    secondary = StudioGoldPrimary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF452B05),
    onSecondaryContainer = StudioGoldAccent,
    tertiary = StudioGoldSecondary,
    background = StudioBackgroundDark,
    onBackground = StudioTextPrimary,
    surface = StudioSurfaceDark,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariantDark,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioBorder
)

private val StudioLightColorScheme = lightColorScheme(
    primary = StudioCrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4E6),
    onPrimaryContainer = Color(0xFF881337),
    secondary = StudioGoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    background = Color(0xFFFFF9F9),
    onBackground = Color(0xFF1E1E1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFF3E8EC),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark studio mode for rich cinematic aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> StudioColorScheme
        else -> StudioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
