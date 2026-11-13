package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.FreedomWallPost
import com.example.model.PostComment
import com.example.model.ReportReason
import com.example.model.StoryItem
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas

@Composable
fun FullScreenStoryViewer(
  stories: List<StoryItem>,
  activeIndex: Int,
  onPrevious: () -> Unit,
  onNext: () -> Unit,
  onClose: () -> Unit,
) {
  val story = stories.getOrNull(activeIndex) ?: return
  val progress = remember(activeIndex) { Animatable(0f) }

  LaunchedEffect(activeIndex) {
    progress.snapTo(0f)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 5500, easing = LinearEasing),
    )
    onNext()
  }

  Dialog(
    onDismissRequest = onClose,
    properties =
      DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false,
      ),
  ) {
    BackHandler(onBack = onClose)
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(Brush.linearGradient(colors = story.gradientColors))
          .statusBarsPadding()
          .navigationBarsPadding()
          .testTag("story_viewer_overlay")
    ) {
      // Tap left / right zones for quick story navigation
      Row(modifier = Modifier.fillMaxSize()) {
        Box(
          modifier =
            Modifier.weight(1f)
              .fillMaxHeight()
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPrevious,
              )
        )
        Box(
          modifier =
            Modifier.weight(1f)
              .fillMaxHeight()
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onNext,
              )
        )
      }

      Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        // Segmented Progress Bar + Author Header
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
          ) {
            stories.forEachIndexed { index, _ ->
              val fillFraction =
                when {
                  index < activeIndex -> 1f
                  index == activeIndex -> progress.value
                  else -> 0f
                }
              Box(
                modifier =
                  Modifier.weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PureWhite.copy(alpha = 0.35f))
              ) {
                Box(
                  modifier =
                    Modifier.fillMaxWidth(fillFraction)
                      .fillMaxHeight()
                      .clip(RoundedCornerShape(2.dp))
                      .background(PureWhite)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Image(
                painter = painterResource(id = story.avatarRes),
                contentDescription = story.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(42.dp).clip(CircleShape),
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = story.authorName,
                  style = MaterialTheme.typography.titleMedium,
                  color = PureWhite,
                )
                Text(
                  text = "${story.categoryTitle} • ${story.timeAgo}",
                  style = MaterialTheme.typography.bodySmall,
                  color = PureWhite.copy(alpha = 0.88f),
                )
              }
            }

            IconButton(
              onClick = onClose,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("story_viewer_close_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.cd_close),
                tint = PureWhite,
              )
            }
          }
        }

        // White Story Content Card
        Surface(
          shape = RoundedCornerShape(26.dp),
          color = PureWhite,
          shadowElevation = 10.dp,
          border = BorderStroke(2.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        ) {
          Column(modifier = Modifier.padding(24.dp)) {
            Surface(
              shape = RoundedCornerShape(50),
              color = story.gradientColors.first().copy(alpha = 0.14f),
            ) {
              Text(
                text = story.categoryTitle,
                style = MaterialTheme.typography.labelMedium,
                color = story.gradientColors.first(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = story.headline,
              style = MaterialTheme.typography.headlineSmall,
              color = InkPrimary,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = story.bodyText,
              style = MaterialTheme.typography.bodyLarge,
              color = InkSecondary,
            )
          }
        }

        // Explicit accessible Previous / Next controls + demo label
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = onPrevious,
            enabled = activeIndex > 0,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("story_prev_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.cd_prev_story),
              tint = if (activeIndex > 0) PureWhite else PureWhite.copy(alpha = 0.35f),
            )
          }

          Text(
            text = stringResource(R.string.story_footer_community),
            style = MaterialTheme.typography.labelSmall,
            color = PureWhite.copy(alpha = 0.85f),
          )

          IconButton(
            onClick = onNext,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("story_next_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = stringResource(R.string.cd_next_story),
              tint = PureWhite,
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
  post: FreedomWallPost?,
  comments: List<PostComment>,
  onAddComment: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (post == null) return
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var draftText by rememberSaveable(post.id) { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .imePadding()
          .navigationBarsPadding()
          .padding(horizontal = 20.dp)
          .padding(bottom = 16.dp)
          .testTag("comments_bottom_sheet")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.comments_title, post.commentCount),
          style = MaterialTheme.typography.titleLarge,
          color = InkPrimary,
        )
        IconButton(onClick = onDismiss, modifier = Modifier.minimumInteractiveComponentSize()) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = InkSecondary,
          )
        }
      }

      Text(
        text = stringResource(R.string.comments_supportive_notice),
        style = MaterialTheme.typography.bodySmall,
        color = InkMuted,
      )

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = HairlineBorder)

      LazyColumn(
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
      ) {
        if (comments.isEmpty()) {
          item {
            Text(
              text = stringResource(R.string.comments_empty),
              style = MaterialTheme.typography.bodyMedium,
              color = InkSecondary,
              modifier = Modifier.padding(vertical = 12.dp),
            )
          }
        } else {
          items(comments, key = { it.id }) { comment ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = SoftCanvas,
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                  )
                  Text(
                    text = comment.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted,
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = comment.text,
                  style = MaterialTheme.typography.bodyMedium,
                  color = InkSecondary,
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedTextField(
          value = draftText,
          onValueChange = { draftText = it },
          placeholder = { Text(text = stringResource(R.string.comments_input_hint)) },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.weight(1f).testTag("comment_input_field"),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            if (draftText.isNotBlank()) {
              onAddComment(draftText)
              draftText = ""
            }
          },
          enabled = draftText.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.height(52.dp).testTag("comment_send_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = stringResource(R.string.comments_send),
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePostBottomSheet(
  post: FreedomWallPost?,
  onCopyLink: (FreedomWallPost) -> Unit,
  onSystemShare: (FreedomWallPost) -> Unit,
  onDismiss: () -> Unit,
) {
  if (post == null) return
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
          .padding(horizontal = 24.dp)
          .padding(bottom = 24.dp)
          .testTag("share_bottom_sheet")
    ) {
      Text(
        text = stringResource(R.string.share_sheet_title),
        style = MaterialTheme.typography.titleLarge,
        color = InkPrimary,
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = stringResource(R.string.share_community_note),
        style = MaterialTheme.typography.bodySmall,
        color = InkMuted,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SoftCanvas,
        border = BorderStroke(1.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = post.shareUrl,
          style = MaterialTheme.typography.bodySmall,
          color = InkSecondary,
          modifier = Modifier.padding(14.dp),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = { onCopyLink(post) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f).height(48.dp).testTag("share_copy_link_button"),
        ) {
          Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = stringResource(R.string.share_copy_link), maxLines = 1)
        }

        Button(
          onClick = { onSystemShare(post) },
          colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f).height(48.dp).testTag("share_system_button"),
        ) {
          Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = stringResource(R.string.share_system_sheet), maxLines = 1)
        }
      }
    }
  }
}

@Composable
fun ReportPostDialog(
  post: FreedomWallPost?,
  onSubmitReport: (FreedomWallPost, ReportReason) -> Unit,
  onDismiss: () -> Unit,
) {
  if (post == null) return
  var selectedReason by remember { mutableStateOf(ReportReason.MISLEADING_CLAIM) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = PureWhite,
    title = {
      Text(
        text = stringResource(R.string.report_dialog_title),
        style = MaterialTheme.typography.titleLarge,
        color = InkPrimary,
      )
    },
    text = {
      Column {
        Text(
          text = stringResource(R.string.report_dialog_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = InkSecondary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        ReportReason.entries.forEach { reason ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedReason = reason }
                .padding(vertical = 4.dp),
          ) {
            RadioButton(
              selected = selectedReason == reason,
              onClick = { selectedReason = reason },
              colors = RadioButtonDefaults.colors(selectedColor = QuestPurple),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(reason.labelRes),
              style = MaterialTheme.typography.bodyMedium,
              color = InkPrimary,
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSubmitReport(post, selectedReason) },
        colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
        modifier = Modifier.testTag("confirm_report_button"),
      ) {
        Text(text = stringResource(R.string.report_submit), fontWeight = FontWeight.SemiBold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.cancel), color = InkSecondary)
      }
    },
  )
}
