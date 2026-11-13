package com.example.ui.components

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
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

private enum class ProfileTab {
  MY_POSTS,
  SAVED,
  STREAK,
  SETTINGS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
  visible: Boolean,
  uiState: PsyQuestUiState,
  userEmail: String?,
  onSaveProfile: (displayName: String, handle: String, bio: String, avatarBase64: String?) -> Unit,
  onDeletePost: (String) -> Unit,
  onSignOut: () -> Unit,
  onDismiss: () -> Unit,
) {
  if (!visible) return

  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val profile = uiState.userProfile

  var activeTab by rememberSaveable { mutableStateOf(ProfileTab.MY_POSTS) }
  var isEditing by rememberSaveable { mutableStateOf(false) }
  var draftName by rememberSaveable(profile?.displayName) {
    mutableStateOf(profile?.displayName ?: "PsyQuest Explorer")
  }
  var draftHandle by rememberSaveable(profile?.handle) {
    mutableStateOf(profile?.handle ?: "explorer")
  }
  var draftBio by rememberSaveable(profile?.bio) {
    mutableStateOf(profile?.bio ?: "On my Psychological Side Quest")
  }
  var draftAvatarBase64 by rememberSaveable(profile?.avatarBase64) {
    mutableStateOf(profile?.avatarBase64)
  }

  val avatarPicker =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri != null) {
        scope.launch {
          val encoded =
            ImageEncodingUtils.encodeUriToCompressedBase64(
              context = context,
              uri = uri,
              maxDimensionPx = 420,
              maxRawBytes = 120_000,
            )
          if (encoded != null) {
            draftAvatarBase64 = encoded
          }
        }
      }
    }

  val decodedAvatar =
    remember(draftAvatarBase64) { ImageEncodingUtils.decodeBase64ToImageBitmap(draftAvatarBase64) }

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
          .padding(horizontal = 20.dp)
          .padding(bottom = 20.dp)
          .testTag("profile_bottom_sheet")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Your PsyQuest Profile", style = MaterialTheme.typography.titleLarge, color = InkPrimary)
        IconButton(onClick = onDismiss) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = InkSecondary)
        }
      }

      // Profile Summary Header
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(64.dp)
              .clip(CircleShape)
              .background(PsyQuestBrushes.StoryRingRainbow)
              .padding(2.5.dp)
              .clip(CircleShape)
              .background(PureWhite)
              .clickable(enabled = isEditing) {
                avatarPicker.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
        ) {
          if (decodedAvatar != null) {
            Image(
              bitmap = decodedAvatar,
              contentDescription = "Profile photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
          } else {
            Image(
              painter = painterResource(id = R.drawable.img_psyquest_official_logo),
              contentDescription = "Default PsyQuest avatar",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
          }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = profile?.displayName ?: draftName,
            style = MaterialTheme.typography.titleMedium,
            color = InkPrimary,
          )
          Text(
            text = "@${profile?.handle ?: draftHandle} • ${userEmail ?: "Google Account"}",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted,
          )
          Text(
            text = profile?.bio ?: draftBio,
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary,
          )
        }
        IconButton(onClick = { isEditing = !isEditing }) {
          Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = QuestPurple)
        }
      }

      if (isEditing) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SoftCanvas,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
              value = draftName,
              onValueChange = { if (it.length <= 40) draftName = it },
              label = { Text("Display Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
              value = draftHandle,
              onValueChange = { if (it.length <= 30) draftHandle = it },
              label = { Text("Username Handle") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
              value = draftBio,
              onValueChange = { if (it.length <= 180) draftBio = it },
              label = { Text("Bio") },
              maxLines = 2,
              modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(
                onClick = {
                  avatarPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                modifier = Modifier.weight(1f),
              ) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Photo")
              }
              Button(
                onClick = {
                  onSaveProfile(draftName, draftHandle, draftBio, draftAvatarBase64)
                  isEditing = false
                },
                enabled = !uiState.isSavingProfile,
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.weight(1f),
              ) {
                Text("Save Profile")
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(
          selected = activeTab == ProfileTab.MY_POSTS,
          onClick = { activeTab = ProfileTab.MY_POSTS },
          label = { Text("Posts (${uiState.myPosts.size})") },
        )
        FilterChip(
          selected = activeTab == ProfileTab.SAVED,
          onClick = { activeTab = ProfileTab.SAVED },
          label = { Text("Saved (${uiState.bookmarks.size})") },
        )
        FilterChip(
          selected = activeTab == ProfileTab.STREAK,
          onClick = { activeTab = ProfileTab.STREAK },
          label = { Text("Streak") },
        )
        FilterChip(
          selected = activeTab == ProfileTab.SETTINGS,
          onClick = { activeTab = ProfileTab.SETTINGS },
          label = { Text("Safety") },
        )
      }

      HorizontalDivider(color = HairlineBorder, modifier = Modifier.padding(vertical = 8.dp))

      LazyColumn(
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
      ) {
        when (activeTab) {
          ProfileTab.MY_POSTS -> {
            if (uiState.myPosts.isEmpty()) {
              item {
                Text(
                  "You haven't published any Freedom Wall posts yet.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = InkSecondary,
                  modifier = Modifier.padding(vertical = 10.dp),
                )
              }
            } else {
              items(uiState.myPosts, key = { it.id }) { post ->
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = SoftCanvas,
                  border = BorderStroke(1.dp, HairlineBorder),
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp),
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(post.bodyText, style = MaterialTheme.typography.bodySmall, color = InkPrimary, maxLines = 2)
                      Text(
                        "${post.likeCount} likes • ${post.commentCount} comments",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted,
                      )
                    }
                    IconButton(onClick = { onDeletePost(post.id) }) {
                      Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete post", tint = QuestCoral)
                    }
                  }
                }
              }
            }
          }
          ProfileTab.SAVED -> {
            if (uiState.bookmarks.isEmpty()) {
              item {
                Text(
                  "No bookmarked Freedom Wall posts or E-Library guides yet.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = InkSecondary,
                  modifier = Modifier.padding(vertical = 10.dp),
                )
              }
            } else {
              items(uiState.bookmarks, key = { it.id }) { bm ->
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = SoftCanvas,
                  border = BorderStroke(1.dp, HairlineBorder),
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = "${bm.itemType} • ${bm.title}",
                      style = MaterialTheme.typography.titleSmall,
                      color = QuestPurple,
                    )
                    if (bm.subtitle.isNotBlank()) {
                      Text(bm.subtitle, style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                    }
                  }
                }
              }
            }
          }
          ProfileTab.STREAK -> {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = SoftCanvas,
                border = BorderStroke(1.dp, PsyQuestBrushes.CardAccentBorder),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text(
                    "Current Streak: ${uiState.currentStreakCount} day(s) • Longest: ${uiState.longestStreakCount} day(s)",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    "Total Private Check-Ins: ${uiState.checkIns.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                  )
                }
              }
            }
          }
          ProfileTab.SETTINGS -> {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = QuestBlue.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, QuestBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                  Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = QuestBlue)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Educational & Wellness Notice", style = MaterialTheme.typography.titleSmall, color = InkPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      "PsyQuest promotes psychological literacy and daily emotional awareness. It does not provide medical or mental health diagnosis, therapy, or emergency crisis intervention. Please contact a licensed professional or local helpline when you need clinical care.",
                      style = MaterialTheme.typography.bodySmall,
                      color = InkSecondary,
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = {
          onDismiss()
          onSignOut()
        },
        border = BorderStroke(1.2.dp, QuestCoral),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("profile_sign_out_button"),
      ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = QuestCoral)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign Out", color = QuestCoral, fontWeight = FontWeight.Bold)
      }
    }
  }
}
