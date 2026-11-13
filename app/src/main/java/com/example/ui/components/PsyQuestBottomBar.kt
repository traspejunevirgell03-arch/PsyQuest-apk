package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.PsyQuestDestination
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple

@Composable
fun PsyQuestBottomBar(
  currentDestination: PsyQuestDestination,
  onNavigate: (PsyQuestDestination) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(color = PureWhite, shadowElevation = 8.dp, modifier = modifier.fillMaxWidth()) {
    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
      HorizontalDivider(thickness = 1.dp, color = HairlineBorder)
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        PsyQuestDestination.entries.forEach { destination ->
          val isSelected = destination == currentDestination
          BottomNavTabItem(
            destination = destination,
            isSelected = isSelected,
            onClick = { onNavigate(destination) },
            modifier = Modifier.weight(1f),
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavTabItem(
  destination: PsyQuestDestination,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val scale by
    animateFloatAsState(
      targetValue = if (isSelected) 1.08f else 0.96f,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "nav_icon_scale",
    )
  val interactionSource = remember { MutableInteractionSource() }
  val label = stringResource(destination.labelRes)
  val a11yDescription = stringResource(destination.contentDescriptionRes)

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(16.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(bounded = true),
          role = Role.Tab,
          onClick = onClick,
        )
        .semantics {
          selected = isSelected
          contentDescription = a11yDescription
        }
        .testTag(destination.testTag)
        .padding(vertical = 4.dp, horizontal = 2.dp),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier.width(46.dp)
          .height(34.dp)
          .clip(RoundedCornerShape(17.dp))
          .background(
            if (isSelected) {
              Brush.horizontalGradient(
                listOf(
                  QuestPurple.copy(alpha = 0.13f),
                  QuestMagenta.copy(alpha = 0.13f),
                  QuestOrange.copy(alpha = 0.13f),
                )
              )
            } else {
              Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
            }
          ),
    ) {
      PsyQuestNavIcon(
        destination = destination,
        size = 26.dp,
        modifier = Modifier.scale(scale),
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = label,
      style =
        MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.5.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        ),
      color = if (isSelected) InkPrimary else InkSecondary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )

    Spacer(modifier = Modifier.height(2.dp))

    Box(
      modifier =
        Modifier.width(if (isSelected) 20.dp else 0.dp)
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(
            Brush.horizontalGradient(listOf(QuestBlue, QuestPurple, QuestMagenta, QuestOrange))
          )
    )
  }
}
