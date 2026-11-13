package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.PsyQuestDestination
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestLime
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.QuestYellow

@Composable
fun RainbowAuroraBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          Brush.linearGradient(
            colors =
              listOf(
                QuestBlue,
                QuestPurple.copy(alpha = 0.88f),
                QuestMagenta.copy(alpha = 0.82f),
                QuestOrange.copy(alpha = 0.86f),
              )
          )
        )
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      // Top-left deep cobalt & sky wash
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(QuestBlue.copy(alpha = 0.95f), Color.Transparent),
            center = Offset(size.width * 0.14f, size.height * 0.12f),
            radius = size.maxDimension * 0.55f,
          ),
        center = Offset(size.width * 0.14f, size.height * 0.12f),
        radius = size.maxDimension * 0.55f,
      )
      // Top-right vibrant lime & yellow glow
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(QuestLime.copy(alpha = 0.92f), QuestYellow.copy(alpha = 0.55f), Color.Transparent),
            center = Offset(size.width * 0.86f, size.height * 0.08f),
            radius = size.maxDimension * 0.52f,
          ),
        center = Offset(size.width * 0.86f, size.height * 0.08f),
        radius = size.maxDimension * 0.52f,
      )
      // Mid-left violet-magenta bloom
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(QuestMagenta.copy(alpha = 0.88f), QuestPurple.copy(alpha = 0.5f), Color.Transparent),
            center = Offset(size.width * 0.10f, size.height * 0.58f),
            radius = size.maxDimension * 0.56f,
          ),
        center = Offset(size.width * 0.10f, size.height * 0.58f),
        radius = size.maxDimension * 0.56f,
      )
      // Mid-right golden-amber & tangerine glow
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(QuestYellow.copy(alpha = 0.85f), QuestOrange.copy(alpha = 0.65f), Color.Transparent),
            center = Offset(size.width * 0.92f, size.height * 0.52f),
            radius = size.maxDimension * 0.50f,
          ),
        center = Offset(size.width * 0.92f, size.height * 0.52f),
        radius = size.maxDimension * 0.50f,
      )
      // Bottom coral-magenta anchor
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(QuestCoral.copy(alpha = 0.85f), QuestMagenta.copy(alpha = 0.45f), Color.Transparent),
            center = Offset(size.width * 0.52f, size.height * 0.94f),
            radius = size.maxDimension * 0.58f,
          ),
        center = Offset(size.width * 0.52f, size.height * 0.94f),
        radius = size.maxDimension * 0.58f,
      )
    }
    content()
  }
}

@Composable
fun PsyQuestNavIcon(
  destination: PsyQuestDestination,
  modifier: Modifier = Modifier,
  size: Dp = 28.dp,
) {
  val iconRes =
    when (destination) {
      PsyQuestDestination.HOME -> R.drawable.ic_nav_home
      PsyQuestDestination.STREAK -> R.drawable.ic_nav_streak
      PsyQuestDestination.CREATE_POST -> R.drawable.ic_nav_add_post
      PsyQuestDestination.GAMES -> R.drawable.ic_nav_games
      PsyQuestDestination.E_LIBRARY -> R.drawable.ic_nav_e_library
    }
  Image(
    painter = painterResource(id = iconRes),
    contentDescription = null,
    contentScale = ContentScale.Fit,
    modifier = modifier.size(size),
  )
}

@Composable
fun PsyQuestStoryBadgeArtwork(modifier: Modifier = Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier =
      modifier
        .clip(CircleShape)
        .background(Color.White)
        .border(1.4.dp, InkPrimary, CircleShape)
        .padding(3.dp),
  ) {
    Image(
      painter = painterResource(id = R.drawable.img_psyquest_official_logo),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize().clip(CircleShape),
    )
  }
}

@Composable
fun GradientBookmarkIcon(isBookmarked: Boolean, modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = this.size.width
    val h = this.size.height
    val ribbon =
      Path().apply {
        moveTo(w * 0.18f, h * 0.12f)
        lineTo(w * 0.82f, h * 0.12f)
        lineTo(w * 0.82f, h * 0.66f)
        lineTo(w * 0.50f, h * 0.90f)
        lineTo(w * 0.18f, h * 0.66f)
        close()
      }
    if (isBookmarked) {
      drawPath(ribbon, brush = PsyQuestBrushes.BookmarkGradient)
    } else {
      drawPath(
        ribbon,
        brush =
          Brush.verticalGradient(
            listOf(QuestPurple.copy(alpha = 0.72f), QuestYellow.copy(alpha = 0.75f))
          ),
      )
    }
    drawPath(
      ribbon,
      color = InkPrimary,
      style = Stroke(width = w * 0.06f, join = StrokeJoin.Round),
    )
  }
}
