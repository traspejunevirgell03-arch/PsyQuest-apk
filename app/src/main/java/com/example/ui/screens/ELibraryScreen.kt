package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ELibraryCatalog
import com.example.model.ELibraryCategory
import com.example.model.ELibraryResource
import com.example.ui.components.RainbowAuroraBackground
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.PsyQuestBrushes
import com.example.ui.theme.PureWhite
import com.example.ui.theme.QuestBlue
import com.example.ui.theme.QuestPurple
import com.example.ui.theme.SoftCanvas
import com.example.ui.viewmodel.PsyQuestUiState

@Composable
fun ELibraryScreen(
  uiState: PsyQuestUiState,
  onToggleBookmark: (ELibraryResource) -> Unit,
  initialResourceId: String? = null,
  onClearInitialResource: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var query by rememberSaveable { mutableStateOf("") }
  var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }
  var savedOnly by rememberSaveable { mutableStateOf(false) }
  var detailResourceId by rememberSaveable(initialResourceId) { mutableStateOf(initialResourceId) }

  val savedIds = uiState.bookmarkedLibraryIds
  val selectedCategory =
    selectedCategoryName?.let { name -> runCatching { ELibraryCategory.valueOf(name) }.getOrNull() }

  val filteredResources =
    remember(query, selectedCategory, savedOnly, savedIds) {
      val q = query.trim().lowercase()
      ELibraryCatalog.resources.filter { res ->
        val matchesCategory = selectedCategory == null || res.category == selectedCategory
        val matchesSaved = !savedOnly || res.id in savedIds
        val matchesQuery =
          q.isEmpty() ||
            res.title.lowercase().contains(q) ||
            res.authorOrOrg.lowercase().contains(q) ||
            res.summary.lowercase().contains(q) ||
            res.category.displayName.lowercase().contains(q)
        matchesCategory && matchesSaved && matchesQuery
      }
    }

  val openExternal: (ELibraryResource) -> Unit = { res ->
    runCatching {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.externalUrl))
      context.startActivity(intent)
    }
  }

  RainbowAuroraBackground(modifier = modifier.fillMaxSize().testTag("screen_e_library")) {
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize(),
    ) {
      item(key = "elibrary_header_card") {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = PureWhite,
          shadowElevation = 6.dp,
          border = BorderStroke(1.5.dp, PsyQuestBrushes.CardAccentBorder),
          modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = stringResource(R.string.e_library_preview_title),
              style = MaterialTheme.typography.titleLarge,
              color = InkPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text =
                "Openly licensed & public-domain psychology textbooks, WHO/NIMH wellness guides, academic articles, and board exam review references.",
              style = MaterialTheme.typography.bodySmall,
              color = InkSecondary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = query,
              onValueChange = { query = it },
              placeholder = { Text("Search topics, authors, or exam guides…") },
              leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = QuestPurple) },
              singleLine = true,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.fillMaxWidth().testTag("elibrary_search_input"),
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              item {
                FilterChip(
                  selected = selectedCategory == null && !savedOnly,
                  onClick = {
                    selectedCategoryName = null
                    savedOnly = false
                  },
                  label = { Text("All (${ELibraryCatalog.resources.size})") },
                )
              }
              item {
                FilterChip(
                  selected = savedOnly,
                  onClick = { savedOnly = !savedOnly },
                  label = { Text("Saved (${savedIds.size})") },
                )
              }
              items(ELibraryCategory.entries, key = { it.name }) { category ->
                FilterChip(
                  selected = selectedCategory == category,
                  onClick = {
                    selectedCategoryName = if (selectedCategory == category) null else category.name
                    savedOnly = false
                  },
                  label = { Text(category.displayName) },
                )
              }
            }
          }
        }
      }

      if (filteredResources.isEmpty()) {
        item(key = "elibrary_empty") {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
          ) {
            Text(
              text = "No matching resources found. Clear search or choose another category.",
              style = MaterialTheme.typography.bodyMedium,
              color = InkSecondary,
              modifier = Modifier.padding(20.dp),
            )
          }
        }
      } else {
        items(filteredResources, key = { it.id }) { resource ->
          val isSaved = resource.id in savedIds
          ELibraryResourceCard(
            resource = resource,
            isSaved = isSaved,
            onOpenDetail = { detailResourceId = resource.id },
            onToggleBookmark = { onToggleBookmark(resource) },
            onOpenExternal = { openExternal(resource) },
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
          )
        }
      }
    }
  }

  val detailResource =
    detailResourceId?.let { id -> ELibraryCatalog.resources.firstOrNull { it.id == id } }
  if (detailResource != null) {
    ELibraryDetailSheet(
      resource = detailResource,
      isSaved = detailResource.id in savedIds,
      onToggleBookmark = { onToggleBookmark(detailResource) },
      onOpenExternal = { openExternal(detailResource) },
      onDismiss = {
        detailResourceId = null
        onClearInitialResource()
      },
    )
  }
}

@Composable
private fun ELibraryResourceCard(
  resource: ELibraryResource,
  isSaved: Boolean,
  onOpenDetail: () -> Unit,
  onToggleBookmark: () -> Unit,
  onOpenExternal: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = PureWhite,
    shadowElevation = 5.dp,
    border = BorderStroke(1.2.dp, PsyQuestBrushes.CardAccentBorder),
    modifier =
      modifier
        .clip(RoundedCornerShape(22.dp))
        .clickable(onClick = onOpenDetail)
        .testTag("elibrary_card_${resource.id}"),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(shape = RoundedCornerShape(50), color = QuestPurple.copy(alpha = 0.12f)) {
          Text(
            text = resource.category.displayName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = QuestPurple,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier.testTag("elibrary_bookmark_${resource.id}"),
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = if (isSaved) "Remove bookmark" else "Save resource",
            tint = if (isSaved) QuestPurple else InkSecondary,
          )
        }
      }

      Text(text = resource.title, style = MaterialTheme.typography.titleMedium, color = InkPrimary)
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = "${resource.authorOrOrg} • ${resource.sourcePlatform} (${resource.licenseLabel})",
        style = MaterialTheme.typography.labelSmall,
        color = QuestBlue,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = resource.summary, style = MaterialTheme.typography.bodySmall, color = InkSecondary)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = resource.readingTime, style = MaterialTheme.typography.labelSmall, color = InkMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = onOpenDetail, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Outlined.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text("Summary")
          }
          Button(
            onClick = onOpenExternal,
            colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
            shape = RoundedCornerShape(12.dp),
          ) {
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text("Open")
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ELibraryDetailSheet(
  resource: ELibraryResource,
  isSaved: Boolean,
  onToggleBookmark: () -> Unit,
  onOpenExternal: () -> Unit,
  onDismiss: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 22.dp)
          .padding(bottom = 22.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = resource.category.displayName,
          style = MaterialTheme.typography.labelLarge,
          color = QuestPurple,
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_close), tint = InkSecondary)
        }
      }

      Text(text = resource.title, style = MaterialTheme.typography.titleLarge, color = InkPrimary)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "${resource.authorOrOrg} • ${resource.sourcePlatform} • ${resource.licenseLabel}",
        style = MaterialTheme.typography.bodySmall,
        color = QuestBlue,
      )
      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = HairlineBorder)
      Spacer(modifier = Modifier.height(10.dp))

      Text(text = resource.summary, style = MaterialTheme.typography.bodyMedium, color = InkSecondary)
      Spacer(modifier = Modifier.height(12.dp))

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = SoftCanvas,
        border = BorderStroke(1.dp, PsyQuestBrushes.CardAccentBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(14.dp)) {
          Text("Key Study Takeaways", style = MaterialTheme.typography.titleSmall, color = InkPrimary)
          resource.keyTakeaways.forEach { bullet ->
            Text("• $bullet", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
          onClick = onToggleBookmark,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(0.42f).height(48.dp),
        ) {
          Text(if (isSaved) "Saved ✓" else "Save")
        }
        Button(
          onClick = onOpenExternal,
          colors = ButtonDefaults.buttonColors(containerColor = QuestPurple, contentColor = PureWhite),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(0.58f).height(48.dp),
        ) {
          Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Open External Guide")
        }
      }
    }
  }
}
