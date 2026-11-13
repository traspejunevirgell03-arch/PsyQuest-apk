package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface PsyQuestDataSource {
  suspend fun ensureUserProfile(defaultDisplayName: String, defaultHandleBase: String): UserProfileDoc
  fun observeUserProfile(userId: String): Flow<UserProfileDoc?>
  suspend fun updateUserProfile(displayName: String, handle: String, bio: String, avatarBase64: String?)
  fun observePublicPosts(): Flow<List<FreedomWallPostDoc>>
  fun observeMyPosts(userId: String): Flow<List<FreedomWallPostDoc>>
  suspend fun publishPost(
    authorName: String,
    authorMeta: String,
    authorAvatarBase64: String?,
    category: String,
    visibility: String,
    bodyText: String,
    imageBase64: String?,
    quoteHighlight: String?,
  ): Result<String>
  suspend fun deletePost(postId: String)
  suspend fun isPostLikedByCurrentUser(postId: String): Boolean
  suspend fun togglePostLike(postId: String, currentlyLiked: Boolean)
  fun observeComments(postId: String): Flow<List<PostCommentDoc>>
  suspend fun addComment(postId: String, authorName: String, text: String)
  suspend fun incrementShareCount(postId: String)
  fun observeHiddenPostIds(userId: String): Flow<Set<String>>
  suspend fun hidePost(postId: String)
  suspend fun unhidePost(postId: String)
  suspend fun reportPost(postId: String, reason: String)
  fun observeCheckIns(userId: String): Flow<List<DailyCheckInDoc>>
  suspend fun saveDailyCheckIn(
    dateKey: String,
    moodKey: String,
    moodEmoji: String,
    reflection: String,
    affirmation: String,
    completedActivity: String,
  )
  fun observeBookmarks(userId: String): Flow<List<UserBookmarkDoc>>
  suspend fun toggleBookmark(itemType: String, targetId: String, title: String, subtitle: String): Boolean
  fun observeGameProgress(userId: String): Flow<Map<String, GameProgressDoc>>
  suspend fun recordGameScore(gameId: String, score: Int)
}

class PsyQuestRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = Firebase.auth,
) : PsyQuestDataSource {
  constructor(
    context: Context
  ) : this(
    db =
      FirebaseFirestore.getInstance(
        context.applicationContext.getString(R.string.firestore_database_id)
      ),
    auth = Firebase.auth,
  )

  private fun requireUserId(): String =
    auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

  private fun <T> Flow<T>.handleSnapshotError(
    operationType: OperationType,
    path: String,
    uid: String,
  ): Flow<T> = catch { error ->
    val firestoreError =
      generateSequence(error) { it.cause }.filterIsInstance<FirebaseFirestoreException>().firstOrNull()
    if (firestoreError == null && error is CancellationException) throw error
    val exceptionToLog = firestoreError ?: (error as? Exception)
    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
      handleFirestoreError(exceptionToLog, operationType, path, auth)
    }
    throw error
  }

  // region User Profile

  override suspend fun ensureUserProfile(defaultDisplayName: String, defaultHandleBase: String): UserProfileDoc {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    return try {
      val snapshot = docRef.get().await()
      if (snapshot.exists()) {
        snapshot.toObject(UserProfileDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
          ?: UserProfileDoc(id = uid, userId = uid, displayName = defaultDisplayName, handle = defaultHandleBase)
      } else {
        val cleanName = defaultDisplayName.trim().take(40).ifBlank { "PsyQuest Explorer" }
        val cleanHandle =
          defaultHandleBase
            .lowercase()
            .replace(Regex("[^a-z0-9_.]"), "")
            .take(26)
            .ifBlank { "explorer" }
        val payload =
          mapOf(
            "userId" to uid,
            "displayName" to cleanName,
            "handle" to cleanHandle,
            "bio" to "On my Psychological Side Quest",
            "currentStreak" to 0L,
            "longestStreak" to 0L,
            "totalCheckIns" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        docRef.set(payload).await()
        UserProfileDoc(
          id = uid,
          userId = uid,
          displayName = cleanName,
          handle = cleanHandle,
          bio = "On my Psychological Side Quest",
        )
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path, auth)
      throw e
    }
  }

  override fun observeUserProfile(userId: String): Flow<UserProfileDoc?> = flow {
    val uid = requireUserId()
    require(uid == userId) { "Profile access restricted to authenticated owner" }
    val docRef = db.collection("users").document(userId)
    emitAll(
      docRef
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) {
            snapshot.toObject(
              UserProfileDoc::class.java,
              DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
            )
          } else {
            null
          }
        }
        .handleSnapshotError(OperationType.GET, docRef.path, uid)
    )
  }

  override suspend fun updateUserProfile(
    displayName: String,
    handle: String,
    bio: String,
    avatarBase64: String?,
  ) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    val cleanHandle =
      handle.trim().removePrefix("@").replace(Regex("[^a-zA-Z0-9_.]"), "").take(30).ifBlank { "explorer" }
    val payload =
      mapOf(
        "displayName" to displayName.trim().take(40).ifBlank { "PsyQuest Explorer" },
        "handle" to cleanHandle,
        "bio" to bio.trim().take(180),
        "avatarBase64" to avatarBase64?.takeIf { it.isNotBlank() },
        "updatedAt" to FieldValue.serverTimestamp(),
      ).filterValues { it != null }
    try {
      docRef.update(payload).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
      throw e
    }
  }

  // endregion

  // region Freedom Wall Posts, Likes & Comments

  override fun observePublicPosts(): Flow<List<FreedomWallPostDoc>> = flow {
    val uid = requireUserId()
    val path = "posts"
    emitAll(
      db.collection(path)
        .whereEqualTo("visibility", "PUBLIC")
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(FreedomWallPostDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override fun observeMyPosts(userId: String): Flow<List<FreedomWallPostDoc>> = flow {
    val uid = requireUserId()
    val path = "posts"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(FreedomWallPostDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

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
    val uid = requireUserId()
    val docRef = db.collection("posts").document()
    val payload =
      mapOf(
        "userId" to uid,
        "authorName" to authorName.trim().take(50).ifBlank { "PsyQuest Explorer" },
        "authorMeta" to authorMeta.trim().take(60),
        "authorAvatarBase64" to authorAvatarBase64?.takeIf { it.isNotBlank() },
        "category" to category,
        "visibility" to visibility,
        "bodyText" to bodyText.trim().take(1500),
        "imageBase64" to imageBase64?.takeIf { it.isNotBlank() },
        "quoteHighlight" to quoteHighlight?.trim()?.take(200)?.takeIf { it.isNotBlank() },
        "likeCount" to 0L,
        "commentCount" to 0L,
        "shareCount" to 0L,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
      ).filterValues { it != null }
    return try {
      docRef.set(payload).await()
      Result.success(docRef.id)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
      Result.failure(e)
    }
  }

  override suspend fun deletePost(postId: String) {
    requireUserId()
    val docRef = db.collection("posts").document(postId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path, auth)
      throw e
    }
  }

  override suspend fun isPostLikedByCurrentUser(postId: String): Boolean {
    val uid = requireUserId()
    val likeRef = db.collection("posts").document(postId).collection("likes").document(uid)
    return try {
      likeRef.get().await().exists()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, likeRef.path, auth)
      false
    }
  }

  override suspend fun togglePostLike(postId: String, currentlyLiked: Boolean) {
    val uid = requireUserId()
    val postRef = db.collection("posts").document(postId)
    val likeRef = postRef.collection("likes").document(uid)
    try {
      if (currentlyLiked) {
        likeRef.delete().await()
        postRef.update(mapOf("likeCount" to FieldValue.increment(-1L), "updatedAt" to FieldValue.serverTimestamp())).await()
      } else {
        likeRef.set(mapOf("userId" to uid, "createdAt" to FieldValue.serverTimestamp())).await()
        postRef.update(mapOf("likeCount" to FieldValue.increment(1L), "updatedAt" to FieldValue.serverTimestamp())).await()
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, postRef.path, auth)
      throw e
    }
  }

  override fun observeComments(postId: String): Flow<List<PostCommentDoc>> = flow {
    val uid = requireUserId()
    val path = "posts/$postId/comments"
    emitAll(
      db.collection("posts")
        .document(postId)
        .collection("comments")
        .whereEqualTo("postId", postId)
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(PostCommentDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedBy { it.createdAt?.toDate()?.time ?: 0L }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override suspend fun addComment(postId: String, authorName: String, text: String) {
    val uid = requireUserId()
    val postRef = db.collection("posts").document(postId)
    val commentRef = postRef.collection("comments").document()
    val payload =
      mapOf(
        "userId" to uid,
        "postId" to postId,
        "authorName" to authorName.trim().take(50).ifBlank { "Explorer" },
        "text" to text.trim().take(500),
        "createdAt" to FieldValue.serverTimestamp(),
      )
    try {
      commentRef.set(payload).await()
      postRef
        .update(mapOf("commentCount" to FieldValue.increment(1L), "updatedAt" to FieldValue.serverTimestamp()))
        .await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, commentRef.path, auth)
      throw e
    }
  }

  override suspend fun incrementShareCount(postId: String) {
    requireUserId()
    val postRef = db.collection("posts").document(postId)
    try {
      postRef
        .update(mapOf("shareCount" to FieldValue.increment(1L), "updatedAt" to FieldValue.serverTimestamp()))
        .await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, postRef.path, auth)
    }
  }

  override fun observeHiddenPostIds(userId: String): Flow<Set<String>> = flow {
    val uid = requireUserId()
    val path = "users/$userId/hidden_posts"
    emitAll(
      db.collection("users")
        .document(userId)
        .collection("hidden_posts")
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(HiddenPostDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .map { it.postId }
            .toSet()
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override suspend fun hidePost(postId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("hidden_posts").document(postId)
    try {
      docRef.set(mapOf("userId" to uid, "postId" to postId, "createdAt" to FieldValue.serverTimestamp())).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
      throw e
    }
  }

  override suspend fun unhidePost(postId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("hidden_posts").document(postId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path, auth)
      throw e
    }
  }

  override suspend fun reportPost(postId: String, reason: String) {
    val uid = requireUserId()
    val docRef = db.collection("post_reports").document()
    val payload =
      mapOf(
        "reporterUid" to uid,
        "postId" to postId,
        "reason" to reason.take(80),
        "createdAt" to FieldValue.serverTimestamp(),
      )
    try {
      docRef.set(payload).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
      throw e
    }
  }

  // endregion

  // region Private Daily Check-ins & Streak Tracking

  override fun observeCheckIns(userId: String): Flow<List<DailyCheckInDoc>> = flow {
    val uid = requireUserId()
    val path = "users/$userId/check_ins"
    emitAll(
      db.collection("users")
        .document(userId)
        .collection("check_ins")
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(DailyCheckInDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedByDescending { it.dateKey }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override suspend fun saveDailyCheckIn(
    dateKey: String,
    moodKey: String,
    moodEmoji: String,
    reflection: String,
    affirmation: String,
    completedActivity: String,
  ) {
    val uid = requireUserId()
    val checkInRef = db.collection("users").document(uid).collection("check_ins").document(dateKey)
    val userRef = db.collection("users").document(uid)
    try {
      val existing = checkInRef.get().await()
      if (existing.exists()) {
        checkInRef
          .update(
            mapOf(
              "moodKey" to moodKey,
              "moodEmoji" to moodEmoji,
              "reflection" to reflection.trim().take(600),
              "affirmation" to affirmation.trim().take(240),
              "completedActivity" to completedActivity.trim().take(120),
              "updatedAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
      } else {
        checkInRef
          .set(
            mapOf(
              "userId" to uid,
              "dateKey" to dateKey,
              "moodKey" to moodKey,
              "moodEmoji" to moodEmoji,
              "reflection" to reflection.trim().take(600),
              "affirmation" to affirmation.trim().take(240),
              "completedActivity" to completedActivity.trim().take(120),
              "createdAt" to FieldValue.serverTimestamp(),
              "updatedAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
      }

      // Compute accurate streaks from recorded check-in dates
      val allSnapshots = db.collection("users").document(uid).collection("check_ins").get().await()
      val dateKeys = allSnapshots.documents.mapNotNull { it.getString("dateKey") }.toSet()
      val (currentStreak, longestStreak) = calculateStreaksFromDates(dateKeys)
      userRef
        .update(
          mapOf(
            "currentStreak" to currentStreak.toLong(),
            "longestStreak" to longestStreak.toLong(),
            "totalCheckIns" to dateKeys.size.toLong(),
            "lastCheckInDate" to (dateKeys.maxOrNull() ?: dateKey),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, checkInRef.path, auth)
      throw e
    }
  }

  // endregion

  // region Bookmarks (Posts & E-Library)

  override fun observeBookmarks(userId: String): Flow<List<UserBookmarkDoc>> = flow {
    val uid = requireUserId()
    val path = "users/$userId/bookmarks"
    emitAll(
      db.collection("users")
        .document(userId)
        .collection("bookmarks")
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(UserBookmarkDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override suspend fun toggleBookmark(
    itemType: String,
    targetId: String,
    title: String,
    subtitle: String,
  ): Boolean {
    val uid = requireUserId()
    val docId = "${itemType}_${targetId}".replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120)
    val docRef = db.collection("users").document(uid).collection("bookmarks").document(docId)
    return try {
      val snapshot = docRef.get().await()
      if (snapshot.exists()) {
        docRef.delete().await()
        false
      } else {
        docRef
          .set(
            mapOf(
              "userId" to uid,
              "itemType" to itemType,
              "targetId" to targetId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(120),
              "title" to title.trim().take(120).ifBlank { "Saved Item" },
              "subtitle" to subtitle.trim().take(200),
              "createdAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
        true
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path, auth)
      throw e
    }
  }

  // endregion

  // region Game Progress

  override fun observeGameProgress(userId: String): Flow<Map<String, GameProgressDoc>> = flow {
    val uid = requireUserId()
    val path = "users/$userId/game_progress"
    emitAll(
      db.collection("users")
        .document(userId)
        .collection("game_progress")
        .snapshots()
        .map { snapshot ->
          snapshot
            .toObjects(GameProgressDoc::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .associateBy { it.gameId }
        }
        .handleSnapshotError(OperationType.LIST, path, uid)
    )
  }

  override suspend fun recordGameScore(gameId: String, score: Int) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("game_progress").document(gameId)
    val clampedScore = score.coerceIn(0, 100_000).toLong()
    try {
      val existing = docRef.get().await()
      if (existing.exists()) {
        val prevHigh = existing.getLong("highScore") ?: 0L
        val prevPlayed = existing.getLong("gamesPlayed") ?: 0L
        docRef
          .update(
            mapOf(
              "highScore" to maxOf(prevHigh, clampedScore),
              "lastScore" to clampedScore,
              "gamesPlayed" to (prevPlayed + 1L),
              "updatedAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
      } else {
        docRef
          .set(
            mapOf(
              "userId" to uid,
              "gameId" to gameId,
              "highScore" to clampedScore,
              "lastScore" to clampedScore,
              "gamesPlayed" to 1L,
              "createdAt" to FieldValue.serverTimestamp(),
              "updatedAt" to FieldValue.serverTimestamp(),
            )
          )
          .await()
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path, auth)
      throw e
    }
  }

  // endregion

  companion object {
    fun calculateStreaksFromDates(
      dateKeys: Set<String>,
      today: LocalDate = LocalDate.now(),
    ): Pair<Int, Int> {
      if (dateKeys.isEmpty()) return 0 to 0
      val parsedDates = dateKeys.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSortedSet()
      if (parsedDates.isEmpty()) return 0 to 0

      // Current streak counts backward from today (or yesterday if today isn't logged yet)
      var anchor =
        when {
          today in parsedDates -> today
          today.minusDays(1) in parsedDates -> today.minusDays(1)
          else -> null
        }
      var currentStreak = 0
      while (anchor != null && anchor in parsedDates) {
        currentStreak++
        anchor = anchor.minusDays(1)
      }

      // Longest consecutive streak across all history
      var longestStreak = 0
      var run = 0
      var prev: LocalDate? = null
      for (date in parsedDates) {
        run = if (prev != null && prev.plusDays(1) == date) run + 1 else 1
        if (run > longestStreak) longestStreak = run
        prev = date
      }
      return currentStreak to longestStreak
    }
  }
}
