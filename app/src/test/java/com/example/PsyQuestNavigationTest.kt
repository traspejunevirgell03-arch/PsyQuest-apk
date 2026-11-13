package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import com.example.data.DailyCheckInDoc
import com.example.data.FreedomWallPostDoc
import com.example.data.GameProgressDoc
import com.example.data.PostCommentDoc
import com.example.data.PsyQuestDataSource
import com.example.data.UserBookmarkDoc
import com.example.data.UserProfileDoc
import com.example.ui.screens.AuthSignInScreen
import com.example.ui.theme.PsyQuestTheme
import com.example.ui.viewmodel.PsyQuestViewModel
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PsyQuestNavigationTest {

  @get:Rule val composeTestRule = createComposeRule()

  private fun createViewModel(): PsyQuestViewModel =
    PsyQuestViewModel(
      repository = InMemoryTestDataSource(),
      currentUserId = "test_uid",
      initialDisplayName = "Test Explorer",
      initialHandleBase = "test_explorer",
    )

  @Test
  fun authSignInScreenDisplaysGoogleSignInAndBranding() {
    composeTestRule.setContent { PsyQuestTheme { AuthSignInScreen(onAuthSuccess = {}) } }
    composeTestRule.onNodeWithTag("auth_sign_in_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("PSYQUEST").assertIsDisplayed()
    composeTestRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
  }

  @Test
  fun allFiveNavigationButtonsSwitchToCorrespondingSections() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithTag("psyquest_brand_wordmark").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_rainbow_background").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_stories_surface").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_stories_strip").assertIsDisplayed()
    composeTestRule.onNodeWithTag("your_story_profile_avatar", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("share_a_thought_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("post_card_post_alexa_sunlight").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_streak_button").performClick()
    composeTestRule.onNodeWithTag("screen_streak").assertIsDisplayed()
    composeTestRule.onNodeWithText("How are you feeling today?").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_create_post_button").performClick()
    composeTestRule.onNodeWithTag("screen_create_post").assertIsDisplayed()
    composeTestRule.onNodeWithText("Create a Post").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_games_button").performClick()
    composeTestRule.onNodeWithTag("screen_games").assertIsDisplayed()
    composeTestRule.onNodeWithText("Psychology Game Center").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_e_library_button").performClick()
    composeTestRule.onNodeWithTag("screen_e_library").assertIsDisplayed()
    composeTestRule.onNodeWithText("Educational Digital Library").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_home_button").performClick()
    composeTestRule.onNodeWithTag("share_a_thought_card").assertIsDisplayed()
  }

  @Test
  fun storiesStripFeedTabsCommentsAndCreatePostWork() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithText("Your Story").assertIsDisplayed()
    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.onNodeWithTag("story_circle_story_maya").performClick()
    composeTestRule.mainClock.advanceTimeBy(100)
    composeTestRule.onNodeWithTag("story_viewer_overlay").assertIsDisplayed()
    composeTestRule.onNodeWithTag("story_next_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(100)
    composeTestRule.onNodeWithTag("story_viewer_close_button").performClick()
    composeTestRule.mainClock.autoAdvance = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("story_viewer_overlay").assertDoesNotExist()

    composeTestRule.onNodeWithTag("like_button_post_alexa_sunlight").performScrollTo().performClick()
    composeTestRule.onNodeWithText("1483").assertIsDisplayed()

    composeTestRule.onNodeWithTag("comment_button_post_alexa_sunlight").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("comments_bottom_sheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("comment_input_field").performTextInput("Helpful reminder!")
    composeTestRule.onNodeWithTag("comment_send_button").performClick()
    composeTestRule.onNodeWithText("Helpful reminder!").assertIsDisplayed()
  }

  @Test
  fun createPostPublishesToFeed() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithTag("nav_create_post_button").performClick()
    composeTestRule.onNodeWithTag("create_post_body_input").performTextInput("Grateful for steady focus today.")
    composeTestRule.onNodeWithTag("publish_post_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Grateful for steady focus today.").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun streakCheckInSavesAndIncrementsStreak() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithTag("nav_streak_button").performClick()
    composeTestRule.onNodeWithTag("mood_option_happy").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("save_checkin_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("streak_feed_list").performScrollToIndex(0)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("1-Day Wellness Streak").assertIsDisplayed()
  }

  @Test
  fun headerSearchNotificationsAndProfileSheetsWork() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithTag("header_search_button").performClick()
    composeTestRule.onNodeWithText("Search PsyQuest").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dismiss_utility_sheet_button").performClick()

    composeTestRule.onNodeWithTag("header_notifications_button").performClick()
    composeTestRule.onNodeWithText("Wellness & Activity").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dismiss_utility_sheet_button").performClick()

    composeTestRule.onNodeWithTag("header_profile_button").performClick()
    composeTestRule.onNodeWithTag("profile_bottom_sheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profile_sign_out_button").assertIsDisplayed()
  }

  @Test
  fun gamesHubOpensTriviaGame() {
    val vm = createViewModel()
    composeTestRule.setContent {
      PsyQuestTheme { PsyQuestAppShell(viewModel = vm, userEmail = "explorer@test.com", onSignOut = {}) }
    }

    composeTestRule.onNodeWithTag("nav_games_button").performClick()
    composeTestRule.onNodeWithTag("play_trivia_button").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("trivia_game_screen").assertIsDisplayed()
  }
}

private class InMemoryTestDataSource : PsyQuestDataSource {
  private val profile =
    MutableStateFlow<UserProfileDoc?>(
      UserProfileDoc(id = "test_uid", userId = "test_uid", displayName = "Test Explorer", handle = "explorer")
    )
  private val posts = MutableStateFlow<List<FreedomWallPostDoc>>(emptyList())
  private val comments = MutableStateFlow<Map<String, List<PostCommentDoc>>>(emptyMap())
  private val hiddenIds = MutableStateFlow<Set<String>>(emptySet())
  private val checkIns = MutableStateFlow<List<DailyCheckInDoc>>(emptyList())
  private val bookmarks = MutableStateFlow<List<UserBookmarkDoc>>(emptyList())
  private val games = MutableStateFlow<Map<String, GameProgressDoc>>(emptyMap())

  override suspend fun ensureUserProfile(defaultDisplayName: String, defaultHandleBase: String): UserProfileDoc =
    profile.value!!

  override fun observeUserProfile(userId: String): Flow<UserProfileDoc?> = profile

  override suspend fun updateUserProfile(displayName: String, handle: String, bio: String, avatarBase64: String?) {
    profile.value = profile.value?.copy(displayName = displayName, handle = handle, bio = bio, avatarBase64 = avatarBase64)
  }

  override fun observePublicPosts(): Flow<List<FreedomWallPostDoc>> = posts

  override fun observeMyPosts(userId: String): Flow<List<FreedomWallPostDoc>> = posts

  override suspend fun publishPost(
    authorName: String,
    authorMeta: String,
    authorAvatarBase64: String?,
    category: String,
    visibility: String,
    bodyText: String,
    imageBase64: String?,
    quoteHighlight: String?,
  ): Result<String> {
    val id = "post_${UUID.randomUUID()}"
    posts.value =
      listOf(
        FreedomWallPostDoc(
          id = id,
          userId = "test_uid",
          authorName = authorName,
          authorMeta = authorMeta,
          category = category,
          visibility = visibility,
          bodyText = bodyText,
          imageBase64 = imageBase64,
          quoteHighlight = quoteHighlight,
        )
      ) + posts.value
    return Result.success(id)
  }

  override suspend fun deletePost(postId: String) {
    posts.value = posts.value.filterNot { it.id == postId }
  }

  override suspend fun isPostLikedByCurrentUser(postId: String): Boolean = false

  override suspend fun togglePostLike(postId: String, currentlyLiked: Boolean) {}

  override fun observeComments(postId: String): Flow<List<PostCommentDoc>> =
    comments.map { it[postId].orEmpty() }

  override suspend fun addComment(postId: String, authorName: String, text: String) {
    val list = comments.value[postId].orEmpty()
    comments.value = comments.value + (postId to (list + PostCommentDoc(id = UUID.randomUUID().toString(), postId = postId, authorName = authorName, text = text)))
  }

  override suspend fun incrementShareCount(postId: String) {}

  override fun observeHiddenPostIds(userId: String): Flow<Set<String>> = hiddenIds

  override suspend fun hidePost(postId: String) {
    hiddenIds.value = hiddenIds.value + postId
  }

  override suspend fun unhidePost(postId: String) {
    hiddenIds.value = hiddenIds.value - postId
  }

  override suspend fun reportPost(postId: String, reason: String) {}

  override fun observeCheckIns(userId: String): Flow<List<DailyCheckInDoc>> = checkIns

  override suspend fun saveDailyCheckIn(
    dateKey: String,
    moodKey: String,
    moodEmoji: String,
    reflection: String,
    affirmation: String,
    completedActivity: String,
  ) {
    val doc =
      DailyCheckInDoc(
        id = dateKey,
        userId = "test_uid",
        dateKey = dateKey,
        moodKey = moodKey,
        moodEmoji = moodEmoji,
        reflection = reflection,
        affirmation = affirmation,
        completedActivity = completedActivity,
      )
    checkIns.value = listOf(doc) + checkIns.value.filterNot { it.dateKey == dateKey }
  }

  override fun observeBookmarks(userId: String): Flow<List<UserBookmarkDoc>> = bookmarks

  override suspend fun toggleBookmark(itemType: String, targetId: String, title: String, subtitle: String): Boolean {
    val existing = bookmarks.value.any { it.itemType == itemType && it.targetId == targetId }
    if (existing) {
      bookmarks.value = bookmarks.value.filterNot { it.itemType == itemType && it.targetId == targetId }
      return false
    }
    bookmarks.value =
      bookmarks.value +
        UserBookmarkDoc(id = "${itemType}_$targetId", userId = "test_uid", itemType = itemType, targetId = targetId, title = title, subtitle = subtitle)
    return true
  }

  override fun observeGameProgress(userId: String): Flow<Map<String, GameProgressDoc>> = games

  override suspend fun recordGameScore(gameId: String, score: Int) {
    val prev = games.value[gameId]
    games.value =
      games.value +
        (gameId to
          GameProgressDoc(
            id = gameId,
            userId = "test_uid",
            gameId = gameId,
            highScore = maxOf(prev?.highScore ?: 0L, score.toLong()),
            lastScore = score.toLong(),
            gamesPlayed = (prev?.gamesPlayed ?: 0L) + 1L,
          ))
  }
}
