package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PsyQuestRepositoryRuleTest : FirestoreEmulatorTestBase() {

  private fun uniqueEmail(prefix: String): String =
    "${prefix}_${UUID.randomUUID().toString().take(8)}@test.com"

  @Test
  fun ensureUserProfile_and_observeUserProfile_succeedsForOwner() = runBlocking {
    val aliceUid = signInTestUser(uniqueEmail("alice_profile"))
    val repo = PsyQuestRepository(firestore, auth)

    val profile =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.ensureUserProfile("Alice Reed", "alice_reed")
      }
    assertEquals(aliceUid, profile.userId)
    assertEquals("Alice Reed", profile.displayName)

    val observed =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeUserProfile(aliceUid).first { it != null }
      }
    assertNotNull(observed)
    assertEquals("Alice Reed", observed?.displayName)
  }

  @Test
  fun publishPost_like_comment_and_observePublicPosts_succeeds() = runBlocking {
    signInTestUser(uniqueEmail("alice_post"))
    val repo = PsyQuestRepository(firestore, auth)
    val uniqueBody = "Mindful breathing reflection ${UUID.randomUUID()}"

    val publishResult =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.publishPost(
          authorName = "Alice Reed",
          authorMeta = "Psychology Student",
          authorAvatarBase64 = null,
          category = "MENTAL_HEALTH_AWARENESS",
          visibility = "PUBLIC",
          bodyText = uniqueBody,
          imageBase64 = null,
          quoteHighlight = "Breathe in calm, breathe out tension.",
        )
      }
    assertTrue("Expected publishPost success: ${publishResult.exceptionOrNull()}", publishResult.isSuccess)
    val postId = publishResult.getOrThrow()

    val posts =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observePublicPosts().first { list -> list.any { it.id == postId } }
      }
    assertTrue(posts.any { it.bodyText == uniqueBody })

    // Bob likes and comments on Alice's public post
    signInTestUser(uniqueEmail("bob_comment"))
    val bobRepo = PsyQuestRepository(firestore, auth)
    withTimeout(DEFAULT_TIMEOUT_MS) {
      bobRepo.togglePostLike(postId, currentlyLiked = false)
      assertTrue(bobRepo.isPostLikedByCurrentUser(postId))
      bobRepo.addComment(postId, "Bob Rivera", "Great reminder!")
    }

    val comments =
      withTimeout(FLOW_TIMEOUT_MS) {
        bobRepo.observeComments(postId).first { list -> list.any { it.text == "Great reminder!" } }
      }
    assertEquals(1, comments.size)
  }

  @Test
  fun saveDailyCheckIn_calculatesStreaksAndStoresPrivately() = runBlocking {
    val aliceUid = signInTestUser(uniqueEmail("alice_streak"))
    val repo = PsyQuestRepository(firestore, auth)
    repo.ensureUserProfile("Alice", "alice")

    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.saveDailyCheckIn(
        dateKey = yesterday.toString(),
        moodKey = "CALM",
        moodEmoji = "😌",
        reflection = "Took a mindful walk.",
        affirmation = "I grow one step at a time.",
        completedActivity = "Deep breathing (2 min)",
      )
      repo.saveDailyCheckIn(
        dateKey = today.toString(),
        moodKey = "GRATEFUL",
        moodEmoji = "😊",
        reflection = "Feeling steady today.",
        affirmation = "Progress matters more than perfection.",
        completedActivity = "Gratitude note",
      )
    }

    val checkIns =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeCheckIns(aliceUid).first { it.size >= 2 }
      }
    assertTrue(checkIns.any { it.dateKey == today.toString() && it.moodKey == "GRATEFUL" })

    val (current, longest) =
      PsyQuestRepository.calculateStreaksFromDates(
        setOf(today.toString(), yesterday.toString()),
        today = today,
      )
    assertEquals(2, current)
    assertEquals(2, longest)
  }

  @Test
  fun bookmarks_and_gameProgress_persistPerUser() = runBlocking {
    val aliceUid = signInTestUser(uniqueEmail("alice_games"))
    val repo = PsyQuestRepository(firestore, auth)
    val targetId = "resource_${UUID.randomUUID().toString().take(8)}"

    val saved =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.toggleBookmark("LIBRARY", targetId, "OpenStax Psychology 2e", "Introduction to Psychology")
      }
    assertTrue(saved)

    val bookmarks =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeBookmarks(aliceUid).first { list -> list.any { it.targetId == targetId } }
      }
    assertEquals(1, bookmarks.count { it.targetId == targetId })

    val removed =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repo.toggleBookmark("LIBRARY", targetId, "OpenStax Psychology 2e", "Introduction to Psychology")
      }
    assertFalse(removed)

    withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.recordGameScore("trivia", 80)
      repo.recordGameScore("trivia", 60)
    }

    val progress =
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeGameProgress(aliceUid).first { it.containsKey("trivia") }
      }
    assertEquals(80L, progress["trivia"]?.highScore)
    assertEquals(60L, progress["trivia"]?.lastScore)
    assertEquals(2L, progress["trivia"]?.gamesPlayed)
  }

  @Test
  fun crossUser_privateCheckInReadAndDeletePost_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(uniqueEmail("alice_iso"))
    val aliceRepo = PsyQuestRepository(firestore, auth)
    aliceRepo.ensureUserProfile("Alice", "alice")
    val postId =
      withTimeout(DEFAULT_TIMEOUT_MS) {
        aliceRepo
          .publishPost(
            authorName = "Alice",
            authorMeta = "Member",
            authorAvatarBase64 = null,
            category = "PERSONAL_THOUGHTS",
            visibility = "PUBLIC",
            bodyText = "Alice public reflection ${UUID.randomUUID()}",
            imageBase64 = null,
            quoteHighlight = null,
          )
          .getOrThrow()
      }
    val todayKey = LocalDate.now().toString()
    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveDailyCheckIn(todayKey, "CALM", "😌", "Private note", "Affirmation", "Walk")
    }

    // Switch to Bob: cannot read Alice's private check-ins or delete Alice's post
    signInTestUser(uniqueEmail("bob_iso"))
    val bobRepo = PsyQuestRepository(firestore, auth)

    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        firestore.collection("users").document(aliceUid).collection("check_ins").document(todayKey).get().await()
      }
      fail("Expected PERMISSION_DENIED when Bob reads Alice's private check-in")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }

    try {
      withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.deletePost(postId) }
      fail("Expected PERMISSION_DENIED when Bob deletes Alice's post")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }
  }

  @Test
  fun unauthenticated_publicPostsSnapshot_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        firestore.collection("posts").whereEqualTo("visibility", "PUBLIC").snapshots().first()
      }
      fail("Expected PERMISSION_DENIED for unauthenticated posts query")
    } catch (e: Exception) {
      val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
        "Expected nested FirebaseFirestoreException, got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }
  }

  private companion object {
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3500L
  }
}
