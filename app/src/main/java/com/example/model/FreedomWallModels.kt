package com.example.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestIndigo
import com.example.ui.theme.QuestLime
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.QuestYellow

enum class FeedTab {
  MY_FEED,
  FOR_YOU,
  FRIENDS,
}

enum class PostCategory(
  @param:StringRes val labelRes: Int,
  val accentStart: Color,
  val accentEnd: Color,
) {
  PERSONAL_THOUGHTS(R.string.cat_personal_thoughts, QuestBlue, QuestPurple),
  POSITIVE_AFFIRMATIONS(R.string.cat_positive_affirmations, QuestOrange, QuestMagenta),
  PSYCHOLOGY_FACTS(R.string.cat_psychology_facts, QuestPurple, QuestIndigo),
  MENTAL_HEALTH_AWARENESS(R.string.cat_mental_health, QuestGreen, QuestBlue),
  STUDENT_EXPERIENCES(R.string.cat_student_experiences, QuestCoral, QuestYellow),
}

enum class ReportReason(@param:StringRes val labelRes: Int) {
  MISLEADING_CLAIM(R.string.report_reason_misleading),
  INAPPROPRIATE(R.string.report_reason_inappropriate),
  HARASSMENT(R.string.report_reason_harassment),
  SPAM(R.string.report_reason_spam),
}

data class StoryItem(
  val id: String,
  val authorName: String,
  @param:DrawableRes val avatarRes: Int,
  val categoryTitle: String,
  val timeAgo: String,
  val headline: String,
  val bodyText: String,
  val gradientColors: List<Color>,
)

data class PostComment(
  val id: String,
  val authorName: String,
  val timeAgo: String,
  val text: String,
)

data class FreedomWallPost(
  val id: String,
  val authorUserId: String = "",
  val authorName: String,
  val authorMeta: String,
  @param:DrawableRes val avatarRes: Int = R.drawable.img_avatar_alexa_1794553327478,
  val authorAvatarBase64: String? = null,
  val timestamp: String,
  val category: PostCategory,
  val feedTabs: Set<FeedTab>,
  val bodyText: String,
  @param:DrawableRes val imageRes: Int? = null,
  val imageBase64: String? = null,
  val imageOverlayQuote: String? = null,
  val highlightCardText: String? = null,
  val likeCount: Int,
  val isLiked: Boolean = false,
  val commentCount: Int,
  val shareCount: Int,
  val isBookmarked: Boolean = false,
  val shareUrl: String,
  val isOwnedByCurrentUser: Boolean = false,
  val isEditorialHighlight: Boolean = false,
)

data class MoodOption(
  val key: String,
  val emoji: String,
  val label: String,
  val accentColor: Color,
)

object WellnessDefaults {
  val moods: List<MoodOption> =
    listOf(
      MoodOption("HAPPY", "😊", "Happy", QuestYellow),
      MoodOption("CALM", "😌", "Calm", QuestGreen),
      MoodOption("GRATEFUL", "🥰", "Grateful", QuestMagenta),
      MoodOption("TIRED", "😴", "Tired", QuestBlue),
      MoodOption("ANXIOUS", "😟", "Anxious", QuestOrange),
      MoodOption("STRESSED", "😣", "Overwhelmed", QuestCoral),
    )

  val affirmations: List<String> =
    listOf(
      "Progress matters more than perfection. One calm breath is already a positive step.",
      "I can honor my current energy while taking small, meaningful steps forward.",
      "Feelings are signals to notice with kindness, not flaws to hide.",
      "Learning, resting, and asking for help are all valid parts of growth.",
      "Today I will speak to myself with the patience I offer a close friend.",
      "Small daily check-ins build lasting self-awareness and resilience.",
    )

  val wellnessActivities: List<String> =
    listOf(
      "2-minute 4-6 slow breathing",
      "Drink a full glass of water",
      "5-4-3-2-1 sensory grounding",
      "5-minute screen-free stretch",
      "Write one gratitude note",
      "Step outside for fresh air & sunlight",
    )
}

object SampleFreedomWallData {
  val stories =
    listOf(
      StoryItem(
        id = "story_maya",
        authorName = "Maya",
        avatarRes = R.drawable.img_avatar_maya_1794553328222,
        categoryTitle = "Positive Affirmations",
        timeAgo = "2h ago",
        headline = "Progress does not need to be loud.",
        bodyText =
          "Resting after a demanding study week is part of sustainable growth, not falling behind.",
        gradientColors = listOf(QuestMagenta, QuestPurple, QuestBlue),
      ),
      StoryItem(
        id = "story_leo",
        authorName = "Leo",
        avatarRes = R.drawable.img_avatar_leo_1794553329613,
        categoryTitle = "Psychology Facts",
        timeAgo = "4h ago",
        headline = "Affect Labeling (Name It to Tame It)",
        bodyText =
          "Neuroimaging studies show that putting feelings into words helps calm emotional reactivity and supports thoughtful regulation.",
        gradientColors = listOf(QuestBlue, QuestPurple, QuestLime),
      ),
      StoryItem(
        id = "story_alexa",
        authorName = "Alexa",
        avatarRes = R.drawable.img_avatar_alexa_1794553327478,
        categoryTitle = "Daily Thoughts",
        timeAgo = "5h ago",
        headline = "Morning Sunlight Reset",
        bodyText =
          "Stepped outside for five quiet minutes before opening my laptop. Small pauses change the pace of the whole day.",
        gradientColors = listOf(QuestOrange, QuestYellow, QuestGreen),
      ),
      StoryItem(
        id = "story_sam",
        authorName = "Sam",
        avatarRes = R.drawable.img_avatar_maya_1794553328222,
        categoryTitle = "Mental Health Reminders",
        timeAgo = "6h ago",
        headline = "Check Your Shoulders & Jaw",
        bodyText =
          "Unclench your jaw, drop your shoulders away from your ears, and take one slow exhale twice as long as your inhale.",
        gradientColors = listOf(QuestGreen, QuestBlue, QuestPurple),
      ),
      StoryItem(
        id = "story_kai",
        authorName = "Kai",
        avatarRes = R.drawable.img_avatar_leo_1794553329613,
        categoryTitle = "Personal Experiences",
        timeAgo = "8h ago",
        headline = "First Study Group Victory",
        bodyText =
          "Asking a classmate for help felt intimidating at first, yet talking through the topic together cut my exam stress in half.",
        gradientColors = listOf(QuestCoral, QuestOrange, QuestMagenta),
      ),
    )

  val initialPosts =
    listOf(
      FreedomWallPost(
        id = "post_alexa_sunlight",
        authorName = "Alexa Reed",
        authorMeta = "San Fabian, Echague",
        avatarRes = R.drawable.img_avatar_alexa_1794553327478,
        timestamp = "7 hours ago",
        category = PostCategory.POSITIVE_AFFIRMATIONS,
        feedTabs = setOf(FeedTab.FOR_YOU, FeedTab.MY_FEED, FeedTab.FRIENDS),
        bodyText =
          "Catching late-afternoon light reminded me to slow down and trust the pace of my own season.",
        imageRes = R.drawable.freedom_wall_sunlight_1794553313514,
        imageOverlayQuote = "Everything\nhappens\nfor a\nreason",
        likeCount = 1482,
        commentCount = 2,
        shareCount = 89,
        shareUrl = "https://psyquest.app/freedom-wall/post-alexa-sunlight",
        isEditorialHighlight = true,
      ),
      FreedomWallPost(
        id = "post_leo_psych_fact",
        authorName = "Dr. Leo Santos",
        authorMeta = "@leosantos.psy",
        avatarRes = R.drawable.img_avatar_leo_1794553329613,
        timestamp = "3 hours ago",
        category = PostCategory.PSYCHOLOGY_FACTS,
        feedTabs = setOf(FeedTab.FOR_YOU, FeedTab.MY_FEED),
        bodyText =
          "Why does writing down worries before sleep help? Expressive journaling offloads unfinished tasks from working memory—a phenomenon related to the Zeigarnik Effect—helping your mind settle more easily.",
        highlightCardText =
          "Psychology Insight: Naming a specific next step on paper reduces mental rumination loops.",
        likeCount = 946,
        commentCount = 1,
        shareCount = 132,
        shareUrl = "https://psyquest.app/freedom-wall/post-zeigarnik-journaling",
        isEditorialHighlight = true,
      ),
      FreedomWallPost(
        id = "post_maya_student_life",
        authorName = "Maya Chen",
        authorMeta = "@mayastudies",
        avatarRes = R.drawable.img_avatar_maya_1794553328222,
        timestamp = "5 hours ago",
        category = PostCategory.STUDENT_EXPERIENCES,
        feedTabs = setOf(FeedTab.FOR_YOU, FeedTab.FRIENDS),
        bodyText =
          "Midterm week honesty: I used to skip meals to cram, and my focus crashed by 4 PM. Switching to 45-minute study blocks with a real water and stretch break actually helped me retain more with far less anxiety.",
        likeCount = 614,
        commentCount = 1,
        shareCount = 47,
        shareUrl = "https://psyquest.app/freedom-wall/post-maya-study-balance",
        isEditorialHighlight = true,
      ),
      FreedomWallPost(
        id = "post_sam_grounding",
        authorName = "Sam Rivera",
        authorMeta = "@mindful.sam",
        avatarRes = R.drawable.img_avatar_alexa_1794553327478,
        timestamp = "9 hours ago",
        category = PostCategory.MENTAL_HEALTH_AWARENESS,
        feedTabs = setOf(FeedTab.FOR_YOU, FeedTab.MY_FEED),
        bodyText =
          "When stress feels loud, try the 5-4-3-2-1 sensory grounding check-in: notice 5 things you see, 4 you feel, 3 you hear, 2 you smell, and 1 slow breath. Grounding does not erase challenges, yet it brings you back to the present moment—and reaching out to a trusted counselor or friend is always a sign of strength.",
        highlightCardText = "5 See • 4 Feel • 3 Hear • 2 Smell • 1 Steady Breath",
        likeCount = 1120,
        commentCount = 1,
        shareCount = 204,
        shareUrl = "https://psyquest.app/freedom-wall/post-54321-grounding",
        isEditorialHighlight = true,
      ),
      FreedomWallPost(
        id = "post_kai_personal_thought",
        authorName = "Kai Mendoza",
        authorMeta = "Quezon City • Friend",
        avatarRes = R.drawable.img_avatar_leo_1794553329613,
        timestamp = "Yesterday",
        category = PostCategory.PERSONAL_THOUGHTS,
        feedTabs = setOf(FeedTab.FOR_YOU, FeedTab.FRIENDS, FeedTab.MY_FEED),
        bodyText =
          "I am learning to speak to myself the same way I speak to a close friend who had a tough week—with patience, curiosity, and zero harsh scorekeeping.",
        likeCount = 528,
        commentCount = 1,
        shareCount = 36,
        shareUrl = "https://psyquest.app/freedom-wall/post-kai-self-compassion",
        isEditorialHighlight = true,
      ),
    )

  val initialCommentsByPostId: Map<String, List<PostComment>> =
    mapOf(
      "post_alexa_sunlight" to
        listOf(
          PostComment(
            id = "c1",
            authorName = "Maya Chen",
            timeAgo = "5h ago",
            text = "Needed this reminder today—that warm sunlight photo feels so peaceful!",
          ),
          PostComment(
            id = "c2",
            authorName = "Kai Mendoza",
            timeAgo = "3h ago",
            text = "Slowing down really helps everything feel clearer.",
          ),
        ),
      "post_leo_psych_fact" to
        listOf(
          PostComment(
            id = "c3",
            authorName = "Alexa Reed",
            timeAgo = "2h ago",
            text = "Keeping a notebook on my nightstand genuinely helps me fall asleep faster!",
          )
        ),
      "post_maya_student_life" to
        listOf(
          PostComment(
            id = "c4",
            authorName = "Sam Rivera",
            timeAgo = "4h ago",
            text = "Rooting for everyone navigating midterms right now. Hydration breaks count!",
          )
        ),
      "post_sam_grounding" to
        listOf(
          PostComment(
            id = "c5",
            authorName = "Dr. Leo Santos",
            timeAgo = "7h ago",
            text = "Great practical grounding exercise for shifting attention back to immediate sensory cues.",
          )
        ),
      "post_kai_personal_thought" to
        listOf(
          PostComment(
            id = "c6",
            authorName = "Maya Chen",
            timeAgo = "18h ago",
            text = "Self-compassion takes practice, and you're doing great.",
          )
        ),
    )
}
