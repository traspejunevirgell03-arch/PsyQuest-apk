package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ELibraryCatalog
import com.example.model.ELibraryResource
import com.example.model.FreedomWallPost
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.HeaderUtilitySheet
import com.example.ui.viewmodel.PsyQuestUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderUtilityBottomSheet(
  activeSheet: HeaderUtilitySheet,
  uiState: PsyQuestUiState,
  onSelectSearchPost: (FreedomWallPost) -> Unit,
  onSelectSearchResource: (ELibraryResource) -> Unit,
  onGoToStreak: () -> Unit,
  onDismiss: () -> Unit,
) {
  if (activeSheet != HeaderUtilitySheet.SEARCH && activeSheet != HeaderUtilitySheet.NOTIFICATIONS) {
    return
  }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val isSearch = activeSheet == HeaderUtilitySheet.SEARCH
  var query by rememberSaveable { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .padding(bottom = 16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isSearch) Icons.Outlined.Search else Icons.Outlined.Notifications,
            contentDescription = null,
            tint = QuestPurple,
            modifier = Modifier.size(24.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text =
              stringResource(
                if (isSearch) R.string.search_sheet_title else R.string.notifications_sheet_title
              ),
            style = MaterialTheme.typography.titleLarge,
            color = InkPrimary,
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = InkSecondary,
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (isSearch) {
        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          placeholder = { Text(stringResource(R.string.search_hint)) },
          leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = QuestPurple) },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("global_search_input"),
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = HairlineBorder)

        val q = query.trim().lowercase()
        val matchedPosts =
          remember(q, uiState.allPosts) {
            if (q.isEmpty()) uiState.allPosts.take(3)
            else
              uiState.allPosts.filter {
                it.bodyText.lowercase().contains(q) || it.authorName.lowercase().contains(q)
              }.take(5)
          }
        val matchedResources =
          remember(q) {
            if (q.isEmpty()) ELibraryCatalog.resources.take(3)
            else
              ELibraryCatalog.resources.filter {
                it.title.lowercase().contains(q) ||
                  it.category.displayName.lowercase().contains(q) ||
                  it.summary.lowercase().contains(q)
              }.take(5)
          }

        LazyColumn(
          contentPadding = PaddingValues(vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
        ) {
          if (matchedPosts.isNotEmpty()) {
            item {
              Text(
                text = "Freedom Wall Posts",
                style = MaterialTheme.typography.labelLarge,
                color = QuestPurple,
              )
            }
            items(matchedPosts, key = { "post_${it.id}" }) { post ->
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = SoftCanvas,
                border = BorderStroke(1.dp, HairlineBorder),
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                      onSelectSearchPost(post)
                      onDismiss()
                    },
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(12.dp),
                ) {
                  Icon(Icons.Outlined.Psychology, contentDescription = null, tint = QuestPurple)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = post.authorName,
                      style = MaterialTheme.typography.titleSmall,
                      color = InkPrimary,
                    )
                    Text(
                      text = post.bodyText,
                      style = MaterialTheme.typography.bodySmall,
                      color = InkSecondary,
                      maxLines = 2,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                }
              }
            }
          }

          if (matchedResources.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "E-Library Guides",
                style = MaterialTheme.typography.labelLarge,
                color = QuestBlue,
              )
            }
            items(matchedResources, key = { "lib_${it.id}" }) { resource ->
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = SoftCanvas,
                border = BorderStroke(1.dp, HairlineBorder),
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                      onSelectSearchResource(resource)
                      onDismiss()
                    },
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(12.dp),
                ) {
                  Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = QuestBlue)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = resource.title,
                      style = MaterialTheme.typography.titleSmall,
                      color = InkPrimary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = "${resource.category.displayName} • ${resource.sourcePlatform}",
                      style = MaterialTheme.typography.bodySmall,
                      color = InkMuted,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                }
              }
            }
          }

          if (matchedPosts.isEmpty() && matchedResources.isEmpty()) {
            item {
              Text(
                text = "No matching posts or library guides found for \"$query\".",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSecondary,
                modifier = Modifier.padding(vertical = 12.dp),
              )
            }
          }
        }
      } else {
        val checkedInToday = uiState.todayCheckIn != null
        val streak = uiState.currentStreakCount
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = SoftCanvas,
          border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector =
                  if (checkedInToday) Icons.Outlined.CheckCircle else Icons.Outlined.LocalFireDepartment,
                contentDescription = null,
                tint = if (checkedInToday) QuestGreen else QuestOrange,
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text =
                    if (checkedInToday) "Today's check-in complete (${uiState.todayCheckIn?.moodEmoji})"
                    else "Ready for today's gentle check-in",
                  style = MaterialTheme.typography.titleSmall,
                  color = InkPrimary,
                )
                Text(
                  text =
                    if (streak > 0) "Current wellness streak: $streak day${if (streak == 1) "" else "s"}"
                    else "No rush—start a supportive streak whenever you're ready.",
                  style = MaterialTheme.typography.bodySmall,
                  color = InkSecondary,
                )
              }
            }

            if (!checkedInToday) {
              Button(
                onClick = {
                  onDismiss()
                  onGoToStreak()
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp),
              ) {
                Text("Check in now", fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text =
            "You have ${uiState.myPosts.size} Freedom Wall post${if (uiState.myPosts.size == 1) "" else "s"} and ${uiState.bookmarks.size} saved item${if (uiState.bookmarks.size == 1) "" else "s"}.",
          style = MaterialTheme.typography.bodySmall,
          color = InkMuted,
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("dismiss_utility_sheet_button"),
      ) {
        Text(text = stringResource(R.string.cd_close), style = MaterialTheme.typography.labelMedium)
      }
    }
  }
}
