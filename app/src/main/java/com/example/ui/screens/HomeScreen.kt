package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.R
import com.example.model.FeedTab
import com.example.model.FreedomWallPost
import com.example.model.PostCategory
import com.example.model.StoryItem
import com.example.ui.components.GradientBookmarkIcon
import com.example.ui.components.PsyQuestStoryBadgeArtwork
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PillStroke
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.PsyQuestUiState
import com.example.util.ImageEncodingUtils

@Composable
fun HomeScreen(
  uiState: PsyQuestUiState,
  onSelectFeedTab: (FeedTab) -> Unit,
  onOpenStory: (Int) -> Unit,
  onCreatePostClick: () -> Unit,
  onToggleLike: (String) -> Unit,
  onOpenComments: (String) -> Unit,
  onOpenShare: (FreedomWallPost) -> Unit,
  onToggleBookmark: (String) -> Unit,
  onCopyPostLink: (FreedomWallPost) -> Unit,
  onReportPost: (String) -> Unit,
  onHidePost: (String) -> Unit,
  onDeletePost: (String) -> Unit,
  onRestoreHiddenPosts: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val feedPosts = uiState.visibleFeedPosts
  var pendingDeletePost by remember { mutableStateOf<FreedomWallPost?>(null) }

  RainbowAuroraBackground(
    modifier = modifier.fillMaxSize().testTag("home_rainbow_background")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Surface(
        color = PureWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().zIndex(1f).testTag("home_stories_surface"),
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
            StoriesStrip(
              userDisplayName = uiState.userProfile?.displayName ?: "You",
              userAvatarBase64 = uiState.userProfile?.avatarBase64,
              userPhotoUrl = uiState.currentUserPhotoUrl,
              stories = uiState.stories,
              onCreatePostClick = onCreatePostClick,
              onOpenStory = onOpenStory,
            )
            FeedFilterTabsRow(
              selectedTab = uiState.selectedFeedTab,
              onSelectTab = onSelectFeedTab,
            )
          }
        }
      }

      LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f).fillMaxWidth().testTag("freedom_wall_feed_list"),
      ) {
        item(key = "share_a_thought_card") {
          ShareAThoughtCard(
            onClick = onCreatePostClick,
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
          )
        }

        if (feedPosts.isEmpty()) {
          item(key = "empty_hidden_feed_card") {
            EmptyFeedRecoveryCard(
              onRestore = onRestoreHiddenPosts,
              modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            )
          }
        } else {
          items(feedPosts, key = { it.id }) { post ->
            FreedomWallPostCard(
              post = post,
              onToggleLike = { onToggleLike(post.id) },
              onOpenComments = { onOpenComments(post.id) },
              onOpenShare = { onOpenShare(post) },
              onToggleBookmark = { onToggleBookmark(post.id) },
              onCopyLink = { onCopyPostLink(post) },
              onReportPost = { onReportPost(post.id) },
              onHidePost = { onHidePost(post.id) },
              onRequestDelete = { pendingDeletePost = post },
              modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            )
          }
        }
      }
    }
  }

  pendingDeletePost?.let { target ->
    AlertDialog(
      onDismissRequest = { pendingDeletePost = null },
      containerColor = PureWhite,
      title = { Text("Delete Post?", style = MaterialTheme.typography.titleLarge, color = InkPrimary) },
      text = {
        Text(
          "This will permanently remove your Freedom Wall post.",
          style = MaterialTheme.typography.bodyMedium,
          color = InkSecondary,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeletePost(target.id)
            pendingDeletePost = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = QuestCoral, contentColor = PureWhite),
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingDeletePost = null }) {
          Text(stringResource(R.string.cancel), color = InkSecondary)
        }
      },
    )
  }
}

@Composable
private fun StoriesStrip(
  userDisplayName: String,
  userAvatarBase64: String?,
  userPhotoUrl: String?,
  stories: List<StoryItem>,
  onCreatePostClick: () -> Unit,
  onOpenStory: (Int) -> Unit,
) {
  val ownerAvatarBitmap =
    remember(userAvatarBase64) { ImageEncodingUtils.decodeBase64ToImageBitmap(userAvatarBase64) }

  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.Top,
    modifier = Modifier.testTag("home_stories_strip"),
  ) {
    item(key = "your_story_badge") {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
          Modifier.width(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onCreatePostClick)
            .padding(vertical = 2.dp)
            .testTag("home_add_story_button"),
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(66.dp)) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.testTag("your_story_profile_avatar")
                .size(64.dp)
                .clip(CircleShape)
                .background(PsyQuestBrushes.StoryRingRainbow)
                .padding(2.dp)
                .clip(CircleShape)
                .background(PureWhite)
                .padding(1.5.dp)
                .clip(CircleShape)
                .background(SoftCanvas),
          ) {
            if (ownerAvatarBitmap != null) {
              Image(
                bitmap = ownerAvatarBitmap,
                contentDescription = stringResource(R.string.your_story_label),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
              )
            } else {
              val initial = userDisplayName.trim().firstOrNull()?.uppercaseChar()?.toString()
              if (initial != null) {
                Text(
                  text = initial,
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = QuestPurple,
                )
              } else {
                Icon(
                  imageVector = Icons.Filled.Person,
                  contentDescription = stringResource(R.string.your_story_label),
                  tint = QuestPurple,
                  modifier = Modifier.size(28.dp),
                )
              }

              if (!userPhotoUrl.isNullOrBlank()) {
                AsyncImage(
                  model = userPhotoUrl,
                  contentDescription = stringResource(R.string.your_story_label),
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
              }
            }
          }
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.align(Alignment.BottomEnd)
                .offset(x = (-1).dp, y = (-1).dp)
                .size(23.dp)
                .clip(CircleShape)
                .background(PsyQuestBrushes.PlusBadgeGradient)
                .border(1.5.dp, PureWhite, CircleShape),
          ) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = stringResource(R.string.cd_create_story),
              tint = PureWhite,
              modifier = Modifier.size(15.dp),
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = stringResource(R.string.your_story_label),
          style = MaterialTheme.typography.labelSmall,
          color = InkPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    itemsIndexed(stories, key = { _, story -> story.id }) { index, story ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
          Modifier.width(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onOpenStory(index) }
            .padding(vertical = 2.dp)
            .testTag("story_circle_${story.id}"),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(66.dp)
              .clip(CircleShape)
              .background(PsyQuestBrushes.StoryRingRainbow)
              .padding(2.5.dp)
              .clip(CircleShape)
              .background(PureWhite)
              .padding(1.5.dp),
        ) {
          Image(
            painter = painterResource(id = story.avatarRes),
            contentDescription = stringResource(R.string.cd_story_avatar, story.authorName),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = story.authorName,
          style = MaterialTheme.typography.labelSmall,
          color = InkSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun FeedFilterTabsRow(
  selectedTab: FeedTab,
  onSelectTab: (FeedTab) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    FeedTabButton(
      label = stringResource(R.string.tab_my_feed),
      selected = selectedTab == FeedTab.MY_FEED,
      onClick = { onSelectTab(FeedTab.MY_FEED) },
      testTag = "feed_tab_my_feed",
    )
    FeedTabButton(
      label = stringResource(R.string.tab_for_you),
      selected = selectedTab == FeedTab.FOR_YOU,
      onClick = { onSelectTab(FeedTab.FOR_YOU) },
      testTag = "feed_tab_for_you",
    )
    FeedTabButton(
      label = stringResource(R.string.tab_friends),
      selected = selectedTab == FeedTab.FRIENDS,
      onClick = { onSelectTab(FeedTab.FRIENDS) },
      testTag = "feed_tab_friends",
    )
  }
}

@Composable
private fun FeedTabButton(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier =
      Modifier.minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .testTag(testTag),
  ) {
    Text(
      text = label,
      style =
        MaterialTheme.typography.titleMedium.copy(
          fontSize = if (selected) 17.sp else 14.sp,
          fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
          letterSpacing = 0.4.sp,
        ),
      color = if (selected) InkPrimary else InkSecondary,
    )
    Spacer(modifier = Modifier.height(3.dp))
    Box(
      modifier =
        Modifier.width(if (selected) 28.dp else 0.dp)
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(PsyQuestBrushes.WordmarkRainbow)
    )
  }
}

@Composable
private fun ShareAThoughtCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = PureWhite,
    shadowElevation = 5.dp,
    border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
    modifier = modifier.clickable(onClick = onClick).testTag("share_a_thought_card"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        PsyQuestStoryBadgeArtwork(modifier = Modifier.size(42.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = stringResource(R.string.share_thought_title),
            style = MaterialTheme.typography.titleSmall,
            color = InkPrimary,
          )
          Text(
            text = stringResource(R.string.share_thought_prompt),
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.clip(RoundedCornerShape(50))
            .background(PsyQuestBrushes.PlusBadgeGradient)
            .padding(horizontal = 12.dp, vertical = 8.dp),
      ) {
        Text(
          text = stringResource(R.string.share_thought_cta),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = PureWhite,
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
fun FreedomWallPostCard(
  post: FreedomWallPost,
  onToggleLike: () -> Unit,
  onOpenComments: () -> Unit,
  onOpenShare: () -> Unit,
  onToggleBookmark: () -> Unit,
  onCopyLink: () -> Unit,
  onReportPost: () -> Unit,
  onHidePost: () -> Unit,
  onRequestDelete: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val decodedAvatar =
    remember(post.authorAvatarBase64) {
      ImageEncodingUtils.decodeBase64ToImageBitmap(post.authorAvatarBase64)
    }
  val decodedImage =
    remember(post.imageBase64) { ImageEncodingUtils.decodeBase64ToImageBitmap(post.imageBase64) }

  Surface(
    shape = RoundedCornerShape(22.dp),
    color = PureWhite,
    shadowElevation = 6.dp,
    border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
    modifier = modifier.testTag("post_card_${post.id}"),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.size(46.dp)
                .clip(CircleShape)
                .background(PsyQuestBrushes.StoryRingRainbow)
                .padding(2.dp)
                .clip(CircleShape)
                .background(PureWhite)
                .padding(1.dp),
          ) {
            if (decodedAvatar != null) {
              Image(
                bitmap = decodedAvatar,
                contentDescription = post.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
              )
            } else {
              Image(
                painter = painterResource(id = post.avatarRes),
                contentDescription = post.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = post.authorName,
              style = MaterialTheme.typography.titleMedium,
              color = InkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = "${post.authorMeta} • ${post.timestamp}",
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("post_menu_button_${post.id}"),
          ) {
            Icon(
              imageVector = Icons.Filled.MoreHoriz,
              contentDescription = stringResource(R.string.cd_post_more),
              tint = InkPrimary,
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            containerColor = PureWhite,
          ) {
            DropdownMenuItem(
              text = {
                Text(
                  text =
                    stringResource(
                      if (post.isBookmarked) R.string.menu_unsave_post else R.string.menu_save_post
                    )
                )
              },
              leadingIcon = {
                Icon(
                  imageVector =
                    if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                  contentDescription = null,
                  tint = QuestPurple,
                )
              },
              onClick = {
                menuExpanded = false
                onToggleBookmark()
              },
            )
            DropdownMenuItem(
              text = { Text(text = stringResource(R.string.menu_copy_link)) },
              leadingIcon = {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = QuestBlue)
              },
              onClick = {
                menuExpanded = false
                onCopyLink()
              },
            )
            if (post.isOwnedByCurrentUser) {
              DropdownMenuItem(
                text = { Text("Delete Post", color = QuestCoral) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = null,
                    tint = QuestCoral,
                  )
                },
                onClick = {
                  menuExpanded = false
                  onRequestDelete()
                },
              )
            } else {
              DropdownMenuItem(
                text = { Text(text = stringResource(R.string.menu_report_post)) },
                leadingIcon = {
                  Icon(imageVector = Icons.Filled.Flag, contentDescription = null, tint = QuestOrange)
                },
                onClick = {
                  menuExpanded = false
                  onReportPost()
                },
              )
              DropdownMenuItem(
                text = { Text(text = stringResource(R.string.menu_hide_post)) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = InkSecondary,
                  )
                },
                onClick = {
                  menuExpanded = false
                  onHidePost()
                },
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      CategoryPillBadge(category = post.category)

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = post.bodyText,
        style = MaterialTheme.typography.bodyLarge,
        color = InkPrimary,
      )

      if (post.highlightCardText != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SoftCanvas,
          border =
            BorderStroke(
              1.2.dp,
              Brush.horizontalGradient(listOf(post.category.accentStart, post.category.accentEnd)),
            ),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
          ) {
            Box(
              modifier =
                Modifier.width(4.dp)
                  .height(34.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(
                    Brush.verticalGradient(listOf(post.category.accentStart, post.category.accentEnd))
                  )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = post.highlightCardText,
              style = MaterialTheme.typography.titleSmall,
              color = InkPrimary,
            )
          }
        }
      }

      if (decodedImage != null || post.imageRes != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .aspectRatio(1.25f)
              .clip(RoundedCornerShape(16.dp))
        ) {
          if (decodedImage != null) {
            Image(
              bitmap = decodedImage,
              contentDescription = "Post attachment image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
          } else if (post.imageRes != null) {
            Image(
              painter = painterResource(id = post.imageRes),
              contentDescription = stringResource(R.string.cd_sample_post_image),
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
          }
          if (post.imageOverlayQuote != null) {
            Box(
              modifier =
                Modifier.fillMaxSize()
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.42f))
                    )
                  )
            )
            Text(
              text = post.imageOverlayQuote,
              style =
                MaterialTheme.typography.titleLarge.copy(
                  fontFamily = PlusJakartaFontFamily,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 20.sp,
                  lineHeight = 27.sp,
                ),
              color = Color(0xFFFEF9C3),
              textAlign = TextAlign.End,
              modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp),
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
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          ReactionPillButton(
            icon = Icons.Filled.Favorite,
            iconTint = if (post.isLiked) QuestCoral else QuestOrange,
            countText = formatCount(post.likeCount),
            contentDescription = stringResource(R.string.cd_like_post),
            onClick = onToggleLike,
            isActive = post.isLiked,
            testTag = "like_button_${post.id}",
          )
          ReactionPillButton(
            icon = Icons.Filled.ChatBubble,
            iconTint = QuestBlue,
            countText = formatCount(post.commentCount),
            contentDescription = stringResource(R.string.cd_comment_post),
            onClick = onOpenComments,
            isActive = false,
            testTag = "comment_button_${post.id}",
          )
          ReactionPillButton(
            icon = Icons.AutoMirrored.Filled.Send,
            iconTint = QuestMagenta,
            countText = formatCount(post.shareCount),
            contentDescription = stringResource(R.string.cd_share_post),
            onClick = onOpenShare,
            isActive = false,
            testTag = "share_button_${post.id}",
          )
        }

        IconButton(
          onClick = onToggleBookmark,
          modifier =
            Modifier.minimumInteractiveComponentSize().testTag("bookmark_button_${post.id}"),
        ) {
          GradientBookmarkIcon(isBookmarked = post.isBookmarked, modifier = Modifier.size(26.dp))
        }
      }
    }
  }
}

@Composable
private fun CategoryPillBadge(category: PostCategory) {
  Surface(
    shape = RoundedCornerShape(50),
    color = category.accentStart.copy(alpha = 0.10f),
    border =
      BorderStroke(
        0.8.dp,
        Brush.horizontalGradient(
          listOf(category.accentStart.copy(alpha = 0.45f), category.accentEnd.copy(alpha = 0.45f))
        ),
      ),
  ) {
    Text(
      text = stringResource(category.labelRes),
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = category.accentStart,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    )
  }
}

@Composable
private fun ReactionPillButton(
  icon: ImageVector,
  iconTint: Color,
  countText: String,
  contentDescription: String,
  onClick: () -> Unit,
  isActive: Boolean,
  testTag: String,
) {
  val scale by
    animateFloatAsState(
      targetValue = if (isActive) 1.04f else 1f,
      animationSpec = spring(stiffness = Spring.StiffnessMedium),
      label = "reaction_pill_scale",
    )
  val borderBrush =
    if (isActive) {
      Brush.horizontalGradient(listOf(QuestCoral, QuestOrange))
    } else {
      Brush.horizontalGradient(listOf(PillStroke, PillStroke))
    }

  Surface(
    shape = RoundedCornerShape(50),
    color = if (isActive) QuestOrange.copy(alpha = 0.10f) else PureWhite,
    border = BorderStroke(1.dp, borderBrush),
    modifier =
      Modifier.scale(scale)
        .clip(RoundedCornerShape(50))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = iconTint,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = countText,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
        color = InkPrimary,
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun EmptyFeedRecoveryCard(
  onRestore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = PureWhite,
    shadowElevation = 5.dp,
    border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
    modifier = modifier.testTag("empty_feed_recovery_card"),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(24.dp),
    ) {
      Text(
        text = stringResource(R.string.empty_feed_title),
        style = MaterialTheme.typography.titleLarge,
        color = InkPrimary,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.empty_feed_body),
        style = MaterialTheme.typography.bodyMedium,
        color = InkSecondary,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onRestore,
        colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(48.dp).testTag("restore_hidden_posts_button"),
      ) {
        Text(text = stringResource(R.string.empty_feed_restore))
      }
    }
  }
}

private fun formatCount(count: Int): String =
  when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
    count >= 10_000 -> String.format("%.1fK", count / 1_000f)
    else -> count.toString()
  }
