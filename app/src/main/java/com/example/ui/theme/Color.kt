package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core PsyQuest Rainbow Palette
val QuestLime = Color(0xFF65E83A)
val QuestGreen = Color(0xFF22C55E)
val QuestYellow = Color(0xFFFACC15)
val QuestOrange = Color(0xFFF97316)
val QuestCoral = Color(0xFFEF4444)
val QuestMagenta = Color(0xFFEC4899)
val QuestPurple = Color(0xFF7C3AED)
val QuestIndigo = Color(0xFF4F46E5)
val QuestBlue = Color(0xFF2563EB)
val QuestCyan = Color(0xFF06B6D4)

// Surfaces & Typography Ink
val PureWhite = Color(0xFFFFFFFF)
val SoftCanvas = Color(0xFFF8FAFC)
val InkPrimary = Color(0xFF18181B)
val InkSecondary = Color(0xFF52525B)
val InkMuted = Color(0xFF71717A)
val HairlineBorder = Color(0xFFE4E4E7)
val PillStroke = Color(0xFF3F3F46)
val TrackBackground = Color(0xFFE5E7EB)

object PsyQuestBrushes {
  val WordmarkRainbow =
    Brush.horizontalGradient(
      colors = listOf(QuestLime, QuestYellow, QuestOrange, QuestMagenta, QuestPurple, QuestIndigo)
    )

  val StoryRingRainbow =
    Brush.sweepGradient(
      colors =
        listOf(
          QuestPurple,
          QuestMagenta,
          QuestCoral,
          QuestOrange,
          QuestYellow,
          QuestLime,
          QuestCyan,
          QuestPurple,
        )
    )

  val PlusBadgeGradient =
    Brush.linearGradient(colors = listOf(QuestCoral, QuestOrange, QuestMagenta, QuestPurple))

  val ProgressBarGradient =
    Brush.horizontalGradient(colors = listOf(QuestPurple, QuestBlue, QuestLime))

  val BookmarkGradient =
    Brush.verticalGradient(colors = listOf(QuestPurple, QuestMagenta, QuestYellow))

  val FlameGradient =
    Brush.linearGradient(colors = listOf(QuestCoral, QuestOrange, QuestYellow))

  val CardAccentBorder =
    Brush.linearGradient(
      colors =
        listOf(
          QuestPurple.copy(alpha = 0.55f),
          QuestMagenta.copy(alpha = 0.55f),
          QuestOrange.copy(alpha = 0.55f),
          QuestLime.copy(alpha = 0.55f),
          QuestBlue.copy(alpha = 0.55f),
        )
    )
}
