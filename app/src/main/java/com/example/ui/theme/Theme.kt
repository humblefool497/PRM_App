package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EnterpriseDarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = CharcoalDark,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealAccent,
    secondary = SlateLight,
    onSecondary = CharcoalDark,
    secondaryContainer = CharcoalBorder,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = TealAccent,
    onTertiary = CharcoalDark,
    background = CharcoalDark,
    onBackground = TextPrimaryDark,
    surface = CharcoalSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = SlateLight,
    error = AlertCrimson,
    onError = TextPrimaryDark,
    errorContainer = AlertCrimsonBg,
    onErrorContainer = AlertCrimson,
    outline = CharcoalBorder
)

private val EnterpriseLightColorScheme = lightColorScheme(
    primary = TealDark,
    onPrimary = LightSurface,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = TealDark,
    secondary = SlateSecondary,
    onSecondary = LightSurface,
    secondaryContainer = LightBorder,
    onSecondaryContainer = LightTextPrimary,
    tertiary = TealPrimary,
    onTertiary = LightSurface,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = AlertCrimson,
    onError = LightSurface,
    outline = LightBorder
)

@Composable
fun OpsManagerTheme(
    darkTheme: Boolean = true, // Default to deep executive charcoal aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EnterpriseDarkColorScheme else EnterpriseLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
