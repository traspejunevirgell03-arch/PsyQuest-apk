package com.example.ui.navigation

import androidx.annotation.StringRes
import com.example.R

enum class PsyQuestDestination(
  val route: String,
  @param:StringRes val labelRes: Int,
  @param:StringRes val contentDescriptionRes: Int,
  val testTag: String,
) {
  HOME(
    route = "home",
    labelRes = R.string.nav_home,
    contentDescriptionRes = R.string.cd_nav_home,
    testTag = "nav_home_button",
  ),
  STREAK(
    route = "streak",
    labelRes = R.string.nav_streak,
    contentDescriptionRes = R.string.cd_nav_streak,
    testTag = "nav_streak_button",
  ),
  CREATE_POST(
    route = "create_post",
    labelRes = R.string.nav_create_post,
    contentDescriptionRes = R.string.cd_nav_create_post,
    testTag = "nav_create_post_button",
  ),
  GAMES(
    route = "games",
    labelRes = R.string.nav_games,
    contentDescriptionRes = R.string.cd_nav_games,
    testTag = "nav_games_button",
  ),
  E_LIBRARY(
    route = "e_library",
    labelRes = R.string.nav_e_library,
    contentDescriptionRes = R.string.cd_nav_e_library,
    testTag = "nav_e_library_button",
  ),
}
