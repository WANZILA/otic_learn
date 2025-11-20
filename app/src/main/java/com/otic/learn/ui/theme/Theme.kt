package com.otic.learn.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OticLightColorScheme = lightColorScheme(
    primary = OticPrimary,
    onPrimary = OticOnPrimary,
    secondary = OticSecondary,
    background = OticBackground,
    surface = OticSurface,
    onBackground = OticOnBackground,
    onSurface = OticOnSurface,
    onSurfaceVariant = OticOnSurfaceSoft,
)

@Composable
fun OticLearnTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OticLightColorScheme,
        // For now we keep default typography/shapes.
        // We can customize these later to match your exact font scale.
        content = content
    )
}
