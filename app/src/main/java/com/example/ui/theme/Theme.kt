package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PsyQuestLightColorScheme =
  lightColorScheme(
    primary = QuestPurple,
    onPrimary = PureWhite,
    primaryContainer = ColorTokens.PrimaryContainer,
    onPrimaryContainer = QuestIndigo,
    secondary = QuestOrange,
    onSecondary = PureWhite,
    secondaryContainer = ColorTokens.SecondaryContainer,
    onSecondaryContainer = InkPrimary,
    tertiary = QuestMagenta,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = InkPrimary,
    surface = PureWhite,
    onSurface = InkPrimary,
    surfaceVariant = SoftCanvas,
    onSurfaceVariant = InkSecondary,
    outline = HairlineBorder,
    outlineVariant = TrackBackground,
  )

private object ColorTokens {
  val PrimaryContainer = QuestPurple.copy(alpha = 0.12f)
  val SecondaryContainer = QuestOrange.copy(alpha = 0.14f)
}

val PsyQuestShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
  )

@Composable
fun PsyQuestTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = PsyQuestLightColorScheme,
    typography = PsyQuestTypography,
    shapes = PsyQuestShapes,
    content = content,
  )
}
