package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FeedTab
import com.example.model.FreedomWallPost
import com.example.model.PostCategory
import com.example.ui.components.PsyQuestStoryBadgeArtwork
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.PsyQuestUiState
import com.example.util.ImageEncodingUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
  uiState: PsyQuestUiState,
  onPublishPost: (
    category: PostCategory,
    visibility: String,
    bodyText: String,
    imageBase64: String?,
    quoteHighlight: String?,
    onDone: () -> Unit,
  ) -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var bodyText by rememberSaveable { mutableStateOf("") }
  var quoteHighlight by rememberSaveable { mutableStateOf("") }
  var selectedCategoryName by rememberSaveable { mutableStateOf(PostCategory.PERSONAL_THOUGHTS.name) }
  var selectedVisibility by rememberSaveable { mutableStateOf("PUBLIC") }
  var imageBase64 by rememberSaveable { mutableStateOf<String?>(null) }
  var showPreview by rememberSaveable { mutableStateOf(false) }

  val selectedCategory =
    runCatching { PostCategory.valueOf(selectedCategoryName) }
      .getOrDefault(PostCategory.PERSONAL_THOUGHTS)
  val decodedPreviewImage =
    remember(imageBase64) { ImageEncodingUtils.decodeBase64ToImageBitmap(imageBase64) }

  val photoPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri != null) {
        scope.launch {
          val encoded =
            ImageEncodingUtils.encodeUriToCompressedBase64(
              context = context,
              uri = uri,
              maxDimensionPx = 768,
              maxRawBytes = 220_000,
            )
          if (encoded != null) {
            imageBase64 = encoded
          } else {
            onShowMessage("Could not process selected photo. Try another image.")
          }
        }
      }
    }

  RainbowAuroraBackground(modifier = modifier.fillMaxSize().testTag("screen_create_post")) {
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
    ) {
      item(key = "create_post_composer_card") {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = PureWhite,
          shadowElevation = 6.dp,
          border = BorderStroke(1.5.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                PsyQuestStoryBadgeArtwork(modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = stringResource(R.string.create_post_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary,
                  )
                  Text(
                    text = "Posting as ${uiState.userProfile?.displayName ?: "PsyQuest Explorer"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted,
                  )
                }
              }

              TextButton(
                onClick = { showPreview = !showPreview },
                enabled = bodyText.isNotBlank(),
                modifier = Modifier.testTag("toggle_preview_button"),
              ) {
                Icon(
                  imageVector = if (showPreview) Icons.Filled.Edit else Icons.Filled.Visibility,
                  contentDescription = null,
                  tint = QuestPurple,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showPreview) "Edit" else "Preview", color = QuestPurple)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (showPreview) {
              val previewPost =
                FreedomWallPost(
                  id = "preview_draft",
                  authorName = uiState.userProfile?.displayName?.ifBlank { "You" } ?: "You",
                  authorMeta =
                    uiState.userProfile?.handle?.takeIf { it.isNotBlank() }?.let { "@$it" }
                      ?: "Freedom Wall",
                  authorAvatarBase64 = uiState.userProfile?.avatarBase64,
                  timestamp = "Preview",
                  category = selectedCategory,
                  feedTabs = setOf(FeedTab.MY_FEED, FeedTab.FOR_YOU),
                  bodyText = bodyText.trim(),
                  imageBase64 = imageBase64,
                  highlightCardText = quoteHighlight.trim().takeIf { it.isNotBlank() },
                  likeCount = 0,
                  commentCount = 0,
                  shareCount = 0,
                  shareUrl = "https://psyquest.app/preview",
                  isOwnedByCurrentUser = true,
                )
              FreedomWallPostCard(
                post = previewPost,
                onToggleLike = {},
                onOpenComments = {},
                onOpenShare = {},
                onToggleBookmark = {},
                onCopyLink = {},
                onReportPost = {},
                onHidePost = {},
              )
            } else {
              Text(
                text = "Choose Category",
                style = MaterialTheme.typography.labelLarge,
                color = InkPrimary,
              )
              Spacer(modifier = Modifier.height(6.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                PostCategory.entries.forEach { category ->
                  val isSelected = category == selectedCategory
                  FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryName = category.name },
                    label = { Text(stringResource(category.labelRes)) },
                    colors =
                      FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.accentStart.copy(alpha = 0.16f),
                        selectedLabelColor = category.accentStart,
                      ),
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = bodyText,
                onValueChange = { if (it.length <= 1500) bodyText = it },
                label = { Text("Share a thought, affirmation, or psychology insight") },
                minLines = 4,
                maxLines = 8,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("create_post_body_input"),
              )
              Text(
                text = "${bodyText.length}/1500",
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
              )

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = quoteHighlight,
                onValueChange = { if (it.length <= 200) quoteHighlight = it },
                label = { Text("Optional Key Takeaway / Highlight Box") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("create_post_highlight_input"),
              )

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "Feed Visibility",
                style = MaterialTheme.typography.labelLarge,
                color = InkPrimary,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                  selected = selectedVisibility == "PUBLIC",
                  onClick = { selectedVisibility = "PUBLIC" },
                  leadingIcon = {
                    Icon(Icons.Outlined.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                  },
                  label = { Text("Public (For You & My Feed)") },
                )
                FilterChip(
                  selected = selectedVisibility == "FRIENDS",
                  onClick = { selectedVisibility = "FRIENDS" },
                  leadingIcon = {
                    Icon(Icons.Outlined.People, contentDescription = null, modifier = Modifier.size(16.dp))
                  },
                  label = { Text("Friends Circle") },
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              if (decodedPreviewImage != null) {
                Box(
                  modifier =
                    Modifier.fillMaxWidth()
                      .aspectRatio(1.4f)
                      .clip(RoundedCornerShape(16.dp))
                ) {
                  Image(
                    bitmap = decodedPreviewImage,
                    contentDescription = "Selected post photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                  )
                  IconButton(
                    onClick = { imageBase64 = null },
                    modifier =
                      Modifier.align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(PureWhite.copy(alpha = 0.9f)),
                  ) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove image", tint = QuestCoral)
                  }
                }
                Spacer(modifier = Modifier.height(10.dp))
              }

              OutlinedButton(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("pick_post_image_button"),
              ) {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = QuestBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (imageBase64 == null) "Attach Image (Photo Picker)" else "Change Photo")
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              OutlinedButton(
                onClick = {
                  bodyText = ""
                  quoteHighlight = ""
                  imageBase64 = null
                  showPreview = false
                },
                enabled =
                  !uiState.isPublishingPost &&
                    (bodyText.isNotEmpty() || quoteHighlight.isNotEmpty() || imageBase64 != null),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(0.4f).height(50.dp),
              ) {
                Text("Clear")
              }

              Button(
                onClick = {
                  onPublishPost(
                    selectedCategory,
                    selectedVisibility,
                    bodyText,
                    imageBase64,
                    quoteHighlight.takeIf { it.isNotBlank() },
                  ) {
                    bodyText = ""
                    quoteHighlight = ""
                    imageBase64 = null
                    showPreview = false
                  }
                },
                enabled = bodyText.isNotBlank() && !uiState.isPublishingPost,
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(0.6f).height(50.dp).testTag("publish_post_button"),
              ) {
                if (uiState.isPublishingPost) {
                  CircularProgressIndicator(
                    color = PureWhite,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                  )
                } else {
                  Text("Publish Post", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      item(key = "create_post_community_note") {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = SoftCanvas,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        ) {
          Text(
            text =
              "Kindness reminder: Freedom Wall shares are for peer encouragement and psychology learning—not clinical or emergency care.",
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary,
            modifier = Modifier.padding(14.dp),
          )
        }
      }
    }
  }
}
