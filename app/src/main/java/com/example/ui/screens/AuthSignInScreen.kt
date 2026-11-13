package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas

@Composable
fun AuthSignInScreen(
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  var isLoading by remember { mutableStateOf(false) }
  var isCheckingAutoSignIn by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    attemptAutoSignIn(
      context = context,
      credentialManager = credentialManager,
      onAuthSuccess = {
        isCheckingAutoSignIn = false
        onAuthSuccess()
      },
      onUnauthenticated = { isCheckingAutoSignIn = false },
      scope = coroutineScope,
    )
  }

  RainbowAuroraBackground(modifier = modifier.fillMaxSize().testTag("auth_sign_in_screen")) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier =
        Modifier.fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(30.dp),
        color = PureWhite,
        shadowElevation = 10.dp,
        border = BorderStroke(2.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth(),
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.size(116.dp)
                .clip(CircleShape)
                .background(PsyQuestBrushes.StoryRingRainbow)
                .padding(3.dp)
                .clip(CircleShape)
                .background(PureWhite)
                .padding(6.dp),
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_psyquest_official_logo),
              contentDescription = stringResource(R.string.app_name),
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = stringResource(R.string.app_brand_wordmark),
            style = MaterialTheme.typography.displaySmall.copy(brush = PsyQuestBrushes.WordmarkRainbow),
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            color = InkSecondary,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(20.dp))

          Surface(
            shape = RoundedCornerShape(18.dp),
            color = SoftCanvas,
            border = BorderStroke(1.dp, PsyQuestBrushes.CardAccentBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.padding(16.dp),
            ) {
              FeatureBulletRow(
                icon = Icons.Filled.Favorite,
                tint = QuestMagenta,
                text = "Freedom Wall community reflections & daily private mood streaks",
              )
              FeatureBulletRow(
                icon = Icons.Filled.Psychology,
                tint = QuestPurple,
                text = "Playable psychology Trivia, 4 Pics 1 Word, Word Hunt & Matching",
              )
              FeatureBulletRow(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                tint = QuestBlue,
                text = "9 open educational psychology library collections & saved reads",
              )
            }
          }

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = QuestCoral.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, QuestCoral.copy(alpha = 0.45f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = QuestCoral,
                modifier = Modifier.padding(12.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(22.dp))

          Button(
            onClick = {
              errorMessage = null
              isLoading = true
              onGoogleSignInClicked(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                  isLoading = false
                  onAuthSuccess()
                },
                onAuthError = { msg ->
                  isLoading = false
                  errorMessage = msg
                },
                scope = coroutineScope,
                onAuthCancelled = { isLoading = false },
              )
            },
            enabled = !isLoading && !isCheckingAutoSignIn,
            colors =
              ButtonDefaults.buttonColors(
                containerColor = QuestPurple,
                contentColor = PureWhite,
              ),
            shape = RoundedCornerShape(16.dp),
            modifier =
              Modifier.fillMaxWidth().height(54.dp).testTag("google_sign_in_button"),
          ) {
            if (isLoading || isCheckingAutoSignIn) {
              CircularProgressIndicator(
                color = PureWhite,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp),
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text =
                  if (isCheckingAutoSignIn) stringResource(R.string.auth_checking_session)
                  else stringResource(R.string.auth_signing_in),
                style = MaterialTheme.typography.labelLarge,
              )
            } else {
              Text(
                text = stringResource(R.string.auth_sign_in_with_google),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = stringResource(R.string.educational_disclaimer_short),
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

@Composable
private fun FeatureBulletRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: androidx.compose.ui.graphics.Color,
  text: String,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier.size(30.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.14f)),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(17.dp),
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = InkPrimary,
    )
  }
}
