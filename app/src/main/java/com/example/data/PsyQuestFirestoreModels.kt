package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfileDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val handle: String = "",
  val bio: String = "",
  val avatarBase64: String? = null,
  val currentStreak: Long = 0L,
  val longestStreak: Long = 0L,
  val totalCheckIns: Long = 0L,
  val lastCheckInDate: String? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class FreedomWallPostDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val authorName: String = "",
  val authorMeta: String = "",
  val authorAvatarBase64: String? = null,
  val category: String = "PERSONAL_THOUGHTS",
  val visibility: String = "PUBLIC",
  val bodyText: String = "",
  val imageBase64: String? = null,
  val quoteHighlight: String? = null,
  val likeCount: Long = 0L,
  val commentCount: Long = 0L,
  val shareCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class PostCommentDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val postId: String = "",
  val authorName: String = "",
  val text: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class DailyCheckInDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val dateKey: String = "",
  val moodKey: String = "",
  val moodEmoji: String = "",
  val reflection: String = "",
  val affirmation: String = "",
  val completedActivity: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class UserBookmarkDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val itemType: String = "POST",
  val targetId: String = "",
  val title: String = "",
  val subtitle: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class GameProgressDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val gameId: String = "",
  val highScore: Long = 0L,
  val lastScore: Long = 0L,
  val gamesPlayed: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class HiddenPostDoc(
  @DocumentId val id: String = "",
  val userId: String = "",
  val postId: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
)
