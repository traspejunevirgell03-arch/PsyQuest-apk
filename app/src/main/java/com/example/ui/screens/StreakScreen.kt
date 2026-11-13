package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WellnessDefaults
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestMagenta
import com.example.ui.theme.QuestOrange
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.PsyQuestUiState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StreakScreen(
  uiState: PsyQuestUiState,
  onSaveCheckIn: (
    moodKey: String,
    moodEmoji: String,
    reflection: String,
    affirmation: String,
    completedActivity: String,
  ) -> Unit,
  modifier: Modifier = Modifier,
) {
  val today = remember { LocalDate.now() }
  val todayCheckIn = uiState.todayCheckIn

  var selectedMoodKey by rememberSaveable { mutableStateOf(todayCheckIn?.moodKey ?: "CALM") }
  var reflection by rememberSaveable { mutableStateOf(todayCheckIn?.reflection ?: "") }
  var selectedActivity by rememberSaveable {
    mutableStateOf(todayCheckIn?.completedActivity ?: WellnessDefaults.wellnessActivities.first())
  }
  var affirmationIndex by rememberSaveable { mutableIntStateOf(today.dayOfYear % WellnessDefaults.affirmations.size) }

  LaunchedEffect(todayCheckIn?.id, todayCheckIn?.moodKey) {
    if (todayCheckIn != null) {
      selectedMoodKey = todayCheckIn.moodKey
      if (reflection.isBlank()) reflection = todayCheckIn.reflection
      if (todayCheckIn.completedActivity.isNotBlank()) {
        selectedActivity = todayCheckIn.completedActivity
      }
    }
  }

  val selectedMood =
    WellnessDefaults.moods.firstOrNull { it.key == selectedMoodKey } ?: WellnessDefaults.moods[1]
  val currentAffirmation = WellnessDefaults.affirmations[affirmationIndex]
  val checkedDateSet = remember(uiState.checkIns) { uiState.checkIns.map { it.dateKey }.toSet() }
  val yesterdayMissed =
    uiState.checkIns.isNotEmpty() &&
      todayCheckIn == null &&
      today.minusDays(1).toString() !in checkedDateSet

  RainbowAuroraBackground(modifier = modifier.fillMaxSize().testTag("screen_streak")) {
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().testTag("streak_feed_list"),
    ) {
      // Streak Summary Card
      item(key = "streak_summary_card") {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = PureWhite,
          shadowElevation = 6.dp,
          border = BorderStroke(1.5.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier =
                    Modifier.size(48.dp)
                      .clip(CircleShape)
                      .background(PsyQuestBrushes.FlameGradient),
                ) {
                  Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(28.dp),
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "${uiState.currentStreakCount}-Day Wellness Streak",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary,
                  )
                  Text(
                    text = "Best: ${uiState.longestStreakCount} days • Total check-ins: ${uiState.checkIns.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                  )
                }
              }
            }

            if (yesterdayMissed) {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = QuestBlue.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, QuestBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text =
                    "Took a break yesterday? That's completely okay. Wellness isn't a score—today is a fresh, kind moment to check in.",
                  style = MaterialTheme.typography.bodySmall,
                  color = InkPrimary,
                  modifier = Modifier.padding(12.dp),
                )
              }
            }
          }
        }
      }

      // Daily Mood Check-In Card
      item(key = "daily_check_in_card") {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = PureWhite,
          shadowElevation = 6.dp,
          border = BorderStroke(1.3.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "How are you feeling today?",
                style = MaterialTheme.typography.titleLarge,
                color = InkPrimary,
              )
              if (todayCheckIn != null) {
                Icon(
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = "Checked in today",
                  tint = QuestGreen,
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              WellnessDefaults.moods.forEach { mood ->
                val isSelected = mood.key == selectedMoodKey
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = if (isSelected) mood.accentColor.copy(alpha = 0.16f) else SoftCanvas,
                  border =
                    BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) mood.accentColor else HairlineBorder,
                    ),
                  modifier =
                    Modifier.clip(RoundedCornerShape(16.dp))
                      .clickable { selectedMoodKey = mood.key }
                      .testTag("mood_option_${mood.key.lowercase()}"),
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  ) {
                    Text(text = mood.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                      text = mood.label,
                      style =
                        MaterialTheme.typography.labelSmall.copy(
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                      color = InkPrimary,
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = reflection,
              onValueChange = { if (it.length <= 600) reflection = it },
              label = { Text("Optional private reflection (saved only for you)") },
              minLines = 2,
              maxLines = 5,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().testTag("checkin_reflection_input"),
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Affirmation
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = SoftCanvas,
              border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp),
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Daily Affirmation",
                    style = MaterialTheme.typography.labelLarge,
                    color = QuestPurple,
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = currentAffirmation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkPrimary,
                  )
                }
                IconButton(
                  onClick = {
                    affirmationIndex = (affirmationIndex + 1) % WellnessDefaults.affirmations.size
                  }
                ) {
                  Icon(Icons.Filled.Refresh, contentDescription = "Next affirmation", tint = QuestPurple)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Small Wellness Activity",
              style = MaterialTheme.typography.labelLarge,
              color = InkPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              WellnessDefaults.wellnessActivities.forEach { activity ->
                FilterChip(
                  selected = selectedActivity == activity,
                  onClick = { selectedActivity = activity },
                  label = { Text(activity) },
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                onSaveCheckIn(
                  selectedMood.key,
                  selectedMood.emoji,
                  reflection,
                  currentAffirmation,
                  selectedActivity,
                )
              },
              enabled = !uiState.isSavingCheckIn,
              colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_checkin_button"),
            ) {
              if (uiState.isSavingCheckIn) {
                CircularProgressIndicator(
                  color = PureWhite,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(20.dp),
                )
              } else {
                Text(
                  text = if (todayCheckIn == null) "Save Today's Check-In" else "Update Today's Check-In",
                  fontWeight = FontWeight.Bold,
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = InkMuted,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text =
                  "Check-ins and mood reflections are private to your account. PsyQuest is educational and not a substitute for clinical care.",
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted,
              )
            }
          }
        }
      }

      // Monthly Check-In Calendar Card
      item(key = "monthly_calendar_card") {
        MonthlyCheckInCalendarCard(
          today = today,
          checkedDateKeys = checkedDateSet,
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        )
      }

      // Recent Private Check-In History
      if (uiState.checkIns.isNotEmpty()) {
        item(key = "history_header") {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhite,
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
          ) {
            Text(
              text = "Recent Private Mood History",
              style = MaterialTheme.typography.titleMedium,
              color = InkPrimary,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
          }
        }
        items(uiState.checkIns.take(7), key = { it.dateKey }) { entry ->
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhite,
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
          ) {
            Row(
              verticalAlignment = Alignment.Top,
              modifier = Modifier.padding(14.dp),
            ) {
              Text(text = entry.moodEmoji, fontSize = 26.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${entry.dateKey} • ${entry.moodKey.lowercase().replaceFirstChar { it.uppercase() }}",
                  style = MaterialTheme.typography.titleSmall,
                  color = InkPrimary,
                )
                if (entry.reflection.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = entry.reflection,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                  )
                }
                if (entry.completedActivity.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Activity: ${entry.completedActivity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = QuestPurple,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MonthlyCheckInCalendarCard(
  today: LocalDate,
  checkedDateKeys: Set<String>,
  modifier: Modifier = Modifier,
) {
  val yearMonth = YearMonth.from(today)
  val monthTitle =
    "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}"
  val firstDayOffset = yearMonth.atDay(1).dayOfWeek.value % 7 // Sun = 0 .. Sat = 6
  val daysInMonth = yearMonth.lengthOfMonth()
  val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")

  Surface(
    shape = RoundedCornerShape(24.dp),
    color = PureWhite,
    shadowElevation = 5.dp,
    border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
    modifier = modifier,
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "Monthly Wellness Calendar • $monthTitle",
        style = MaterialTheme.typography.titleMedium,
        color = InkPrimary,
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth()) {
        dayHeaders.forEach { d ->
          Text(
            text = d,
            style = MaterialTheme.typography.labelSmall,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))

      val totalCells = firstDayOffset + daysInMonth
      val rows = (totalCells + 6) / 7
      for (r in 0 until rows) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
          for (c in 0 until 7) {
            val cellIndex = r * 7 + c
            val dayNum = cellIndex - firstDayOffset + 1
            if (dayNum in 1..daysInMonth) {
              val date = yearMonth.atDay(dayNum)
              val dateKey = date.toString()
              val isChecked = dateKey in checkedDateKeys
              val isToday = date == today

              Box(
                contentAlignment = Alignment.Center,
                modifier =
                  Modifier.weight(1f)
                    .height(34.dp)
                    .padding(horizontal = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                      when {
                        isChecked -> Brush.horizontalGradient(listOf(QuestPurple, QuestMagenta, QuestOrange))
                        isToday -> Brush.horizontalGradient(listOf(QuestBlue.copy(alpha = 0.14f), QuestPurple.copy(alpha = 0.14f)))
                        else -> Brush.horizontalGradient(listOf(SoftCanvas, SoftCanvas))
                      }
                    )
                    .then(
                      if (isToday && !isChecked) Modifier.border(1.5.dp, QuestPurple, RoundedCornerShape(10.dp))
                      else Modifier
                    ),
              ) {
                Text(
                  text = dayNum.toString(),
                  style = MaterialTheme.typography.labelMedium,
                  color = if (isChecked) PureWhite else InkPrimary,
                )
              }
            } else {
              Spacer(modifier = Modifier.weight(1f).height(34.dp))
            }
          }
        }
      }
    }
  }
}
