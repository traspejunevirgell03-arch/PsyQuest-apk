package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.PsyQuestRepository
import com.example.model.FreedomWallPost
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.FullScreenStoryViewer
import com.example.ui.components.HeaderUtilityBottomSheet
import com.example.ui.components.ProfileBottomSheet
import com.example.ui.components.PsyQuestBottomBar
import com.example.ui.components.PsyQuestTopHeader
import com.example.ui.components.ReportPostDialog
import com.example.ui.components.SharePostBottomSheet
import com.example.ui.navigation.PsyQuestDestination
import com.example.ui.screens.AuthSignInScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.ELibraryScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StreakScreen
import com.example.ui.theme.PsyQuestTheme
import com.example.ui.viewmodel.HeaderUtilitySheet
import com.example.ui.viewmodel.PsyQuestViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { PsyQuestTheme { PsyQuestRoot() } }
  }
}

@Composable
fun PsyQuestRoot() {
  val auth = remember { Firebase.auth }
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser

  if (user == null) {
    AuthSignInScreen(onAuthSuccess = {})
  } else {
    key(user.uid) {
      PsyQuestAuthenticatedApp(user = user)
    }
  }
}

@Composable
fun PsyQuestAuthenticatedApp(user: FirebaseUser) {
  val context = LocalContext.current
  val credentialManager = remember(context) { CredentialManager.create(context) }
  val scope = rememberCoroutineScope()

  val viewModel: PsyQuestViewModel =
    viewModel(
      key = user.uid,
      factory =
        viewModelFactory {
          initializer {
            val app = checkNotNull(this[APPLICATION_KEY]) { "APPLICATION_KEY missing" }
            val repo = PsyQuestRepository(app)
            val fallbackName = user.displayName?.takeIf { it.isNotBlank() } ?: "PsyQuest Explorer"
            val handleBase = user.email?.substringBefore("@") ?: "explorer"
            PsyQuestViewModel(
              repository = repo,
              currentUserId = user.uid,
              initialDisplayName = fallbackName,
              initialHandleBase = handleBase,
              initialPhotoUrl = user.photoUrl?.toString(),
            )
          }
        },
    )

  PsyQuestAppShell(
    viewModel = viewModel,
    userEmail = user.email,
    onSignOut = {
      signOutUser(credentialManager = credentialManager, scope = scope)
    },
  )
}

@Composable
fun PsyQuestAppShell(
  viewModel: PsyQuestViewModel,
  userEmail: String?,
  onSignOut: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: PsyQuestDestination.HOME.route
  val currentDestination =
    PsyQuestDestination.entries.firstOrNull { it.route == currentRoute } ?: PsyQuestDestination.HOME

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  var pendingLibraryResourceId by remember { mutableStateOf<String?>(null) }

  val linkCopiedMsg = context.getString(R.string.snackbar_link_copied)
  val postSavedMsg = context.getString(R.string.snackbar_post_saved)
  val postUnsavedMsg = context.getString(R.string.snackbar_post_unsaved)
  val postHiddenMsg = context.getString(R.string.snackbar_post_hidden)
  val undoLabel = context.getString(R.string.snackbar_undo)
  val reportSubmittedMsg = context.getString(R.string.snackbar_report_submitted)

  val showSnackbar: (String) -> Unit = { message ->
    scope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
    }
  }

  val copyPostLink: (FreedomWallPost) -> Unit = { post ->
    clipboardManager.setText(AnnotatedString(post.shareUrl))
    viewModel.dismissShareSheet()
    showSnackbar(linkCopiedMsg)
  }

  val launchSystemShare: (FreedomWallPost) -> Unit = { post ->
    viewModel.incrementShareCount(post.id)
    viewModel.dismissShareSheet()
    val sendIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "PsyQuest Freedom Wall — ${post.authorName}")
        putExtra(Intent.EXTRA_TEXT, "${post.bodyText}\n\n${post.shareUrl}")
      }
    val chooser = Intent.createChooser(sendIntent, context.getString(R.string.share_sheet_title))
    context.startActivity(chooser)
  }

  val navigateToTab: (PsyQuestDestination) -> Unit = { destination ->
    navController.navigate(destination.route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    topBar = {
      PsyQuestTopHeader(
        userDisplayName = uiState.userProfile?.displayName ?: "PsyQuest",
        userAvatarBase64 = uiState.userProfile?.avatarBase64,
        onSearchClick = viewModel::openSearchSheet,
        onNotificationsClick = viewModel::openNotificationsSheet,
        onProfileClick = viewModel::openProfileSheet,
      )
    },
    bottomBar = {
      PsyQuestBottomBar(
        currentDestination = currentDestination,
        onNavigate = navigateToTab,
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = PsyQuestDestination.HOME.route,
      enterTransition = { fadeIn(animationSpec = tween(180)) },
      exitTransition = { fadeOut(animationSpec = tween(150)) },
      popEnterTransition = { EnterTransition.None },
      popExitTransition = { ExitTransition.None },
      modifier = Modifier.fillMaxSize().padding(innerPadding),
    ) {
      composable(PsyQuestDestination.HOME.route) {
        HomeScreen(
          uiState = uiState,
          onSelectFeedTab = viewModel::selectFeedTab,
          onOpenStory = viewModel::openStory,
          onCreatePostClick = { navigateToTab(PsyQuestDestination.CREATE_POST) },
          onToggleLike = viewModel::togglePostLike,
          onOpenComments = viewModel::openComments,
          onOpenShare = { post -> viewModel.openShareSheet(post.id) },
          onToggleBookmark = { postId ->
            val savedNow = viewModel.togglePostBookmark(postId)
            showSnackbar(if (savedNow) postSavedMsg else postUnsavedMsg)
          },
          onCopyPostLink = copyPostLink,
          onReportPost = viewModel::openReportDialog,
          onHidePost = { postId ->
            viewModel.hidePost(postId)
            scope.launch {
              snackbarHostState.currentSnackbarData?.dismiss()
              val result =
                snackbarHostState.showSnackbar(
                  message = postHiddenMsg,
                  actionLabel = undoLabel,
                  duration = SnackbarDuration.Short,
                )
              if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastHiddenPost()
              }
            }
          },
          onDeletePost = { postId ->
            viewModel.deleteOwnPost(postId) { showSnackbar("Post deleted.") }
          },
          onRestoreHiddenPosts = viewModel::restoreAllHiddenPosts,
        )
      }

      composable(PsyQuestDestination.STREAK.route) {
        StreakScreen(
          uiState = uiState,
          onSaveCheckIn = { moodKey, emoji, reflection, affirmation, activity ->
            viewModel.saveDailyCheckIn(
              moodKey = moodKey,
              moodEmoji = emoji,
              reflection = reflection,
              affirmation = affirmation,
              completedActivity = activity,
              onSaved = { showSnackbar("Daily check-in saved! Streak updated.") },
              onError = { err -> showSnackbar(err) },
            )
          },
        )
      }

      composable(PsyQuestDestination.CREATE_POST.route) {
        CreatePostScreen(
          uiState = uiState,
          onPublishPost = { category, visibility, bodyText, imageBase64, quoteHighlight, onResetForm ->
            viewModel.publishPost(
              category = category,
              visibility = visibility,
              bodyText = bodyText,
              imageBase64 = imageBase64,
              quoteHighlight = quoteHighlight,
              onSuccess = {
                onResetForm()
                showSnackbar("Published to the Freedom Wall!")
                navigateToTab(PsyQuestDestination.HOME)
              },
              onError = { err -> showSnackbar(err) },
            )
          },
          onShowMessage = showSnackbar,
        )
      }

      composable(PsyQuestDestination.GAMES.route) {
        GamesScreen(
          uiState = uiState,
          onRecordScore = { gameId, score ->
            viewModel.recordGameResult(gameId, score)
            showSnackbar("Saved score: $score pts!")
          },
        )
      }

      composable(PsyQuestDestination.E_LIBRARY.route) {
        ELibraryScreen(
          uiState = uiState,
          onToggleBookmark = { res ->
            val saved = viewModel.toggleLibraryBookmark(res)
            showSnackbar(if (saved) "Saved to E-Library bookmarks" else "Removed bookmark")
          },
          initialResourceId = pendingLibraryResourceId,
          onClearInitialResource = { pendingLibraryResourceId = null },
        )
      }
    }

    val storyIndex = uiState.activeStoryIndex
    if (storyIndex != null) {
      FullScreenStoryViewer(
        stories = uiState.stories,
        activeIndex = storyIndex,
        onPrevious = viewModel::previousStory,
        onNext = viewModel::nextStory,
        onClose = viewModel::closeStory,
      )
    }

    val activeCommentPost = uiState.activeCommentPost
    CommentsBottomSheet(
      post = activeCommentPost,
      comments = activeCommentPost?.let { uiState.commentsByPostId[it.id].orEmpty() }.orEmpty(),
      onAddComment = { text ->
        activeCommentPost?.let { viewModel.addComment(it.id, text) }
      },
      onDismiss = viewModel::dismissComments,
    )

    SharePostBottomSheet(
      post = uiState.activeSharePost,
      onCopyLink = copyPostLink,
      onSystemShare = launchSystemShare,
      onDismiss = viewModel::dismissShareSheet,
    )

    ReportPostDialog(
      post = uiState.activeReportPost,
      onSubmitReport = { post, reason ->
        viewModel.submitReport(post.id, reason)
        showSnackbar(reportSubmittedMsg)
      },
      onDismiss = viewModel::dismissReportDialog,
    )

    HeaderUtilityBottomSheet(
      activeSheet = uiState.activeSheet,
      uiState = uiState,
      onSelectSearchPost = { post ->
        navigateToTab(PsyQuestDestination.HOME)
        viewModel.openComments(post.id)
      },
      onSelectSearchResource = { resource ->
        pendingLibraryResourceId = resource.id
        navigateToTab(PsyQuestDestination.E_LIBRARY)
      },
      onGoToStreak = { navigateToTab(PsyQuestDestination.STREAK) },
      onDismiss = viewModel::dismissSheet,
    )

    ProfileBottomSheet(
      visible = uiState.activeSheet == HeaderUtilitySheet.PROFILE,
      uiState = uiState,
      userEmail = userEmail,
      onSaveProfile = { name, handle, bio, avatarBase64 ->
        viewModel.updateUserProfile(
          displayName = name,
          handle = handle,
          bio = bio,
          avatarBase64 = avatarBase64,
          onSuccess = { showSnackbar("Profile updated!") },
          onError = { err -> showSnackbar(err) },
        )
      },
      onDeletePost = { postId ->
        viewModel.deleteOwnPost(postId) { showSnackbar("Post deleted.") }
      },
      onSignOut = onSignOut,
      onDismiss = viewModel::dismissSheet,
    )
  }
}
