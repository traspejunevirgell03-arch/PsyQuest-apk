package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestPurple
import com.example.util.ImageEncodingUtils

@Composable
fun PsyQuestTopHeader(
  userDisplayName: String,
  userAvatarBase64: String?,
  onSearchClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val avatarBitmap = remember(userAvatarBase64) { ImageEncodingUtils.decodeBase64ToImageBitmap(userAvatarBase64) }
  val profileDesc = stringResource(R.string.cd_profile)

  Surface(color = PureWhite, modifier = modifier.fillMaxWidth()) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.app_brand_wordmark),
        style = MaterialTheme.typography.displaySmall.copy(brush = PsyQuestBrushes.WordmarkRainbow),
        modifier = Modifier.testTag("psyquest_brand_wordmark"),
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedHeaderActionButton(
          onClick = onSearchClick,
          description = stringResource(R.string.cd_search),
          testTag = "header_search_button",
        ) {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = InkPrimary,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(2.dp))
        OutlinedHeaderActionButton(
          onClick = onNotificationsClick,
          description = stringResource(R.string.cd_notifications),
          testTag = "header_notifications_button",
        ) {
          Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = InkPrimary,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(2.dp))
        IconButton(
          onClick = onProfileClick,
          modifier =
            Modifier.minimumInteractiveComponentSize()
              .semantics { contentDescription = profileDesc }
              .testTag("header_profile_button"),
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.size(36.dp)
                .clip(CircleShape)
                .background(PsyQuestBrushes.StoryRingRainbow)
                .padding(1.8.dp)
                .clip(CircleShape)
                .background(PureWhite),
          ) {
            if (avatarBitmap != null) {
              Image(
                bitmap = avatarBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
              )
            } else {
              val initial = userDisplayName.trim().firstOrNull()?.uppercaseChar()?.toString()
              if (initial != null) {
                Text(
                  text = initial,
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = QuestPurple,
                )
              } else {
                Icon(
                  imageVector = Icons.Outlined.Person,
                  contentDescription = null,
                  tint = InkPrimary,
                  modifier = Modifier.size(19.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OutlinedHeaderActionButton(
  onClick: () -> Unit,
  description: String,
  testTag: String,
  iconContent: @Composable () -> Unit,
) {
  IconButton(
    onClick = onClick,
    modifier =
      Modifier.minimumInteractiveComponentSize()
        .semantics { contentDescription = description }
        .testTag(testTag),
  ) {
    Surface(
      shape = CircleShape,
      color = PureWhite,
      border = BorderStroke(1.2.dp, InkPrimary),
      modifier = Modifier.size(36.dp),
    ) {
      Box(contentAlignment = Alignment.Center, modifier = Modifier.background(PureWhite)) {
        iconContent()
      }
    }
  }
}
