package com.anshi.maths.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF6A3DE8)
val PurpleLight = Color(0xFFEDE6FF)
val Cream = Color(0xFFFFF8F0)
val Sun = Color(0xFFFFB300)
val CorrectGreen = Color(0xFF2E9E4F)
val WrongRed = Color(0xFFE53935)

private val colors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = Color(0xFF2A1070),
    secondary = Sun,
    onSecondary = Color(0xFF3A2A00),
    background = Cream,
    onBackground = Color(0xFF1F1B24),
    surface = Color.White,
    onSurface = Color(0xFF1F1B24),
)

@Composable
fun AnshiMathsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}
