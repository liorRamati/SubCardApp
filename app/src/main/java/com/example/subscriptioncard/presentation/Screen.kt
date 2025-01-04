package com.example.subscriptioncard.presentation

sealed class Screen(val route: String) {
    object OptionsScreen: Screen("options_screen")
    object CardDisplayScreen: Screen("card_display_screen")
    object CardHistoryScreen: Screen("card_history_screen")
}