package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PsychologyGamesData
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.QuestYellow
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.PsyQuestUiState
import kotlin.math.abs

enum class ActiveGame {
  HUB,
  TRIVIA,
  FOUR_PICS,
  WORD_HUNT,
  CONNECT_MATCH,
  MYTH_FACT,
}

@Composable
fun GamesScreen(
  uiState: PsyQuestUiState,
  onRecordScore: (gameId: String, score: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  var activeGame by rememberSaveable { mutableStateOf(ActiveGame.HUB) }

  if (activeGame != ActiveGame.HUB) {
    BackHandler { activeGame = ActiveGame.HUB }
  }

  RainbowAuroraBackground(modifier = modifier.fillMaxSize().testTag("screen_games")) {
    when (activeGame) {
      ActiveGame.HUB ->
        GamesHubContent(
          uiState = uiState,
          onSelectGame = { activeGame = it },
        )
      ActiveGame.TRIVIA ->
        TriviaGameScreen(
          onFinish = { score -> onRecordScore("trivia", score) },
          onBack = { activeGame = ActiveGame.HUB },
        )
      ActiveGame.FOUR_PICS ->
        FourPicsGameScreen(
          onFinish = { score -> onRecordScore("four_pics", score) },
          onBack = { activeGame = ActiveGame.HUB },
        )
      ActiveGame.WORD_HUNT ->
        WordHuntGameScreen(
          onFinish = { score -> onRecordScore("word_hunt", score) },
          onBack = { activeGame = ActiveGame.HUB },
        )
      ActiveGame.CONNECT_MATCH ->
        ConnectMatchGameScreen(
          onFinish = { score -> onRecordScore("connect_match", score) },
          onBack = { activeGame = ActiveGame.HUB },
        )
      ActiveGame.MYTH_FACT ->
        MythFactGameScreen(
          onFinish = { score -> onRecordScore("myth_fact", score) },
          onBack = { activeGame = ActiveGame.HUB },
        )
    }
  }
}

@Composable
private fun GamesHubContent(
  uiState: PsyQuestUiState,
  onSelectGame: (ActiveGame) -> Unit,
) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize(),
  ) {
    item(key = "games_header_card") {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.5.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Psychology Game Center",
            style = MaterialTheme.typography.titleLarge,
            color = InkPrimary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text =
              "Play engaging cognitive and psychology challenges. High scores and rounds played save to your PsyQuest profile.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary,
          )
        }
      }
    }

    item(key = "game_trivia") {
      GameHubCard(
        title = "Psychology Trivia",
        subtitle = "8 brain, memory, social, and research ethics questions with explanations.",
        icon = Icons.Filled.Quiz,
        gradient = listOf(QuestPurple, QuestBlue),
        highScore = uiState.gameProgress["trivia"]?.highScore?.toInt() ?: 0,
        gamesPlayed = uiState.gameProgress["trivia"]?.gamesPlayed?.toInt() ?: 0,
        testTag = "play_trivia_button",
        onClick = { onSelectGame(ActiveGame.TRIVIA) },
      )
    }

    item(key = "game_four_pics") {
      GameHubCard(
        title = "4 Pics 1 Word",
        subtitle = "Connect 4 visual psychology clues to uncover key wellness concepts.",
        icon = Icons.Filled.Lightbulb,
        gradient = listOf(QuestOrange, QuestMagenta),
        highScore = uiState.gameProgress["four_pics"]?.highScore?.toInt() ?: 0,
        gamesPlayed = uiState.gameProgress["four_pics"]?.gamesPlayed?.toInt() ?: 0,
        testTag = "play_four_pics_button",
        onClick = { onSelectGame(ActiveGame.FOUR_PICS) },
      )
    }

    item(key = "game_word_hunt") {
      GameHubCard(
        title = "Psychology Word Hunt",
        subtitle = "Tap start and end letters on an 8×8 grid to uncover hidden mental wellness terms.",
        icon = Icons.Filled.GridOn,
        gradient = listOf(QuestGreen, QuestBlue),
        highScore = uiState.gameProgress["word_hunt"]?.highScore?.toInt() ?: 0,
        gamesPlayed = uiState.gameProgress["word_hunt"]?.gamesPlayed?.toInt() ?: 0,
        testTag = "play_word_hunt_button",
        onClick = { onSelectGame(ActiveGame.WORD_HUNT) },
      )
    }

    item(key = "game_connect") {
      GameHubCard(
        title = "Connect & Match",
        subtitle = "Pair foundational psychology terms with their real-world definitions.",
        icon = Icons.Filled.Extension,
        gradient = listOf(QuestMagenta, QuestPurple),
        highScore = uiState.gameProgress["connect_match"]?.highScore?.toInt() ?: 0,
        gamesPlayed = uiState.gameProgress["connect_match"]?.gamesPlayed?.toInt() ?: 0,
        testTag = "play_connect_button",
        onClick = { onSelectGame(ActiveGame.CONNECT_MATCH) },
      )
    }

    item(key = "game_myth_fact") {
      GameHubCard(
        title = "Bonus: Myth vs. Fact",
        subtitle = "Test popular psychology beliefs against empirical brain science.",
        icon = Icons.Filled.Psychology,
        gradient = listOf(QuestCoral, QuestYellow),
        highScore = uiState.gameProgress["myth_fact"]?.highScore?.toInt() ?: 0,
        gamesPlayed = uiState.gameProgress["myth_fact"]?.gamesPlayed?.toInt() ?: 0,
        testTag = "play_myth_fact_button",
        onClick = { onSelectGame(ActiveGame.MYTH_FACT) },
      )
    }
  }
}

@Composable
private fun GameHubCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  gradient: List<Color>,
  highScore: Int,
  gamesPlayed: Int,
  testTag: String,
  onClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = PureWhite,
    shadowElevation = 5.dp,
    border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
    modifier =
      Modifier.widthIn(max = 600.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .clickable(onClick = onClick)
        .testTag(testTag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(16.dp),
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradient)),
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = PureWhite, modifier = Modifier.size(28.dp))
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = InkSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = QuestOrange,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Best: $highScore • Played: $gamesPlayed",
            style = MaterialTheme.typography.labelSmall,
            color = QuestPurple,
          )
        }
      }
    }
  }
}

@Composable
private fun GameTopBar(title: String, scoreLabel: String, onBack: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = PureWhite,
    shadowElevation = 4.dp,
    modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("game_back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Games", tint = InkPrimary)
        }
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
      }
      Surface(shape = RoundedCornerShape(50), color = QuestPurple.copy(alpha = 0.12f)) {
        Text(
          text = scoreLabel,
          style = MaterialTheme.typography.labelLarge,
          color = QuestPurple,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
private fun TriviaGameScreen(onFinish: (Int) -> Unit, onBack: () -> Unit) {
  val questions = PsychologyGamesData.triviaQuestions
  var currentIndex by remember { mutableIntStateOf(0) }
  var selectedOption by remember { mutableStateOf<Int?>(null) }
  var score by remember { mutableIntStateOf(0) }
  var completed by remember { mutableStateOf(false) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize().testTag("trivia_game_screen"),
  ) {
    item { GameTopBar("Psychology Trivia", "Score: $score", onBack) }
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          if (completed) {
            Text("Quiz Complete!", style = MaterialTheme.typography.headlineSmall, color = InkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              "You scored $score / ${questions.size * 10} points.",
              style = MaterialTheme.typography.titleMedium,
              color = QuestPurple,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(48.dp)) {
                Text("Game Hub")
              }
              Button(
                onClick = {
                  currentIndex = 0
                  selectedOption = null
                  score = 0
                  completed = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.weight(1f).height(48.dp),
              ) {
                Text("Play Again")
              }
            }
          } else {
            val q = questions[currentIndex]
            Text(
              "Question ${currentIndex + 1} of ${questions.size} • ${q.topic}",
              style = MaterialTheme.typography.labelLarge,
              color = QuestPurple,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(q.prompt, style = MaterialTheme.typography.titleLarge, color = InkPrimary)
            Spacer(modifier = Modifier.height(14.dp))

            q.options.forEachIndexed { idx, option ->
              val isChosen = selectedOption == idx
              val isCorrect = idx == q.correctIndex
              val bgColor =
                when {
                  selectedOption == null -> SoftCanvas
                  isCorrect -> QuestGreen.copy(alpha = 0.16f)
                  isChosen -> QuestCoral.copy(alpha = 0.16f)
                  else -> SoftCanvas
                }
              val borderColor =
                when {
                  selectedOption == null -> HairlineBorder
                  isCorrect -> QuestGreen
                  isChosen -> QuestCoral
                  else -> HairlineBorder
                }
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = bgColor,
                border = BorderStroke(1.3.dp, borderColor),
                modifier =
                  Modifier.fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(enabled = selectedOption == null) {
                      selectedOption = idx
                      if (idx == q.correctIndex) score += 10
                    },
              ) {
                Text(
                  text = option,
                  style = MaterialTheme.typography.bodyLarge,
                  color = InkPrimary,
                  modifier = Modifier.padding(14.dp),
                )
              }
            }

            if (selectedOption != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = QuestBlue.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = q.explanation,
                  style = MaterialTheme.typography.bodySmall,
                  color = InkPrimary,
                  modifier = Modifier.padding(12.dp),
                )
              }
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = {
                  if (currentIndex + 1 < questions.size) {
                    currentIndex++
                    selectedOption = null
                  } else {
                    completed = true
                    onFinish(score)
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.fillMaxWidth().height(48.dp),
              ) {
                Text(if (currentIndex + 1 < questions.size) "Next Question" else "Finish & Save Score")
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FourPicsGameScreen(onFinish: (Int) -> Unit, onBack: () -> Unit) {
  val puzzles = PsychologyGamesData.fourPicsPuzzles
  var puzzleIndex by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }
  var typedChars by remember(puzzleIndex) { mutableStateOf("") }
  var showHint by remember(puzzleIndex) { mutableStateOf(false) }
  var solvedCurrent by remember(puzzleIndex) { mutableStateOf(false) }
  var finished by remember { mutableStateOf(false) }

  val puzzle = puzzles[puzzleIndex]
  val letterBank =
    remember(puzzleIndex) {
      val extras = listOf('A', 'E', 'I', 'O', 'R', 'S', 'T', 'N', 'L', 'M', 'P', 'Y')
      val combined = puzzle.answer.toList() + extras.take((12 - puzzle.answer.length).coerceAtLeast(3))
      combined.sorted()
    }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize().testTag("four_pics_game_screen"),
  ) {
    item { GameTopBar("4 Pics 1 Word", "Score: $score", onBack) }
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          if (finished) {
            Text("All Puzzles Solved!", style = MaterialTheme.typography.headlineSmall, color = InkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Final Score: $score points", style = MaterialTheme.typography.titleMedium, color = QuestPurple)
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(48.dp)) {
                Text("Game Hub")
              }
              Button(
                onClick = {
                  puzzleIndex = 0
                  score = 0
                  finished = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.weight(1f).height(48.dp),
              ) {
                Text("Play Again")
              }
            }
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                "Puzzle ${puzzleIndex + 1} of ${puzzles.size}",
                style = MaterialTheme.typography.labelLarge,
                color = QuestPurple,
              )
              TextButton(onClick = { showHint = !showHint }) {
                Text(if (showHint) "Hide Hint" else "Hint")
              }
            }

            if (showHint) {
              Text(puzzle.hint, style = MaterialTheme.typography.bodySmall, color = QuestBlue)
              Spacer(modifier = Modifier.height(8.dp))
            }

            // 2x2 Visual Clue Tiles
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              for (row in 0..1) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                  for (col in 0..1) {
                    val clue = puzzle.clues[row * 2 + col]
                    Surface(
                      shape = RoundedCornerShape(16.dp),
                      color = SoftCanvas,
                      border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
                      modifier = Modifier.weight(1f).height(88.dp),
                    ) {
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp),
                      ) {
                        Text(clue.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          clue.caption,
                          style = MaterialTheme.typography.labelSmall,
                          color = InkSecondary,
                          textAlign = TextAlign.Center,
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Answer slots
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
              for (i in puzzle.answer.indices) {
                val ch = typedChars.getOrNull(i)?.toString() ?: ""
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (solvedCurrent) QuestGreen.copy(alpha = 0.16f) else SoftCanvas,
                  border = BorderStroke(1.5.dp, if (solvedCurrent) QuestGreen else QuestPurple),
                  modifier = Modifier.size(38.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = ch,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = InkPrimary,
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!solvedCurrent) {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally),
              ) {
                letterBank.forEach { letter ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = QuestPurple.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, QuestPurple.copy(alpha = 0.4f)),
                    modifier =
                      Modifier.size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                          if (typedChars.length < puzzle.answer.length) {
                            val next = typedChars + letter
                            typedChars = next
                            if (next == puzzle.answer) {
                              solvedCurrent = true
                              score += if (showHint) 10 else 15
                            }
                          }
                        },
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = QuestPurple,
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                  onClick = { if (typedChars.isNotEmpty()) typedChars = typedChars.dropLast(1) },
                  modifier = Modifier.weight(1f),
                ) { Text("Backspace") }
                OutlinedButton(
                  onClick = { typedChars = "" },
                  modifier = Modifier.weight(1f),
                ) { Text("Clear") }
              }
              if (typedChars.length == puzzle.answer.length && typedChars != puzzle.answer) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  "Not quite—tap Clear or Backspace to try another combination.",
                  style = MaterialTheme.typography.bodySmall,
                  color = QuestCoral,
                )
              }
            } else {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = QuestGreen.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = "Correct: ${puzzle.answer}! ${puzzle.psychNote}",
                  style = MaterialTheme.typography.bodySmall,
                  color = InkPrimary,
                  modifier = Modifier.padding(12.dp),
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  if (puzzleIndex + 1 < puzzles.size) {
                    puzzleIndex++
                  } else {
                    finished = true
                    onFinish(score)
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.fillMaxWidth().height(48.dp),
              ) {
                Text(if (puzzleIndex + 1 < puzzles.size) "Next Word" else "Finish & Save Score")
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordHuntGameScreen(onFinish: (Int) -> Unit, onBack: () -> Unit) {
  val puzzles = PsychologyGamesData.wordHuntPuzzles
  var gridIndex by remember { mutableIntStateOf(0) }
  val puzzle = puzzles[gridIndex]
  var foundWords by remember(gridIndex) { mutableStateOf(setOf<String>()) }
  var highlightedCells by remember(gridIndex) { mutableStateOf(setOf<Pair<Int, Int>>()) }
  var startCell by remember(gridIndex) { mutableStateOf<Pair<Int, Int>?>(null) }
  var score by remember { mutableIntStateOf(0) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize().testTag("word_hunt_game_screen"),
  ) {
    item { GameTopBar("Word Hunt", "Score: $score", onBack) }
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(puzzle.title, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
            TextButton(onClick = { gridIndex = (gridIndex + 1) % puzzles.size }) {
              Text("Switch Grid")
            }
          }
          Text(
            "Tap the FIRST letter and LAST letter of any hidden word (horizontal or vertical).",
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary,
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Target word chips
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            puzzle.targetWords.forEach { word ->
              val isFound = word in foundWords
              Surface(
                shape = RoundedCornerShape(50),
                color = if (isFound) QuestGreen.copy(alpha = 0.18f) else SoftCanvas,
                border = BorderStroke(1.dp, if (isFound) QuestGreen else HairlineBorder),
              ) {
                Text(
                  text = if (isFound) "✓ $word" else word,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isFound) QuestGreen else InkPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 8x8 Grid
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            puzzle.gridRows.forEachIndexed { r, rowStr ->
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                rowStr.forEachIndexed { c, ch ->
                  val coord = r to c
                  val isStart = startCell == coord
                  val isFoundCell = coord in highlightedCells
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                      Modifier.weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                          when {
                            isStart -> QuestOrange
                            isFoundCell -> QuestGreen.copy(alpha = 0.25f)
                            else -> SoftCanvas
                          }
                        )
                        .border(
                          1.dp,
                          if (isStart) QuestOrange else if (isFoundCell) QuestGreen else HairlineBorder,
                          RoundedCornerShape(8.dp),
                        )
                        .clickable {
                          val anchor = startCell
                          if (anchor == null) {
                            startCell = coord
                          } else {
                            val line = extractStraightLine(anchor, coord, puzzle.gridRows)
                            if (line != null) {
                              val word = line.first
                              val reversed = word.reversed()
                              val match =
                                when {
                                  word in puzzle.targetWords && word !in foundWords -> word
                                  reversed in puzzle.targetWords && reversed !in foundWords -> reversed
                                  else -> null
                                }
                              if (match != null) {
                                val nextFound = foundWords + match
                                foundWords = nextFound
                                highlightedCells = highlightedCells + line.second
                                score += 15
                                if (nextFound.size == puzzle.targetWords.size) {
                                  onFinish(score)
                                }
                              }
                            }
                            startCell = null
                          }
                        },
                  ) {
                    Text(
                      text = ch.toString(),
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (isStart) PureWhite else InkPrimary,
                    )
                  }
                }
              }
            }
          }

          if (foundWords.size == puzzle.targetWords.size) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "All ${puzzle.targetWords.size} words found! Score saved.",
              style = MaterialTheme.typography.titleSmall,
              color = QuestGreen,
            )
          }
        }
      }
    }
  }
}

private fun extractStraightLine(
  start: Pair<Int, Int>,
  end: Pair<Int, Int>,
  grid: List<String>,
): Pair<String, Set<Pair<Int, Int>>>? {
  val dr = end.first - start.first
  val dc = end.second - start.second
  if (dr != 0 && dc != 0 && abs(dr) != abs(dc)) return null
  val steps = maxOf(abs(dr), abs(dc))
  val stepR = dr.compareTo(0)
  val stepC = dc.compareTo(0)
  val coords = mutableSetOf<Pair<Int, Int>>()
  val sb = StringBuilder()
  for (i in 0..steps) {
    val r = start.first + i * stepR
    val c = start.second + i * stepC
    coords += (r to c)
    sb.append(grid[r][c])
  }
  return sb.toString() to coords
}

@Composable
private fun ConnectMatchGameScreen(onFinish: (Int) -> Unit, onBack: () -> Unit) {
  val rounds = PsychologyGamesData.connectRounds
  var roundIndex by remember { mutableIntStateOf(0) }
  val pairs = rounds[roundIndex]
  val shuffledDefs = remember(roundIndex) { pairs.shuffled() }
  var selectedTermId by remember(roundIndex) { mutableStateOf<String?>(null) }
  var matchedIds by remember(roundIndex) { mutableStateOf(setOf<String>()) }
  var feedback by remember(roundIndex) { mutableStateOf<String?>(null) }
  var score by remember { mutableIntStateOf(0) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize().testTag("connect_game_screen"),
  ) {
    item { GameTopBar("Connect & Match", "Score: $score", onBack) }
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            "Round ${roundIndex + 1} of ${rounds.size}: Tap a term, then tap its matching psychology definition.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary,
          )
          feedback?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(it, style = MaterialTheme.typography.labelLarge, color = QuestPurple)
          }
          Spacer(modifier = Modifier.height(12.dp))

          Text("1. Psychology Concepts", style = MaterialTheme.typography.labelLarge, color = QuestPurple)
          Spacer(modifier = Modifier.height(6.dp))
          pairs.forEach { item ->
            val isMatched = item.id in matchedIds
            val isSelected = selectedTermId == item.id
            Surface(
              shape = RoundedCornerShape(12.dp),
              color =
                when {
                  isMatched -> QuestGreen.copy(alpha = 0.15f)
                  isSelected -> QuestPurple.copy(alpha = 0.15f)
                  else -> SoftCanvas
                },
              border =
                BorderStroke(
                  1.3.dp,
                  when {
                    isMatched -> QuestGreen
                    isSelected -> QuestPurple
                    else -> HairlineBorder
                  },
                ),
              modifier =
                Modifier.fillMaxWidth()
                  .padding(vertical = 3.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable(enabled = !isMatched) { selectedTermId = item.id },
            ) {
              Text(
                text = if (isMatched) "✓ ${item.term}" else item.term,
                style = MaterialTheme.typography.titleSmall,
                color = InkPrimary,
                modifier = Modifier.padding(12.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text("2. Matching Definitions", style = MaterialTheme.typography.labelLarge, color = QuestBlue)
          Spacer(modifier = Modifier.height(6.dp))
          shuffledDefs.forEach { def ->
            val isMatched = def.id in matchedIds
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isMatched) QuestGreen.copy(alpha = 0.15f) else SoftCanvas,
              border = BorderStroke(1.dp, if (isMatched) QuestGreen else HairlineBorder),
              modifier =
                Modifier.fillMaxWidth()
                  .padding(vertical = 3.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable(enabled = !isMatched && selectedTermId != null) {
                    if (selectedTermId == def.id) {
                      val nextMatched = matchedIds + def.id
                      matchedIds = nextMatched
                      score += 10
                      feedback = "Matched ${def.term}!"
                      selectedTermId = null
                      if (nextMatched.size == pairs.size) {
                        onFinish(score)
                      }
                    } else {
                      feedback = "Not a match—try another definition!"
                    }
                  },
            ) {
              Text(
                text = def.definition,
                style = MaterialTheme.typography.bodySmall,
                color = InkPrimary,
                modifier = Modifier.padding(12.dp),
              )
            }
          }

          if (matchedIds.size == pairs.size) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { roundIndex = (roundIndex + 1) % rounds.size },
              colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
              modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
              Text(if (roundIndex + 1 < rounds.size) "Next Round" else "Replay Round 1")
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MythFactGameScreen(onFinish: (Int) -> Unit, onBack: () -> Unit) {
  val items = PsychologyGamesData.mythFactQuestions
  var index by remember { mutableIntStateOf(0) }
  var chosenAnswer by remember { mutableStateOf<Boolean?>(null) }
  var score by remember { mutableIntStateOf(0) }
  var finished by remember { mutableStateOf(false) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize().testTag("myth_fact_game_screen"),
  ) {
    item { GameTopBar("Myth vs. Fact", "Score: $score", onBack) }
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = PureWhite,
        shadowElevation = 6.dp,
        border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          if (finished) {
            Text("Challenge Complete!", style = MaterialTheme.typography.headlineSmall, color = InkPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Score: $score / ${items.size * 10}", style = MaterialTheme.typography.titleMedium, color = QuestPurple)
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = onBack,
              colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
              modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
              Text("Back to Games")
            }
          } else {
            val q = items[index]
            Text("Statement ${index + 1} of ${items.size}", style = MaterialTheme.typography.labelLarge, color = QuestPurple)
            Spacer(modifier = Modifier.height(10.dp))
            Text(q.statement, style = MaterialTheme.typography.titleLarge, color = InkPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  chosenAnswer = false
                  if (!q.isFact) score += 10
                },
                enabled = chosenAnswer == null,
                colors = ButtonDefaults.buttonColors(containerColor = QuestOrange, contentColor = PureWhite),
                modifier = Modifier.weight(1f).height(48.dp),
              ) { Text("Myth") }
              Button(
                onClick = {
                  chosenAnswer = true
                  if (q.isFact) score += 10
                },
                enabled = chosenAnswer == null,
                colors = ButtonDefaults.buttonColors(containerColor = QuestBlue, contentColor = PureWhite),
                modifier = Modifier.weight(1f).height(48.dp),
              ) { Text("Fact") }
            }

            if (chosenAnswer != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = InkPrimary)
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = {
                  if (index + 1 < items.size) {
                    index++
                    chosenAnswer = null
                  } else {
                    finished = true
                    onFinish(score)
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
                modifier = Modifier.fillMaxWidth().height(48.dp),
              ) { Text(if (index + 1 < items.size) "Next Statement" else "Finish & Save") }
            }
          }
        }
      }
    }
  }
}
