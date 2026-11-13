package com.example.ui.viewmodel

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DailyCheckInDoc
import com.example.data.FreedomWallPostDoc
import com.example.data.GameProgressDoc
import com.example.data.PostCommentDoc
import com.example.data.PsyQuestDataSource
import com.example.data.PsyQuestRepository
import com.example.data.UserBookmarkDoc
import com.example.data.UserProfileDoc
import com.example.model.ELibraryResource
import com.example.model.FeedTab
import com.example.model.FreedomWallPost
import com.example.model.PostCategory
import com.example.model.PostComment
import com.example.model.ReportReason
import com.example.model.SampleFreedomWallData
import com.example.model.StoryItem
import com.google.firebase.firestore.FirebaseFirestoreException
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HeaderUtilitySheet {
  NONE,
  SEARCH,
  NOTIFICATIONS,
  PROFILE,
}

data class PsyQuestUiState(
  val currentUserId: String = "",
  val currentUserPhotoUrl: String? = null,
  val userProfile: UserProfileDoc? = null,
  val selectedFeedTab: FeedTab = FeedTab.FOR_YOU,
  val stories: List<StoryItem> = SampleFreedomWallData.stories,
  val activeStoryIndex: Int? = null,
  val allPosts: List<FreedomWallPost> = SampleFreedomWallData.initialPosts,
  val hiddenPostIds: Set<String> = emptySet(),
  val reportedPostIds: Set<String> = emptySet(),
  val lastHiddenPostId: String? = null,
  val commentsByPostId: Map<String, List<PostComment>> = SampleFreedomWallData.initialCommentsByPostId,
  val activeCommentPostId: String? = null,
  val activeSharePostId: String? = null,
  val activeReportPostId: String? = null,
  val activeSheet: HeaderUtilitySheet = HeaderUtilitySheet.NONE,
  val checkIns: List<DailyCheckInDoc> = emptyList(),
  val bookmarks: List<UserBookmarkDoc> = emptyList(),
  val gameProgress: Map<String, GameProgressDoc> = emptyMap(),
  val isPublishingPost: Boolean = false,
  val isSavingCheckIn: Boolean = false,
  val isSavingProfile: Boolean = false,
  val statusMessage: String? = null,
) {
  val visibleFeedPosts: List<FreedomWallPost>
    get() =
      allPosts.filter { post ->
        selectedFeedTab in post.feedTabs && post.id !in hiddenPostIds
      }

  val bookmarkedPostIds: Set<String>
    get() = bookmarks.filter { it.itemType == "POST" }.map { it.targetId }.toSet()

  val bookmarkedLibraryIds: Set<String>
    get() = bookmarks.filter { it.itemType == "LIBRARY" }.map { it.targetId }.toSet()

  val myPosts: List<FreedomWallPost>
    get() = allPosts.filter { it.isOwnedByCurrentUser }

  val currentStreakCount: Int
    get() =
      PsyQuestRepository.calculateStreaksFromDates(checkIns.map { it.dateKey }.toSet()).first

  val longestStreakCount: Int
    get() =
      PsyQuestRepository.calculateStreaksFromDates(checkIns.map { it.dateKey }.toSet()).second

  val todayCheckIn: DailyCheckInDoc?
    get() = checkIns.firstOrNull { it.dateKey == LocalDate.now().toString() }

  val activeCommentPost: FreedomWallPost?
    get() = activeCommentPostId?.let { id -> allPosts.firstOrNull { it.id == id } }

  val activeSharePost: FreedomWallPost?
    get() = activeSharePostId?.let { id -> allPosts.firstOrNull { it.id == id } }

  val activeReportPost: FreedomWallPost?
    get() = activeReportPostId?.let { id -> allPosts.firstOrNull { it.id == id } }
}

private data class LocalUiOverrides(
  val selectedFeedTab: FeedTab = FeedTab.FOR_YOU,
  val activeStoryIndex: Int? = null,
  val activeCommentPostId: String? = null,
  val activeSharePostId: String? = null,
  val activeReportPostId: String? = null,
  val activeSheet: HeaderUtilitySheet = HeaderUtilitySheet.NONE,
  val likedFirestorePostIds: Set<String> = emptySet(),
  val samplePosts: List<FreedomWallPost> = SampleFreedomWallData.initialPosts,
  val sampleComments: Map<String, List<PostComment>> = SampleFreedomWallData.initialCommentsByPostId,
  val locallyHiddenIds: Set<String> = emptySet(),
  val reportedIds: Set<String> = emptySet(),
  val lastHiddenPostId: String? = null,
  val isPublishingPost: Boolean = false,
  val isSavingCheckIn: Boolean = false,
  val isSavingProfile: Boolean = false,
  val statusMessage: String? = null,
)

private data class RemoteStreamsBundle(
  val profile: UserProfileDoc? = null,
  val publicPosts: List<FreedomWallPostDoc> = emptyList(),
  val myPosts: List<FreedomWallPostDoc> = emptyList(),
  val hiddenIds: Set<String> = emptySet(),
  val checkIns: List<DailyCheckInDoc> = emptyList(),
  val bookmarks: List<UserBookmarkDoc> = emptyList(),
  val gameProgress: Map<String, GameProgressDoc> = emptyMap(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class PsyQuestViewModel(
  private val repository: PsyQuestDataSource,
  private val currentUserId: String,
  initialDisplayName: String,
  initialHandleBase: String,
  private val initialPhotoUrl: String? = null,
) : ViewModel() {

  private val localOverrides = MutableStateFlow(LocalUiOverrides())
  private val activeCommentTarget = MutableStateFlow<String?>(null)

  init {
    viewModelScope.launch {
      runCatching { repository.ensureUserProfile(initialDisplayName, initialHandleBase) }
        .onFailure { Log.w(TAG, "Profile ensure failed: ${it.message}") }
    }
  }

  private fun <T> Flow<T>.safeCatch(fallback: T, label: String): Flow<T> =
    catch { error ->
      val firestoreError =
        generateSequence(error) { it.cause }.filterIsInstance<FirebaseFirestoreException>().firstOrNull()
      if (firestoreError == null && error is CancellationException) throw error
      val root = firestoreError ?: error
      Log.w(TAG, "Error observing $label: ${root.message}", root)
      emit(fallback)
    }

  private val remoteBundleFlow: Flow<RemoteStreamsBundle> =
    combine(
      repository.observeUserProfile(currentUserId).safeCatch(null, "profile"),
      repository.observePublicPosts().safeCatch(emptyList(), "public_posts"),
      repository.observeMyPosts(currentUserId).safeCatch(emptyList(), "my_posts"),
      repository.observeHiddenPostIds(currentUserId).safeCatch(emptySet(), "hidden_posts"),
      repository.observeCheckIns(currentUserId).safeCatch(emptyList(), "check_ins"),
    ) { profile, publicPosts, myPosts, hiddenIds, checkIns ->
      RemoteStreamsBundle(
        profile = profile,
        publicPosts = publicPosts,
        myPosts = myPosts,
        hiddenIds = hiddenIds,
        checkIns = checkIns,
      )
    }.combine(
      combine(
        repository.observeBookmarks(currentUserId).safeCatch(emptyList(), "bookmarks"),
        repository.observeGameProgress(currentUserId).safeCatch(emptyMap(), "game_progress"),
      ) { bookmarks, games -> bookmarks to games }
    ) { bundle, (bookmarks, games) ->
      bundle.copy(bookmarks = bookmarks, gameProgress = games)
    }

  private val activeFirestoreCommentsFlow: StateFlow<List<PostCommentDoc>> =
    activeCommentTarget
      .flatMapLatest { postId ->
        if (postId == null || localOverrides.value.samplePosts.any { it.id == postId }) {
          flowOf(emptyList())
        } else {
          repository.observeComments(postId).safeCatch(emptyList(), "comments_$postId")
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = emptyList(),
      )

  val uiState: StateFlow<PsyQuestUiState> =
    combine(remoteBundleFlow, localOverrides, activeFirestoreCommentsFlow) {
      remote,
      local,
      activeComments ->
      val bookmarkedPostIds =
        remote.bookmarks.filter { it.itemType == "POST" }.map { it.targetId }.toSet()

      val mergedFirestoreDocs =
        (remote.myPosts + remote.publicPosts).distinctBy { it.id }.sortedByDescending {
          it.createdAt?.toDate()?.time ?: Long.MAX_VALUE
        }

      val livePosts =
        mergedFirestoreDocs.map { doc ->
          doc.toUiPost(
            currentUserId = currentUserId,
            isLiked = doc.id in local.likedFirestorePostIds,
            isBookmarked = doc.id in bookmarkedPostIds,
          )
        }

      val editorialPosts =
        local.samplePosts.map { sample ->
          sample.copy(isBookmarked = sample.id in bookmarkedPostIds)
        }

      val combinedPosts = livePosts + editorialPosts

      val commentsMap =
        if (activeComments.isNotEmpty() && local.activeCommentPostId != null) {
          val mapped =
            activeComments.map { doc ->
              PostComment(
                id = doc.id,
                authorName = doc.authorName,
                timeAgo = formatRelativeTime(doc.createdAt?.toDate()?.time),
                text = doc.text,
              )
            }
          local.sampleComments + (local.activeCommentPostId to mapped)
        } else {
          local.sampleComments
        }

      PsyQuestUiState(
        currentUserId = currentUserId,
        currentUserPhotoUrl = initialPhotoUrl,
        userProfile = remote.profile,
        selectedFeedTab = local.selectedFeedTab,
        stories = SampleFreedomWallData.stories,
        activeStoryIndex = local.activeStoryIndex,
        allPosts = combinedPosts,
        hiddenPostIds = remote.hiddenIds + local.locallyHiddenIds,
        reportedPostIds = local.reportedIds,
        lastHiddenPostId = local.lastHiddenPostId,
        commentsByPostId = commentsMap,
        activeCommentPostId = local.activeCommentPostId,
        activeSharePostId = local.activeSharePostId,
        activeReportPostId = local.activeReportPostId,
        activeSheet = local.activeSheet,
        checkIns = remote.checkIns,
        bookmarks = remote.bookmarks,
        gameProgress = remote.gameProgress,
        isPublishingPost = local.isPublishingPost,
        isSavingCheckIn = local.isSavingCheckIn,
        isSavingProfile = local.isSavingProfile,
        statusMessage = local.statusMessage,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
      initialValue = PsyQuestUiState(currentUserId = currentUserId, currentUserPhotoUrl = initialPhotoUrl),
    )

  fun selectFeedTab(tab: FeedTab) {
    localOverrides.update { it.copy(selectedFeedTab = tab) }
  }

  fun openStory(index: Int) {
    if (index in SampleFreedomWallData.stories.indices) {
      localOverrides.update { it.copy(activeStoryIndex = index) }
    }
  }

  fun nextStory() {
    localOverrides.update { state ->
      val current = state.activeStoryIndex ?: return@update state
      val next = current + 1
      if (next < SampleFreedomWallData.stories.size) {
        state.copy(activeStoryIndex = next)
      } else {
        state.copy(activeStoryIndex = null)
      }
    }
  }

  fun previousStory() {
    localOverrides.update { state ->
      val current = state.activeStoryIndex ?: return@update state
      state.copy(activeStoryIndex = (current - 1).coerceAtLeast(0))
    }
  }

  fun closeStory() {
    localOverrides.update { it.copy(activeStoryIndex = null) }
  }

  fun togglePostLike(postId: String) {
    val isSample = localOverrides.value.samplePosts.any { it.id == postId }
    if (isSample) {
      localOverrides.update { state ->
        val updated =
          state.samplePosts.map { post ->
            if (post.id == postId) {
              val nextLiked = !post.isLiked
              val nextCount = if (nextLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
              post.copy(isLiked = nextLiked, likeCount = nextCount)
            } else {
              post
            }
          }
        state.copy(samplePosts = updated)
      }
      return
    }

    val currentlyLiked = postId in localOverrides.value.likedFirestorePostIds
    localOverrides.update { state ->
      val nextSet =
        if (currentlyLiked) state.likedFirestorePostIds - postId
        else state.likedFirestorePostIds + postId
      state.copy(likedFirestorePostIds = nextSet)
    }
    viewModelScope.launch {
      runCatching {
          val serverLiked = repository.isPostLikedByCurrentUser(postId)
          repository.togglePostLike(postId, currentlyLiked = serverLiked)
        }
        .onFailure { Log.w(TAG, "togglePostLike failed: ${it.message}") }
    }
  }

  fun togglePostBookmark(postId: String): Boolean {
    val post = uiState.value.allPosts.firstOrNull { it.id == postId } ?: return false
    val currentlyBookmarked = post.isBookmarked
    viewModelScope.launch {
      runCatching {
        repository.toggleBookmark(
          itemType = "POST",
          targetId = post.id,
          title = post.authorName,
          subtitle = post.bodyText.take(120),
        )
      }
    }
    return !currentlyBookmarked
  }

  fun toggleLibraryBookmark(resource: ELibraryResource): Boolean {
    val currentlySaved = resource.id in uiState.value.bookmarkedLibraryIds
    viewModelScope.launch {
      runCatching {
        repository.toggleBookmark(
          itemType = "LIBRARY",
          targetId = resource.id,
          title = resource.title,
          subtitle = "${resource.category.displayName} • ${resource.sourcePlatform}",
        )
      }
    }
    return !currentlySaved
  }

  fun openComments(postId: String) {
    activeCommentTarget.value = postId
    localOverrides.update { it.copy(activeCommentPostId = postId) }
  }

  fun dismissComments() {
    activeCommentTarget.value = null
    localOverrides.update { it.copy(activeCommentPostId = null) }
  }

  fun addComment(postId: String, text: String) {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return
    val authorName =
      uiState.value.userProfile?.displayName?.takeIf { it.isNotBlank() } ?: "PsyQuest Explorer"

    val isSample = localOverrides.value.samplePosts.any { it.id == postId }
    if (isSample) {
      localOverrides.update { state ->
        val existing = state.sampleComments[postId].orEmpty()
        val newComment =
          PostComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = authorName,
            timeAgo = "Just now",
            text = trimmed,
          )
        val updatedPosts =
          state.samplePosts.map { post ->
            if (post.id == postId) post.copy(commentCount = post.commentCount + 1) else post
          }
        state.copy(
          sampleComments = state.sampleComments + (postId to (existing + newComment)),
          samplePosts = updatedPosts,
        )
      }
      return
    }

    viewModelScope.launch {
      runCatching { repository.addComment(postId, authorName, trimmed) }
        .onFailure { Log.w(TAG, "addComment failed: ${it.message}") }
    }
  }

  fun openShareSheet(postId: String) {
    localOverrides.update { it.copy(activeSharePostId = postId) }
  }

  fun dismissShareSheet() {
    localOverrides.update { it.copy(activeSharePostId = null) }
  }

  fun incrementShareCount(postId: String) {
    val isSample = localOverrides.value.samplePosts.any { it.id == postId }
    if (isSample) {
      localOverrides.update { state ->
        val updated =
          state.samplePosts.map { post ->
            if (post.id == postId) post.copy(shareCount = post.shareCount + 1) else post
          }
        state.copy(samplePosts = updated)
      }
      return
    }
    viewModelScope.launch { runCatching { repository.incrementShareCount(postId) } }
  }

  fun openReportDialog(postId: String) {
    localOverrides.update { it.copy(activeReportPostId = postId) }
  }

  fun dismissReportDialog() {
    localOverrides.update { it.copy(activeReportPostId = null) }
  }

  fun submitReport(postId: String, reason: ReportReason) {
    localOverrides.update { state ->
      state.copy(reportedIds = state.reportedIds + postId, activeReportPostId = null)
    }
    viewModelScope.launch {
      runCatching { repository.reportPost(postId, reason.name) }
        .onFailure { Log.w(TAG, "reportPost failed: ${it.message}") }
    }
  }

  fun hidePost(postId: String) {
    localOverrides.update { state ->
      state.copy(locallyHiddenIds = state.locallyHiddenIds + postId, lastHiddenPostId = postId)
    }
    viewModelScope.launch { runCatching { repository.hidePost(postId) } }
  }

  fun undoLastHiddenPost() {
    val target = localOverrides.value.lastHiddenPostId ?: return
    localOverrides.update { state ->
      state.copy(locallyHiddenIds = state.locallyHiddenIds - target, lastHiddenPostId = null)
    }
    viewModelScope.launch { runCatching { repository.unhidePost(target) } }
  }

  fun restoreAllHiddenPosts() {
    val hiddenNow = uiState.value.hiddenPostIds
    localOverrides.update { it.copy(locallyHiddenIds = emptySet(), lastHiddenPostId = null) }
    viewModelScope.launch {
      hiddenNow.forEach { postId -> runCatching { repository.unhidePost(postId) } }
    }
  }

  fun publishPost(
    category: PostCategory,
    visibility: String,
    bodyText: String,
    imageBase64: String?,
    quoteHighlight: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    val cleanBody = bodyText.trim()
    if (cleanBody.isEmpty()) {
      onError("Please write a reflection or caption before publishing.")
      return
    }
    val profile = uiState.value.userProfile
    val authorName = profile?.displayName?.takeIf { it.isNotBlank() } ?: "PsyQuest Explorer"
    val authorMeta =
      profile?.handle?.takeIf { it.isNotBlank() }?.let { "@$it" } ?: "PsyQuest Member"

    localOverrides.update { it.copy(isPublishingPost = true) }
    viewModelScope.launch {
      val result =
        repository.publishPost(
          authorName = authorName,
          authorMeta = authorMeta,
          authorAvatarBase64 = profile?.avatarBase64,
          category = category.name,
          visibility = visibility,
          bodyText = cleanBody,
          imageBase64 = imageBase64,
          quoteHighlight = quoteHighlight,
        )
      localOverrides.update { it.copy(isPublishingPost = false) }
      if (result.isSuccess) {
        onSuccess()
      } else {
        onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to publish post.")
      }
    }
  }

  fun deleteOwnPost(postId: String, onDone: () -> Unit = {}) {
    viewModelScope.launch {
      runCatching { repository.deletePost(postId) }
        .onSuccess { onDone() }
        .onFailure { Log.w(TAG, "deletePost failed: ${it.message}") }
    }
  }

  fun saveDailyCheckIn(
    moodKey: String,
    moodEmoji: String,
    reflection: String,
    affirmation: String,
    completedActivity: String,
    onSaved: () -> Unit,
    onError: (String) -> Unit,
  ) {
    val todayKey = LocalDate.now().toString()
    localOverrides.update { it.copy(isSavingCheckIn = true) }
    viewModelScope.launch {
      runCatching {
          repository.saveDailyCheckIn(
            dateKey = todayKey,
            moodKey = moodKey,
            moodEmoji = moodEmoji,
            reflection = reflection,
            affirmation = affirmation,
            completedActivity = completedActivity,
          )
        }
        .onSuccess {
          localOverrides.update { it.copy(isSavingCheckIn = false) }
          onSaved()
        }
        .onFailure { error ->
          localOverrides.update { it.copy(isSavingCheckIn = false) }
          onError(error.localizedMessage ?: "Could not save check-in.")
        }
    }
  }

  fun recordGameResult(gameId: String, score: Int) {
    viewModelScope.launch {
      runCatching { repository.recordGameScore(gameId, score) }
        .onFailure { Log.w(TAG, "recordGameScore failed: ${it.message}") }
    }
  }

  fun updateUserProfile(
    displayName: String,
    handle: String,
    bio: String,
    avatarBase64: String?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
  ) {
    localOverrides.update { it.copy(isSavingProfile = true) }
    viewModelScope.launch {
      runCatching { repository.updateUserProfile(displayName, handle, bio, avatarBase64) }
        .onSuccess {
          localOverrides.update { it.copy(isSavingProfile = false) }
          onSuccess()
        }
        .onFailure { error ->
          localOverrides.update { it.copy(isSavingProfile = false) }
          onError(error.localizedMessage ?: "Could not update profile.")
        }
    }
  }

  fun openSearchSheet() {
    localOverrides.update { it.copy(activeSheet = HeaderUtilitySheet.SEARCH) }
  }

  fun openNotificationsSheet() {
    localOverrides.update { it.copy(activeSheet = HeaderUtilitySheet.NOTIFICATIONS) }
  }

  fun openProfileSheet() {
    localOverrides.update { it.copy(activeSheet = HeaderUtilitySheet.PROFILE) }
  }

  fun dismissSheet() {
    localOverrides.update { it.copy(activeSheet = HeaderUtilitySheet.NONE) }
  }

  private fun FreedomWallPostDoc.toUiPost(
    currentUserId: String,
    isLiked: Boolean,
    isBookmarked: Boolean,
  ): FreedomWallPost {
    val parsedCategory =
      runCatching { PostCategory.valueOf(category) }.getOrDefault(PostCategory.PERSONAL_THOUGHTS)
    val tabs =
      when (visibility) {
        "FRIENDS" -> setOf(FeedTab.MY_FEED, FeedTab.FRIENDS)
        else -> setOf(FeedTab.FOR_YOU, FeedTab.MY_FEED)
      }
    return FreedomWallPost(
      id = id,
      authorUserId = userId,
      authorName = authorName.ifBlank { "PsyQuest Member" },
      authorMeta = authorMeta.ifBlank { "Freedom Wall" },
      authorAvatarBase64 = authorAvatarBase64,
      timestamp = formatRelativeTime(createdAt?.toDate()?.time),
      category = parsedCategory,
      feedTabs = tabs,
      bodyText = bodyText,
      imageBase64 = imageBase64,
      highlightCardText = quoteHighlight,
      likeCount = likeCount.toInt().coerceAtLeast(0),
      isLiked = isLiked,
      commentCount = commentCount.toInt().coerceAtLeast(0),
      shareCount = shareCount.toInt().coerceAtLeast(0),
      isBookmarked = isBookmarked,
      shareUrl = "https://psyquest.app/freedom-wall/$id",
      isOwnedByCurrentUser = userId == currentUserId,
      isEditorialHighlight = false,
    )
  }

  private fun formatRelativeTime(millis: Long?): String {
    if (millis == null || millis <= 0L) return "Just now"
    return DateUtils.getRelativeTimeSpanString(
        millis,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
      )
      .toString()
  }

  private companion object {
    const val TAG = "PsyQuestVM"
    const val STOP_TIMEOUT_MS = 5000L
  }
}
